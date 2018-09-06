package com.erkan.zombienado2.server;

import com.badlogic.gdx.ai.pfa.*;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.erkan.zombienado2.client.world.Car;
import com.erkan.zombienado2.client.world.World;
import com.erkan.zombienado2.server.misc.FilterConstants;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Erik on 2018-08-06.
 */
public class Zombie {
    public static final float DEF_SPAWN_RATE = .1f;
    public static final float DEF_WAVE_SIZE = 40;
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
    private Vector2 sub_target;
    boolean sub_reached = false;


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

        if (body.getPosition().cpy().sub(move_target.cpy()).len() < .2f)
            return true;

        return false;
    }

    public


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
            freePath = true;
            WorldManager.getWorld().rayCast(new RayCastCallback() {
                @Override
                public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                    if (fixture.getFilterData().categoryBits == FilterConstants.OBSTACLE_FIXTURE) {
                        freePath = false;
                        return 0;
                    }
                    return 1;
                }
            }, body.getPosition().cpy(), move_target.cpy());

            if (freePath){
                sub_target = move_target;
            } else if (sub_reached) {
                IndexedAStarPathFinder<Vector2> pf = new IndexedAStarPathFinder<Vector2>(WorldManager.getNavigationGraph());
                GraphPath<Vector2> path = new DefaultGraphPath<>();


                Vector2 closest_goal = null;
                float closesDistance = 999f;
                List<Vector2> visible = WorldManager.getNavigationGraph().getVisibleNodes(move_target.cpy());
                for (Vector2 vec:
                        visible) {
                    float len = vec.cpy().sub(move_target.cpy()).len();
                    if (len < closesDistance){
                        closesDistance = len;
                        closest_goal = vec;
                    }
                }

                Vector2 closest_me = null;
                closesDistance = 999f;
                visible = WorldManager.getNavigationGraph().getVisibleNodes(body.getPosition().cpy());
                for (Vector2 vec:
                        visible) {
                    float len = vec.cpy().sub(body.getPosition().cpy()).len();
                    if (len < closesDistance){
                        closesDistance = len;
                        closest_me = vec;
                    }
                }

                boolean found = pf.searchNodePath(closest_me, closest_goal, (node, endNode) -> endNode.cpy().sub(node.cpy()).len(), path);

                if (!found) {
                    System.out.println("closest me: "+closest_me);
                    System.out.println("closest goal: "+closest_goal);
                    System.out.println("path not found");
                    return;
                }

                sub_target = path.get(path.getCount() == 1 ? 0 : 1);
                if (prio_first){
                    sub_target = path.get(0);
                    prio_first = false;
                }
                System.out.println(path.get(0));
                System.out.println(path.get(1));
                sub_reached = false;
            } else {
                if (sub_target == null){
                    sub_reached = true;
                    return;
                }

                float dist = sub_target.cpy().sub(body.getPosition().cpy()).len();
                if (dist < .1f) {
                    sub_reached = true;
                    return;
                }

                WorldManager.getWorld().rayCast(new RayCastCallback() {
                    @Override
                    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                        if (fixture.getFilterData().categoryBits == FilterConstants.OBSTACLE_FIXTURE) {
                            sub_reached = true;
                            return 0;
                        }
                        return 1;
                    }
                }, body.getPosition().cpy(), sub_target.cpy());
            }

            Vector2 dir = sub_target.cpy().sub(body.getPosition().cpy()).setLength(1);
            body.setLinearVelocity(dir.scl(behavior.equals(Behavior.Hunting) ? VELOCITY : ROAM_VELOCITY));
            rotation = dir.angle();
        }

    }

    boolean freePath;
    int force_update_timer = 0;
    Vector2 last_pos;
    boolean prio_first = false;

    public void destroy(){
        if (body != null){
            WorldManager.destroyBody(body);
        }
    }
    public enum Behavior {
        Roaming, Hunting, Standing
    }

}
