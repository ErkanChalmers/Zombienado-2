package com.erkan.zombienado2.server.loots;

import com.erkan.zombienado2.server.PlayerModel;

/**
 * Created by Erik on 2018-09-13.
 */
public class MedPack extends Loot {
    public MedPack(float x, float y, boolean isStatic) {
        super(x, y, isStatic);
    }

    @Override
    public void pickup(PlayerModel player) {
        if (alive)
            player.addHealth(20f);
        alive = false;
    }

    @Override
    public String toString() {
        return "M";
    }
}
