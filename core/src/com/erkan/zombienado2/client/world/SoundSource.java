package com.erkan.zombienado2.client.world;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.erkan.zombienado2.client.PhysicsHandler;
import com.erkan.zombienado2.server.misc.FilterConstants;

/**
 * Created by Erik on 2019-02-04.
 */
public class SoundSource {
    float x, y;
    Sound sound;

    public SoundSource(float x, float y, float r, Sound sound){
        Body b = PhysicsHandler.createCircle(r, FilterConstants.SOUND_FIXTURE,  FilterConstants.PLAYER_FIXTURE);
        b.getFixtureList().get(0).setSensor(true);
        b.getFixtureList().get(0).setUserData(this);
        b.setTransform(x, y, 0);
        this.x = x;
        this.y = y;
        this.sound = sound;
    }

    public Sound getSound(){
        return sound;
    }
}
