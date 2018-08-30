package com.erkan.zombienado2.data.world;

import com.badlogic.gdx.math.Vector2;
import com.erkan.zombienado2.data.world.physics.Component;

/**
 * Created by Erik on 2018-08-16.
 */
public class Boundary {

    Vector2 start;

    Vector2 end;
    float width = Component.DEPTH/2; //Depth from component
    boolean seeThrough = false;

    public Boundary(Vector2 start, Vector2 end){
        this.start = start;
        this.end = end;
    }

    public Boundary setSeeThrough(boolean seeThrough){
        this.seeThrough = seeThrough;
        return this;
    }

    public Vector2 getStart() {
        return start;
    }

    public Vector2 getEnd() {
        return end;
    }

    public float getWidth() {
        return width;
    }

    public boolean isSeeThrough() {
        return seeThrough;
    }
}
