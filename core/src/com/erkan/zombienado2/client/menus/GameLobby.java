package com.erkan.zombienado2.client.menus;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.erkan.zombienado2.client.Character;

/**
 * Created by Erik on 2018-09-16.
 */
public class GameLobby extends Menu{
    Stage stage;
    Skin skin;

    SelectBox<String> sb_character;
    List<String> player_list;

    public GameLobby(JoinGameListener listener) {
        super(listener);
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void create() {
        skin = new Skin(Gdx.files.internal("ui/skin/neon-ui.json"));

        stage = new Stage();
        Table table = new Table(skin);
        table.setFillParent(true);

        table.add(new Label("Ready players", skin)).colspan(2);
        table.row();
        player_list = new List<>(skin);

        table.add(player_list).colspan(2).height(300).width(300).padTop(2);

        table.row();

        sb_character = new SelectBox<>(skin);
        Array<String> characters = new Array<>();
        Character.getCharactes().forEach(name -> characters.add(name));
        sb_character.setItems(characters);
        table.add(new Label("Character: ", skin)).align(Align.right);
        table.add(sb_character).align(Align.left).padBottom(7f);
        table.row();
        Label lbl_name = new Label("Name: ",skin);
        TextField tf_name = new TextField("",skin);
        table.add(lbl_name).align(Align.right);
        table.add(tf_name).align(Align.left);
        table.row();

        TextButton btn_ready = new TextButton("Ready", skin);
        btn_ready.align(Align.left);

        table.add(btn_ready).colspan(2);

        btn_ready.addListener(new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (tf_name.getText().equals("") || tf_name.getText().contains(" ")){
                    return;
                }

                jg.ready(tf_name.getText(), sb_character.getSelected());
                Gdx.input.setInputProcessor(null);
            }
        });
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    public void addCharacter(String name, String character){
        Array<String> characters = sb_character.getItems();
        Array<String> new_characters = new Array<>();
        characters.forEach(c -> {
            if (!c.equals(character))
                new_characters.add(c);
        });
        sb_character.setItems(new_characters);

        Array<String> players = player_list.getItems();
        Array<String> new_players = new Array<>();
        players.forEach(p -> {
            new_players.add(p);
        });
        new_players.add(name + " as " + character);
        player_list.setItems(new_players);
    }

    @Override
    public void render() {
        //Gdx.gl.glClearColor(.3f, .3f, .3f, 1);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }
}
