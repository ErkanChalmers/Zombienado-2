package com.erkan.zombienado2.server;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.erkan.zombienado2.data.world.physics.Component;
import com.erkan.zombienado2.data.world.physics.BoxData;
import com.erkan.zombienado2.server.misc.FilterConstants;

/**
 * Created by Erik on 2018-07-29.
 */
public class WorldManager {
    private static World world = null;

    public static void setWorld(World world, ContactListener cl){
        WorldManager.world = world;
        world.setContactListener(cl);
    }

    public static World getWorld(){
        return world;
    }

    public synchronized static Body createRect(float w, float h, Short... bits){
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w, h);

        FixtureDef fix = new FixtureDef();
        fix.shape = shape;

        Body body = world.createBody(def);
        body.createFixture(fix);

        shape.dispose();
        return body;
    }

    public synchronized static Body createCircle(float radius, Short categoryBits, Short maskBits){
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fix = new FixtureDef();
        fix.shape = shape;
        fix.filter.categoryBits = categoryBits;
        fix.filter.maskBits = maskBits;



        Body body = world.createBody(def);
        body.createFixture(fix);
        shape.dispose();
        return body;
    }

    public synchronized static void destroyBody(Body body){
        world.destroyBody(body);
    }

    public synchronized static void createPrefab(BoxData boxData){
        System.out.println(boxData.getComponentsList().size());
        boxData.getComponentsList().stream().forEach((componentAt -> {
            Component component = componentAt.getComponent();

            component.getBodies().stream().forEach(componentBody -> {
                BodyDef def = new BodyDef();
                def.type = BodyDef.BodyType.StaticBody;

                Shape shape;
                if (componentBody.getClass().equals(Component.RectangleBody.class)){
                    Component.RectangleBody rb =
                            ((Component.RectangleBody)componentBody);
                    shape = new PolygonShape();
                    ((PolygonShape)shape).setAsBox(rb.getWidth(), rb.getHeight());
                } else {
                    Component.CircularBody cb =
                            ((Component.CircularBody)componentBody);
                    shape = new CircleShape();
                    shape.setRadius(cb.getRadius());
                }

                FixtureDef fix = new FixtureDef();
                fix.shape = shape;
                fix.filter.categoryBits = FilterConstants.OBSTACLE_FIXTURE;
                fix.filter.maskBits = FilterConstants.ENEMY_FIXTURE | FilterConstants.PLAYER_FIXTURE | FilterConstants.PROJECTILE_FIXTURE;

                Body b = world.createBody(def);
                b.createFixture(fix);
                shape.dispose();
                b.setTransform(new Vector2(componentAt.getPosition().x + componentBody.getX(boxData.getR()), componentAt.getPosition().y + componentBody.getY(boxData.getR())), MathUtils.degreesToRadians * boxData.getR());

            });
        }));
    }
}
