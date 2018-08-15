package com.erkan.zombienado2.data.world.physics;

import com.badlogic.gdx.math.MathUtils;

import java.util.*;

/**
 * Created by Erik on 2018-08-04.
 */
public class Component {
    public static final float LENGTH = 1.7f;
    public static final float DEPTH = .5f;

    public static final Component WALL_CORNER_TOP_LEFT;
    public static final Component WALL_CORNER_TOP_RIGHT;
    public static final Component WALL_CORNER_BOTTOM_LEFT;
    public static final Component WALL_CORNER_BOTTOM_RIGHT;

    public static final Component WALL_TOP;
    public static final Component WALL_BOTTOM;
    public static final Component WALL_LEFT;
    public static final Component WALL_RIGHT;


    public static final Component DEBUG;

    public static HashMap<Integer, Component> components;

    public static Component get(int id){
        return components.get(id);
    }

    static {
        //DEBUG
        DEBUG = new Component();
        DEBUG.addCircle(0,0, 1);

        components = new HashMap<>();
        //CORNERS
        WALL_CORNER_TOP_LEFT = new Component();
        WALL_CORNER_TOP_LEFT.addRectangle(0, LENGTH/2 - DEPTH/2, LENGTH/2, DEPTH/2);
        WALL_CORNER_TOP_LEFT.addRectangle(DEPTH/2 - LENGTH/2, 0, DEPTH/2, LENGTH/2);

        WALL_CORNER_TOP_RIGHT = new Component();
        WALL_CORNER_TOP_RIGHT.addRectangle(0, LENGTH/2 - DEPTH/2, LENGTH/2, DEPTH/2);
        WALL_CORNER_TOP_RIGHT.addRectangle(LENGTH/2 - DEPTH/2, 0, DEPTH/2, LENGTH/2);

        WALL_CORNER_BOTTOM_LEFT = new Component();
        WALL_CORNER_BOTTOM_LEFT.addRectangle(0, DEPTH/2-LENGTH/2, LENGTH/2, DEPTH/2);
        WALL_CORNER_BOTTOM_LEFT.addRectangle(DEPTH/2 - LENGTH/2, 0, DEPTH/2, LENGTH/2);

        WALL_CORNER_BOTTOM_RIGHT = new Component();
        WALL_CORNER_BOTTOM_RIGHT.addRectangle(0, DEPTH/2-LENGTH/2, LENGTH/2, DEPTH/2);
        WALL_CORNER_BOTTOM_RIGHT.addRectangle(LENGTH/2 - DEPTH/2, 0, DEPTH/2, LENGTH/2);

        //STANDARD
        WALL_TOP = new Component();
        WALL_TOP.addRectangle(0, LENGTH/2 - DEPTH/2, LENGTH/2, DEPTH/2);

        WALL_BOTTOM = new Component();
        WALL_BOTTOM.addRectangle(0, DEPTH/2-LENGTH/2, LENGTH/2, DEPTH/2);

        WALL_LEFT = new Component();
        WALL_LEFT.addRectangle(DEPTH/2 - LENGTH/2, 0, DEPTH/2, LENGTH/2);

        WALL_RIGHT = new Component();
        WALL_RIGHT.addRectangle(LENGTH/2 - DEPTH/2, 0, DEPTH/2, LENGTH/2);

        components.put(1, WALL_TOP);
        components.put(2, WALL_RIGHT);
        components.put(3, WALL_BOTTOM);
        components.put(4, WALL_LEFT);
        components.put(5, WALL_CORNER_TOP_LEFT);
        components.put(6, WALL_CORNER_TOP_RIGHT);
        components.put(7, WALL_CORNER_BOTTOM_RIGHT);
        components.put(8, WALL_CORNER_BOTTOM_LEFT);
    }

    public static int getID(Component component){
        for (Map.Entry<Integer, Component> entry : components.entrySet()) {
            if (Objects.equals(component, entry.getValue())) {
                return entry.getKey();
            }
        }
        return -1;
    }

    List<ComponentBody> bodies = new LinkedList<>();

    private Component(){
    }

    public void addRectangle(float x, float y, float w, float h){
        bodies.add(new RectangleBody(x, y, w, h));
    }

    public void addCircle(float x, float y, float radius){
        bodies.add(new CircularBody(x, y, radius));
    }


    public List<ComponentBody> getBodies(){
        return bodies;
    }


    public abstract class ComponentBody{
        float x, y;

        public ComponentBody(float x, float y){
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public float getX(float degree){
            return  x * MathUtils.cosDeg(degree) - y * MathUtils.sinDeg(degree);
        }

        public float getY(float degree){
            return x * MathUtils.sinDeg(degree) + y * MathUtils.cosDeg(degree);
        }

        public float getY() {
            return y;
        }
    }

    public class CircularBody extends ComponentBody {
        float radius;

        public CircularBody(float x, float y, float r){
            super(x, y);
            radius = r;
        }

        public float getRadius(){
            return radius;
        }
    }

    public class RectangleBody extends ComponentBody{
        float h;
        float w;

        public RectangleBody(float x, float y, float w, float h){
            super(x, y);
            this.h = h;
            this.w = w;
        }

        public float getHeight() {
            return h;
        }

        public float getWidth() {
            return w;
        }



    }
}
