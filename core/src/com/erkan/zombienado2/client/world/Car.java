package com.erkan.zombienado2.client.world;

import box2dLight.ConeLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.erkan.zombienado2.client.PhysicsHandler;
import com.erkan.zombienado2.data.world.physics.StaticRectangle;
import com.erkan.zombienado2.graphics.Transform;
import com.erkan.zombienado2.server.misc.FilterConstants;

/**
 * Created by Erik on 2018-08-27.
 */
public class Car extends DynamicObject implements Solid {
    private static final Texture texture = new Texture("world/objects/car.png");
    static {
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }
    ConeLight c1;
    ConeLight c2;
    ConeLight c3;
    ConeLight c4;
    Sprite sprite;
    StaticRectangle rectangle;

    float rot = MathUtils.random(360f);

    public Car(float x, float y, float r){
        super(x, y, r);
        rectangle = new StaticRectangle(x, y, 3f, 1.5f, r);
        sprite = new Sprite(texture);
        sprite.setSize(Transform.to_screen_space(3f), Transform.to_screen_space(1.5f));
        sprite.setOriginCenter();
        sprite.setCenter(Transform.to_screen_space(x), Transform.to_screen_space(y));
        sprite.setRotation(r + 90);
        PhysicsHandler.createRect(sprite.getWidth()/2, sprite.getHeight()/2, BodyDef.BodyType.StaticBody, FilterConstants.OBSTACLE_FIXTURE, FilterConstants.OBSTACLE_FIXTURE, (short)(FilterConstants.LIGHT | FilterConstants.PHYSICS_FIXTURE)).setTransform(Transform.to_screen_space(x), Transform.to_screen_space(y), MathUtils.degreesToRadians * (r+90));

        c1 = PhysicsHandler.createConeLight(Transform.to_screen_space(x), Transform.to_screen_space(y), new Color(1, 0, 0, 1), Transform.to_screen_space(2.5f), 0, 35f);
        c1.setContactFilter(FilterConstants.LIGHT, FilterConstants.LIGHT, (short)(FilterConstants.PLAYER_FIXTURE | FilterConstants.OBSTACLE_FIXTURE | FilterConstants.ENEMY_FIXTURE));
        c2 = PhysicsHandler.createConeLight(Transform.to_screen_space(x), Transform.to_screen_space(y), new Color(0, 0, 1, 1), Transform.to_screen_space(2.5f), 180, 35f);
        c2.setContactFilter(FilterConstants.LIGHT, FilterConstants.LIGHT, (short)(FilterConstants.PLAYER_FIXTURE | FilterConstants.OBSTACLE_FIXTURE | FilterConstants.ENEMY_FIXTURE));

        c3 = PhysicsHandler.createConeLight(Transform.to_screen_space(x), Transform.to_screen_space(y), new Color(1, 0, 0, 1), Transform.to_screen_space(2), 180, 25f);
        c3.setContactFilter(FilterConstants.LIGHT, FilterConstants.LIGHT, (short)(FilterConstants.PLAYER_FIXTURE | FilterConstants.OBSTACLE_FIXTURE | FilterConstants.ENEMY_FIXTURE));
        c4 = PhysicsHandler.createConeLight(Transform.to_screen_space(x), Transform.to_screen_space(y), new Color(0, 0, 1, 1), Transform.to_screen_space(2), 180, 25f);
        c4.setContactFilter(FilterConstants.LIGHT, FilterConstants.LIGHT, (short)(FilterConstants.PLAYER_FIXTURE | FilterConstants.OBSTACLE_FIXTURE | FilterConstants.ENEMY_FIXTURE));
    }

    @Override
    public StaticRectangle getBounds() {
        return rectangle;
    }

    @Override
    public void update(float dt) {
        rot += dt * 720;
        rot %= 360f;
        c1.setDirection(rot);
        c2.setDirection(rot + 180);
        c3.setDirection(rot);
        c4.setDirection(rot + 180);
    }

    @Override
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
