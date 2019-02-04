package com.erkan.zombienado2.server.loots;

import com.erkan.zombienado2.client.Player;
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
        if (alive && player.getHealth() != Player.MAX_HEALTH) {
            player.addHealth(20f);
            alive = false;
        }
    }

    @Override
    public void leave(PlayerModel player) {
        //No need
    }

    @Override
    public String toString() {
        return "M";
    }

    @Override
    public void performAction(PlayerModel player) {
        //nothing needed here
    }
}
