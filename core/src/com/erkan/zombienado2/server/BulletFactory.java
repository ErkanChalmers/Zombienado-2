package com.erkan.zombienado2.server;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.MassData;
import com.erkan.zombienado2.server.misc.FilterConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Erik on 2018-07-30.
 */
public class BulletFactory {
    private final static float bullet_velocity = 90;
    public final static long TIME_TO_LIVE = 2000;

    //TODO: FIXTURES AMD SHIT
    public static Body createBullet(Vector2 origin, Vector2 direction, float dmg, int player_identifier){
        short mask = FilterConstants.ENEMY_FIXTURE | FilterConstants.OBSTACLE_FIXTURE;
        Body body = WorldManager.createCircle(.05f, FilterConstants.PROJECTILE_FIXTURE, mask);
        body.setBullet(true);
        body.setTransform(origin, 0);
        body.setLinearVelocity(direction.scl(bullet_velocity));
        Map<String, Number> ud = new HashMap<>();
        ud.put("time_stamp", System.currentTimeMillis());
        ud.put("identifier", player_identifier);
        ud.put("damage", dmg);
        body.setUserData(ud);

        //body
        return body;
    }
}
