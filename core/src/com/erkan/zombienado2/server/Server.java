package com.erkan.zombienado2.server;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.World;
import com.erkan.zombienado2.client.Player;
import com.erkan.zombienado2.client.world.*;
import com.erkan.zombienado2.data.world.Map;
import com.erkan.zombienado2.data.weapons.WeaponData;
import com.erkan.zombienado2.graphics.Transform;
import com.erkan.zombienado2.networking.ServerHeaders;
import com.erkan.zombienado2.server.loots.Loot;
import com.erkan.zombienado2.server.loots.MedPack;
import com.erkan.zombienado2.server.loots.WeaponPack;
import com.erkan.zombienado2.server.misc.FilterConstants;
import com.erkan.zombienado2.server.networking.ConnectionListener;
import com.erkan.zombienado2.server.networking.ConnectionManager;

import java.net.Socket;
import java.sql.Connection;
import java.util.*;

/**
 * Created by Erik on 2018-07-29.
 */
public class Server implements ConnectionListener, ContactListener {
    private static Server instance = null;
    private final int CLIENTS_TO_ACCEPT;

    private static boolean isAwaitingConnection = true;

    private long last_tick;
    private int maintained_tickrate;

    PlayerModel[] players;
    List<Body> alive_bullets = new LinkedList<>();
    List<Zombie> zombies = new LinkedList<>();
    List<Loot> loot = new LinkedList<>();
    List<Vector2> addLootList = new LinkedList<>();

    int wave = 1;
    boolean wave_ongoing = false;
    float between_wave_timer = 0;

    int score = 0;
    boolean game_over = false;


    public final float TIME_BETWEEN_WAVES = 10f;


    public Server(final int PORT, final int clients_to_accept){
        if (instance == null)
            instance = this;
        Box2D.init();
        CLIENTS_TO_ACCEPT = clients_to_accept;
        players = new PlayerModel[CLIENTS_TO_ACCEPT];
        World world = new World(new Vector2(0, 0), false);
        WorldManager.setWorld(world, this);
        Map.TEST_MAP.getStructures().stream().forEach(structure -> WorldManager.createPrefab(structure.getFirst()));
        Map.TEST_MAP.getBoundaries().stream().forEach(wall -> WorldManager.createWall(wall.getFirst()));
        Map.TEST_MAP.get_all_objects().stream().forEach(obj -> {
            if (obj instanceof Solid){
                WorldManager.createRect(((Solid)obj).getBounds());
            }
        });
        WorldManager.getNavigationGraph().construct();
        //has to be done last i think, because context need to be initialized (eg world)
        ConnectionManager.init(this, PORT);
        ConnectionManager.accept(CLIENTS_TO_ACCEPT);
    }

