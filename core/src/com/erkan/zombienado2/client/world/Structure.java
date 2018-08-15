package com.erkan.zombienado2.client.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.erkan.zombienado2.data.world.physics.BoxData;
import com.erkan.zombienado2.data.world.physics.Component;
import com.erkan.zombienado2.graphics.Transform;

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
    public Structure(BoxData bd, BuildType bt){
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
