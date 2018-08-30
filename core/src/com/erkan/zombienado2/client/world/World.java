package com.erkan.zombienado2.client.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Erik on 2018-08-16.
 */
public class World {
    List<Structure> structures = new ArrayList<>();
    List<Wall> walls = new ArrayList<>();
    List<DynamicObject> dynObjs_top = new ArrayList<>();
    List<DynamicObject> dynObjs_front = new ArrayList<>();
    List<DynamicObject> dynObjs_back = new ArrayList<>();

    public World(){
        Wall.init();
    }

    public void add(Structure structure){
        structures.add(structure);
    }


    public void add_top(DynamicObject object){
        dynObjs_top.add(object);
    }
    public void add_front(DynamicObject object){
        dynObjs_front.add(object);
    }
    public void add_back(DynamicObject object){
        dynObjs_back.add(object);
    }


    public void add(Wall wall){
        walls.add(wall);
    }

    public void render_back(SpriteBatch batch){
        structures.stream().forEach(structure -> structure.render_floor(batch));
        dynObjs_back.stream().forEach(obj -> {obj.update(Gdx.graphics.getDeltaTime()); obj.render(batch);});
    }

    public void render_front(SpriteBatch batch){
        walls.stream().forEach(wall -> wall.render(batch));
        structures.stream().forEach(structure -> structure.render_walls(batch));
        dynObjs_front.stream().forEach(obj -> {obj.update(Gdx.graphics.getDeltaTime()); obj.render(batch);});
    }

    public void render_top(SpriteBatch batch){
        dynObjs_top.stream().forEach(obj -> {obj.update(Gdx.graphics.getDeltaTime()); obj.render(batch);});
        structures.stream().forEach(structure -> structure.render_roof(batch));
    }
}
