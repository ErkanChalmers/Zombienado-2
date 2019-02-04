package com.erkan.zombienado2.client.menus;

import com.erkan.zombienado2.data.world.Tuple;

/**
 * Created by Erik on 2018-08-16.
 */
public interface JoinGameListener {
    Tuple<Boolean, String> join(final String IP, final int PORT);
    void ready(final String name, final String character);
}
