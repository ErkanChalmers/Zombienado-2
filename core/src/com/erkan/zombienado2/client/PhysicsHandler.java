package com.erkan.zombienado2.client;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.erkan.zombienado2.data.world.physics.Component;
import com.erkan.zombienado2.data.world.physics.BoxData;
import com.erkan.zombienado2.graphics.Transform;
import com.erkan.zombienado2.server.misc.FilterConstants;


/**
 * Created by Erik on 2018-08-04.
 */
public class PhysicsHandler {
    private static RayHandler rayHandler;
    private static World world;

    public static void init(){
        Box2D.init();
        world = new World(new Vector2(0, 0), false);
        rayHandler = new RayHandler(world, Gdx.graphics.getWidth()/8, Gdx.graphics.getHeight()/8);

        rayHandler.setAmbientLight(.01f);
        //rayHandler.setBlurNum(2);
        RayHandler.useDiffuseLight(false);
        Light.setGlobalContactFilter(FilterConstants.LIGHT, FilterConstants.LIGHT, (short)~FilterConstants.PHYSICS_FIXTURE);
        //rayHandler.setAmbientLight(0.0f, 0, 0, 1f);

    }

    public static World getWorld(){
        return world;
    }
    public static RayHandler getRayHandler(){ return rayHandler; }

    final static float STEP_TIME = 1f/60f;
    static float accumulator = 0;

    public static void update() {
        float delta = Gdx.graphics.getDeltaTime();
        accumulator += Math.min(delta, 0.25f);
        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;
            getWorld().step(1f / 60f, 6, 2);
        }
    }

    public synchronized static void destroyBody(Body body){
        world.destroyBody(body);
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

    public static Body createCircle(float radius, Short categoryBits, Short maskBits){
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

    public static void createPrefab(BoxData boxData){
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
                    ((PolygonShape)shape).setAsBox(Transform.to_screen_space(rb.getWidth()), Transform.to_screen_space(rb.getHeight()));
                   // System.out.println(Transform.to_screen_space(rb.getWidth())+ " " + Transform.to_screen_space(rb.getHeight()));
                } else {
                    Component.CircularBody cb =
                            ((Component.CircularBody)componentBody);
                    shape = new CircleShape();
                    shape.setRadius(cb.getRadius());
                }

                FixtureDef fix = new FixtureDef();
                fix.filter.categoryBits = FilterConstants.OBSTACLE_FIXTURE;
                fix.filter.maskBits = FilterConstants.PHYSICS_FIXTURE | FilterConstants.LIGHT |FilterConstants.TOP_LIGHT;
                fix.shape = shape;

                Body b = world.createBody(def);
                b.createFixture(fix);
                shape.dispose();
                b.setTransform(Transform.to_screen_space(new Vector2(componentAt.getPosition().x + componentBody.getX(boxData.getR()), componentAt.getPosition().y +  componentBody.getY(boxData.getR()))), MathUtils.degreesToRadians * boxData.getR());
               // System.out.println("added: "+b.getPosition());
            });
        }));
    }


    public static PointLight createPointLight(float x, float y, Color color, float distance){
        return new PointLight(rayHandler, 200, color, distance, x, y);
    }

    public static ConeLight createConeLight(float x, float y, Color color, float distance, float direction_degree, float cone_degree){
        return new ConeLight(rayHandler, 200 , color, distance, x, y, direction_degree, cone_degree);
    }
}
