package com.erkan.zombienado2.client.networking;

import com.badlogic.gdx.math.Vector2;
import com.erkan.zombienado2.data.weapons.WeaponData;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Erik on 2018-07-29.
 */
public class ServerProxy {
    private static BufferedReader reader;
    private static OutputStream out;

    private static ConnectionListener cl;

    public static void addListener(ConnectionListener cl){
        ServerProxy.cl = cl;
    }

    public static void connect(final String IP, final int PORT){
        try {
            Socket socket = new Socket(InetAddress.getByName(IP), PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = socket.getOutputStream();
            new Thread(() -> {
                String msg;
                try {
                    while ((msg = reader.readLine()) != null) {
                        onMsgReceived(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void onMsgReceived(String msg){
        String[] args = msg.split(" ");
        cl.onMsgReceived(args);
    }

    public static void send(String msg){
        msg = msg +"\n";
        try {
            out.write(msg.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ping(){
        send("ping");
    }
    public static void join(String name, String character){
        send("create "+name+" "+character);
    }
    public static void move(Vector2 movementvector){
        send("move "+movementvector.x + " " + movementvector.y);
    }

    public static void rotate(float rot){
        send("rotate "+ rot);
    }

    public static void fire(){
        send("fire");
    }
    public static void reload(){
        send("reload");
    }
    public static void switch_weapon(){
        send("switch_weapon");
    }
}
