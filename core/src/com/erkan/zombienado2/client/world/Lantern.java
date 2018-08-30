package com.erkan.zombienado2.client.world;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.erkan.zombienado2.client.PhysicsHandler;
import com.erkan.zombienado2.graphics.Transform;
import com.erkan.zombienado2.server.misc.FilterConstants;

/**
 * Created by Erik on 2018-08-27.
 */
public class Lantern extends DynamicObject {
    static final float MAX_R = 1f;
    static final float MIN_R = 0.7f;
    static final float MAX_A = 1f;
    static final float MIN_A = 0.7f;

    //PointLight light;
    PointLight light_top;


    public Lantern(float x, float y){
        super(x, y, 0);
       // light = PhysicsHandler.createPointLight(Transform.to_screen_space(x), Transform.to_screen_space(y), new Color(1, .4f, .0f, 1), Transform.to_screen_space(5.0f));
        light_top = PhysicsHandler.createPointLight(Transform.to_screen_space(x), Transform.to_screen_space(y), new Color(1, .4f, .0f, 1f), Transform.to_screen_space(5.0f));
        light_top.setContactFilter(FilterConstants.TOP_LIGHT, FilterConstants.TOP_LIGHT, FilterConstants.OBSTACLE_FIXTURE);
    }

    @Override
    public void update(float dt) {
        float r = MathUtils.clamp(light_top.getColor().r + MathUtils.random(-.005f, .005f), MIN_R, MAX_R);
        float a = MathUtils.clamp(light_top.getColor().a + MathUtils.random(-.01f, .01f), MIN_A, MAX_A);
       // light.setColor(new Color(r, light.getColor().g, light.getColor().b, a));
        light_top.setColor(new Color(r, light_top.getColor().g, light_top.getColor().b, a));
    }

    @Override
    public void render(SpriteBatch batch) {

    }
}
