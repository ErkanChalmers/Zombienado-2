package com.erkan.zombienado2.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.erkan.zombienado2.client.Client;
import com.erkan.zombienado2.server.Server;

public class DesktopLauncher {
	public static void main (String[] arg) {
/*
		System.out.println("Launcher: creating server");
		new Thread(()-> {
			new Server(9021, 1);
		}).start();
*/
		System.out.println("Launcher: creating client");
		LwjglApplicationConfiguration client_configuration = new LwjglApplicationConfiguration();
		client_configuration.title = "Zombienado 2";
		client_configuration.width = 1920;
		client_configuration.height = 1080;
		client_configuration.samples = 3;
		//"192.168.1.196"
		new LwjglApplication(new Client("192.168.1.168", 9021), client_configuration);

	}

}
