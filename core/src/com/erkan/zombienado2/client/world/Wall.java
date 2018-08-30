package com.erkan.zombienado2.client.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.erkan.zombienado2.client.PhysicsHandler;
import com.erkan.zombienado2.data.world.Boundary;
import com.erkan.zombienado2.graphics.Transform;

import java.awt.*;

/**
 * Created by Erik on 2018-08-16.
 */
public class Wall {
    public static Texture barbed_wire;
    public static Texture wood_fence;
    static void init() {
        barbed_wire = new Texture("world/objects/barbedwire.png");
        wood_fence = new Texture("world/objects/woodfence.png");
    }

    private Texture thisTexture;
    private Boundary boundary;
    float x1, y1, x2, y2, dx, dy;

    public Wall(Boundary boundary, Type type){
        x1 = boundary.getStart().x;
        y1 = boundary.getStart().y;
        x2 = boundary.getEnd().x;
        y2 = boundary.getEnd().y;
        dx = x2-x1;
        dy = y2-y1;
        switch (type){
            case BARBED_WIRE:
                thisTexture = barbed_wire;
                boundary.setSeeThrough(true);
                break;
            case WOOD_FENCE:
                thisTexture = wood_fence;
                break;
        }
        PhysicsHandler.createWall(boundary);

    }

    public void render(SpriteBatch batch){
        Vector2 vector = Transform.to_screen_space(new Vector2(dx, dy));
        float total_length = vector.len();
        vector.setLength(64f); //component width
        Sprite sprite = new Sprite(thisTexture);
        for (float i = 0; i <= total_length/64f; i++){
            sprite.setCenter(Transform.to_screen_space(x1) + vector.x * i, Transform.to_screen_space(y1) + vector.y *i);;
            sprite.setRotation(vector.angle());
            sprite.draw(batch);
        }
    }

    public enum Type {
        BARBED_WIRE, WOOD_FENCE
    }
}
