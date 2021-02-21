package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Server extends Thread {
	private Integer idServer;
	private ServerSocket serverSocket;
	private String t0;
	private String t1;
	private String t2;

	public Server() {
	}

	public Server(ServerSocket serverSocket) {
		if (serverSocket == null) {
			throw new IllegalArgumentException("O server socket n√£o pode ser nulo");
		}
		this.serverSocket = serverSocket;
	}

	private void printServerInfo() {
		try {
			System.out.println("-----------------------------------");
			System.out.println("InformaÁıes do servidor:");
			String hostname;

			hostname = InetAddress.getLocalHost().getHostName();

			System.out.println("Hostname: " + hostname);
			System.out.println("Porta do servidor:" + this.serverSocket.getLocalPort());
			System.out.println("O servidor est· ouvindo:" + this.serverSocket.getLocalSocketAddress());
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
		}

	}

	private void acceptRequestClient() {
		try (Socket client = serverSocket.accept()) {
			processRequestClient(client);
		} catch (IOException e) {
			System.err.println(e.getLocalizedMessage());
			System.err.println(e.getMessage());
		}
	}

	private void processRequestClient(Socket client) {

		try {

			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			String mensagemCliente = in.readUTF();
			System.out.println("Cliente enviou:" + mensagemCliente);

			t0 = mensagemCliente;

			t1 = horaAtual();

			Thread.sleep(1564);

			t2 = horaAtual();

			String respostaCliente = t0 + "-" + t1 + "-" + t2;

			ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());

			out.writeUTF(respostaCliente);
			out.flush();
			in.close();
			out.close();

		} catch (IOException | InterruptedException e) {
			System.err.println(e.getLocalizedMessage());
			System.err.println(e.getMessage());

		}
	}

	private String horaAtual() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date hora = Calendar.getInstance().getTime(); // Ou qualquer outra forma que tem
		String dataFormatada = sdf.format(hora);
		return dataFormatada;
	}

	@Override
	public void run() {
		printServerInfo();
		while (true) {
			acceptRequestClient();
		}
	}

	public ServerSocket getServerSocket() {
		if (serverSocket == null) {
			throw new IllegalArgumentException("O server socket n√£o pode ser nulo");
		}
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		if (serverSocket == null) {
			throw new IllegalArgumentException("O server socket n√£o pode ser nulo");
		}
		this.serverSocket = serverSocket;
	}

	public Integer getIdServer() {
		return idServer;
	}

	public void setIdServer(Integer idServer) {
		if (idServer <= 0) {
			throw new IllegalArgumentException("O id n√£o pode ser zero ou negativo. ID: " + idServer);
		}
		this.idServer = idServer;
	}

}
