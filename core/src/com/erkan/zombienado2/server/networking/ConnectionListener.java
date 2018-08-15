package com.erkan.zombienado2.server.networking;

/**
 * Created by Erik on 2018-07-29.
 */
public interface ConnectionListener {
    void onMsgReceive(int identifier, String msg);
}
