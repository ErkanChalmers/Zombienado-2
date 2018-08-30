package com.erkan.zombienado2.client;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.erkan.zombienado2.data.weapons.WeaponData;
import com.erkan.zombienado2.server.PlayerModel;
import com.erkan.zombienado2.server.misc.FilterConstants;


import java.util.logging.Filter;

import static com.erkan.zombienado2.graphics.Transform.*;

/**
 * Created by Erik on 2018-07-30.
 */
public abstract class Player {
    public static final float MAX_HEALTH = 50f;

    private boolean init_done;

    Character character;
    Animation<TextureRegion> muzzle_animation;

    Sprite torso;
    Body body;

    private String name;

    public float rotation = 0f;
    public Vector2 position = new Vector2(0, 0);
    private float elapsed_running_time = 0;
    private boolean isrunning = false;
    private float elapsed_shooting_time = 0;
    private boolean isshooting = false;
    public float distance_to_focus = to_screen_space(10);
    private float health;


    private Vector2 direction = new Vector2();

    private Weapon weapon;


    public Player(String name, Character character){
        this.name = name;
        weapon = new Weapon(WeaponData.PISTOL);
        this.character = character;
        this.torso = new Sprite(character.torso_1h);
        this.body = PhysicsHandler.createCircle(to_screen_space(PlayerModel.RADIUS), FilterConstants.PLAYER_FIXTURE, (short)(FilterConstants.LIGHT | FilterConstants.PHYSICS_FIXTURE));
        flash_light = PhysicsHandler.createConeLight(to_screen_space(position.x), to_screen_space(position.y), new Color(.45f,.45f,.45f,.95f), to_screen_space(8), rotation, 25);
        //flash_focus = PhysicsHandler.createPointLight(to_screen_space(position.x), to_screen_space(position.y), new Color(.45f, .45f, .45f, .95f), 300);
        muzzle_elumination = PhysicsHandler.createPointLight(to_screen_space(position.x), to_screen_space(position.y), new Color(1,1,0,1f), 400);
        health = MAX_HEALTH;
    }

    public String getName(){
        return name;
    }

    public Character getCharacter(){
        return character;
    }

    //must be done from GL context
    private void init(){
        muzzle_animation = new Animation<TextureRegion>(1f/25f, Weapon.muzzleflash_arraay);
    }

    public float getHealth(){
        return health;
    }

    public void setHealth(float health){
        this.health = health;
    }

    public Weapon getWeapon(){
        return weapon;
    }

    public void shoot(){
        isshooting = true;
        elapsed_shooting_time = 0;
        //Weapon.muzzleflash.setPlayMode(Animation.PlayMode.NORMAL);
    }

    public void reload(){
        //do something? method inherited by sub classes
    }

    public void setWeapon(WeaponData wd){
        weapon = new Weapon(wd);
        switch (wd.held_type){
            case ONE_HANDED:
                torso = new Sprite(character.torso_1h);
                break;
            case TWO_HANDED:
                torso = new Sprite(character.torso_2h);
                break;
            case DUAL_WEILDED:
                torso = new Sprite(character.torso_dw);
                break;
        }
    }

    public void run(boolean run){
        isrunning = run;
    }

    public void run(Vector2 dir) {
        if (dir.len() == 0){
            isrunning = false;
            return;
        }
        direction = dir;
        isrunning = true;
    }

    PointLight muzzle_elumination;
    ConeLight flash_light;
   // PointLight flash_focus;

