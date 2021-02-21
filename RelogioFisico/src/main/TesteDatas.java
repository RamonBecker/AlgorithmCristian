package main;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

class TesteDatas {
    private static final DateTimeFormatter FORMATO_HORAS = DateTimeFormatter
            .ofPattern("HH:mm:ss")
            .withResolverStyle(ResolverStyle.STRICT);

    private static LocalTime faltando(LocalTime agora, LocalTime desejada) {
        return desejada.minusSeconds(agora.getHour()).minusMinutes(agora.getMinute()).minusSeconds(agora.getSecond());
        		//minusHours(agora.getHour()).minusMinutes(agora.getMinute());
    }

    private static void mostrar(LocalTime horario, String objetivo) {
        LocalTime desejada = LocalTime.parse(objetivo, FORMATO_HORAS);
        LocalTime falta = faltando(horario, desejada);
        
//        System.out.println(
//                "Entre " + horario.format(FORMATO_HORAS)
//                + " e " + desejada.format(FORMATO_HORAS)
//                + ", a diferença é de " + falta.format(FORMATO_HORAS)
//                + ".");
    }

    public static void main(String[] args) {
        LocalTime agora = LocalTime.now();
        mostrar(agora, "07:30:11");
    }
}