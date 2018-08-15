package com.erkan.zombienado2.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.erkan.zombienado2.graphics.Transform;
import com.erkan.zombienado2.server.WorldManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.erkan.zombienado2.server.misc.FilterConstants;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by Erik on 2018-08-13.
 */
public class Zombie {
    public final static Texture[] walking = new Texture[4];
    public final static Texture[] spawning = new Texture[2];
    public final static Texture[] attacking  = new Texture[2];
    public final static Texture[] dying  = new Texture[7];

    public final static Texture[] guts  = new Texture[6];

    public static final Sound splatter = Gdx.audio.newSound(Gdx.files.internal("audio/zombie_splatter.mp3"));

    public static void init(){
        walking[0] = new Texture("animations/zombie/zombie/move/zombie_move_0001.png");
        walking[1] = new Texture("animations/zombie/zombie/move/zombie_move_0002.png");
        walking[2] = new Texture("animations/zombie/zombie/move/zombie_move_0003.png");
        walking[3] = new Texture("animations/zombie/zombie/move/zombie_move_0004.png");

        spawning[0] = new Texture("animations/zombie/zombie/spawn/zombie_spawn_0001.png");
        spawning[1] = new Texture("animations/zombie/zombie/spawn/zombie_spawn_0002.png");

        attacking[0] = new Texture("animations/zombie/zombie/attack/zombie_attack_0001.png");
        attacking[1] = new Texture("animations/zombie/zombie/attack/zombie_attack_0002.png");

        guts[0] = new Texture("animations/zombie/zombie/zombie_guts_0001.png");
        guts[1] = guts[0];
        guts[2] = guts[0];
        guts[3] = guts[0];
        guts[4] = new Texture("animations/zombie/zombie/zombie_guts_0002.png");
        guts[5] = new Texture("animations/zombie/zombie/zombie_guts_0003.png");

        dying[0] = new Texture("animations/zombie/zombie/splatter_1_0001.png");
        dying[1] = new Texture("animations/zombie/zombie/splatter_1_0002.png");
        dying[2] = new Texture("animations/zombie/zombie/splatter_1_0003.png");
        dying[3] = new Texture("animations/zombie/zombie/splatter_1_0004.png");
        dying[4] = new Texture("animations/zombie/zombie/splatter_1_0005.png");
        dying[5] = new Texture("animations/zombie/zombie/splatter_1_0006.png");
        dying[6] = new Texture("animations/zombie/zombie/splatter_1_0007.png");
    }

    private static float fade = 1f;
    private static boolean isFading;

    public static void fade(){
        isFading = true;
    }

    public static void reset_fade(){
        isFading = false;
        fade = 1f;
    }

    public static void static_update(){
        if (isFading){
            fade-=Gdx.graphics.getDeltaTime()/10f;
            if (fade < 0)
                fade = 0;
        }
    }


    Body body;
    float maxHealth;
    float health;
    boolean kill_me = false;
    boolean isDead = false;

    Animation spawn;
    Animation walk;
    Animation attack;
    Animation die;
    Texture dead;

    Body[] guts_parts = new Body[guts.length];

    State state;

    float x = 0, y = 0;
    float rot = 0;

    public Zombie(float maxHealth){
        //HIGHER RADIUS TO DECREASE BLEED
        this.maxHealth = maxHealth;
        this.health = maxHealth;

        walk = new com.badlogic.gdx.graphics.g2d.Animation<>(1f/10f, walking);
        walk.setPlayMode(Animation.PlayMode.LOOP);

        attack = new com.badlogic.gdx.graphics.g2d.Animation<>(1f/5f, attacking);
        attack.setPlayMode(Animation.PlayMode.LOOP);

        spawn = new com.badlogic.gdx.graphics.g2d.Animation<>(1f/5f, spawning);
        spawn.setPlayMode(Animation.PlayMode.LOOP);

        die = new com.badlogic.gdx.graphics.g2d.Animation<>(1f/25f, dying);

        state = State.spawning;
    }

    public float getHealth(){
        return health / maxHealth;
    }

    public void setHealth(float health){
        if (health == 0){
            kill_me = true;
        }
        this.health = health;
    }

    public void setPosition(float x, float y){
        if (body == null && isAlive())
            createBody();
        body.setTransform(Transform.to_screen_space(x), Transform.to_screen_space(y), 0);
    }

