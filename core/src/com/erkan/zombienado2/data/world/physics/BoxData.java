package com.erkan.zombienado2.data.world.physics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.erkan.zombienado2.graphics.Transform;

import java.util.*;
import java.util.List;

/**
 * Created by Erik on 2018-08-04.
 */
public class BoxData {
    List<ComponentAt> list = new LinkedList<>();

    float x, y, r, width, height;

    public BoxData(float x, float y, float r, int[][] array){
        this.x = x;
        this.y = y;
        this.r = r;
        width = array[0].length * Component.LENGTH;
        height = array.length * Component.LENGTH;

        Vector2 origin = Transform.rotate(new Vector2(width/2, height/2), r);
        for (int step_y = 0; step_y < array.length; step_y++){
            for (int step_x = 0; step_x < array[step_y].length; step_x++){
                Component comp = Component.get(array[step_y][step_x]);
                if (comp != null) {
                    Vector2 step = Transform.rotate(new Vector2(-width/2 + step_x * Component.LENGTH + Component.LENGTH/2,height/2 - step_y * Component.LENGTH - Component.LENGTH/2), r);
                    add(new Vector2(x + step.x, y  + step.y), comp);
                }
            }
        }
        //FOR DEBUG
        //add(new Vector2(x, y), Component.WALL_LEFT);
        //add(new Vector2(x, y), Component.DEBUG);
    }

    void add(Vector2 position, Component component){
        list.add(new ComponentAt(position, component));
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getR() {
        return r;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public List<ComponentAt> getComponentsList(){
        return list;
    }

    public class ComponentAt {

        Component component;
        Vector2 position;
        public ComponentAt(Vector2 position, Component component){
            this.component = component;
            this.position = position;
        }

        public Component getComponent() {
            return component;
        }

        public Vector2 getPosition() {
            return position;
        }
    }
}