    @Override
    public void onMsgReceive(int identifier, String msg) {
        //System.out.println("Server got: " + msg);
        String[] arguments = msg.split(" ");

        switch (arguments[0]){
            case "create":
                if (isAwaitingConnection)
                    createPlayer(arguments[1], arguments[2], identifier);
                break;
            case "move":
                if (players[identifier].is_alive())
                    players[identifier].setVelocity(new Vector2(Float.parseFloat(arguments[1]), Float.parseFloat(arguments[2])));
                break;
            case "rotate":
                if (players[identifier].is_alive())
                    players[identifier].setRotation(Float.parseFloat(arguments[1]));
                break;
            case "fire":
                if (!players[identifier].is_alive())
                    break;
                synchronized (this) { //Woops, retarded code below
                    if (players[identifier].getWeapon().fire()) {
                        if (players[identifier].getWeapon().getWeaponData().bullets_per_round == 1) {
                            float recoiled_rotation = players[identifier].rotation + (float) (Math.random() - .5f) * players[identifier].getWeapon().getCurrent_spread() * (players[identifier].isMoving() ? WeaponModel.MOVEMENT_MULTIPLIER : 1);
                            float dx = (float) Math.cos(Math.toRadians(recoiled_rotation));
                            float dy = (float) Math.sin(Math.toRadians(recoiled_rotation));
                            Vector2 dir = new Vector2(dx, dy);
                            Vector2 origin = new Vector2(players[identifier].body.getPosition().x + (float) Math.cos(Math.toRadians(players[identifier].rotation - 10f)) * .5f, players[identifier].body.getPosition().y + (float) Math.sin(Math.toRadians(players[identifier].rotation - 10f)) * .5f);
                            alive_bullets.add(BulletFactory.createBullet(origin, dir, players[identifier].getWeapon().getWeaponData().damage, identifier));
                        } else {
                            int bullets = players[identifier].getWeapon().getWeaponData().bullets_per_round;
                            float spacing_angle = players[identifier].getWeapon().getWeaponData().spread / (float)bullets;
                            for (int i = 0; i < bullets; i++) {

                                float recoiled_rotation = players[identifier].rotation - players[identifier].getWeapon().getWeaponData().spread / 2  + (float) spacing_angle * i + (float)(Math.random()-.5f) * players[identifier].getWeapon().getWeaponData().recoil;
                                float dx = (float) Math.cos(Math.toRadians(recoiled_rotation));
                                float dy = (float) Math.sin(Math.toRadians(recoiled_rotation));
                                Vector2 dir = new Vector2(dx, dy);
                                Vector2 origin = new Vector2(players[identifier].body.getPosition().x + (float) Math.cos(Math.toRadians(players[identifier].rotation - 10f)) * .5f, players[identifier].body.getPosition().y + (float) Math.sin(Math.toRadians(players[identifier].rotation - 10f)) * .5f);
                                alive_bullets.add(BulletFactory.createBullet(origin, dir, players[identifier].getWeapon().getWeaponData().damage, identifier));
                            }
                        }
                        ConnectionManager.broadcast(ServerHeaders.CREATE_BULLET, identifier); //for fx and stuff
                    } else if (players[identifier].getWeapon().getClip() == 0){
                        onMsgReceive(identifier,"reload"); //selfcall to reload if out of amo
                    }
                }
                break;
            case "reload":
                if (!players[identifier].is_alive())
                    break;
                if (players[identifier].getWeapon().reload()){
                    ConnectionManager.broadcast(ServerHeaders.PLAYER_RELOAD, identifier);
                }
                break;
            case "action":
                if (!players[identifier].is_alive())
                    break;
                players[identifier].performAction();
                break;
            case "switch_weapon":
                if (!players[identifier].is_alive())
                    break;
                players[identifier].switchWeapon();
                break;
            case "ping":
                ConnectionManager.send(identifier, ServerHeaders.PING_RESPONSE + " " + maintained_tickrate);
                break;
        }
    }

    @Override
    public void connect(int identifier, Socket socket){
        ConnectionManager.send(identifier, ServerHeaders.JOIN_SELF +" " + identifier + " "+ CLIENTS_TO_ACCEPT);
        ConnectionManager.getConnections().forEach(con -> {
            ConnectionManager.send(identifier, ServerHeaders.CONNECT_TO_LOBBY + " " + con);
        });
        for (int i = 0; i < players.length; i++) {
            PlayerModel player = players[i];
            if (player != null)
                ConnectionManager.send(identifier, ServerHeaders.JOIN_PLAYER +" " + i+" "+ player.name+ " " + player.character);
        }

        ConnectionManager.broadcast(ServerHeaders.CONNECT_TO_LOBBY, socket.getRemoteSocketAddress().toString());
    }

    /**
     * Only if connection reset, and client rejoins
     * @param identifier
     * @param socket
     */
    @Override
    public void reconnect(int identifier, Socket socket) {
        ConnectionManager.send(identifier, ServerHeaders.JOIN_SELF + " " + identifier + " " + CLIENTS_TO_ACCEPT);
        for (int i = 0; i < players.length; i++) {
            PlayerModel player = players[i];
            ConnectionManager.send(identifier, ServerHeaders.JOIN_PLAYER +" " + i+" "+ player.name+ " " + player.character);
        }
    }


    public void createPlayer(String name, String character, int identifier){
        players[identifier] = new PlayerModel(name, character);
        players[identifier].body.setTransform(5f,5f, 0f);
        ConnectionManager.broadcast(ServerHeaders.JOIN_PLAYER, identifier, players[identifier].name, players[identifier].character);
      /*  for (int i = 0; i < players.length; i++) {
            PlayerModel player = players[i];
            if (player != null)
                ConnectionManager.send(identifier, ServerHeaders.JOIN_PLAYER + " " + i + " " + player.name + " " + player.character); //send to self that other have joined
        } */

        for (int i = 0; i < players.length; i++){
            if (players[i] == null)
                return;
        }
        isAwaitingConnection = false;
        launch();

    }

