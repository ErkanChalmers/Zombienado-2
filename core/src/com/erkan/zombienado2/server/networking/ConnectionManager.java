package com.erkan.zombienado2.server.networking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import java.net.Socket;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Erik on 2018-07-29.
 */
public class ConnectionManager {
    static ConnectionListener cl = null;
    static ServerSocket socket;
    static List<Socket> clients = new ArrayList<>();

    public static void init(ConnectionListener listener, final int PORT) {
        cl = listener;
        try {
            socket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void accept(int amount){
        if (cl == null)
            throw new IllegalStateException("Connection Manager not initialized");
        int connected = 0;
        while (connected < amount){
            try {
                Socket newConnection = socket.accept();
                clients.add(newConnection);

                System.out.println(newConnection.getRemoteSocketAddress() + " connected with status "+newConnection.isConnected());

                int identifier = connected;
                connected++;

                send(identifier, "Identifier: "+identifier);

                new Thread(()->{
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(newConnection.getInputStream()));
                        String msg = "";
                        while ((msg = reader.readLine()) != null){
                            cl.onMsgReceive(identifier, msg);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void broadcast(Object... args) {
        StringBuilder sb = new StringBuilder();
        for (Object arg: args) {
            sb.append(arg).append(" ");
        }
        broadcast(sb.toString());
    }

    public static void broadcast(String msg){
        msg = msg +"\n";
        for (Socket socket:clients) {
            try {
                socket.getOutputStream().write(msg.getBytes());
                socket.getOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void send(int identifier, String msg){
        msg = msg +"\n";
        try {
            clients.get(identifier).getOutputStream().write(msg.getBytes());
            clients.get(identifier).getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
