package com.erkan.zombienado2.server;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.erkan.zombienado2.data.world.Map;
import com.erkan.zombienado2.data.weapons.WeaponData;
import com.erkan.zombienado2.networking.ServerHeaders;
import com.erkan.zombienado2.server.misc.FilterConstants;
import com.erkan.zombienado2.server.networking.ConnectionListener;
import com.erkan.zombienado2.server.networking.ConnectionManager;

import java.net.Socket;
import java.util.*;

/**
 * Created by Erik on 2018-07-29.
 */
public class Server implements ConnectionListener, ContactListener {
    private final int CLIENTS_TO_ACCEPT;

    private static boolean isAwaitingConnection = true;

    PlayerModel[] players;
    List<Body> alive_bullets = new LinkedList<>();
    List<Zombie> zombies = new LinkedList<>();

    int wave = 1;
    boolean wave_ongoing = false;
    float between_wave_timer = 0;
    public final float TIME_BETWEEN_WAVES = 10f;


    public Server(final int PORT, final int clients_to_accept){
        Box2D.init();
        CLIENTS_TO_ACCEPT = clients_to_accept;
        players = new PlayerModel[CLIENTS_TO_ACCEPT];
        World world = new World(new Vector2(0, 0), false);
        WorldManager.setWorld(world, this);
        Map.TEST_MAP.getStructures().stream().forEach(structure -> WorldManager.createPrefab(structure.getFirst()));

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
                players[identifier].setVelocity(new Vector2(Float.parseFloat(arguments[1]), Float.parseFloat(arguments[2])));
                break;
            case "rotate":
                players[identifier].setRotation(Float.parseFloat(arguments[1]));
                break;
            case "fire":
                synchronized (this) { //Woops, retarded code below
                    if (players[identifier].weapon.fire()) {
                        if (players[identifier].weapon.getWeaponData().bullets_per_round == 1) {
                            float recoiled_rotation = players[identifier].rotation + (float) (Math.random() - .5f) * players[identifier].weapon.getCurrent_spread();
                            float dx = (float) Math.cos(Math.toRadians(recoiled_rotation));
                            float dy = (float) Math.sin(Math.toRadians(recoiled_rotation));
                            Vector2 dir = new Vector2(dx, dy);
                            Vector2 origin = new Vector2(players[identifier].body.getPosition().x + (float) Math.cos(Math.toRadians(players[identifier].rotation - 10f)) * .5f, players[identifier].body.getPosition().y + (float) Math.sin(Math.toRadians(players[identifier].rotation - 10f)) * .5f);
                            alive_bullets.add(BulletFactory.createBullet(origin, dir, players[identifier].weapon.getWeaponData().damage, identifier));
                        } else {
                            int bullets = players[identifier].weapon.getWeaponData().bullets_per_round;
                            float spacing_angle = players[identifier].weapon.getWeaponData().spread / (float)bullets;
                            for (int i = 0; i < bullets; i++) {

                                float recoiled_rotation = players[identifier].rotation - players[identifier].weapon.getWeaponData().spread / 2  + (float) spacing_angle * i + (float)(Math.random()-.5f) * players[identifier].weapon.getWeaponData().recoil;
                                float dx = (float) Math.cos(Math.toRadians(recoiled_rotation));
                                float dy = (float) Math.sin(Math.toRadians(recoiled_rotation));
                                Vector2 dir = new Vector2(dx, dy);
                                Vector2 origin = new Vector2(players[identifier].body.getPosition().x + (float) Math.cos(Math.toRadians(players[identifier].rotation - 10f)) * .5f, players[identifier].body.getPosition().y + (float) Math.sin(Math.toRadians(players[identifier].rotation - 10f)) * .5f);
                                alive_bullets.add(BulletFactory.createBullet(origin, dir, players[identifier].weapon.getWeaponData().damage, identifier));
                            }
                        }
                        ConnectionManager.broadcast(ServerHeaders.CREATE_BULLET, identifier); //for fx and stuff
                    } else if (players[identifier].weapon.getClip() == 0){
                        onMsgReceive(identifier,"reload"); //selfcall to reload if out of amo
                    }
                }
                break;
            case "reload":
                if (players[identifier].weapon.reload()){
                    ConnectionManager.broadcast(ServerHeaders.PLAYER_RELOAD, identifier);
                }
                break;
            case "switch_weapon":
                if (!players[identifier].weapon.getWeaponData().toString().equals(arguments[1])) {
                    players[identifier].weapon = new WeaponModel(WeaponData.getWeapon(arguments[1]));
                }
                break;
        }
    }

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
        ConnectionManager.send(identifier, ServerHeaders.JOIN_SELF +" " + identifier + " "+ CLIENTS_TO_ACCEPT);
        for (int i = 0; i < players.length; i++){
            if (players[i] == null)
                return;
        }
        isAwaitingConnection = false;
        launch();

    }

    public void launch(){
        for (int i = 0; i < players.length; i++) {
            PlayerModel player = players[i];
            ConnectionManager.broadcast(ServerHeaders.JOIN_PLAYER, i, player.name, player.character);
        }

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

        for (int i = 0; i < players.length; i++) {
            PlayerModel player = players[i];
            ConnectionManager.broadcast(ServerHeaders.UPDATE_PLAYER, i, player.body.getPosition().x, player.body.getPosition().y, player.rotation, player.weapon.getWeaponData().toString());
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


        if (wave_ongoing) {
            zombie_spawn_accumulator += delta;
            float wave_size = Zombie.DEF_WAVE_SIZE + (float)wave;
            float spawn_rate = Zombie.DEF_SPAWN_RATE + Zombie.DEF_SPAWN_RATE/(float)wave;
            float max_health = Zombie.DEF_MAX_HEALTH + wave;

            if (zombies.size() < wave_size && zombie_spawn_accumulator > spawn_rate) {
                zombie_spawn_accumulator -= spawn_rate;
                zombies.add(new Zombie(MathUtils.random(-10f, 10f), MathUtils.random(-10f, 10f), max_health));
            }

            boolean all_dead = zombies.size() == wave_size;
            StringBuilder sb = new StringBuilder();
            sb.append(ServerHeaders.UPDATE_ZOMBIES);
            Iterator<Zombie> zombie_iterator = zombies.iterator();

            while (zombie_iterator.hasNext()) {
                Zombie zombie = zombie_iterator.next();
                zombie.update();
                if (zombie.updateDirection()) {
                    Vector2 shortest_distance = new Vector2(9999, 9999);
                    for (PlayerModel player : players) {
                        Vector2 pp = player.body.getPosition();
                        Vector2 zp = zombie.getPosition();
                        Vector2 dist = pp.sub(zp);
                        if (dist.len() < shortest_distance.len()) {
                            shortest_distance = dist;
                        }
                    }
                    //TODO: set position instead of direction!
                    if (zombie.getBehavior().equals(Zombie.Behavior.Hunting) && shortest_distance.len() < Zombie.HUNT_RANGE || shortest_distance.len() < Zombie.AGRO_RANGE) {
                        zombie.setDirection(shortest_distance.setLength(1));
                        zombie.setBehavior(Zombie.Behavior.Hunting);
                    } else {
                        if (MathUtils.randomBoolean(.1f)){
                            zombie.setDirection(new Vector2(0, 0));
                            zombie.setBehavior(Zombie.Behavior.Standing);
                        } else {
                            zombie.setDirection(new Vector2(MathUtils.random(-1f, 1f), MathUtils.random(-1f, 1f)).setLength(1));
                            zombie.setBehavior(Zombie.Behavior.Roaming);
                        }
                    }
                }

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
    }

    private void startWave(){
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
    }

    @Override
    public void beginContact(Contact contact) {

    }

    @Override
    public void endContact(Contact contact) {

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
                    dmg = Math.abs(dmg);
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
                    dmg = Math.abs(dmg);
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

                }
                zombie.setAttacking();
            }
        }

        if (contact.getFixtureB().getFilterData().categoryBits == FilterConstants.ENEMY_FIXTURE){
            if (contact.getFixtureA().getFilterData().categoryBits == FilterConstants.PLAYER_FIXTURE){
                Zombie zombie = (Zombie)contact.getFixtureB().getBody().getUserData();
                if (zombie.attack_finished()){

                }
                zombie.setAttacking();
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
