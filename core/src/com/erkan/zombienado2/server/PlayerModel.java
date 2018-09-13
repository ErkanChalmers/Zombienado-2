package com.erkan.zombienado2.server;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.erkan.zombienado2.data.weapons.WeaponData;
import com.erkan.zombienado2.server.misc.FilterConstants;

/**
 * Represents a player on the server
 */
public class PlayerModel {
    public static final float RADIUS = .25f;
    public static final float MAX_HEALT = 50f;

    private float velocity = 3f;

    public String character;
    public String name;
    public Body body;
    public Vector2 movement_vector = new Vector2(0, 0);
    public float rotation;
    private float health;

    public WeaponModel weapon;

    public PlayerModel(String name, String character){
        short mask = FilterConstants.ENEMY_FIXTURE | FilterConstants.OBSTACLE_FIXTURE | FilterConstants.PLAYER_FIXTURE;
        body = WorldManager.createCircle(RADIUS, FilterConstants.PLAYER_FIXTURE, mask);
        body.setUserData(this);
        this.name = name;
        this.weapon = new WeaponModel(WeaponData.PISTOL);
        this.character = character;
        this.health = MAX_HEALT;
    }

    public void update(float dt){
        weapon.update(dt);
    }

    public void setVelocity(Vector2 vel) {
        movement_vector = vel;
        body.setLinearVelocity(vel.scl(velocity));
    }

    public void setRotation(float rotation){
        this.rotation = rotation;
    }

    public float getHealth(){
        return health;
    }

    public void inflict_damage(float damage){
        health = Math.max(0, health - damage);
    }
}
