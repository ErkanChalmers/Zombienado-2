package com.erkan.zombienado2.data.world;

import com.erkan.zombienado2.client.world.Structure;
import com.erkan.zombienado2.data.world.physics.BoxData;

import java.util.*;

/**
 * Created by Erik on 2018-08-05.
 */
public class Map {
//    public final float width, height;
    List<Tuple<BoxData, Structure.BuildType>> structures = new ArrayList<>();
/*
    public Map(float width, float height){
        this.width = width;
        this.height = height;
    }
*/
    public List<Tuple<BoxData, Structure.BuildType>> getStructures() {
        return structures;
    }


    public static Map TEST_MAP;
    static {
        TEST_MAP = new Map();
        TEST_MAP.structures.add(new Tuple<>(new BoxData(0, 0, 45, Prefabs.HOUSE_1), Structure.BuildType.WOOD));
        TEST_MAP.structures.add(new Tuple<>(new BoxData(20, 20, 90, Prefabs.HOUSE_1), Structure.BuildType.BRICK));
        TEST_MAP.structures.add(new Tuple<>(new BoxData(20, 0, 5, Prefabs.POLICE_STATION), Structure.BuildType.STONE));
        TEST_MAP.structures.add(new Tuple<>(new BoxData(4, -15, 0, Prefabs.HOUSE_2), Structure.BuildType.BRICK));
    }
}
