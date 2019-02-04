package com.erkan.zombienado2.client;

import com.badlogic.gdx.audio.Sound;
import com.erkan.zombienado2.client.world.SoundSource;
import com.erkan.zombienado2.data.world.Tuple;

import java.util.*;

/**
 * Created by Erik on 2018-08-07.
 */

/**
 * Most basic and retarded solution i could come to think of.
 * Background: If >16 sounds are played concurrently, shit happens to crash... so i need to stop the damn sounds.
 */
public class SoundManager {
    static SoundSource queued = null;

    static Queue<Tuple<Sound, Long>> queue = new LinkedList<>();
    static List<Tuple<Sound, Long>> list = new LinkedList<>();
    static float volume = 1;


    static synchronized void playSound(Sound sound){
        playSound(sound, 1, 1, 0);
    }

    static synchronized void playSound(Sound sound, float volume, float pitch, float pan){
        long id = sound.play(volume * SoundManager.volume, pitch, pan);
        queue.add(new Tuple<>(sound, id));
        if (queue.size() > 12){
            Tuple<Sound, Long> pair = queue.poll();
            pair.getFirst().stop(pair.getSecond());
        }
    }

    static synchronized void playNonInterrupt(Sound sound){
        long id = sound.play(); // fix stuff
        list.add(new Tuple<>(sound, id));
    }

    public static float getVolume(){
        return volume;
    }

    public static void setVolume(float volume){
        SoundManager.volume = volume;
        queue.stream().forEach(t -> t.getFirst().setVolume(t.getSecond(), volume));
        list.stream().forEach(t -> t.getFirst().setVolume(t.getSecond(), volume));
    }

    public static void queueSound(SoundSource soundsource){
        queued = soundsource;
    }

    //TODO:
    public static void playQueued(){
        if (queued != null){
            playNonInterrupt(queued.getSound());
        }
    }
}