    public void setBehavior(com.erkan.zombienado2.server.Zombie.Behavior behavior){
        switch (behavior){
            case Roaming:
                walk.setFrameDuration(1f/5f);
                break;
            case Hunting:
                walk.setFrameDuration(1f/10f);
                break;
            case Standing:
                //walk.setPlayMode(Animation.PlayMode.);
                break;
        }
    }

    public void setRotation(float degree){
        rot = degree;
    }

    public boolean isAlive(){
        return !isDead;
    }

    public boolean isDying(){
        return kill_me && !isDead;
    }

    public void die(){ //Must happen on main thread
        isDead = true;
        PhysicsHandler.destroyBody(body);
        dead = guts[MathUtils.random(0, 2)];
        elapsed = 0;
        state = State.dying;
        body = null;
        SoundManager.addSound(splatter, splatter.play());

        for (int i = 0; i < guts_parts.length; i++){
            guts_parts[i] = PhysicsHandler.createCircle(Transform.to_screen_space(com.erkan.zombienado2.server.Zombie.RADIUS), FilterConstants.PHYSICS_FIXTURE, (short)(FilterConstants.OBSTACLE_FIXTURE | FilterConstants.PLAYER_FIXTURE));
            guts_parts[i].setTransform(x, y, rot);
            Vector2 impulse = new Vector2(MathUtils.random(Transform.to_screen_space(-2f), MathUtils.random(Transform.to_screen_space(2f))), MathUtils.random(MathUtils.random(Transform.to_screen_space(-2f), MathUtils.random(Transform.to_screen_space(2f)))));
            float rot = MathUtils.random(-3000f, 3000f);
            guts_parts[i].setLinearVelocity(impulse);
            guts_parts[i].setLinearDamping(2f);
            guts_parts[i].setAngularVelocity(rot);
            guts_parts[i].setAngularDamping(2f);
        }


        //TODO: play death animation
    }

    public void update(){
        //TODO: play walking / atk animation
    }

    public void spawn(){
        //TODO: play spawn animation
    }

    public void attack(){
        state = State.attacking;
    }

    private void createBody(){
        body = PhysicsHandler.createCircle(Transform.to_screen_space(com.erkan.zombienado2.server.Zombie.RADIUS * 1.2f), FilterConstants.ENEMY_FIXTURE, (short)-1);
    }


    public void render_gore(SpriteBatch batch){
        if (isAlive())
            return;
        for (int i = 0; i < guts_parts.length; i++){
            Sprite gut = new Sprite(guts[i]);
            gut.setColor(1f, 1f, 1f, fade);
            gut.setCenter(guts_parts[i].getPosition().x, guts_parts[i].getPosition().y);
            gut.setRotation(guts_parts[i].getAngle());
            gut.draw(batch);
        }
    }


    float elapsed= 0;
    float elapsed_attack = 0;
    public void render(SpriteBatch batch){
        if (isDying()){
            die();
        }

        if (body != null){
            x = body.getPosition().x;
            y = body.getPosition().y;
        } else if(isAlive()) {
            createBody();
        }
        elapsed += Gdx.graphics.getDeltaTime();


        if(state.equals(State.attacking)){
            elapsed_attack+=Gdx.graphics.getDeltaTime();
        }

        Sprite sprite;
        Sprite overlay = null;
        if (isAlive()) {
            switch (state) {
                case spawning:
                    sprite = new Sprite((Texture) spawn.getKeyFrame(elapsed));
                    if (spawn.isAnimationFinished(elapsed)) {
                        state = State.moving;
                    }
                    break;
                case attacking:
                    sprite = new Sprite((Texture) attack.getKeyFrame(elapsed));
                    if (attack.isAnimationFinished(elapsed)){
                        state = State.moving;
                        elapsed_attack = 0;
                    }
                    break;
                default:
                    sprite = new Sprite((Texture) walk.getKeyFrame(elapsed));
                    break;
            }
            sprite.setCenter(x, y);
            sprite.setRotation(rot + 90);
            sprite.draw(batch);
        } else {
            if (!die.isAnimationFinished(elapsed))
                overlay = new Sprite((Texture)die.getKeyFrame(elapsed));

        }
        //sprite.setOrigin(0, 0);


        if (overlay != null){
            overlay.setCenter(x, y);
            overlay.setRotation(rot + 90);
            overlay.scale(1.1f);
            overlay.draw(batch);
        }

    }

    public void destroy(){
        if (body != null){
            PhysicsHandler.destroyBody(body);
        }
        for (int i = guts_parts.length-1; i >= 0; i--) {
            PhysicsHandler.destroyBody(guts_parts[i]);
        }
    }

    private enum State {
        attacking, moving, spawning, dying
    }
}
