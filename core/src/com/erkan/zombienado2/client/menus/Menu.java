package com.erkan.zombienado2.client.menus;

import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by Erik on 2018-09-16.
 */
public abstract class Menu {
    JoinGameListener jg;
    public Menu(JoinGameListener listener){
        jg = listener;
    }
    abstract public Stage getStage();
    abstract public void create();
    abstract public void render();
}
