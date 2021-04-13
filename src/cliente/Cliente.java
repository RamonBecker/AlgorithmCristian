package cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Cliente {

	private int porta;
	private DateTimeFormatter dtfPadrao = DateTimeFormat.forPattern("HH:mm:ss");
	private DateTime dataDefasada;

	public void enviar() throws NoSuchAlgorithmException, NoSuchProviderException, InterruptedException {
		try (Socket socket = new Socket("localhost", this.porta)) {

			System.out.println("Enviando mensagem para o servidor");
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeUTF(gerarHoraDefasada());
			out.flush();

			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			String respostaServidor = in.readUTF();

			System.out.println("Resposta servidor:" + respostaServidor);
			processarRespostaServidor(respostaServidor);
			in.close();
			out.close();

		} catch (IOException e) {
			System.err.println(e.getLocalizedMessage());
			System.out.println(e.getMessage());
		}
	}

	private void processarRespostaServidor(String respostaServidor) {
		String[] datas = respostaServidor.split("-");

		int diaMes = dataDefasada.getDayOfMonth();
		int mes = dataDefasada.getMonthOfYear();
		int ano = dataDefasada.getYear();

		List<DateTime> aux_datas = new ArrayList<DateTime>();
		for (int i = 0; i < datas.length; i++) {
			String string = datas[i];
			System.out.println("Tempo " + i + " :" + string);
			String[] horarios = string.split(":");
			aux_datas.add(new DateTime(ano, mes, diaMes, Integer.parseInt(horarios[0]), Integer.parseInt(horarios[1]),
					Integer.parseInt(horarios[2])));
		}

		DateTime acrescentando_segundos = dataDefasada.plusSeconds(5);
		System.out.println("Tempo 3 :" + acrescentando_segundos.toString(dtfPadrao) + "\n");

		Interval intervaloT1T0 = new Interval(aux_datas.get(0), aux_datas.get(1));

		Interval intervaloT2T3 = new Interval(acrescentando_segundos, aux_datas.get(2));

		Duration duracaoT1T0 = intervaloT1T0.toDuration();

		Duration duracaoT2T3 = intervaloT2T3.toDuration();
		
		
		long aux_resultado_duracaoT1T0 = duracaoT1T0.getStandardSeconds();
		long aux_resultado_duracaoT2T3 = duracaoT2T3.getStandardSeconds();
		System.out.println("Diferença dos tempos 0 e 1: " + aux_resultado_duracaoT1T0 + " segundos");
		System.out.println("Diferença dos tempos 2 e 3: " + aux_resultado_duracaoT2T3 + " segundos");
		
		long calculo_final = (aux_resultado_duracaoT1T0 + aux_resultado_duracaoT2T3) / 2;
		
		System.out.println("Tempo de sincronização: "+calculo_final+" segundos");
	}

	private String gerarHoraDefasada() {
		Random random = new Random();
		int aux_segundos_gerado = random.nextInt(59);

		DateTime dataAtual = new DateTime();
		dataDefasada = dataAtual.minusSeconds(aux_segundos_gerado);
		System.out.println("Horário atual:" + dataAtual.toString(dtfPadrao));

		String conversao_data_string = dataDefasada.toString(dtfPadrao);

		System.out.println("Defasagem de horário:" + conversao_data_string);

		return conversao_data_string;

	}

	public int getPorta() {
		return porta;
	}

	public void setPorta(int porta) {
		this.porta = porta;
	}

}