    public void launch(){
        /*
        for (int i = 0; i < players.length; i++) {
            PlayerModel player = players[i];
            ConnectionManager.broadcast(ServerHeaders.JOIN_PLAYER, i, player.name, player.character);
        }
*/
        ConnectionManager.broadcast(ServerHeaders.LAUNCH_GAME);
        startTickSequence();
    }

    private static final long TICK_RATE = (long)(1f/60f * 1000f);
    private boolean running;
    private void startTickSequence(){
        running = true;
        new Thread(()->{
            long last = System.currentTimeMillis();
            long elapsed;
            while (running){
                tick();
                long now = System.currentTimeMillis();
                elapsed = now - last;
                last = now;
                try {
                    Thread.sleep(TICK_RATE); //TODO
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public final static float STEP_TIME = 1f/60f;
    float accumulator = 0;

    float zombie_spawn_accumulator = 0;
    public void tick(){
        if(game_over)
            return;


        float delta = STEP_TIME;//Gdx.graphics.getDeltaTime();
        accumulator += Math.min(delta, 0.25f);
        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;
            synchronized (this){
                WorldManager.getWorld().step(1f/60f, 6, 2);
            }
        }

        for (int i = 0; i < players.length; i++) {
            PlayerModel player = players[i];
            player.update(delta);
        }

        boolean allPlayersDead = true;
        for (int i = 0; i < players.length; i++) {
            PlayerModel player = players[i];
            ConnectionManager.broadcast(ServerHeaders.UPDATE_PLAYER, i, player.body.getPosition().x, player.body.getPosition().y, player.rotation, player.getHealth(), player.getWeapon().getWeaponData().toString(), player.getWeapon().getClip(), player.getWeapon().getExcessAmmo(), player.movement_vector.x, player.movement_vector.y, player.is_alive());
            if (player.is_alive())
                allPlayersDead = false;
        }

        if (allPlayersDead){
            gameOver();
            return;
        }

        long now = System.currentTimeMillis();
        synchronized (this) {
            StringBuilder sb = new StringBuilder();
            sb.append(ServerHeaders.UPDATE_BULLETS);
            Iterator<Body> bullet_iterator = alive_bullets.iterator();

            while (bullet_iterator.hasNext()) {
                Body bullet = bullet_iterator.next();
                long timestamp = (Long) ((HashMap) bullet.getUserData()).get("time_stamp");
                float dmg_left = (float) ((HashMap) bullet.getUserData()).get("damage");
                if (dmg_left == 0f || now - timestamp > BulletFactory.TIME_TO_LIVE) {
                    bullet_iterator.remove();
                    WorldManager.getWorld().destroyBody(bullet);
                } else {
                    sb.append(" " + bullet.getPosition().x + " " + bullet.getPosition().y + " " + bullet.getLinearVelocity().x + " " + bullet.getLinearVelocity().y);
                }
            }
            ConnectionManager.broadcast(sb.toString());
        }

        for (Vector2 vec : addLootList) {
            loot.add(new MedPack(vec.x, vec.y, false));
        }
        addLootList = new ArrayList<>();

        synchronized (this) {
            StringBuilder sb = new StringBuilder();
            sb.append(ServerHeaders.UPDATE_LOOT);
            Iterator<Loot> loot_iterator = loot.iterator();

            while (loot_iterator.hasNext()) {
                Loot loot = loot_iterator.next();
                if (loot.shouldBeRemoved(System.currentTimeMillis())){
                    loot.destroy();
                    loot_iterator.remove();
                } else
                    sb.append(" " + loot.getX() + " " + loot.getY() + " " + loot.toString());
            }
            ConnectionManager.broadcast(sb.toString());
        }


        if (wave_ongoing) {
            zombie_spawn_accumulator += delta;
            float wave_size = (Zombie.DEF_WAVE_SIZE + (float)wave * 2) * players.length;
            float spawn_rate = Zombie.DEF_SPAWN_RATE; //TODO:change this
            float max_health = Zombie.DEF_MAX_HEALTH + wave * 2;

            if (zombies.size() < wave_size && zombie_spawn_accumulator > spawn_rate) {
                zombie_spawn_accumulator -= spawn_rate;
                Vector2 spawnpos = Map.TEST_MAP.getRandomSpawnpoint();
                zombies.add(new Zombie(spawnpos.x, spawnpos.y, max_health));
            }

            boolean all_dead = zombies.size() == wave_size;
            StringBuilder sb = new StringBuilder();
            sb.append(ServerHeaders.UPDATE_ZOMBIES);
            Iterator<Zombie> zombie_iterator = zombies.iterator();

            while (zombie_iterator.hasNext()) {
                Zombie zombie = zombie_iterator.next();
                Vector2 shortest_distance = new Vector2(9999, 9999);
                Vector2 target_position = null;

                for (PlayerModel player : players) {
                    if (!player.is_alive())
                        continue;
                    Vector2 zp = zombie.getPosition().cpy();
                    Vector2 pp = new Vector2(player.body.getPosition().x, player.body.getPosition().y);
                    Vector2 dist = pp.cpy().sub(zp);
                    if (dist.len() < shortest_distance.len()) {
                        shortest_distance = dist;
                        target_position = pp;
                    }
                }

                if (zombie.getBehavior().equals(Zombie.Behavior.Hunting) && shortest_distance.len() < Zombie.HUNT_RANGE || shortest_distance.len() < Zombie.AGRO_RANGE) {
                    zombie.setBehavior(Zombie.Behavior.Hunting);
                    zombie.setTarget(target_position);
                } else if (zombie.position_reached()){
                    zombie.setBehavior(Zombie.Behavior.Roaming);
                    Vector2 zombPos = zombie.getPosition().cpy();
                    zombie.setTarget(zombPos.add(new Vector2().setToRandomDirection().scl(MathUtils.random(5f))));
                }

                zombie.update(STEP_TIME);

                if (zombie.isAlive())
                    all_dead = false;
                sb.append(" " + zombie.getPosition().x + " " + zombie.getPosition().y + " " + zombie.getRotation() + " " + zombie.getHealth() + " " + zombie.isAttacking() + " " + zombie.getBehavior());
            }
            ConnectionManager.broadcast(sb.toString());
            if (all_dead)
                clearWave();
        } else {
            //if wave not ongoing
            between_wave_timer += delta;
            if (between_wave_timer >= TIME_BETWEEN_WAVES){
                startWave();
            }
        }

        long timestamp = System.currentTimeMillis();
        float dt = (timestamp - last_tick)/1000f;
        maintained_tickrate = (int)(1f/dt);
        last_tick = timestamp;
    }

    private void gameOver(){
        ConnectionManager.broadcast(ServerHeaders.GAME_OVER, score);
        game_over = true;
        //TODO: close Connection and exit
    }

    private void startWave(){
        Map.TEST_MAP.getLootPoints().stream().forEach(pos -> {
            float chance = MathUtils.random(5f);
            /*boolean occupied = false;
            WorldManager.getWorld().QueryAABB(f -> {
                if (f.isSensor()){
                    occupied = true;
                }
            }, pos.x, pos.y, pos.x, pos.y);
            */
            if (chance < 2)
                loot.add(new WeaponPack(pos.x, pos.y, true));
            else if (chance < 4)
                loot.add(new MedPack(pos.x, pos.y, true));

        });

        ConnectionManager.broadcast(ServerHeaders.WAVE_START, wave);
        between_wave_timer = 0;
        wave_ongoing = true;
        //TEST
    }

    private void clearWave(){
        ConnectionManager.broadcast(ServerHeaders.WAVE_END, wave);
        wave_ongoing = false;
        wave++;
        zombies.stream().forEach(zombie -> zombie.destroy());
        zombies = new LinkedList<>();

        zombie_spawn_accumulator = 0;
        score += 25;

        for (PlayerModel player: players) {
            if (!player.is_alive())
                player.addHealth(20f);
        }
    }

    @Override
    public void beginContact(Contact contact) {
        if (contact.getFixtureA().getFilterData().categoryBits == FilterConstants.LOOT_FIXTURE){
            if (contact.getFixtureB().getFilterData().categoryBits == FilterConstants.PLAYER_FIXTURE){
                ((Loot)contact.getFixtureA().getBody().getUserData()).pickup(((PlayerModel)contact.getFixtureB().getBody().getUserData()));
            }
        }

        if (contact.getFixtureB().getFilterData().categoryBits == FilterConstants.LOOT_FIXTURE){
            if (contact.getFixtureA().getFilterData().categoryBits == FilterConstants.PLAYER_FIXTURE){
                ((Loot)contact.getFixtureB().getBody().getUserData()).pickup(((PlayerModel)contact.getFixtureA().getBody().getUserData()));
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        if (contact.getFixtureA().getFilterData().categoryBits == FilterConstants.LOOT_FIXTURE){
            if (contact.getFixtureB().getFilterData().categoryBits == FilterConstants.PLAYER_FIXTURE){
                ((Loot)contact.getFixtureA().getBody().getUserData()).leave(((PlayerModel)contact.getFixtureB().getBody().getUserData()));
            }
        }

        if (contact.getFixtureB().getFilterData().categoryBits == FilterConstants.LOOT_FIXTURE){
            if (contact.getFixtureA().getFilterData().categoryBits == FilterConstants.PLAYER_FIXTURE){
                ((Loot)contact.getFixtureB().getBody().getUserData()).leave(((PlayerModel)contact.getFixtureA().getBody().getUserData()));
            }
        }
    }

    @Override
    public synchronized void preSolve(Contact contact, Manifold oldManifold) {
        if (contact.getFixtureA().getFilterData().categoryBits == FilterConstants.PROJECTILE_FIXTURE){

            HashMap<String, Number> ud = (HashMap<String, Number>)contact.getFixtureA().getBody().getUserData();
            float dmg = (float)ud.get("damage");

            if (contact.getFixtureB().getFilterData().categoryBits == FilterConstants.ENEMY_FIXTURE){
                Zombie zombie = (Zombie)contact.getFixtureB().getBody().getUserData();
                zombie.setBehavior(Zombie.Behavior.Hunting);
                dmg = zombie.takeDamage(dmg);
                if (dmg < 0){

                    if (MathUtils.random(10f) < 1f){
                        addLootList.add(new Vector2(zombie.getPosition().x, zombie.getPosition().y));
                    }

                    dmg = Math.abs(dmg);
                    score += 3;
                } else
                    dmg = 0;
            } else {
                dmg = 0;
            }

            ud.replace("damage", dmg);
            contact.getFixtureA().getBody().setUserData(ud);
            contact.setEnabled(false);

        } else if (contact.getFixtureB().getFilterData().categoryBits == FilterConstants.PROJECTILE_FIXTURE){
            HashMap<String, Number> ud = (HashMap<String, Number>) contact.getFixtureB().getBody().getUserData();
            float dmg = (float)ud.get("damage");

            if (contact.getFixtureA().getFilterData().categoryBits == FilterConstants.ENEMY_FIXTURE){
                Zombie zombie = (Zombie)contact.getFixtureA().getBody().getUserData();
                zombie.setBehavior(Zombie.Behavior.Hunting);
                dmg = zombie.takeDamage(dmg);
                if (dmg < 0){

                    if (MathUtils.random(10f) < 1f){
                       addLootList.add(new Vector2(zombie.getPosition().x, zombie.getPosition().y));
                    }

                    dmg = Math.abs(dmg);
                    score += 3;
                } else
                    dmg = 0;
            } else {
                dmg = 0;
            }

            ud.replace("damage", dmg);
            contact.getFixtureB().getBody().setUserData(ud);
            contact.setEnabled(false);
        }


        if (contact.getFixtureA().getFilterData().categoryBits == FilterConstants.ENEMY_FIXTURE){
            if (contact.getFixtureB().getFilterData().categoryBits == FilterConstants.PLAYER_FIXTURE){
                Zombie zombie = (Zombie)contact.getFixtureA().getBody().getUserData();
                if (zombie.attack_finished()){
                    PlayerModel player = (PlayerModel)contact.getFixtureB().getBody().getUserData();
                    player.inflict_damage(3f);
                }
                zombie.setAttacking();
            }
        }

        if (contact.getFixtureB().getFilterData().categoryBits == FilterConstants.ENEMY_FIXTURE){
            if (contact.getFixtureA().getFilterData().categoryBits == FilterConstants.PLAYER_FIXTURE){
                Zombie zombie = (Zombie)contact.getFixtureB().getBody().getUserData();
                if (zombie.attack_finished()){
                    PlayerModel player = (PlayerModel)contact.getFixtureA().getBody().getUserData();
                    player.inflict_damage(3f);
                }
                zombie.setAttacking();
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
