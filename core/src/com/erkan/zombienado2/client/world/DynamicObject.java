package com.erkan.zombienado2.client.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Erik on 2018-08-06.
 */
public abstract class DynamicObject {
    protected float x, y, r;


    protected DynamicObject(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public abstract void update(float dt);
    public abstract void render(SpriteBatch batch);
}
