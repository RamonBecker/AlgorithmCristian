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

public class Servidor extends Thread {
	private ServerSocket serverSocket;
	private String tempo0;
	private String tempo1;
	private String tempo2;
	
	public Servidor(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	
	@Override
	public void run() {
		informacaoServidor();
		
		while(true) {
			System.out.println("Aguardando requisições....");
		try (Socket cliente = serverSocket.accept()) {
			processarRequisicaoCliente(cliente);
		} catch (IOException e) {
			System.err.println(e.getLocalizedMessage());
			System.err.println(e.getMessage());
		}
		}

	}
	
	
	private void processarRequisicaoCliente(Socket cliente) {
		try {

			ObjectInputStream in = new ObjectInputStream(cliente.getInputStream());
			String mensagemCliente = in.readUTF();
			System.out.println("O cliente enviou:" + mensagemCliente);

			tempo0 = mensagemCliente;

			tempo1 = pegarHoraAtual();

			Thread.sleep(2000);

			tempo2 = pegarHoraAtual();

			String respostaCliente = tempo0 + "-" + tempo1 + "-" + tempo2;

			System.out.println("Enviando resposta para o cliente: "+respostaCliente);
			ObjectOutputStream out = new ObjectOutputStream(cliente.getOutputStream());

			out.writeUTF(respostaCliente);
			out.flush();
			in.close();
			out.close();

		} catch (IOException | InterruptedException e) {
			System.err.println(e.getLocalizedMessage());
			System.err.println(e.getMessage());

		}
	}


	private String pegarHoraAtual() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date hora = Calendar.getInstance().getTime(); // Ou qualquer outra forma que tem
		String dataFormatada = sdf.format(hora);
		return dataFormatada;
	}

	private void informacaoServidor() {
		try {
			System.out.println("-----------------------------------");
			System.out.println("Informações do servidor:");
			String hostname;

			hostname = InetAddress.getLocalHost().getHostName();

			System.out.println("Hostname: " + hostname);
			System.out.println("Porta do servidor:" + this.serverSocket.getLocalPort());
			System.out.println("O servidor está ouvindo:" + this.serverSocket.getLocalSocketAddress());
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
		}

	}


	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public String getTempo0() {
		return tempo0;
	}

	public void setTempo0(String tempo0) {
		this.tempo0 = tempo0;
	}

	public String getTempo1() {
		return tempo1;
	}

	public void setTempo1(String tempo1) {
		this.tempo1 = tempo1;
	}

	public String getTempo2() {
		return tempo2;
	}

	public void setTempo2(String tempo2) {
		this.tempo2 = tempo2;
	}

}
