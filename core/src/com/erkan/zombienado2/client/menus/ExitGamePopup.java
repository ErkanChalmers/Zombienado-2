package com.erkan.zombienado2.client.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.erkan.zombienado2.client.SoundManager;

/**
 * Created by Erik on 2018-09-18.
 */
public class ExitGamePopup {
    Stage stage;
    Skin skin;
    Window dialog;

    public ExitGamePopup(PopupListener listener) {
        skin = new Skin(Gdx.files.internal("ui/skin/neon-ui.json"));
        stage = new Stage();


        TextButton btn_cancel = new TextButton("Continue", skin);
        TextButton btn_exit = new TextButton("Exit", skin);

        btn_cancel.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.keepPlaying();
            }
        });
        btn_exit.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.exit();
            }
        });

        Slider slider = new Slider(0, 100, 1, false, skin);
        slider.setValue(SoundManager.getVolume()*100f);
        Label lbl_vol = new Label("Volume "+slider.getValue()+"%", skin);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider s = (Slider)actor;
                float val = s.getValue();
                lbl_vol.setText("Volume: "+s.getValue()+"%");
                SoundManager.setVolume(s.getValue()/100f);
            }
        });

        dialog = new Window("", skin);
        dialog.add(new Label("Menu", skin)).colspan(2).padBottom(10);
        dialog.row();
        dialog.add(lbl_vol).colspan(2);
        dialog.row();
        dialog.add(slider).colspan(2);
        dialog.row();
        dialog.add(btn_cancel);
        dialog.add(btn_exit);
        dialog.pack();
        stage.addActor(dialog);
    }

    boolean showing = false;

    public void show(){
        showing = true;
        Gdx.input.setInputProcessor(stage);
        dialog.setPosition(Gdx.graphics.getWidth()/2 - dialog.getWidth()/2, Gdx.graphics.getHeight()/2);
        //dialog(stage);
    }

    public void hide(){
        showing = false;
        //not possible when hiding every frame..
        //Gdx.input.setInputProcessor(null);
        //dialog.hide();
    }

    public void render(SpriteBatch batch) {
        if (showing)
            stage.draw();
    }
}
