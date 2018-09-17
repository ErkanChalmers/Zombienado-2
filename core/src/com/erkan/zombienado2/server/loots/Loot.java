package com.erkan.zombienado2.server.loots;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.erkan.zombienado2.server.PlayerModel;
import com.erkan.zombienado2.server.WorldManager;
import com.erkan.zombienado2.server.misc.FilterConstants;

/**
 * Created by Erik on 2018-09-13.
 */
public abstract class Loot {
    private Body body;
    boolean alive;
    boolean isStatic = true;
    float timestamp;

    public Loot(float x, float y, boolean isStatic){
        body = WorldManager.createCircle(.5f, FilterConstants.LOOT_FIXTURE, FilterConstants.PLAYER_FIXTURE);
        body.setUserData(this);
        body.setTransform(x, y, 0);
        body.getFixtureList().get(0).setSensor(true);
        alive = true;
        timestamp = System.currentTimeMillis();
        this.isStatic = isStatic;
    }

    public float getX(){
        return body.getPosition().x;
    }

    public float getY(){
        return body.getPosition().y;
    }

    public boolean shouldBeRemoved(float timeStamp){
        if (!alive)
            return true;
        if (!isStatic && (timeStamp - timestamp) > 15000)
            return true;
        return false;
    }

    public abstract void pickup(PlayerModel player);

    public void destroy(){
        if (body != null){
            WorldManager.destroyBody(body);
        }
    }

    @Override
    public abstract String toString();
}
