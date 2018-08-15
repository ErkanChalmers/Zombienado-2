package com.erkan.zombienado2.client;

import com.badlogic.gdx.audio.Sound;
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
    static HashMap<Sound, List<Long>> sounds = new HashMap<>();
    static Queue<Tuple<Sound, Long>> queue = new LinkedList<>();

    static synchronized void addSound(Sound sound, Long id){
        queue.add(new Tuple<>(sound, id));
        if (queue.size() > 12){
            Tuple<Sound, Long> pair = queue.poll();
            pair.getFirst().stop(pair.getSecond());
        }
    }
}
