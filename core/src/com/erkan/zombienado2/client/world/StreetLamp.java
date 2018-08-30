package com.erkan.zombienado2.client.world;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.erkan.zombienado2.client.PhysicsHandler;
import com.erkan.zombienado2.graphics.Transform;
import com.erkan.zombienado2.server.misc.FilterConstants;

/**
 * Created by Erik on 2018-08-17.
 */
public class StreetLamp extends DynamicObject {
    static Texture texture = new Texture("misc/street_lamp.png");

    static Color light = new Color(1, 1, 1, .8f);
    float luminosity = .8f;

    Sprite sprite;
    PointLight pl;
    PointLight p_focus;


    public StreetLamp(float x, float y, float r){
        super(x, y, r);
        pl = PhysicsHandler.createPointLight(Transform.to_screen_space(x), Transform.to_screen_space(y), light, Transform.to_screen_space(7));
        //pl.setContactFilter((short) FilterConstants.TOP_LIGHT, (short)FilterConstants.TOP_LIGHT, (short)FilterConstants.OBSTACLE_FIXTURE);
        p_focus = PhysicsHandler.createPointLight(Transform.to_screen_space(x), Transform.to_screen_space(y), light, Transform.to_screen_space(6));
        p_focus.setContactFilter((short) FilterConstants.TOP_LIGHT, (short)FilterConstants.TOP_LIGHT, (short)FilterConstants.OBSTACLE_FIXTURE);
        sprite = new Sprite(texture);
        sprite.setOrigin(texture.getWidth()/2, texture.getHeight()/2);
        sprite.setPosition(Transform.to_screen_space(x) - texture.getWidth()/2, Transform.to_screen_space(y) - texture.getHeight()/2);
        sprite.rotate(r);
        sprite.setColor(new Color(0.2f, 0.2f, 0.2f, 1f));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

    }

    @Override
    public void update(float dt) {
        luminosity += MathUtils.random(-0.001f, 0.001f);
        luminosity = MathUtils.clamp(luminosity, .7f, .85f);
        pl.getColor().a=luminosity;
    }

    @Override
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
