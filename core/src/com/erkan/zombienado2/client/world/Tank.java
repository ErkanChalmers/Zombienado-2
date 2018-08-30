package com.erkan.zombienado2.client.world;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.erkan.zombienado2.client.PhysicsHandler;
import com.erkan.zombienado2.data.world.physics.StaticRectangle;
import com.erkan.zombienado2.graphics.Transform;
import com.erkan.zombienado2.server.misc.FilterConstants;

/**
 * Created by Erik on 2018-08-27.
 */
public class Tank extends DynamicObject implements Solid {
    private static Texture base_texture = new Texture("world/objects/tank.png");
    private static Texture tower_texture = new Texture("world/objects/tank_tower.png");

    static {
        base_texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        tower_texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    private StaticRectangle rectangle;
    private Sprite veichle;
    private Sprite tower;

    PointLight baselight;
    ConeLight flash_left;
    ConeLight flash_right;

    public Tank(float x, float y, float r){
        super(x, y , r);

        rotation = r + 90;
        min_rot = rotation - 130;
        max_rot = rotation + 130;
        rectangle = new StaticRectangle(x, y, 4f, 2.5f, r);

        veichle = new Sprite(base_texture);
        veichle.setSize(Transform.to_screen_space(4f), Transform.to_screen_space(2.5f));
        veichle.setOriginCenter();
        veichle.setCenter(Transform.to_screen_space(x), Transform.to_screen_space(y));
        veichle.setRotation(r + 90);

        Vector2 off_tower = Transform.to_screen_space(Transform.rotate(new Vector2(0f, -0.4f), r));
        tower = new Sprite(tower_texture);
        tower.setSize(Transform.to_screen_space(4.5f), Transform.to_screen_space(4.5f));
        tower.setOriginCenter();
        tower.setCenter(Transform.to_screen_space(x) + off_tower.x, Transform.to_screen_space(y) + off_tower.y);
        tower.setRotation(r + 90);

        PhysicsHandler.createRect(veichle.getWidth()/2, veichle.getHeight()/2, BodyDef.BodyType.StaticBody, FilterConstants.OBSTACLE_FIXTURE, FilterConstants.OBSTACLE_FIXTURE, (short)(FilterConstants.LIGHT | FilterConstants.PHYSICS_FIXTURE)).setTransform(Transform.to_screen_space(x), Transform.to_screen_space(y), MathUtils.degreesToRadians * (r+90));

        baselight = PhysicsHandler.createPointLight(Transform.to_screen_space(x) + off_tower.x, Transform.to_screen_space(y) + off_tower.y, new Color(.8f,.6f,.3f,0.3f), Transform.to_screen_space(2.5f));

        Vector2 off_right = Transform.to_screen_space(Transform.rotate(new Vector2(.5f, 1.2f), r));
        Vector2 off_left = Transform.to_screen_space(Transform.rotate(new Vector2(-.5f, 1.2f), r));


        flash_left = PhysicsHandler.createConeLight(Transform.to_screen_space(x) +off_left.x, Transform.to_screen_space(y) + off_left.y, new Color(1,1,1,1f), Transform.to_screen_space(17f), r + 90, 15);
        flash_right = PhysicsHandler.createConeLight(Transform.to_screen_space(x) + off_right.x, Transform.to_screen_space(y) + off_right.y, new Color(1,1,1,1f), Transform.to_screen_space(17f), r + 90, 15);
    }

    @Override
    public StaticRectangle getBounds() {
        return rectangle;
    }

    float elapsed;
    float rotate_to;

    float rotation;

    float max_rot;
    float min_rot;

    @Override
    public void update(float dt) {
        elapsed+=dt;
        if (elapsed > 7){
            elapsed = 0;
            rotate_to = MathUtils.random(min_rot, max_rot);
        }

        if (rotation < rotate_to)
            rotation += dt * 40;
        else
            rotation -= dt * 40;

        tower.setRotation(rotation);

    }

    @Override
    public void render(SpriteBatch batch) {
        veichle.draw(batch);
        tower.draw(batch);
    }
}
