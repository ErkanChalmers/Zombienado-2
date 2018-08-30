package com.erkan.zombienado2.data.weapons;

import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Created by Erik on 2018-07-30.
 */
public class WeaponData {
    public enum HeldType {
        ONE_HANDED, TWO_HANDED, DUAL_WEILDED
    }

    public final float rate_of_fire;
    public final float damage;
    public final float recoil;
    public final float spread;
    public final int bullets_per_round;
    public final int mag_size;
    public final float reload_time;
    public final float scope;
    public final HeldType held_type;
    public final String texture_path;
    public final String audio_path;
    public final String audio_reload_path;

    public static WeaponData PISTOL = new WeaponData(6, 2, 3, 3, 1,10, 1, .70f, HeldType.ONE_HANDED, "weapons/1h_pistol.png", "audio/pistol.mp3", "audio/pistol_reload.mp3");
    public static WeaponData UZI = new WeaponData(4, 22, 2, 20, 1,30, 2, .70f, HeldType.ONE_HANDED, "weapons/1h_smg.png", "audio/smg.mp3", "audio/rifle_reload.mp3");
    public static WeaponData ASSAULT_RIFLE = new WeaponData(9, 11, 2, 10,1, 30, 2.5f,1f, HeldType.TWO_HANDED, "weapons/2h_machinegun.png", "audio/ar.mp3", "audio/rifle_reload.mp3");
    public static WeaponData AK47 = new WeaponData(11, 7, 4, 15,1, 30, 2.8f,1f, HeldType.TWO_HANDED, "weapons/2h_ak47.png", "audio/ar2.mp3", "audio/rifle_reload.mp3");
    public static WeaponData SNIPER = new WeaponData(132, .5f, 0, 0, 1,8, 3f, 1.5f, HeldType.TWO_HANDED, "weapons/2h_sniper.png", "audio/sniper.mp3", "audio/rifle_reload.mp3");
    public static WeaponData SHOTGUN_PUMP = new WeaponData(7, .5f, 3, 12, 6,8, 3f, 1f, HeldType.TWO_HANDED, "weapons/2h_shotgun.png", "audio/shotgun_pump.mp3", "audio/shotgun_reload.mp3");
    public static WeaponData SHOTGUN_AUTO = new WeaponData(3, 2.5f, 3, 12, 12,8, 3f, 1f, HeldType.TWO_HANDED, "weapons/2h_autoshotgun.png", "audio/shotgun2.mp3", "audio/shotgun_reload.mp3");
   // public static WeaponData UZI_AKIMBO = new WeaponData(3, 32, 4, 30, 1,40, 3, HeldType.DUAL_WEILDED, "weapons/2h_smg.png", "audio/smg.mp3", "audio/rifle_reload.mp3");

    static final HashMap<String, WeaponData> weapons;

    static {
        weapons = new HashMap<>();
        weapons.put("PISTOL", PISTOL);
        weapons.put("UZI", UZI);
        weapons.put("ASSAULT_RIFLE", ASSAULT_RIFLE);
        weapons.put("AK47", AK47);
        weapons.put("SNIPER", SNIPER);
        weapons.put("SHOTGUN_PUMP", SHOTGUN_PUMP);
        weapons.put("SHOTGUN_AUTO", SHOTGUN_AUTO);
    }

    public static WeaponData getWeapon(String name){
        return weapons.get(name);
    }

    public String toString(){
        for (Map.Entry<String, WeaponData> entry : weapons.entrySet()) {
            if (Objects.equals(this, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public WeaponData(float dmg, float rof, float rec, float spr, int bpr, int mag_size, float rt, float scope, HeldType type, String texture_path, String audio_path, String audio_reload_path){
        rate_of_fire = rof;
        damage = dmg;
        recoil = rec;
        spread = spr;
        this.bullets_per_round = bpr;
        this.mag_size = mag_size;
        reload_time = rt;
        this.scope = scope;
        held_type = type;
        this.texture_path = texture_path;
        this.audio_path = audio_path;
        this.audio_reload_path = audio_reload_path;
    }
}
