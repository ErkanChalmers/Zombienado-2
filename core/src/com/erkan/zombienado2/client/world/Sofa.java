package com.erkan.zombienado2.client.world;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.erkan.zombienado2.data.world.physics.StaticRectangle;
import com.erkan.zombienado2.graphics.Transform;

/**
 * Created by Erik on 2018-08-27.
 */
public class Sofa extends DynamicObject implements Solid {
    private static final Texture texture = new Texture("world/objects/objects_house_0004_Layer-5.png");

    private Sprite sprite;
    private StaticRectangle rectangle;

    public Sofa(float x, float y, float r){
        super(x, y, r);

        rectangle = new StaticRectangle(x, y, 2, 1, r);
        sprite = new Sprite(texture);
        sprite.setSize(Transform.to_screen_space(2f), Transform.to_screen_space(1f));
        sprite.setOriginCenter();
        sprite.setCenter(Transform.to_screen_space(x), Transform.to_screen_space(y));
        sprite.setRotation(r + 90);
    }

    @Override
    public StaticRectangle getBounds() {
        return rectangle;
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
