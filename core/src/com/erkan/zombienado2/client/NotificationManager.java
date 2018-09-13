package com.erkan.zombienado2.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.audio.Mp3;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by Erik on 2018-09-12.
 */
public class NotificationManager {
    private final static Sound sound_effect = Gdx.audio.newSound(Gdx.files.internal("audio/misc/notification_effect.mp3"));
    private final static Sound sound_close = Gdx.audio.newSound(Gdx.files.internal("audio/misc/notification_close.mp3"));
    private static BitmapFont font;
    private static String text = "";
    private static Color R = new Color(1f,0,0,.5f);
    private static Color G = new Color(1,1f,1,1f);
    private static Color B = new Color(0,0,1f,.5f);

    private static float elapsed;
    private static boolean closed = true;

    static float distortin_offset = 0;
    static float offset_x = 0;

    static void init(){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/notification_font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 52;
        font = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose();
    }

    static void post(String text){
        NotificationManager.text = text;
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(font, text);
        offset_x = glyphLayout.width/2;
        SoundManager.addPrioSound(sound_effect, sound_effect.play());
        closed = false;
        elapsed = 0;
    }

    static void draw(SpriteBatch batch, float w, float h){
        if (elapsed < 3f) {
            distortin_offset += MathUtils.random(-1f, 1f);
            distortin_offset = MathUtils.clamp(distortin_offset, 0, 6f);
            font.setColor(R);
            font.draw(batch, text, w - offset_x - distortin_offset, h + distortin_offset / 2);
            font.setColor(B);
            font.draw(batch, text, w - offset_x + distortin_offset, h - distortin_offset / 2);
            font.setColor(G);
            font.draw(batch, text, w - offset_x, h);
            elapsed += Gdx.graphics.getDeltaTime();
        } else if (!closed) {
            SoundManager.addPrioSound(sound_close, sound_close.play());
            closed = true;
        }
    }

}
