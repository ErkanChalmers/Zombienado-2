package com.erkan.zombienado2.data.world.physics;

/**
 * Created by Erik on 2018-08-27.
 */
public class StaticRectangle {
    float x;
    float y;
    float w;
    float h;
    float r;

    public StaticRectangle(float x,float y, float w, float h, float r){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.r = r;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getW() {
        return w;
    }

    public float getH() {
        return h;
    }

    public float getR() {
        return r;
    }
}
