package com.erkan.zombienado2.server;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.erkan.zombienado2.client.Weapon;
import com.erkan.zombienado2.data.weapons.WeaponData;
import com.erkan.zombienado2.server.misc.FilterConstants;

/**
 * Represents a player on the server
 */
public class PlayerModel {
    public static final float RADIUS = .25f;
    public static final float MAX_HEALT = 50f;

    private float velocity = 3f;

    private boolean is_moving = false;

    public String character;
    public String name;
    public Body body;
    public Vector2 movement_vector = new Vector2(0, 0);
    public float rotation;
    private float health;
    private boolean is_alive = true;

    private WeaponModel primary;
    private WeaponModel secondary;

    private WeaponModel weapon;



    public PlayerModel(String name, String character){
        short mask = FilterConstants.ENEMY_FIXTURE | FilterConstants.OBSTACLE_FIXTURE | FilterConstants.PLAYER_FIXTURE | FilterConstants.LOOT_FIXTURE;
        body = WorldManager.createCircle(RADIUS, FilterConstants.PLAYER_FIXTURE, mask);
        body.setUserData(this);
        this.name = name;
        this.secondary = new WeaponModel(WeaponData.PISTOL);
        this.weapon = secondary;
        this.character = character;
        this.health = MAX_HEALT;
    }


    Action action = null;
    public void setAction(Action action){
        this.action = action;
    }

    public Action getAction(){ //:S
        return action;
    }

    public void performAction(){
        if (action != null)
            action.performAction(this);
    }

    public void switchWeapon(){
        if (primary == null)
            return;

        if (weapon == primary)
            weapon = secondary;
        else
            weapon = primary;
    }

    public WeaponModel getWeapon(){
        return weapon;
    }
    public WeaponModel getPrimary(){
        return primary;
    }

    public void setPrimaryWeapon(WeaponData weapon){
        primary = new WeaponModel(weapon);
        this.weapon = primary;
    }

    public boolean is_alive(){
        return is_alive;
    }

    public boolean isMoving(){
        return is_moving;
    }

    public void update(float dt){
        weapon.update(dt);
    }

    public void setVelocity(Vector2 vel) {
        movement_vector = vel;
        body.setLinearVelocity(vel.scl(velocity));

        if (vel.len() == 0)
            is_moving = false;
        else is_moving = true;
    }

    public void setRotation(float rotation){
        this.rotation = rotation;
    }

    public float getHealth(){
        return health;
    }

    public void addHealth(float hp){
        health+=hp;
        health = MathUtils.clamp(health, 0, MAX_HEALT);
        if (!is_alive){
            is_alive = true;
            //short mask = FilterConstants.ENEMY_FIXTURE | FilterConstants.OBSTACLE_FIXTURE | FilterConstants.PLAYER_FIXTURE;
            //body.getFixtureList().get(0).getFilterData().maskBits = mask;
            body.getFixtureList().get(0).setSensor(false);
            body.setActive(true);
        }
    }

    public void inflict_damage(float damage){
        health = Math.max(0, health - damage);
        if (health == 0){
            is_alive = false;
            body.getFixtureList().get(0).setSensor(true);
            body.setLinearVelocity(0, 0);
            //short mask = FilterConstants.OBSTACLE_FIXTURE;
            //body.getFixtureList().get(0).getFilterData().maskBits = mask;
        }
    }
}
