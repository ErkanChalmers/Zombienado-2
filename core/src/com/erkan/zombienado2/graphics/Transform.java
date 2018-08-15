package com.erkan.zombienado2.graphics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Erik on 2018-08-01.
 */
public class Transform {
    public static final float ppm = 64;

    public static float scale_to_world(float value){
        return value / ppm;
    }

    public static float to_screen_space(float value){
        return value * ppm;
    }

    public static Vector2 to_screen_space(Vector2 vec){
        return new Vector2(vec.x * ppm, vec.y * ppm);
    }

    public static Vector2 rotate(Vector2 vector, float degree){
        float x = vector.x;
        float y = vector.y;
        float new_x = x * MathUtils.cosDeg(degree) - y * MathUtils.sinDeg(degree);
        float new_y = x * MathUtils.sinDeg(degree) + y * MathUtils.cosDeg(degree);
        return new Vector2(new_x, new_y);
    }
}
