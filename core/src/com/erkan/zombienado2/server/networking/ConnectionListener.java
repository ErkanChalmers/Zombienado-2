package com.erkan.zombienado2.server.networking;

import java.net.Socket;

/**
 * Created by Erik on 2018-07-29.
 */
public interface ConnectionListener {
    void onMsgReceive(int identifier, String msg);
    void connect(int identifier, Socket socket);
    void reconnect(int identifier, Socket socket);
}
