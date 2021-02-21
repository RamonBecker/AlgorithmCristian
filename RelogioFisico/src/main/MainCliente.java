package main;

import cliente.Cliente;

public class MainCliente {

	public static void main(String[] args) {
		Cliente cliente = new Cliente();
		cliente.setPorta(2800);
		cliente.enviar();

	}

}
