package main;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;


import cliente.Cliente;

public class MainCliente {
	public static void main(String[] args) {
		try {
			Cliente cliente = new Cliente();
			cliente.setPorta(2800);
			cliente.enviar();
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
