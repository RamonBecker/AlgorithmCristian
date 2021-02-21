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
import java.time.temporal.ChronoUnit;
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

	public Cliente() {
		lista_datas = new ArrayList<String>();
	}

	private static final DateTimeFormatter FORMATO_HORAS = DateTimeFormatter.ofPattern("HH:mm:ss")
			.withResolverStyle(ResolverStyle.STRICT);

	private static LocalTime faltando(LocalTime agora, LocalTime desejada) {
        return desejada.minusHours(agora.getHour()).minusMinutes(agora.getMinute()).minusSeconds(agora.getSecond());
				//desejada.minusHours(agora.getHour()).minusMinutes(agora.getMinute());
	}

	private static void mostrar(LocalTime horario, String objetivo, String aux1, String aux2) {
		LocalTime desejada = LocalTime.parse(objetivo, FORMATO_HORAS);
		LocalTime falta = faltando(horario, desejada);
		System.out.println("Diferença de "+aux1+" "+aux2);
		System.out.println("falta:"+falta);
//		System.out.println("Entre " + horario.format(FORMATO_HORAS) + " e " + desejada.format(FORMATO_HORAS)
//				+ ", a diferença é de " + falta.format(FORMATO_HORAS) + ".");
	}

	private void calcularTempo() {

		System.out.println("---------------");
		System.out.println(lista_datas);

		String tempo0 = lista_datas.get(0);
		String tempo1 = lista_datas.get(1);
		String tempo2 = lista_datas.get(2);
		String tempo3 = lista_datas.get(3);

		System.out.println("Tempo 0:" + tempo0);
		System.out.println("Tempo 1:" + tempo1);
		System.out.println("Tempo 2:" + tempo2);
		System.out.println("Tempo 3:" + tempo3);

		LocalTime lt0 = LocalTime.parse(tempo0);
		LocalTime lt1 = LocalTime.parse(tempo1);
		LocalTime lt2 = LocalTime.parse(tempo2);
		LocalTime lt3 = LocalTime.parse(tempo3);

		mostrar(lt0, tempo1, tempo1, tempo0);
		mostrar(lt3, tempo2, tempo2, tempo3);

	}

	private String horaAtual() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date hora = Calendar.getInstance().getTime(); // Ou qualquer outra forma que tem
		String dataFormatada = sdf.format(hora);
		return dataFormatada;
	}

	private String gerarHora() throws NoSuchAlgorithmException, NoSuchProviderException {
		secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
		String dataFormatada = horaAtual();
		// horaAtual();
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

		System.out.println("Resposta do servidor:" + resposta);
		datas = resposta.split("-");

		for (int i = 0; i < datas.length; i++) {
			System.out.println("Datas:" + datas[i]);
			lista_datas.add(datas[i]);
		}

		for (int i = 0; i < datas.length; i++) {
			String data = datas[i];
			juntarDatas(data, i);
		}

		t3 = datas[0];
		// int aux_t3 = Integer.parseInt(datasSeparadas.get(0)) +5;
//		t3 = String.valueOf(aux_t3);
//		

		juntarDatas(t3);
//
		System.out.println(datasSeparadas);
		System.out.println("--------");
		calcularTempo();

	}

	private void juntarDatas(String data) {

		String[] cortarDatas = null;

		if (data.contains(":")) {
			String aux_data = "";
			cortarDatas = data.split(":");
			String aux_datas1 = "";
			for (int j = 0; j < cortarDatas.length; j++) {
				aux_data = cortarDatas[j];

				getDatasSeparadas().get(3).add(aux_data);
			}

			String aux_segundos1 = "";
			for (int i = 0; i < cortarDatas.length; i++) {
				if (i == 2) {
					aux_segundos1 = cortarDatas[i];
					break;
				}
				getDatasSeparadas().get(4).add(cortarDatas[i]);
				// aux_datas1 += cortarDatas[i]+":";
			}
			System.out.println("Aux segundos1:"+aux_segundos1);
			int calc_segundos1 = Integer.parseInt(aux_segundos1) + 5;
			System.out.println("Soma dos segundos:"+calc_segundos1);
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
				
				if(aux_calc_segundos < 10) {
					aux_segundos_digito = "0"+aux_calc_segundos;
					aux_tempo1 = horas + ":" + aux_minutos1 + ":" + aux_segundos_digito;

				}else {
					aux_tempo1 = horas + ":" + aux_minutos1 + ":" + aux_calc_segundos;
				}

		
			} else {
				
				
				if (minutos < 10) {
					aux_minutos1 = "0" + minutos;
				}
				
				if(calc_segundos1 < 10) {
					aux_segundos2 = "0"+calc_segundos1;
				}
				aux_tempo1 = horas + ":" + aux_minutos1 + ":" + aux_segundos2;
			}

			System.out.println("Aux tempo:" + aux_tempo1);
			lista_datas.add(aux_tempo1);
			System.out.println("Segundos:" + aux_segundos1);

			List<String> lista_tempo = getDatasSeparadas().get(3);
			int calc_segundos = Integer.parseInt(lista_tempo.get(2)) + 5;

			int aux_segundos = 0;

			String conversao_segundos = "";

			if (calc_segundos > 60) {
				aux_segundos = calc_segundos - 60;
				conversao_segundos = String.valueOf(aux_segundos);

				if (aux_segundos < 10) {
					conversao_segundos = "0" + String.valueOf(aux_segundos);
				}

				int calc_minutos = Integer.parseInt(lista_tempo.get(1)) + 1;
				int calc_horas = Integer.parseInt(lista_tempo.get(0));

				if (calc_minutos > 59) {
					calc_horas += 1;
				}
				String aux_tempo = String.valueOf(calc_horas + calc_minutos) + conversao_segundos;
				lista_tempo.clear();
				lista_tempo.add(aux_tempo);
			} else {

				lista_tempo.remove(2);
				lista_tempo.add(2, String.valueOf(calc_segundos));
				String aux_tempo = "";
				for (int i = 0; i < lista_tempo.size(); i++) {
					String aux_datas = lista_tempo.get(i);
					aux_tempo += aux_datas;
				}

				lista_tempo.clear();
				lista_tempo.add(aux_tempo);

			}

			getDatasSeparadas().put(3, lista_tempo);
		}
	}

	private void juntarDatas(String data, int i) {

		String[] cortarDatas = null;

		if (getDatasSeparadas().containsKey(i)) {
			if (data.contains(":")) {
				String aux_data = "";
				cortarDatas = data.split(":");
				for (int j = 0; j < cortarDatas.length; j++) {
					aux_data += cortarDatas[j];
					// datasSeparadas.get(i).add(aux_data);

				}
				getDatasSeparadas().get(i).add(aux_data);
				aux_data = "";
			}
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
