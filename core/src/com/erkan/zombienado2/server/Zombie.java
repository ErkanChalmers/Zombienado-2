package com.erkan.zombienado2.server;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.erkan.zombienado2.server.misc.FilterConstants;

/**
 * Created by Erik on 2018-08-06.
 */
public class Zombie {
    public static final float DEF_SPAWN_RATE = .0001f;
    public static final float DEF_WAVE_SIZE = 300;
    public static final float DEF_MAX_HEALTH = 10;
    private static final float TIME_BEFORE_MOVE = .5f;
    private static final float ATTACK_TIME = 2f;

    public static final float RADIUS = .3f;
    public static float VELOCITY = 2;
    public static float ROAM_VELOCITY = .5f;

    public static float AGRO_RANGE = 7f;
    public static float HUNT_RANGE = 20f;

    private boolean alive = true;

    private Behavior behavior;
    private float health;
    private Body body;
    private float rotation = 0;
    float spawn_time;
    float attack_time;
    boolean isAttacking;
    float x, y;

    public Zombie(float x, float y, float health){
        //TODO: timer for moving
        body = WorldManager.createCircle(RADIUS, FilterConstants.ENEMY_FIXTURE, (short)(FilterConstants.ENEMY_FIXTURE | FilterConstants.PLAYER_FIXTURE | FilterConstants.OBSTACLE_FIXTURE | FilterConstants.PROJECTILE_FIXTURE));
        body.setTransform(x, y, 0);
        body.setUserData(this);
        spawn_time = TIME_BEFORE_MOVE;
        behavior = Behavior.Roaming;
        this.health = health;

    }

    public void setAttacking(){

        if (!isAttacking){
            attack_time = ATTACK_TIME;
        }

        isAttacking = true;
    }

    public boolean isAttacking(){
        boolean res = isAttacking;
        if (isAttacking)
            isAttacking = false;
        return res;
    }

    public boolean attack_finished(){
        if (isAttacking && attack_time <= 0){
            isAttacking = false;
            return true;
        }
        return false;
    }

    public float takeDamage(float dmg){
        health -= dmg;
        float left = health;
        if (health <= 0) {
            health = 0;
            die();
        }
        return left;
    }

    public Body getBody(){
        return body;
    }

    void die(){
        alive = false;
    }

    public boolean isAlive(){
        return alive;
    }

    public Vector2 getPosition(){
        if (body == null){
            return new Vector2(0, 0);
        }
        return body.getPosition();
    }

    public float getRotation(){
        return rotation;
    }

    public float getHealth(){
        return health;
    }

    void setDirection(Vector2 direction){
        body.setLinearVelocity(direction.scl(behavior.equals(Behavior.Hunting) ? VELOCITY : ROAM_VELOCITY));
        rotation = direction.angle();
    }

    void setBehavior(Behavior behavior){
        this.behavior = behavior;
    }

    public Behavior getBehavior(){
        return behavior;
    }

    boolean updateDirection(){
        return alive && tick == 0 && spawn_time <= 0;
    }

    int tick = 0;
    void update(){
        if (!alive && body != null){
            WorldManager.destroyBody(body);
            body = null;
        }

        if (isAttacking)
            attack_time -= Server.STEP_TIME;

        spawn_time -= Server.STEP_TIME;

        tick++;
        if (tick >= 5 && behavior.equals(Behavior.Hunting) || tick > 200){
            tick = 0;
        }
    }

    public void destroy(){
        if (body != null){
            WorldManager.destroyBody(body);
        }
    }
    public enum Behavior {
        Roaming, Hunting, Standing
    }

}
