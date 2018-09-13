package com.erkan.zombienado2.data.world;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.erkan.zombienado2.client.world.*;
import com.erkan.zombienado2.data.world.physics.BoxData;

import java.util.*;

/**
 * Created by Erik on 2018-08-05.
 */
public class Map {
//    public final float width, height;
    List<Tuple<BoxData, Structure.BuildType>> structures = new ArrayList<>();
    List<Tuple<Boundary, com.erkan.zombienado2.client.world.Wall.Type>> walls = new ArrayList<>();
    List<Vector2> spawns = new ArrayList<>();
    List<DynamicObject> objs_back;
    List<DynamicObject> objs_front;
    List<DynamicObject> objs_top;
/*
    public Map(float width, float height){
        this.width = width;
        this.height = height;
    }
*/
    public List<Tuple<BoxData, Structure.BuildType>> getStructures() {
    return structures;
}
    public List<Tuple<Boundary, com.erkan.zombienado2.client.world.Wall.Type>> getBoundaries() {
        return walls;
    }
    public List<DynamicObject> getObjs_back() {
        return objs_back;
    }
    public List<DynamicObject> getObjs_front() {
        return objs_front;
    }
    public List<DynamicObject> getObjs_top() {
        return objs_top;
    }
    public List<DynamicObject> get_all_objects() {
        List<DynamicObject> list = new ArrayList<>();
        list.addAll(objs_back);
        list.addAll(objs_front);
        list.addAll(objs_top);
        return list;
    }
    public List<Vector2> getSpawns(){
        return spawns;
    }
    public Vector2 getRandomSpawnpoint(){
        int index = MathUtils.random(0, spawns.size()-1);
        return spawns.get(index);
    }


