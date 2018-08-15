package com.erkan.zombienado2.client.networking;

/**
 * Created by Erik on 2018-07-30.
 */
public interface ConnectionListener {
    void onMsgReceived(String... args);
}
