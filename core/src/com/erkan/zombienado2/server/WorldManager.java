package com.erkan.zombienado2.server;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.erkan.zombienado2.data.world.Boundary;
import com.erkan.zombienado2.data.world.physics.Component;
import com.erkan.zombienado2.data.world.physics.BoxData;
import com.erkan.zombienado2.data.world.physics.StaticRectangle;
import com.erkan.zombienado2.graphics.*;
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

    public synchronized static Body createRect(StaticRectangle rectangle){
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(rectangle.getW()/2, rectangle.getH()/2);

        FixtureDef fix = new FixtureDef();
        fix.shape = shape;
        fix.filter.categoryBits = FilterConstants.OBSTACLE_FIXTURE;
        fix.filter.maskBits = FilterConstants.PLAYER_FIXTURE | FilterConstants.PROJECTILE_FIXTURE | FilterConstants.ENEMY_FIXTURE;

        Body body = world.createBody(def);
        body.createFixture(fix);
        body.setTransform(rectangle.getX(), rectangle.getY(), MathUtils.degreesToRadians * (rectangle.getR()+90));
        System.out.println(rectangle.getX() +" "+rectangle.getY());
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

    public synchronized static void createWall(Boundary boundary){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        PolygonShape shape = new PolygonShape();
        float len = Math.abs(boundary.getStart().cpy().sub(boundary.getEnd().cpy()).len());
        shape.setAsBox(len/2, boundary.getWidth()/2);
        FixtureDef fix = new FixtureDef();
        fix.shape = shape;
        fix.filter.categoryBits = FilterConstants.OBSTACLE_FIXTURE;
        fix.filter.maskBits = FilterConstants.ENEMY_FIXTURE | FilterConstants.PLAYER_FIXTURE | FilterConstants.PROJECTILE_FIXTURE;
        Body b = world.createBody(bodyDef);
        b.createFixture(fix);
        shape.dispose();

        float x = boundary.getStart().x + (boundary.getEnd().x - boundary.getStart().x) /2f;
        float y = boundary.getStart().y + (boundary.getEnd().y - boundary.getStart().y)  / 2f;


        float rot = boundary.getEnd().cpy().sub(boundary.getStart().cpy()).angleRad();

        b.setTransform(x, y, rot);

    }

    public synchronized static void createPrefab(BoxData boxData){
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
