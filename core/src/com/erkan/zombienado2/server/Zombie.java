package com.erkan.zombienado2.server;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.erkan.zombienado2.server.misc.FilterConstants;

/**
 * Created by Erik on 2018-08-06.
 */
public class Zombie {
    public static final float DEF_SPAWN_RATE = .1f;
    public static final float DEF_WAVE_SIZE = 200;
    public static final float DEF_MAX_HEALTH = 10;
    private static final float TIME_BEFORE_MOVE = .5f;
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
    float spawn_time;
    float attack_time;
    boolean isAttacking;

    public Zombie(float x, float y, float health){
        //TODO: timer for moving
        body = WorldManager.createCircle(RADIUS, FilterConstants.ENEMY_FIXTURE, (short)(FilterConstants.ENEMY_FIXTURE | FilterConstants.PLAYER_FIXTURE | FilterConstants.OBSTACLE_FIXTURE | FilterConstants.PROJECTILE_FIXTURE));
        body.setTransform(x, y, 0);
        body.setUserData(this);
        spawn_time = TIME_BEFORE_MOVE;
        rotation = MathUtils.random(360);
        behavior = Behavior.Standing;
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
        return body.getPosition();
    }

    public float getRotation(){
        return rotation;
    }

    public float getHealth(){
        return health;
    }

    Vector2 roam_dir;
    void setDirection(Vector2 direction){
        if (behavior.equals(Behavior.Hunting)){
            direction.scl(VELOCITY);
            body.setLinearVelocity(direction.x, direction.y);
            rotation = direction.angle();
            return;
        }

        if (elasped_roam == 0) {
            roam_dir = direction.scl(behavior.equals(Behavior.Roaming) ? ROAM_VELOCITY : 0);
        }
    }

    void setBehavior(Behavior behavior){
        this.behavior = behavior;
    }

    public Behavior getBehavior(){
        return behavior;
    }

    boolean updateDirection(){
        return alive && elapsed_update == 0 && spawn_time <= 0;
    }

    float elapsed_update = 0;
    float elasped_roam = 0;
    void update(float dt){
        if (!alive && body != null){
            WorldManager.destroyBody(body);
            body = null;
        }

        if (body != null){
            if (roam_dir != null && behavior.equals(Behavior.Roaming)) {
                body.setLinearVelocity(roam_dir.x, roam_dir.y);
                if (roam_dir.len() > .02f)
                    rotation = roam_dir.angle();
            }
        }

        if (isAttacking) {
            attack_time -= Server.STEP_TIME;

            if (attack_time < -1f)
                isAttacking = false;
        }
        spawn_time -= Server.STEP_TIME;

        elapsed_update += dt;
        if (elapsed_update >= .2){
            elapsed_update = 0;
        }
        if (behavior.equals(Behavior.Roaming)){
            elasped_roam += dt;
            if (elasped_roam >= 5f){
                elasped_roam = 0;
            }
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
