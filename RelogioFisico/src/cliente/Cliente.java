package cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Cliente {

	private String hora;
	private String minutos;
	private String segundos;
	private SecureRandom secureRandom;
	private int porta;
	private String t3;
	private String[] datas;
	private HashMap<Integer, List<String>> datasSeparadas;
	private List<String> lista_datas;
	private List<String> lista_tempos_t1_t0;
	private List<String> lista_tempos_t2_t3;

	public Cliente() {
		lista_datas = new ArrayList<String>();
		lista_tempos_t1_t0 = new ArrayList<String>();
		lista_tempos_t2_t3 = new ArrayList<String>();
	}

	private static final DateTimeFormatter FORMATO_HORAS = DateTimeFormatter.ofPattern("HH:mm:ss")
			.withResolverStyle(ResolverStyle.STRICT);

	private static LocalTime faltando(LocalTime agora, LocalTime desejada) {
		return desejada.minusHours(agora.getHour()).minusMinutes(agora.getMinute()).minusSeconds(agora.getSecond());
	}

	private LocalTime mostrar(LocalTime horario, String objetivo, String aux1, String aux2) {
		LocalTime desejada = LocalTime.parse(objetivo, FORMATO_HORAS);
		LocalTime falta = faltando(horario, desejada);
		System.out.println("Diferença de " + aux2 + " e " + aux1 + " = " + falta);
		return falta;
	}

	private void calcularTempo() {

		String tempo0 = lista_datas.get(0);
		String tempo1 = lista_datas.get(1);
		String tempo2 = lista_datas.get(2);
		String tempo3 = lista_datas.get(3);

		LocalTime lt0 = LocalTime.parse(tempo0);
		LocalTime lt3 = LocalTime.parse(tempo3);

		LocalTime diferenca_t1_t0 = mostrar(lt0, tempo1, tempo1, tempo0);
		LocalTime diferenca_t2_t3 = mostrar(lt3, tempo2, tempo2, tempo3);

		String aux_diferenca_t1_t0 = String.valueOf(diferenca_t1_t0);
		String aux_diferenca_t2_t3 = String.valueOf(diferenca_t2_t3);

		String[] datas_t1_t0 = aux_diferenca_t1_t0.split(":");
		String[] datas_t2_t3 = aux_diferenca_t2_t3.split(":");

		for (int i = 0; i < datas_t1_t0.length; i++) {
			String data = datas_t1_t0[i];

			lista_tempos_t1_t0.add(data);

		}

		for (int i = 0; i < datas_t2_t3.length; i++) {
			String data = datas_t2_t3[i];
			lista_tempos_t2_t3.add(data);

		}

		int soma_segudos = Integer.parseInt(lista_tempos_t1_t0.get(2)) + Integer.parseInt(lista_tempos_t2_t3.get(2));

		int soma_minutos = Integer.parseInt(lista_tempos_t1_t0.get(1)) + Integer.parseInt(lista_tempos_t2_t3.get(1));
		int soma_horas = Integer.parseInt(lista_tempos_t1_t0.get(0)) + Integer.parseInt(lista_tempos_t2_t3.get(0));

		String aux_horario_defasagem = "";
		if (soma_segudos >= 60) {
			int aux_calc_segundos = soma_segudos - 60;
			soma_minutos++;
			int aux_calc_minutos = soma_minutos;
			if (soma_minutos >= 60) {
				aux_calc_minutos = aux_calc_minutos - 60;
				soma_horas++;
			}

			if (aux_calc_segundos != 0) {
				aux_calc_segundos = aux_calc_segundos / 2;

			}

			if (aux_calc_minutos != 0) {
				aux_calc_minutos = aux_calc_minutos / 2;

			}
			if (soma_horas != 0) {
				soma_horas = soma_horas / 2;

			}

			aux_horario_defasagem = soma_horas + " horas e " + aux_calc_minutos + " minutos e " + aux_calc_segundos
					+ " segundos";
		} else {

			if (soma_segudos != 0) {
				soma_segudos = soma_segudos / 2;
			}
			if (soma_minutos != 0) {
				soma_minutos = soma_minutos / 2;
			}
			if (soma_horas != 0) {
				soma_horas = soma_horas / 2;

			}
			aux_horario_defasagem = soma_horas + " horas e " + soma_minutos + " minutos e " + soma_segudos
					+ " segundos";

		}

		System.out.println("A defasagem é de:" + aux_horario_defasagem);
	}

	private String horaAtual() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date hora = Calendar.getInstance().getTime();
		String dataFormatada = sdf.format(hora);
		return dataFormatada;
	}

	private String gerarHora() throws NoSuchAlgorithmException, NoSuchProviderException {
		secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
		String dataFormatada = horaAtual();
		System.out.println("Hora atual:" + dataFormatada);

		int aux_gerador_segundos = secureRandom.nextInt(59);
		System.out.println("\n");
		String[] hora_cortada = dataFormatada.split(":");
		this.hora = hora_cortada[0];
		this.minutos = hora_cortada[1];
		this.segundos = hora_cortada[2];

		System.out.println("Segundo gerado:" + aux_gerador_segundos);

		int verificacao_conta_negativo = Integer.parseInt(this.segundos) - aux_gerador_segundos;
		System.out.println("Resultado desconto:" + verificacao_conta_negativo);
		String aux_hora = "";

		if (verificacao_conta_negativo < 0) {

			int contaSegundos = 60 - (-verificacao_conta_negativo);
			int contaMinutos = Integer.parseInt(this.minutos) - 1;
			int contaHora = Integer.parseInt(this.hora);

			if (contaMinutos < 0) {
				contaHora = Integer.parseInt(hora_cortada[0]) - 1;
				aux_hora = contaHora + ":" + "59";
			} else {

				if (contaMinutos < 10) {
					aux_hora = this.hora + ":" + "0" + String.valueOf(contaMinutos);
				} else {
					aux_hora = this.hora + ":" + contaMinutos;

				}

			}

			String aux_conta_segundos = String.valueOf(contaSegundos);

			if (contaSegundos < 10) {
				aux_conta_segundos = "0" + contaSegundos;
			}

			aux_hora += ":" + aux_conta_segundos;

		} else {

			String aux_segundo = "";
			if (verificacao_conta_negativo < 10) {
				aux_segundo = "0" + String.valueOf(verificacao_conta_negativo);
				aux_hora = this.hora + ":" + this.minutos + ":" + aux_segundo;
			} else {
				aux_hora = this.hora + ":" + this.minutos + ":" + verificacao_conta_negativo;
			}

		}

		System.out.println("Hora descontada:" + aux_hora);
		return aux_hora;
	}

	public void enviar() {
		try (Socket socket = new Socket("localhost", this.porta)) {

			System.out.println("Enviando mensagem para o servidor");
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeUTF(gerarHora());
			out.flush();

			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			String responseServer = in.readUTF();

			in.close();
			out.close();
			processarRespostaServidor(responseServer);

		} catch (IOException | NoSuchAlgorithmException | NoSuchProviderException | InterruptedException e) {
			System.err.println(e.getLocalizedMessage());
			System.out.println(e.getMessage());
		}
	}

	private void processarRespostaServidor(String resposta) throws InterruptedException {
		datas = resposta.split("-");
		
		for (int i = 0; i < datas.length; i++) {
			lista_datas.add(datas[i]);
		}
		t3 = datas[0];

		processarTempo3(t3);
		calcularTempo();

	}

	private void processarTempo3(String data) {

		String[] cortarDatas = null;

		if (data.contains(":")) {
			cortarDatas = data.split(":");

			String aux_segundos1 = "";
			for (int i = 0; i < cortarDatas.length; i++) {
				if (i == 2) {
					aux_segundos1 = cortarDatas[i];
					break;
				}
				getDatasSeparadas().get(4).add(cortarDatas[i]);
			}

			int calc_segundos1 = Integer.parseInt(aux_segundos1) + 5;
			int minutos = Integer.parseInt(getDatasSeparadas().get(4).get(1));
			int horas = Integer.parseInt(getDatasSeparadas().get(4).get(0));
			String aux_tempo1 = "";

			String aux_minutos1 = String.valueOf(minutos);
			String aux_segundos2 = String.valueOf(calc_segundos1);

			if (calc_segundos1 > 60) {
				int aux_calc_segundos = 60 - calc_segundos1;

				minutos++;

				if (minutos > 60) {
					horas++;
				}

				if (minutos < 10) {
					aux_minutos1 = "0" + minutos;
				}

				String aux_segundos_digito = "";

				if (aux_calc_segundos < 10) {
					aux_segundos_digito = "0" + aux_calc_segundos;
					aux_tempo1 = horas + ":" + aux_minutos1 + ":" + aux_segundos_digito;

				} else {
					aux_tempo1 = horas + ":" + aux_minutos1 + ":" + aux_calc_segundos;
				}

			} else {

				if (minutos < 10) {
					aux_minutos1 = "0" + minutos;
				}

				if (calc_segundos1 < 10) {
					aux_segundos2 = "0" + calc_segundos1;
				}
				aux_tempo1 = horas + ":" + aux_minutos1 + ":" + aux_segundos2;
			}

			lista_datas.add(aux_tempo1);
		}
	}
	
	public int getPorta() {
		return porta;
	}

	public void setPorta(int porta) {
		if (porta < 0) {
			throw new IllegalArgumentException("A porta não pode ser negativa!");
		}
		this.porta = porta;
	}

	public HashMap<Integer, List<String>> getDatasSeparadas() {
		if (datasSeparadas == null) {
			datasSeparadas = new HashMap<Integer, List<String>>();
			datasSeparadas.put(0, new ArrayList<>());
			datasSeparadas.put(1, new ArrayList<>());
			datasSeparadas.put(2, new ArrayList<>());
			datasSeparadas.put(3, new ArrayList<>());
			datasSeparadas.put(4, new ArrayList<>());

		}
		return datasSeparadas;
	}

}
