package com.erkan.zombienado2.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by Erik on 2018-09-12.
 */
public class NotificationManager {
    private final static Sound sound_effect = Gdx.audio.newSound(Gdx.files.internal("audio/misc/notification_effect.mp3"));
    private final static Sound sound_close = Gdx.audio.newSound(Gdx.files.internal("audio/misc/notification_close.mp3"));
    private static BitmapFont font;
    private static BitmapFont notification_font;
    private static String text = "";
    private static Color R = new Color(1f,.1f, .5f,.5f);
    private static Color G = new Color(1,1f,1,.8f);
    private static Color B = new Color(0f,.7f,1f,.5f);

    private static float elapsed;
    private static float elapsed_queue;
    private static boolean closed = true;

    private static Queue<String> notification_queue = new ArrayDeque<>();

    static float distortion_offset = 0;
    static float offset_x = 0;

    static void init(){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/notification_font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 52;
        font = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose();

        notification_font = new BitmapFont();
    }

    static void post(String text){
        NotificationManager.text = text;
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(font, text);
        offset_x = glyphLayout.width/2;
        SoundManager.playNonInterrupt(sound_effect);
        closed = false;
        elapsed = 0;
    }

    static void push_notification(String string){
        notification_queue.add(string);
        elapsed_queue = 0;
    }

    static void draw(SpriteBatch batch, float w, float h){
        if (elapsed < 3f) {
            distortion_offset += MathUtils.random(-.3f, .3f);
            distortion_offset = MathUtils.clamp(distortion_offset, 0, 6f);
            font.setColor(R);
            font.draw(batch, text, w - offset_x - distortion_offset, h + distortion_offset / 2);
            font.setColor(B);
            font.draw(batch, text, w - offset_x + distortion_offset, h - distortion_offset / 2);
            font.setColor(G);
            font.draw(batch, text, w - offset_x, h);
            elapsed += Gdx.graphics.getDeltaTime();
        } else if (!closed) {
            SoundManager.playNonInterrupt(sound_close);
            closed = true;
        }

        Color color = new Color(1, 1, 1, (3f-elapsed_queue)/3);

        for (int i = 0; i < notification_queue.size(); i++) {
            String text = (String)notification_queue.toArray()[i];
            GlyphLayout glyphLayout = new GlyphLayout();
            glyphLayout.setText(notification_font, text);
            notification_font.setColor(color);
            notification_font.draw(batch, text, 10, Gdx.graphics.getHeight() - 10 - i * 20);
            color.a = MathUtils.clamp(color.a - .2f, 0, 1);
        }

        if (elapsed_queue > 3f && notification_queue.size() > 0){
            elapsed_queue = 0;
            notification_queue.clear();
        }

        elapsed_queue+= Gdx.graphics.getDeltaTime();
    }

}
