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
        return body.getPosition().cpy();
    }

    public float getRotation(){
        return rotation;
    }

    public float getHealth(){
        return health;
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

        switch (behavior){
            case Standing:
                update_standing();
                break;
            case Hunting:
                update_hunting();
                break;
            case Roaming:
                update_roaming();
                break;
        }
    }

    void proximity_scan(){
        Vector2 shortest_distance = new Vector2(9999, 9999);
        Vector2 target_position;
        for (Vector2 p_pos : Server.getPlayerPositions()) {
            Vector2 zp = getPosition().cpy();
            Vector2 dist = p_pos.sub(zp);
            if (dist.len() < shortest_distance.len()) {
                shortest_distance = dist;
                target_position = p_pos;
            }
        }

        if (getBehavior().equals(Behavior.Hunting) && shortest_distance.len() < Zombie.HUNT_RANGE || shortest_distance.len() < Zombie.AGRO_RANGE) {
            setBehavior(Behavior.Hunting);
        } else {
            setBehavior(Behavior.Roaming);
        }
    }

    void update_hunting(){

        //TODO: set position instead of direction!


            zombie.setDirection(shortest_distance.setLength(1));
        } else {
            if (MathUtils.randomBoolean(.1f)){
                zombie.setBehavior(Zombie.Behavior.Standing);
                zombie.setDirection(new Vector2(0, 0));
            } else {
                zombie.setBehavior(Zombie.Behavior.Roaming);
                zombie.setDirection(new Vector2().setToRandomDirection());
            }
        }
    }

    void update_roaming(){

    }

    void update_standing(){

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
