package com.erkan.zombienado2.client.world;

import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.erkan.zombienado2.client.PhysicsHandler;
import com.erkan.zombienado2.data.world.physics.StaticRectangle;
import com.erkan.zombienado2.graphics.Transform;
import com.erkan.zombienado2.server.misc.FilterConstants;

/**
 * Created by Erik on 2018-08-27.
 */
public class TV extends DynamicObject implements Solid{
    private static final Texture tv_texture = new Texture("world/objects/objects_house_0021_Layer-22.png");
    private static final Sound tv_sound = Gdx.audio.newSound(Gdx.files.internal("audio/zombietvmp3.mp3"));
    private StaticRectangle rectangle;

    Sprite sprite;
    PointLight lighteffect;

    public TV(float x, float y, float r){
        super(x, y, r);
        new SoundSource(Transform.to_screen_space(x), Transform.to_screen_space(y), Transform.to_screen_space(5), tv_sound);
        rectangle = new StaticRectangle(x, y, 1, 1, r);
        sprite = new Sprite(tv_texture);
        sprite.setSize(Transform.to_screen_space(1f), Transform.to_screen_space(1f));
        sprite.setOriginCenter();
        sprite.setCenter(Transform.to_screen_space(x), Transform.to_screen_space(y));
        sprite.setRotation(r + 90);
        PhysicsHandler.createRect(sprite.getWidth()/2, sprite.getHeight()/2, BodyDef.BodyType.StaticBody, FilterConstants.OBSTACLE_FIXTURE, FilterConstants.OBSTACLE_FIXTURE, (short)(FilterConstants.LIGHT | FilterConstants.PHYSICS_FIXTURE)).setTransform(Transform.to_screen_space(x), Transform.to_screen_space(y), MathUtils.degreesToRadians * r);

        lighteffect = PhysicsHandler.createPointLight(Transform.to_screen_space(x) + MathUtils.cosDeg(r) * (sprite.getWidth()/2 + 2f),Transform.to_screen_space(y) + MathUtils.sinDeg(r) * (sprite.getWidth()/2 + 2f), new Color(1,1,1,1), Transform.to_screen_space(2f));
        PhysicsHandler.createPointLight(Transform.to_screen_space(x),Transform.to_screen_space(y), new Color(1,1,1,.3f), Transform.to_screen_space(1f));

    }

    @Override
    public void update(float dt) {
        Color c = new Color(MathUtils.random(1f), MathUtils.random(1f),MathUtils.random(1f), 1);
        lighteffect.setColor(c);
    }

    @Override
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    @Override
    public StaticRectangle getBounds() {
        return rectangle;
    }
}
