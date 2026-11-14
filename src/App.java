import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class App {
    public static void main(String[] args) {
        Sistema sistema = new Sistema();
        cargarDatosIniciales(sistema);
        new Consola(sistema).iniciar();
    }

    private static void cargarDatosIniciales(Sistema sistema) {
        Participante p1 = sistema.registrarParticipante("P001", "Atlas", "Empresa A");
        Participante p2 = sistema.registrarParticipante("P002", "Titanes", "Empresa A");
        Participante p3 = sistema.registrarParticipante("P003", "Maya", "Empresa B");
        Participante p4 = sistema.registrarParticipante("P004", "Quetzal", "Empresa B");
        Participante p5 = sistema.registrarParticipante("P005", "Jaguar", "Empresa C");
        Participante p6 = sistema.registrarParticipante("P006", "Cumbre", "Empresa C");
        Participante p7 = sistema.registrarParticipante("P007", "Volcan", "Empresa D");
        Participante p8 = sistema.registrarParticipante("P008", "Estrellas", "Empresa D");

        Torneo futbol = sistema.crearTorneo("T001", "Copa Futbol Sala", new FutbolSala());
        Torneo penales = sistema.crearTorneo("T002", "Copa Futbol Sala Penales", new FutbolSalaPenales());
        Torneo baloncesto = sistema.crearTorneo("T003", "Copa Baloncesto", new Baloncesto());
        Torneo voleibol = sistema.crearTorneo("T004", "Copa Voleibol", new Voleibol());
        Torneo relevos = sistema.crearTorneo("T005", "Copa Relevos", new Relevos());

        inscribir(sistema, futbol, Arrays.asList(p1, p2, p3, p4));
        inscribir(sistema, penales, Arrays.asList(p3, p4, p5, p6));
        inscribir(sistema, baloncesto, Arrays.asList(p1, p2, p5, p6));
        inscribir(sistema, voleibol, Arrays.asList(p2, p3, p7, p8));
        inscribir(sistema, relevos, Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8));

        sistema.cerrarInscripciones(futbol.getCodigo());
        sistema.cerrarInscripciones(penales.getCodigo());
        sistema.cerrarInscripciones(baloncesto.getCodigo());
        sistema.cerrarInscripciones(voleibol.getCodigo());
        sistema.cerrarInscripciones(relevos.getCodigo());

        programarTodos(sistema, futbol, LocalDate.of(2026, 11, 16), 10, "Arena Norte");
        programarTodos(sistema, penales, LocalDate.of(2026, 11, 17), 10, "Arena Sur");
        programarPrimero(sistema, baloncesto, LocalDate.of(2026, 11, 18), 10, "Arena Centro");
        programarPrimero(sistema, voleibol, LocalDate.of(2026, 11, 19), 10, "Arena Centro");
        programarPrimero(sistema, relevos, LocalDate.of(2026, 11, 20), 10, "Pista Principal");

        Evento partidoFutbol = futbol.getEventos().get(0);
        sistema.registrarResultado(partidoFutbol.getId(), new ResultadoPartido(3, 1, null, null));

        Evento partidoPenales = penales.getEventos().get(0);
        sistema.registrarResultado(partidoPenales.getId(), new ResultadoPartido(2, 2, 4, 3));
    }

    private static void inscribir(Sistema sistema, Torneo torneo, List<Participante> participantes) {
        for (Participante participante : participantes) {
            sistema.inscribir(torneo.getCodigo(), participante.getCodigo());
        }
    }

    private static void programarTodos(Sistema sistema, Torneo torneo, LocalDate fecha, int hora, String sede) {
        int posicion = 0;
        for (Evento evento : torneo.getEventos()) {
            sistema.programarEvento(evento.getId(), fecha, hora + posicion, sede + " " + (posicion + 1));
            posicion++;
        }
    }

    private static void programarPrimero(Sistema sistema, Torneo torneo, LocalDate fecha, int hora, String sede) {
        if (!torneo.getEventos().isEmpty()) {
            sistema.programarEvento(torneo.getEventos().get(0).getId(), fecha, hora, sede);
        }
    }
}
