package com.erkan.zombienado2.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.erkan.zombienado2.data.weapons.WeaponData;

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
            SoundManager.playSound(walk_sound[i]);
            elapsed_step = 0;
        }
        elapsed_step+= Gdx.graphics.getDeltaTime();
    }

    @Override
    public void shoot(){
        super.shoot();
        float pitch = 1 + ((float)Math.random() - 0.5f)*0.1f;
        SoundManager.playSound(getWeapon().getSound(), 1, pitch, 0);
    }

    @Override
    public void reload(){
        super.reload();
        SoundManager.playSound(getWeapon().getReloadSound());

    }

    @Override
    public boolean setWeapon(WeaponData wd){
        if (!super.setWeapon(wd)) return false;
        SoundManager.playSound(change_weapon);
        return true;
    }

    @Override
    public void setAlive(boolean alive){
        if (super.alive && !alive){
            NotificationManager.post("You are dead");
        }

        if (!super.alive && alive){
            NotificationManager.post("Your friends have rescued you");
        }
        super.setAlive(alive);

    }
}
