package com.erkan.zombienado2.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.erkan.zombienado2.server.Server;

/**
 * Created by Erik on 2018-08-16.
 */
public class MainMenu {

    Texture bg_main;
    Texture bg_host;
    Texture bg_connect;

    Texture logo;

    Texture ui_back;


    Skin skin;

    Stage mainStage;
    Stage hostStage;
    Stage connectStage;

    JoinGameListener jg;

    public MainMenu(JoinGameListener listener){
        jg = listener;
    }


    public void create(){
        skin = new Skin(Gdx.files.internal("ui/skin/biological-attack-ui.json"));
        bg_main = new Texture("ui/ui_main_background.jpg");
        bg_main.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        bg_host = new Texture("ui/ui_host_background.jpg");
        bg_host.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        bg_connect = new Texture("ui/ui_connect_background.jpg");
        bg_connect.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        logo = new Texture("ui/logo2.png");
        logo.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        ui_back = new Texture("ui/back.png");

        mainStage = new Stage();


        Table table = new Table(skin);
        table.setFillParent(true);
        table.setBackground(new TextureRegionDrawable(new TextureRegion(ui_back)));

        //table.setDebug(true);

        TextButton btn_select_host = new TextButton("Host", skin);
        btn_select_host.align(Align.left);
        table.add(btn_select_host).padBottom(20);

        btn_select_host.addListener(new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.setInputProcessor(hostStage);
                hostStage.addAction(Actions.moveTo(0,
                        0,
                        .05f));

            }
        });

        table.row();

        TextButton btn_select_connect = new TextButton("Connect", skin);
        table.add(btn_select_connect);

        btn_select_connect.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.setInputProcessor(connectStage);
                connectStage.addAction(Actions.moveTo(0,
                        0,
                        .05f));

            }
        });

        mainStage.addActor(table);



        //HOST
        hostStage = new Stage();
        hostStage.getRoot().setX(mainStage.getWidth());

        TextButton btn_back = new TextButton("Back", skin);
        btn_back.setPosition(10, hostStage.getHeight()-30);
        hostStage.addActor(btn_back);
        btn_back.addListener(new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.setInputProcessor(mainStage);
                hostStage.addAction(Actions.moveTo(mainStage.getWidth(),
                        0,
                        .08f));
            }
        });

        Table table1 = new Table();
        table1.setFillParent(true);
        table1.setBackground(new TextureRegionDrawable(new TextureRegion(ui_back)));

        Label lbl_ts = new Label("Team size", skin);
        TextField tf_ts = new TextField("1", skin);
        table1.add(lbl_ts).align(Align.right).padRight(10);
        table1.add(tf_ts);
        table1.row();
        Label lbl_port = new Label("Port", skin);
        TextField tf_port = new TextField("9021",skin);
        table1.add(lbl_port).align(Align.right).padRight(10);
        table1.add(tf_port);

        table1.row();
        table1.row();
        TextButton btn_host = new TextButton("Host", skin);
        table1.add(btn_host).colspan(2);


        btn_host.addListener(new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y) {System.out.println("Launcher: creating server");
                Gdx.input.setInputProcessor(null);
                new Thread(()-> {
                    new Server(Integer.parseInt(tf_port.getText()), Integer.parseInt(tf_ts.getText()));
                }).start();
                jg.join("127.0.0.1", Integer.parseInt(tf_port.getText()));
            }
        });

        hostStage.addActor(table1);

        //Connect
        connectStage = new Stage();
        connectStage.getRoot().setX(mainStage.getWidth());

        TextButton btn_back2 = new TextButton("Back", skin);
        btn_back2.setPosition(10, connectStage.getHeight()-30);
        connectStage.addActor(btn_back2);
        btn_back2.addListener(new ClickListener(){


            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.setInputProcessor(mainStage);
                connectStage.addAction(Actions.moveTo(mainStage.getWidth(),
                        0,
                        .08f));
            }
        });

        Table table2 = new Table();
        table2.setFillParent(true);
        table2.setBackground(new TextureRegionDrawable(new TextureRegion(ui_back)));
        Label lbl_port2 = new Label("Host Address", skin);
        TextField tf_ip = new TextField("",skin);
        TextField tf_port2 = new TextField("9021",skin);
        table2.add(lbl_port2);
        table2.add(tf_ip).width(200);
        table2.add(tf_port2).width(70);
        table2.row();
        TextButton btn_connect = new TextButton("Connect", skin);
        btn_connect.addListener(new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.setInputProcessor(null);
                jg.join(tf_ip.getText(), Integer.parseInt(tf_port2.getText()));
            }
        });
        table2.add(btn_connect).colspan(3);
        connectStage.addActor(table2);

        btn_back2.setZIndex(10);
        btn_back.setZIndex(10);


        Gdx.input.setInputProcessor(mainStage);

    }

    public void render(){


        Gdx.gl.glClearColor(.3f, .3f, .3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

        mainStage.act(Gdx.graphics.getDeltaTime());
        mainStage.getBatch().begin();
        mainStage.getBatch().draw(bg_main, mainStage.getRoot().getX(), mainStage.getRoot().getY(), mainStage.getWidth(), mainStage.getHeight());
        mainStage.getBatch().draw(logo, 0 +50 ,20);
        mainStage.getBatch().end();
        mainStage.draw();

        hostStage.act(Gdx.graphics.getDeltaTime());
        hostStage.getBatch().begin();
        hostStage.getBatch().draw(bg_host, hostStage.getRoot().getX(), hostStage.getRoot().getY(), hostStage.getWidth(), hostStage.getHeight());
        hostStage.getBatch().draw(logo, 0 +50 ,20);
        hostStage.getBatch().end();
        hostStage.draw();

        connectStage.act(Gdx.graphics.getDeltaTime());
        connectStage.getBatch().begin();
        connectStage.getBatch().draw(bg_connect, connectStage.getRoot().getX(), connectStage.getRoot().getY(), connectStage.getWidth(), connectStage.getHeight());
        connectStage.getBatch().end();
        connectStage.draw();
        connectStage.getBatch().begin();
        connectStage.getBatch().draw(logo, 0 +50 , 20);
        connectStage.getBatch().end();
    }

}
