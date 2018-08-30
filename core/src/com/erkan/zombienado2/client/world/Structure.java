package com.erkan.zombienado2.client.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.erkan.zombienado2.client.PhysicsHandler;
import com.erkan.zombienado2.data.world.physics.BoxData;
import com.erkan.zombienado2.data.world.physics.Component;
import com.erkan.zombienado2.graphics.Transform;
import com.badlogic.gdx.physics.box2d.*;
import com.erkan.zombienado2.server.misc.FilterConstants;

/**
 * Created by Erik on 2018-08-05.
 */
public class Structure {
    static final Material WOOD;
    static final Material STONE;
    static final Material BRICK;

    static {
        WOOD = new Material("world/build_materials/wood/");
        STONE = new Material("world/build_materials/stone/");
        BRICK = new Material("world/build_materials/brick/");
    }

    final float txt_width = Transform.to_screen_space(Component.LENGTH);

    BoxData bd;
    Material material;
    Sprite roof;
    boolean roof_hidden;
    float roof_alpha;

    public Structure(BoxData bd, BuildType bt){
        PhysicsHandler.createPrefab(bd);
        this.bd = bd;
       // material = WOOD;
        switch (bt){
            case WOOD:
                material = WOOD;
                break;
            case STONE:
                material = STONE;
                break;
            case BRICK:
                material = BRICK;
                break;
        }

        roof = new Sprite(STONE.floor);
        roof.setColor(new Color(0f, 0f, 0f, 1f));
        roof.setSize(Transform.to_screen_space(bd.getWidth())- txt_width/4, Transform.to_screen_space(bd.getHeight())- txt_width/4);
        roof.setCenter(Transform.to_screen_space(bd.getX()), Transform.to_screen_space(bd.getY()));
        roof.setOriginCenter();
        roof.setRotation(bd.getR());

        Body sensor = PhysicsHandler.createRect(roof.getWidth()/2, roof.getHeight()/2, BodyDef.BodyType.StaticBody, FilterConstants.ROOF_SENSOR,FilterConstants.ROOF_SENSOR, FilterConstants.PLAYER_FIXTURE);
        sensor.setTransform(Transform.to_screen_space(bd.getX()), Transform.to_screen_space(bd.getY()), bd.getR() * MathUtils.degreesToRadians);
        sensor.getFixtureList().get(0).setSensor(true);
        sensor.getFixtureList().get(0).setUserData(this);

    }

    public void hide_roof(){
        roof_hidden = true;
    }

    public void show_roof(){roof_hidden = false;
    }

        //TODO: refactor.. really, this is incredibly awkward
    public void render_floor(SpriteBatch batch) {
        final float len = txt_width/2;
        int width_step = (int)(Transform.to_screen_space(bd.getWidth())/len);
        int height_step = (int)(Transform.to_screen_space(bd.getHeight())/len);
        Vector2 offset = Transform.rotate(new Vector2(-width_step*len/2, -height_step*len/2), bd.getR());
        for (int x = 0; x < width_step; x++){
            for (int y = 0; y < height_step; y++){
                Vector2 step = Transform.rotate(new Vector2(x, y), bd.getR());
                Sprite sprite = new Sprite(material.floor);
                sprite.setSize(len, len);
                sprite.setOrigin(0, 0);

                sprite.setPosition(Transform.to_screen_space(bd.getX()) + len * step.x + offset.x, Transform.to_screen_space(bd.getY()) + len * step.y + offset.y);
                sprite.rotate(bd.getR());
                sprite.draw(batch);
            }
        }

    }

    public void render_walls(SpriteBatch batch){

        bd.getComponentsList().stream().forEach(componentAt -> {
            Vector2 pos = componentAt.getPosition();
            int id = Component.getID(componentAt.getComponent());
            if (id == -1)
                return;
            Sprite sprite = new Sprite(getTexture(id));
            sprite.setSize(txt_width, txt_width);
            sprite.setOrigin(txt_width/2, txt_width/2);
            sprite.setPosition(Transform.to_screen_space(pos.x) - txt_width/2, Transform.to_screen_space(pos.y) - txt_width/2);
            sprite.setRotation(bd.getR());
            sprite.draw(batch);
        });
    }

    public void render_roof(SpriteBatch batch){
        if (roof_hidden){
            roof_alpha -= Gdx.graphics.getDeltaTime();
        } else {
            roof_alpha += Gdx.graphics.getDeltaTime();
        }
        roof_alpha = MathUtils.clamp(roof_alpha, 0, 1f);


        roof.setAlpha(roof_alpha);
        roof.draw(batch);
    }

    Texture getTexture(int id){
        switch (id){
            case 1: return material.t;
            case 2: return material.r;
            case 3: return material.b;
            case 4: return material.l;
            case 5: return material.tl;
            case 6: return material.tr;
            case 7: return material.br;
            case 8: return material.bl;
        }
        return null;
    }


    public enum BuildType{
        WOOD, STONE_BRICK, STONE, BRICK
    }

    public static class Material {
        public final Texture t, r, b, l, tl, tr, br, bl, floor;
        public Material(String directory){
            t = new Texture(directory+"t.png");
            r = new Texture(directory+"r.png");
            b = new Texture(directory+"b.png");
            l = new Texture(directory+"l.png");
            tl = new Texture(directory+"tl.png");
            tr = new Texture(directory+"tr.png");
            br = new Texture(directory+"br.png");
            bl = new Texture(directory+"bl.png");
            floor = new Texture(directory+"floor.png");

            floor.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
    }
}
