package com.erkan.zombienado2.client;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Erik on 2018-07-29.
 */
public abstract class Entity {
    public abstract void update(float dt);
    public abstract void render(SpriteBatch batch);
}
