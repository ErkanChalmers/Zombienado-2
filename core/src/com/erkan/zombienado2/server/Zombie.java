package com.erkan.zombienado2.server;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.Transform;
import com.erkan.zombienado2.server.misc.FilterConstants;

/**
 * Created by Erik on 2018-08-06.
 */
public class Zombie {
    public static final float DEF_SPAWN_RATE = .1f;
    public static final float DEF_WAVE_SIZE = 0;
    public static final float DEF_MAX_HEALTH = 10;
    private static final float ATTACK_TIME = .5f;

    public static final float RADIUS = .3f;
    public static float VELOCITY = 2.5f;
    public static float ROAM_VELOCITY = .5f;

    public static float AGRO_RANGE = 7f;
    public static float HUNT_RANGE = 20f;

    private boolean alive = true;

    private Behavior behavior;
    private float health;
    private Body body;
    private float rotation = 0;
    private float attack_time;
    private boolean isAttacking;

    private Vector2 move_target;


    public Zombie(float x, float y, float health){
        //TODO: timer for moving
        body = WorldManager.createCircle(RADIUS, FilterConstants.ENEMY_FIXTURE, (short)(FilterConstants.ENEMY_FIXTURE | FilterConstants.PLAYER_FIXTURE | FilterConstants.OBSTACLE_FIXTURE | FilterConstants.PROJECTILE_FIXTURE));
        body.setTransform(x, y, 0);
        body.setUserData(this);
        rotation = MathUtils.random(360);
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
        return body.getPosition().cpy();
    }

    public float getRotation(){
        return rotation;
    }

    public float getHealth(){
        return health;
    }


    boolean position_reached(){
        if (behavior.equals(Behavior.Hunting))
            return true;
        if (move_target == null)
            return true;

        if (body.getPosition().cpy().sub(move_target).len() < .2f)
            return true;

        return false;
    }

    void setTarget(Vector2 target) {
        move_target = target;
    }

    void setBehavior(Behavior behavior){
        this.behavior = behavior;
    }

    public Behavior getBehavior(){
        return behavior;
    }

    void update(float dt){
        if (!alive && body != null){
            WorldManager.destroyBody(body);
            body = null;
        }

        if (isAttacking) {
            attack_time -= Server.STEP_TIME;

            if (attack_time < -1f)
                isAttacking = false;
        }

        if (!behavior.equals(Behavior.Standing) && move_target != null && body != null){
            Vector2 dir = move_target.cpy().sub(body.getPosition()).setLength(1);
            body.setLinearVelocity(dir.scl(behavior.equals(Behavior.Hunting) ? VELOCITY : ROAM_VELOCITY));
            rotation = dir.angle();
            System.out.println(behavior);
            System.out.println(body.getPosition());
            System.out.println(move_target);
            System.out.println("_________________");
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
