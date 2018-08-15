package com.erkan.zombienado2.server;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.erkan.zombienado2.data.weapons.WeaponData;
import com.erkan.zombienado2.server.misc.FilterConstants;

/**
 * Represents a player on the server
 */
public class PlayerModel {
    public static final float RADIUS = .25f;

    private float velocity = 3f;

    public String character;
    public String name;
    public Body body;
    public float rotation;

    public WeaponModel weapon;

    public PlayerModel(String name, String character){
        short mask = FilterConstants.ENEMY_FIXTURE | FilterConstants.OBSTACLE_FIXTURE | FilterConstants.PLAYER_FIXTURE;
        body = WorldManager.createCircle(RADIUS, FilterConstants.PLAYER_FIXTURE, mask);
        this.name = name;
        this.weapon = new WeaponModel(WeaponData.PISTOL);
        this.character = character;
    }

    public void update(float dt){
        weapon.update(dt);
    }

    public void setVelocity(Vector2 vel){
        body.setLinearVelocity(vel.scl(velocity));
    }

    public void setRotation(float rotation){
        this.rotation = rotation;
    }
}
