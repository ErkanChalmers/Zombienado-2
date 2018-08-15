package com.erkan.zombienado2.server;

import com.badlogic.gdx.Gdx;
import com.erkan.zombienado2.data.weapons.WeaponData;

/**
 * Created by Erik on 2018-07-30.
 */
public class WeaponModel {
    private WeaponData weaponData;
    private float elapsedtime = 0;
    private float elapsed_time_spread = 0;
    private float elapsed_time_reload = 0;
    private boolean fireOk = false;
    private boolean reduce_spread = false;
    private boolean reloading;

    private int clip;
    private float current_spread;

    public WeaponModel(WeaponData weapon){
        weaponData = weapon;
        clip = weapon.mag_size;
    }

    public WeaponData getWeaponData(){
        return weaponData;
    }

    public float getCurrent_spread(){
        return current_spread;
    }

    public void update(float dt){
        if (!fireOk)
            elapsedtime += dt;
        if (elapsedtime > 1f / weaponData.rate_of_fire){
            fireOk = true;
            elapsedtime -= 1f / weaponData.rate_of_fire;
        }

        elapsed_time_spread += dt;
        if (elapsed_time_spread > 1f / weaponData.rate_of_fire){
            elapsed_time_spread -= 1f / weaponData.rate_of_fire;
            if (fireOk && reduce_spread) {
                current_spread -= 5;
                if (current_spread < 0)
                    current_spread = 0;
            } else if (fireOk){
                reduce_spread = true;
            }
        }

        if (reloading){
            elapsed_time_reload += dt;
            if (elapsed_time_reload > weaponData.reload_time){
                clip = weaponData.mag_size;
                elapsed_time_reload = 0;
                reloading = false;
            }
        }
    }

    public int getClip(){
        return clip;
    }

    public boolean reload(){
        if (!reloading && clip < weaponData.mag_size){
            reloading = true;
            elapsed_time_reload = 0;
            return true;
        }
        return false;
    }

    public boolean fire(){
        if (reloading || clip == 0)
            return false;

        boolean res = fireOk;
        if (fireOk) {
            fireOk = false;
            reduce_spread = false;
            clip--;
            if (current_spread <= weaponData.spread)
                current_spread += weaponData.recoil;
            if (current_spread > weaponData.spread)
                current_spread = weaponData.spread;
        }
        return res;
    }
}