    public static Map TEST_MAP;
    static {
        TEST_MAP = new Map();
        TEST_MAP.structures.add(new Tuple<>(new BoxData(-5, 0, 45, Prefabs.HOUSE_1), Structure.BuildType.WOOD));
        TEST_MAP.structures.add(new Tuple<>(new BoxData(-13, 8, 45, Prefabs.HOUSE_1), Structure.BuildType.WOOD));
        TEST_MAP.structures.add(new Tuple<>(new BoxData(-21, 16, 45, Prefabs.HOUSE_1), Structure.BuildType.WOOD));

        TEST_MAP.structures.add(new Tuple<>(new BoxData(-17, -10, 135, Prefabs.HOUSE_1), Structure.BuildType.BRICK));
        TEST_MAP.structures.add(new Tuple<>(new BoxData(-26.5f, -1, 135, Prefabs.HOUSE_1), Structure.BuildType.BRICK));

        TEST_MAP.structures.add(new Tuple<>(new BoxData(20, 20, 90, Prefabs.HOUSE_1), Structure.BuildType.BRICK));
        TEST_MAP.structures.add(new Tuple<>(new BoxData(20, 0, 5, Prefabs.POLICE_STATION), Structure.BuildType.STONE));
        TEST_MAP.structures.add(new Tuple<>(new BoxData(4, -15, 0, Prefabs.HOUSE_2), Structure.BuildType.BRICK));
        TEST_MAP.structures.add(new Tuple<>(new BoxData(2, 17, -7, Prefabs.OFFICE_BUILDING), Structure.BuildType.BRICK));

        //TEST
        //TEST_MAP.structures.add(new Tuple<>(new BoxData(10, 0, 0, Prefabs.TEST), Structure.BuildType.BRICK));

        TEST_MAP.walls.add(new Tuple<>(new Boundary(new Vector2(11.2f, -9.3f),new Vector2(16.5f, -9.4f )), Wall.Type.BARBED_WIRE));
        TEST_MAP.walls.add(new Tuple<>(new Boundary(new Vector2(24f, 19.7f),new Vector2(24f, 22f )), Wall.Type.WOOD_FENCE));
        TEST_MAP.walls.add(new Tuple<>(new Boundary(new Vector2(7.9f, 25.4f),new Vector2(16.0f, 24.8f )), Wall.Type.BARBED_WIRE));
        TEST_MAP.walls.add(new Tuple<>(new Boundary(new Vector2(24f, 15f),new Vector2(24f, 9.5f )), Wall.Type.WOOD_FENCE));

        TEST_MAP.walls.add(new Tuple<>(new Boundary(new Vector2(11.4f, -20.7f),new Vector2(11.4f, -25f )), Wall.Type.WOOD_FENCE));
        TEST_MAP.walls.add(new Tuple<>(new Boundary(new Vector2(11.4f, -25f ),new Vector2(-6f, -25f )), Wall.Type.WOOD_FENCE));
        TEST_MAP.walls.add(new Tuple<>(new Boundary(new Vector2(-6f, -25f ),new Vector2(-16.4f, -16.2f )), Wall.Type.WOOD_FENCE));

        TEST_MAP.walls.add(new Tuple<>(new Boundary(new Vector2(-27f, 5.3f ),new Vector2(-21.5f, 9.9f )), Wall.Type.WOOD_FENCE));
        TEST_MAP.walls.add(new Tuple<>(new Boundary(new Vector2(-14.7f, 16.6f ),new Vector2(-12.4f, 14.4f )), Wall.Type.WOOD_FENCE));
        TEST_MAP.walls.add(new Tuple<>(new Boundary(new Vector2(-25.9f, -7.3f ),new Vector2(-23.3f, -9.4f )), Wall.Type.WOOD_FENCE));
        TEST_MAP.walls.add(new Tuple<>(new Boundary(new Vector2(-6.7f, 8.7f ),new Vector2(-3.9f, 8.6f )), Wall.Type.BARBED_WIRE));
        TEST_MAP.walls.add(new Tuple<>(new Boundary(new Vector2(-25.3f, 13.4f ),new Vector2(-23.6f, 11.8f )), Wall.Type.WOOD_FENCE));

        TEST_MAP.objs_back = new ArrayList<>();
        TEST_MAP.objs_front= new ArrayList<>();
        TEST_MAP.objs_top = new ArrayList<>();

        TEST_MAP.objs_top.add(new BrokenLamp(15, -5, 95)); //move creation
        TEST_MAP.objs_top.add(new StreetLamp(14.2f, 3.8f, 95)); //move creation
        TEST_MAP.objs_top.add(new StreetLamp(7.4f, 16.3f, -97)); //move creation
        TEST_MAP.objs_top.add(new RoofFan(-3f, 0)); //move creation
        TEST_MAP.objs_top.add(new Lantern(5.8f, -12.5f)); //move creation
        TEST_MAP.objs_back.add(new TV(-2f, 2.75f, -45f)); //move creation
        TEST_MAP.objs_back.add(new Sofa(-0.07f, .22f, 135f)); //move creation
        TEST_MAP.objs_back.add(new Sofa(6.1f, 18f, 180f)); //move creation
        TEST_MAP.objs_back.add(new Sofa(16.8f, -8.12f, 20f)); //move creation
        TEST_MAP.objs_back.add(new Sofa(16.4f, -6.00f, 5f)); //move creation
        TEST_MAP.objs_back.add(new Car(9.5f, 31.5f, 135f)); //move creation
        TEST_MAP.objs_back.add(new Car(12.5f, 30.0f, 70f)); //move creation
        TEST_MAP.objs_back.add(new Tank(10.5f, 27.5f, -80f)); //move creation

        TEST_MAP.spawns.add(new Vector2(-15, -9));
        TEST_MAP.spawns.add(new Vector2(-27, -3));
        TEST_MAP.spawns.add(new Vector2(-13, 10));
        TEST_MAP.spawns.add(new Vector2(-2, -10));
        TEST_MAP.spawns.add(new Vector2(2, -24));
        TEST_MAP.spawns.add(new Vector2(21, 21));
        TEST_MAP.spawns.add(new Vector2(23, 5));
        TEST_MAP.spawns.add(new Vector2(22, -6));
        TEST_MAP.spawns.add(new Vector2(8, -22));
        TEST_MAP.spawns.add(new Vector2(-1, -13));
    }
}