    public void render(SpriteBatch batch){
        if (!init_done) {
            init();
            init_done = true;
        }
        body.setTransform(to_screen_space(position), MathUtils.degreesToRadians * rotation);
        flash_light.setPosition(to_screen_space(position.x), to_screen_space(position.y));
        flash_light.setDirection(rotation);
        //flash_focus.setPosition(to_screen_space(position.x) + distance_to_focus * MathUtils.cos(MathUtils.degreesToRadians * rotation), to_screen_space(position.y) +  distance_to_focus  * MathUtils.sin(MathUtils.degreesToRadians * rotation));
        flash_light.setDistance(distance_to_focus+to_screen_space(3));
        //flash_light.setConeDegree(25 - distance_to_focus / 40);
      //  flash_light.setDistance(distance_to_focus+200);
        //flash_focus.setDistance(distance_to_focus/2 - 20);


        if (isrunning)
            elapsed_running_time += Gdx.graphics.getDeltaTime();
        else
            elapsed_running_time = 0;

        if (isshooting)
            elapsed_shooting_time += Gdx.graphics.getDeltaTime();
        else
            elapsed_shooting_time += 99999;

        //ON SHOT FIRED
        if (muzzle_animation.getKeyFrameIndex(elapsed_shooting_time) == 0){
            if (weapon.getWeaponData().held_type.equals(WeaponData.HeldType.ONE_HANDED))
                rec_offset = - 5;
            else
                rec_offset = - 10;



            muzzle_elumination.setPosition(to_screen_space(position.x) + 85 * MathUtils.cos(MathUtils.degreesToRadians * rotation), to_screen_space(position.y) + 85 * MathUtils.sin(MathUtils.degreesToRadians * rotation));
            muzzle_elumination.setColor(new Color(1,.6f,0,.9f));
        }

        if (rec_offset < 0)
            rec_offset +=0.5;


        float dir_ang = direction.angle();

        if (rotation < 0){
            rotation+=360;
        }
        float ang = (dir_ang - rotation)/2;
        if (Math.abs(ang) > 90)
            ang = 180 + ang;

      if (ang < -45){
            ang+= 90;
      } else if (ang > 45)
          ang -= 90;

        ang = rotation + ang;

        Sprite legs = new Sprite(character.walk_animation.getKeyFrame(elapsed_running_time), Character.WIDTH, Character.WIDTH);

        legs.setRotation(ang - 90);
        legs.setCenter(to_screen_space(position.x),  to_screen_space(position.y));
        legs.setOrigin( Character.WIDTH/2,  Character.WIDTH/2);
        legs.draw(batch);

        torso.setRotation(rotation - 90);
        torso.setOrigin( Character.WIDTH/2,  Character.WIDTH/2);
        torso.setCenter(to_screen_space(position.x) + (rec_offset/2f) * MathUtils.cos(MathUtils.degreesToRadians * (rotation)),
                     to_screen_space(position.y) + (rec_offset/2f) * MathUtils.sin(MathUtils.degreesToRadians * (rotation)));

        torso.draw(batch);

        Sprite wep = new Sprite(weapon.getTexture());
        wep.setCenter(to_screen_space(position.x) + (25+rec_offset) * MathUtils.cos(MathUtils.degreesToRadians * (rotation - 10 + rec_offset*.7f)),
                    to_screen_space(position.y) + (25+rec_offset) * MathUtils.sin(MathUtils.degreesToRadians * (rotation - 10 + rec_offset*.7f)));
        wep.setRotation(rotation-90);
        wep.draw(batch);


        Sprite muzzle = new Sprite(muzzle_animation.getKeyFrame(elapsed_shooting_time));
        muzzle.setSize(128, 128);
        muzzle.setOrigin(muzzle.getWidth()/2, muzzle.getHeight()/2);
        muzzle.setRotation(rotation-90);
        muzzle.setCenter(to_screen_space(position.x) + 64 * MathUtils.cos(MathUtils.degreesToRadians * rotation), to_screen_space(position.y) + 64 * MathUtils.sin(MathUtils.degreesToRadians * rotation));
        muzzle.draw(batch);
        if (muzzle_animation.isAnimationFinished(elapsed_shooting_time)){
            isshooting = false;
        }

        if (muzzle_elumination != null){
            Color c = muzzle_elumination.getColor();

            muzzle_elumination.setColor(c.r, c.g, c.b, c.a - 0.1f);
        }
   }

    int rec_offset = 0;
}
