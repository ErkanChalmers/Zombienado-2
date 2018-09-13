package com.erkan.zombienado2.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.erkan.zombienado2.graphics.Transform;

/**
 * Created by Erik on 2018-07-30.
 */
public class TeamMate extends Player {

    public TeamMate(String name, Character character){
        super(name, character);
    }

    float elapsed_step = 0;
    @Override
    public void run(Vector2 dir){
        super.run(dir);

        float dx = position.x - Client.camera_world_coordinates.x;
        float dy = position.y - Client.camera_world_coordinates.y;
        Vector2 vec = new Vector2(dx, dy);
        float distance2 = vec.len2();
        float tmp = (100f - distance2)/100f;

        float vol = (float)Math.max(0.1f, tmp);
        float pan = vec.setLength(1f).x;


        if (dir.len() != 0 && elapsed_step > .25f) {
            int i = MathUtils.random(walk_sound.length-1);
            SoundManager.addSound(walk_sound[i], walk_sound[i].play(vol, 1, pan));
            elapsed_step = 0;
        }
        elapsed_step+= Gdx.graphics.getDeltaTime();
    }
    @Override
    public void shoot(){
        super.shoot();

        float dx = position.x - Client.camera_world_coordinates.x;
        float dy = position.y - Client.camera_world_coordinates.y;
        Vector2 vec = new Vector2(dx, dy);
        float distance2 = vec.len2();
        float tmp = (100f - distance2)/100f;

        float vol = (float)Math.max(0.1f, tmp);
        float pan = vec.setLength(1f).x;

        SoundManager.addSound(getWeapon().getSound(), getWeapon().getSound().play(vol, 1, pan));
    }

    @Override
    public void reload(){
        super.reload();
        float dx = position.x - Client.camera_world_coordinates.x;
        float dy = position.y - Client.camera_world_coordinates.y;
        Vector2 vec = new Vector2(dx, dy);
        float distance2 = vec.len2();
        float tmp = (100f - distance2)/100f;

        float vol = (float)Math.max(0.1f, tmp);
        float pan = vec.setLength(1f).x;
        SoundManager.addSound(getWeapon().getReloadSound(), getWeapon().getReloadSound().play(vol, 1, pan));
    }

    public void hud_draw(SpriteBatch batch, BitmapFont font){
        GlyphLayout gl = new GlyphLayout(font, getName());
        Vector2 screen_pos = Transform.to_screen_space(new Vector2(Client.camera_world_coordinates.x, Client.camera_world_coordinates.y));

        Vector2 draw_pos = new Vector2(- gl.width/2 + (Transform.to_screen_space(super.position.x)- screen_pos.x)/(Client.camera_zoom) + Gdx.graphics.getWidth()/2,  35 + (Transform.to_screen_space(super.position.y)- screen_pos.y )/(Client.camera_zoom) +  Gdx.graphics.getHeight()/2);
        draw_pos.x = MathUtils.clamp(draw_pos.x, 20, Gdx.graphics.getWidth() - 20 - gl.width);
        draw_pos.y = MathUtils.clamp(draw_pos.y, 20, Gdx.graphics.getHeight() - 20);

        font.draw(batch, getName(), draw_pos.x, draw_pos.y);
    }
}
