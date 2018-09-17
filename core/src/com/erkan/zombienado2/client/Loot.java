package com.erkan.zombienado2.client;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.erkan.zombienado2.data.weapons.WeaponData;
import com.erkan.zombienado2.graphics.Transform;

/**
 * Created by Erik on 2018-09-16.
 */
public class Loot {
    static Texture txt_medpack;

    static void loadTextures(){
        txt_medpack = new Texture("misc/heart.png");
    }

    float x, y;
    String type;

    public Loot(float x, float y, String type){
        this.x = Transform.to_screen_space(x);
        this.y = Transform.to_screen_space(y);

        this.type = type;
    }

    public void draw(SpriteBatch batch){
        String[] args = type.split(":");
        Sprite sprite = null;
        if (args[0].equals("W")){
            sprite = new Sprite(new Texture(WeaponData.getWeapon(args[1]).texture_path));
        } else if (args[0].equals("M")){
            sprite = new Sprite(txt_medpack);
        }
        sprite.setCenter(x, y);
        sprite.draw(batch);
        //sprite.getTexture().dispose();
    }
}
