package com.erkan.zombienado2.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.erkan.zombienado2.data.weapons.WeaponData;


/**
 * Created by Erik on 2018-08-01.
 */
public class Weapon {
    public static TextureRegion[] muzzleflash_arraay;

    static void init() {
        TextureRegion[][] reg = TextureRegion.split(new Texture("animations/muzzle/gunFlash1.png"),  512, 512);
        TextureRegion[] frames = new TextureRegion[9];
        int index = 0;
        for (int i = 0; i < reg.length; i++){
            for (int j = 0; j < reg[i].length; j++){
                frames[index++] = reg[i][j];
            }
        }
        muzzleflash_arraay = frames;
    }

    private Texture texture;
    private Sound sound;
    private Sound reload_sound;

    private WeaponData wd;

    public WeaponData getWeaponData(){
        return wd;
    }

    public Weapon(WeaponData weaponData){
        wd = weaponData;
    }

    public Texture getTexture(){
        if (texture == null)
            texture = new Texture(wd.texture_path);

        return texture;
    }

    public Sound getSound(){
        if (sound == null)
            sound = Gdx.audio.newSound(Gdx.files.internal(wd.audio_path));

        return sound;
    }

    public Sound getReloadSound(){
        if (reload_sound == null)
            reload_sound = Gdx.audio.newSound(Gdx.files.internal(wd.audio_reload_path));

        return reload_sound;
    }
}
