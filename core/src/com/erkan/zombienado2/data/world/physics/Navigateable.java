package com.erkan.zombienado2.data.world.physics;

import com.badlogic.gdx.math.Vector2;

import java.util.List;

/**
 * Created by Erik on 2018-09-05.
 */
public interface Navigateable {
    void addNavVector(Vector2 vec);
    List<Vector2> getNavVectors();
}
