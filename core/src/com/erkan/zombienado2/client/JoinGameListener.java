package com.erkan.zombienado2.client;

/**
 * Created by Erik on 2018-08-16.
 */
public interface JoinGameListener {
    void join(final String IP,final int PORT);
    void ready(final String name, final String character);
}
