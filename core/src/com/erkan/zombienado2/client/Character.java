package com.erkan.zombienado2.client;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;

import java.util.*;

/**
 * Created by Erik on 2018-08-04.
 */
public class Character {
    public static final int WIDTH = 64;

    public final Texture torso_1h;
    public final Texture torso_2h;
    public final Texture torso_dw;
    public final Animation<Texture> walk_animation;

    public Character(String path){
        walk_animation = new com.badlogic.gdx.graphics.g2d.Animation(1f/10f,
                new Texture(path+"/legs_0001.png"),
                new Texture(path+"/legs_0002.png"),
                new Texture(path+"/legs_0003.png"),
                new Texture(path+"/legs_0004.png"));
        walk_animation.setPlayMode(com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP);

        torso_1h = new Texture(path+"torso_1h.png");
        torso_2h = new Texture(path+"torso_2h.png");
        torso_dw = new Texture(path+"torso_DW.png");

        torso_1h.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        torso_2h.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        torso_dw.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    public static final Character SOLDIER = new Character("animations/soldier/");
    public static final Character NURSE = new Character("animations/nurse/");
    public static final Character OFFICER = new Character("animations/officer/");
    public static final Character BUSINESS = new Character("animations/business/");
    public static final Character SCHOOLGIRL = new Character("animations/schoolgirl/");

    static final HashMap<String, Character> characters;

    static {
        characters = new HashMap<>();
        characters.put("Soldier", SOLDIER);
        characters.put("Nurse", NURSE);
        characters.put("Officer", OFFICER);
        characters.put("Businessman", BUSINESS);
        characters.put("Schoolgirl", SCHOOLGIRL);
    }

    public static Character getCharacter(String name){
        return characters.get(name);
    }

    public static Set<String> getCharactes(){
        Set<String> names = new HashSet<>();
        characters.entrySet().stream().forEach(c -> {
            names.add(c.getKey());
        });
        return names;
    }

    public String toString(){
        for (Map.Entry<String, Character> entry : characters.entrySet()) {
            if (Objects.equals(this, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}