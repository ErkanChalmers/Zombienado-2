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
    static Object lock = new Object();

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

                send(identifier, "CONNECT");
                send(identifier, "Identifier: "+identifier);
                cl.connect(identifier, newConnection);
                listen(newConnection, identifier);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void listen(Socket socket, int identifier){
        new Thread(()->{
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String msg = "";
                while ((msg = reader.readLine()) != null){
                    cl.onMsgReceive(identifier, msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
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
        synchronized (lock) {
            for (Socket socket : clients) {
                if (socket == null)
                    continue;
                try {
                    socket.getOutputStream().write(msg.getBytes());
                    socket.getOutputStream().flush();
                } catch (IOException e) {
                    reset(socket);
                    e.printStackTrace();
                }
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

    private static void reset(Socket socket){
        System.out.println("reset1");
            try {
                socket.getOutputStream().flush();
                socket.getOutputStream().close();
                socket.close();
            } catch (IOException ioex) {
                ioex.printStackTrace();
            }

            int identifier = clients.indexOf(socket);
            clients.set(identifier, null);

            new Thread(() -> {
                try {
                    System.out.println("awaiting reconnect...");
                    clients.set(identifier, ConnectionManager.socket.accept());
                    send(identifier, "RECONNECT");
                    send(identifier, "Identifier: " + identifier + "");
                    cl.reconnect(identifier, socket);
                    listen(clients.get(identifier), identifier);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

    }

    public static List<String> getConnections(){
        List<String> connections = new ArrayList<>();
        for (Socket socket: clients) {
            connections.add(socket.getRemoteSocketAddress().toString());
        }
        return connections;
    }
}
