package com.erkan.zombienado2.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Erik on 2018-07-30.
 */
public class Self extends Player {
    public Self(String name, Character character){
        super(name, character);
    }


    float elapsed_step = 0;
    @Override
    public void run(Vector2 dir){
        super.run(dir);
        if (dir.len() != 0 && elapsed_step > .25f) {
            int i = MathUtils.random(walk_sound.length-1);
            SoundManager.addSound(walk_sound[i], walk_sound[i].play());
            elapsed_step = 0;
        }
        elapsed_step+= Gdx.graphics.getDeltaTime();
    }

    @Override
    public void shoot(){
        super.shoot();
        float pitch = 1 + ((float)Math.random() - 0.5f)*0.1f;
        SoundManager.addSound(getWeapon().getSound(), getWeapon().getSound().play(1, pitch, 0));
    }

    @Override
    public void reload(){
        super.reload();
        SoundManager.addSound(getWeapon().getReloadSound(), getWeapon().getReloadSound().play());

    }
}
