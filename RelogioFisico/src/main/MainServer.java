package main;

import java.io.IOException;
import java.net.ServerSocket;

import servidor.Server;

public class MainServer {

	public static void main(String[] args) {

		try {
			Server server = new Server(new ServerSocket(2800));
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
