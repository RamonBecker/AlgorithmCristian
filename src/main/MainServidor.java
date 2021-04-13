package main;

import java.io.IOException;
import java.net.ServerSocket;
import servidor.Servidor;

public class MainServidor {

	public static void main(String[] args) {


		try {
			Servidor server = new Servidor(new ServerSocket(2800));
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
