package com.erkan.zombienado2.client;

/**
 * Created by Erik on 2018-07-30.
 */
public class Self extends Player {
    public Self(String name, Character character){
        super(name, character);
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
