package com.erkan.zombienado2.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.erkan.zombienado2.client.Client;
import com.erkan.zombienado2.server.Server;

public class DesktopLauncher {
	public static void main (String[] arg) {



		System.out.println("Launcher: creating client");
		LwjglApplicationConfiguration client_configuration = new LwjglApplicationConfiguration();
		client_configuration.title = "Zombienado 2";
		client_configuration.addIcon("ui/icon.png", Files.FileType.Internal);
		//client_configuration.width = 1250;
		//client_configuration.height = 720;
		client_configuration.fullscreen = true;
		client_configuration.width = 2560;
		client_configuration.height = 1440;
		client_configuration.samples = 3;
		//"192.168.1.196"
		new LwjglApplication(new Client(), client_configuration);

	}

}
