package com.erkan.zombienado2.server.loots;

import com.badlogic.gdx.math.MathUtils;
import com.erkan.zombienado2.client.Weapon;
import com.erkan.zombienado2.data.weapons.WeaponData;
import com.erkan.zombienado2.data.world.Tuple;
import com.erkan.zombienado2.server.PlayerModel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by Erik on 2018-09-13.
 */
public class WeaponPack extends Loot {
    String weapon;
    static List<Tuple<Integer, String>> probSet = new ArrayList<>();
    static int prob_total;
    static {
        probSet.add(new Tuple<>(35, WeaponData.UZI.toString()));
        probSet.add(new Tuple<>(15, WeaponData.AK47.toString()));
        probSet.add(new Tuple<>(12, WeaponData.ASSAULT_RIFLE.toString()));
        probSet.add(new Tuple<>(3, WeaponData.M249.toString()));
        probSet.add(new Tuple<>(15, WeaponData.SHOTGUN_PUMP.toString()));
        probSet.add(new Tuple<>(5, WeaponData.SHOTGUN_AUTO.toString()));
        probSet.add(new Tuple<>(5, WeaponData.SNIPER.toString()));
        for (Tuple<Integer, String> t: probSet) {
            prob_total+=t.getFirst();
        }
    }

    public WeaponPack(float x, float y, boolean isStatic) {
        super(x, y, isStatic);
        System.out.println(prob_total);
        int rand = MathUtils.random(prob_total);
        int accumulator = 0;
        for (Tuple<Integer, String> t : probSet) {
            accumulator += t.getFirst();
            if (rand <= accumulator){
                weapon = t.getSecond();
                return;
            }
        }
    }

    @Override
    public void pickup(PlayerModel player) {
        if (alive) {
            if (player.getPrimary() == null){
                player.setPrimaryWeapon(WeaponData.getWeapon(weapon));
                alive = false;
            } else if (player.getPrimary().getWeaponData().toString().equals(weapon)) {
                player.getPrimary().addAmmo(player.getWeapon().getWeaponData().mag_size * 3);
                alive = false;
            } else {
                player.setAction(this);
            }
        }
    }

    @Override
    public void leave(PlayerModel player) {
        if (alive) {
            if (player.getAction().equals(this))
                player.setAction(null);
        }
    }

    @Override
    public String toString() {
        return "W:"+weapon;
    }

    @Override
    public void performAction(PlayerModel player) {
        player.setPrimaryWeapon(WeaponData.getWeapon(weapon));
        alive = false;
    }
}
