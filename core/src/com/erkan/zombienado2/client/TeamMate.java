package com.erkan.zombienado2.client;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Erik on 2018-07-30.
 */
public class TeamMate extends Player {
    public TeamMate(String name, Character character){
        super(name, character);
    }

    @Override
    public void shoot(){
        super.shoot();

        float dx = position.x - Client.camera_world_coordinates.x;
        float dy = position.y - Client.camera_world_coordinates.y;
        Vector2 vec = new Vector2(dx, dy);
        float distance2 = vec.len2();
        float tmp = (100f - distance2)/100f;

        float vol = (float)Math.max(0.1f, tmp);
        float pan = vec.setLength(1f).x;

        SoundManager.addSound(getWeapon().getSound(), getWeapon().getSound().play(vol, 1, pan));
    }

    @Override
    public void reload(){
        super.reload();
        float dx = position.x - Client.camera_world_coordinates.x;
        float dy = position.y - Client.camera_world_coordinates.y;
        Vector2 vec = new Vector2(dx, dy);
        float distance2 = vec.len2();
        float tmp = (100f - distance2)/100f;

        float vol = (float)Math.max(0.1f, tmp);
        float pan = vec.setLength(1f).x;
        SoundManager.addSound(getWeapon().getReloadSound(), getWeapon().getReloadSound().play(vol, 1, pan));
    }
}
