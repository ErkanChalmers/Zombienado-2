package com.erkan.zombienado2.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.erkan.zombienado2.data.weapons.WeaponData;

/**
 * Created by Erik on 2018-07-30.
 */
public class WeaponModel {
    static float MOVEMENT_MULTIPLIER = 2.3f;

    private WeaponData weaponData;
    private float elapsedtime = 0;
    private float elapsed_time_spread = 0;
    private float elapsed_time_reload = 0;
    private boolean fireOk = false;
    private boolean reduce_spread = false;
    private boolean reloading;

    private int clip;
    private float current_spread;

    private int excess_ammo = 0;

    public WeaponModel(WeaponData weapon){
        weaponData = weapon;
        clip = weapon.mag_size;
        excess_ammo = weapon.mag_size * 3;
        if (weaponData.equals(WeaponData.PISTOL))
            excess_ammo = -1;
    }

    public WeaponData getWeaponData(){
        return weaponData;
    }

    public void addAmmo(int ammo){
        excess_ammo += ammo;
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
                int before_reload = clip;
                if (excess_ammo == -1){
                    clip = weaponData.mag_size;
                } else {
                    clip = Math.min(weaponData.mag_size, excess_ammo);
                    excess_ammo -= (clip - before_reload);
                }

                elapsed_time_reload = 0;
                reloading = false;
            }
        }
    }

    public int getClip(){
        return clip;
    }

    public int getExcessAmmo(){
        return excess_ammo;
    }

    public boolean reload(){
        if (!reloading && clip < weaponData.mag_size && (excess_ammo > 0 || excess_ammo == -1)){
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
