package com.erkan.zombienado2.client.world;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.erkan.zombienado2.client.PhysicsHandler;
import com.erkan.zombienado2.graphics.Transform;
import com.erkan.zombienado2.server.misc.FilterConstants;

/**
 * Created by Erik on 2018-08-17.
 */
public class RoofFan extends DynamicObject {
    static Color color = new Color(1f,.6f,.4f,.7f);

    ConeLight c1;
    ConeLight c2;
    ConeLight c3;

    PointLight c_bg;

    float rot = 0;


    public RoofFan(float x, float y){
        super(Transform.to_screen_space(x), Transform.to_screen_space(y), 0);
        c1 = PhysicsHandler.createConeLight(this.x, this.y, color, Transform.to_screen_space(4.5f), 0, 30);
        c2 = PhysicsHandler.createConeLight(this.x, this.y, color, Transform.to_screen_space(4.5f), 120, 30);
        c3 = PhysicsHandler.createConeLight(this.x, this.y, color, Transform.to_screen_space(4.5f), 240, 30);
        color.a = 0.5f;
        c_bg = PhysicsHandler.createPointLight(this.x, this.y, color, Transform.to_screen_space(4.5f));

        c1.setContactFilter((short) FilterConstants.TOP_LIGHT, (short)FilterConstants.TOP_LIGHT, (short)FilterConstants.OBSTACLE_FIXTURE);
        c2.setContactFilter((short) FilterConstants.TOP_LIGHT, (short)FilterConstants.TOP_LIGHT, (short)FilterConstants.OBSTACLE_FIXTURE);
        c3.setContactFilter((short) FilterConstants.TOP_LIGHT, (short)FilterConstants.TOP_LIGHT, (short)FilterConstants.OBSTACLE_FIXTURE);
    }

    @Override
    public void update(float dt) {
        rot+=dt * 550;
        rot = rot%360;
        c1.setDirection(rot);
        c2.setDirection(rot + 120);
        c3.setDirection(rot + 240);
    }

    @Override
    public void render(SpriteBatch batch) {
        return;
    }
}
