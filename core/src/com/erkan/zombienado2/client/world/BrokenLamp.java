package com.erkan.zombienado2.client.world;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.erkan.zombienado2.client.PhysicsHandler;
import com.erkan.zombienado2.graphics.Transform;
import com.badlogic.gdx.graphics.Texture;
import com.erkan.zombienado2.server.misc.FilterConstants;

/**
 * Created by Erik on 2018-08-06.
 */
public class BrokenLamp extends DynamicObject{
    static Texture texture = new Texture("misc/street_lamp.png");
    static float update_rate = .05f;

    static Color non_shaded = new Color(0.1f, 0.1f, 0.1f, 1f);
    static Color shaded = new Color(0.05f, 0.05f, 0.05f, 1f);

    static Color light = new Color(1, 1, 1, .8f);
    static Color dark = new Color(1, 1, 1, .01f);


    Sprite sprite;
    PointLight pl;
    float elapsed = 0;
    int index = 0;


    public BrokenLamp(float x, float y, float r){
        super(x, y, r);
        pl = PhysicsHandler.createPointLight(Transform.to_screen_space(x), Transform.to_screen_space(y), light, Transform.to_screen_space(4));
        pl.setContactFilter((short)FilterConstants.TOP_LIGHT, (short)FilterConstants.TOP_LIGHT, (short)FilterConstants.OBSTACLE_FIXTURE);
        sprite = new Sprite(texture);
        sprite.setOrigin(texture.getWidth()/2, texture.getHeight()/2);
        sprite.setPosition(Transform.to_screen_space(x) - texture.getWidth()/2, Transform.to_screen_space(y) - texture.getHeight()/2);
        sprite.rotate(r);
        sprite.setColor(new Color(0.2f, 0.2f, 0.2f, 1f));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

    }

    @Override
    public void update(float dt) {
        elapsed += dt;
        if (elapsed < update_rate) {
            return;
        }
        elapsed -= update_rate;
        boolean light = Math.random() > 0.1f;

        if (light){
            pl.setColor(this.light);
            sprite.setColor(non_shaded);
        }
        else{
            pl.setColor(dark);
            sprite.setColor(shaded);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
