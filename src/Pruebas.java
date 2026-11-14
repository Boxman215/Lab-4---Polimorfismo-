import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class Pruebas {
    private static int aprobadas;

    public static void main(String[] args) {
        pruebaCuotas();
        pruebaPuntos();
        pruebaDesempates();
        pruebaInvalidos();
        pruebaConflictos();
        pruebaCalendarioYRanking();
        pruebaFinalizacion();
        System.out.println("Pruebas aprobadas: " + aprobadas);
    }

    private static void pruebaCuotas() {
        Sistema sistema = new Sistema();
        comprobarCerca(sistema.cotizar(TipoDisciplina.FUTBOL_SALA, 4), 2400.0, "Cuota futbol sala");
        comprobarCerca(sistema.cotizar(TipoDisciplina.FUTBOL_SALA_PENALES, 4), 3000.0, "Cuota futbol sala penales");
        comprobarCerca(sistema.cotizar(TipoDisciplina.BALONCESTO, 4), 2000.0, "Cuota baloncesto");
        comprobarCerca(sistema.cotizar(TipoDisciplina.VOLEIBOL, 4), 1800.0, "Cuota voleibol");
        comprobarCerca(sistema.cotizar(TipoDisciplina.RELEVOS, 3), 900.0, "Cuota relevos");
        comprobar(sistema.getTorneos().isEmpty(), "La cotizacion no crea torneos");
    }

    private static void pruebaPuntos() {
        Contexto futbol = contexto(new FutbolSala(), 4);
        futbol.sistema.registrarResultado(futbol.evento.getId(), new ResultadoPartido(3, 1, null, null));
        comprobar(fila(futbol.torneo, futbol.participantes.get(0)).getPuntos() == 3, "Puntos de futbol sala");
        comprobar(fila(futbol.torneo, futbol.participantes.get(0)).getDiferencia() == 2, "Diferencia de futbol sala");

        Contexto penales = contexto(new FutbolSalaPenales(), 4);
        penales.sistema.registrarResultado(penales.evento.getId(), new ResultadoPartido(2, 2, 4, 3));
        comprobar(fila(penales.torneo, penales.participantes.get(0)).getPuntos() == 2, "Puntos por penales");
        comprobar(fila(penales.torneo, penales.participantes.get(1)).getPuntos() == 1, "Puntos del perdedor por penales");

        Contexto baloncesto = contexto(new Baloncesto(), 4);
        baloncesto.sistema.registrarResultado(baloncesto.evento.getId(), new ResultadoPartido(80, 75, null, null));
        comprobar(fila(baloncesto.torneo, baloncesto.participantes.get(0)).getPuntos() == 2, "Puntos de baloncesto");
        comprobar(fila(baloncesto.torneo, baloncesto.participantes.get(0)).getDiferencia() == 5, "Diferencia de baloncesto");

        Contexto voleibol = contexto(new Voleibol(), 4);
        voleibol.sistema.registrarResultado(voleibol.evento.getId(), new ResultadoPartido(3, 2, null, null));
        comprobar(fila(voleibol.torneo, voleibol.participantes.get(0)).getPuntos() == 2, "Puntos de voleibol");

        Contexto relevos = contexto(new Relevos(), 4);
        Map<Participante, Double> tiempos = new LinkedHashMap<>();
        tiempos.put(relevos.participantes.get(0), 50.1);
        tiempos.put(relevos.participantes.get(1), 51.9);
        tiempos.put(relevos.participantes.get(2), null);
        tiempos.put(relevos.participantes.get(3), 55.0);
        relevos.sistema.registrarResultado(relevos.evento.getId(), new ResultadoRelevos(tiempos));
        comprobar(fila(relevos.torneo, relevos.participantes.get(0)).getPuntos() == 10, "Puntos del primer relevo");
        comprobar(fila(relevos.torneo, relevos.participantes.get(2)).getPuntos() == 0, "Descalificacion en relevos");
    }

    private static void pruebaInvalidos() {
        Contexto contexto = contexto(new Baloncesto(), 4);
        int puntosAntes = fila(contexto.torneo, contexto.participantes.get(0)).getPuntos();
        boolean rechazado = false;
        try {
            contexto.sistema.registrarResultado(contexto.evento.getId(), new ResultadoPartido(10, 10, null, null));
        } catch (IllegalArgumentException ex) {
            rechazado = true;
        }
        comprobar(rechazado, "Se rechaza empate de baloncesto");
        comprobar(contexto.evento.getEstado() == EstadoEvento.PROGRAMADO, "El evento conserva su estado");
        comprobar(fila(contexto.torneo, contexto.participantes.get(0)).getPuntos() == puntosAntes,
                "La tabla no cambia con resultado invalido");

        Contexto carrera = contexto(new Relevos(), 4);
        Map<Participante, Double> tiempos = new LinkedHashMap<>();
        for (Participante participante : carrera.participantes) {
            tiempos.put(participante, 10.0);
        }
        rechazado = false;
        try {
            carrera.sistema.registrarResultado(carrera.evento.getId(), new ResultadoRelevos(tiempos));
        } catch (IllegalArgumentException ex) {
            rechazado = true;
        }
        comprobar(rechazado, "Se rechazan tiempos repetidos");
        comprobar(carrera.evento.getEstado() == EstadoEvento.PROGRAMADO, "La carrera conserva su estado");
    }

    private static void pruebaDesempates() {
        Participante diferenciaMayor = new Participante("P1", "Uno", "Empresa 1");
        Participante diferenciaMenor = new Participante("P2", "Dos", "Empresa 2");
        Participante masVictorias = new Participante("P3", "Tres", "Empresa 3");
        Participante menosVictorias = new Participante("P4", "Cuatro", "Empresa 4");
        Participante nombreA = new Participante("P5", "Ana", "Empresa 5");
        Participante nombreB = new Participante("P6", "Beto", "Empresa 6");

        FilaTabla filaDiferenciaMayor = new FilaTabla(diferenciaMayor);
        filaDiferenciaMayor.acumular(new Puntos(diferenciaMayor, 3, 5, true));
        FilaTabla filaDiferenciaMenor = new FilaTabla(diferenciaMenor);
        filaDiferenciaMenor.acumular(new Puntos(diferenciaMenor, 3, 2, true));
        FilaTabla filaMasVictorias = new FilaTabla(masVictorias);
        filaMasVictorias.acumular(new Puntos(masVictorias, 4, 0, true));
        filaMasVictorias.acumular(new Puntos(masVictorias, 0, 0, false));
        FilaTabla filaMenosVictorias = new FilaTabla(menosVictorias);
        filaMenosVictorias.acumular(new Puntos(menosVictorias, 4, 0, false));
        filaMenosVictorias.acumular(new Puntos(menosVictorias, 0, 0, false));
        FilaTabla filaNombreA = new FilaTabla(nombreA);
        filaNombreA.acumular(new Puntos(nombreA, 1, 0, false));
        FilaTabla filaNombreB = new FilaTabla(nombreB);
        filaNombreB.acumular(new Puntos(nombreB, 1, 0, false));

        List<FilaTabla> filas = new ArrayList<>();
        filas.add(filaDiferenciaMenor);
        filas.add(filaDiferenciaMayor);
        filas.sort(Torneo.comparadorTabla());
        comprobar(filas.get(0) == filaDiferenciaMayor, "Desempate por diferencia");

        filas.clear();
        filas.add(filaMenosVictorias);
        filas.add(filaMasVictorias);
        filas.sort(Torneo.comparadorTabla());
        comprobar(filas.get(0) == filaMasVictorias, "Desempate por victorias");

        filas.clear();
        filas.add(filaNombreB);
        filas.add(filaNombreA);
        filas.sort(Torneo.comparadorTabla());
        comprobar(filas.get(0) == filaNombreA, "Desempate por nombre");
    }

    private static void pruebaConflictos() {
        Sistema sistema = new Sistema();
        for (int i = 1; i <= 8; i++) {
            sistema.registrarParticipante("P" + i, "Participante " + i, "Empresa " + i);
        }
        Torneo primero = sistema.crearTorneo("T1", "Primero", new Baloncesto());
        Torneo segundo = sistema.crearTorneo("T2", "Segundo", new Baloncesto());
        Torneo tercero = sistema.crearTorneo("T3", "Tercero", new Baloncesto());
        inscribirRango(sistema, primero, 1, 4);
        inscribirRango(sistema, segundo, 5, 8);
        inscribirRango(sistema, tercero, 1, 4);
        sistema.cerrarInscripciones("T1");
        sistema.cerrarInscripciones("T2");
        sistema.cerrarInscripciones("T3");
        sistema.programarEvento(1, LocalDate.of(2026, 12, 1), 10, "Sede A");
        boolean rechazado = false;
        try {
            sistema.programarEvento(7, LocalDate.of(2026, 12, 1), 10, "Sede A");
        } catch (IllegalArgumentException ex) {
            rechazado = true;
        }
        comprobar(rechazado, "Se rechaza sede ocupada");
        sistema.programarEvento(7, LocalDate.of(2026, 12, 1), 10, "Sede B");
        rechazado = false;
        try {
            sistema.programarEvento(13, LocalDate.of(2026, 12, 1), 10, "Sede C");
        } catch (IllegalArgumentException ex) {
            rechazado = true;
        }
        comprobar(rechazado, "Se rechaza participante ocupado");
    }

    private static void pruebaCalendarioYRanking() {
        Sistema sistema = new Sistema();
        Participante p1 = sistema.registrarParticipante("P1", "Ana", "Empresa A");
        Participante p2 = sistema.registrarParticipante("P2", "Beto", "Empresa A");
        Participante p3 = sistema.registrarParticipante("P3", "Caro", "Empresa B");
        Participante p4 = sistema.registrarParticipante("P4", "Dino", "Empresa B");
        Torneo torneo = sistema.crearTorneo("T1", "Calendario", new FutbolSala());
        sistema.inscribir("T1", p1.getCodigo());
        sistema.inscribir("T1", p2.getCodigo());
        sistema.inscribir("T1", p3.getCodigo());
        sistema.inscribir("T1", p4.getCodigo());
        sistema.cerrarInscripciones("T1");
        sistema.programarEvento(1, LocalDate.of(2026, 12, 3), 12, "Sede Z");
        sistema.programarEvento(2, LocalDate.of(2026, 12, 3), 10, "Sede A");
        sistema.programarEvento(3, LocalDate.of(2026, 12, 2), 15, "Sede B");
        List<Evento> calendario = sistema.calendarioGeneral();
        comprobar(calendario.get(0).getId() == 3, "Calendario ordenado por fecha");
        comprobar(calendario.get(1).getId() == 2, "Calendario ordenado por hora");

        sistema.registrarResultado(1, new ResultadoPartido(3, 0, null, null));
        String ranking = sistema.rankingEmpresas();
        comprobar(ranking.contains("Empresa A"), "Ranking de empresas");
        comprobar(torneo.getIngresos() == 2400.0, "Ingresos por inscripciones");
        comprobar(sistema.cotizar(TipoDisciplina.FUTBOL_SALA, 4) == 2400.0,
                "Cotizacion correcta sin alterar ingresos");
        comprobar(torneo.getPremio() == 0.0, "La cotizacion no entrega premio");
    }

    private static void pruebaFinalizacion() {
        Contexto contexto = contexto(new Relevos(), 3);
        Map<Participante, Double> tiempos = new LinkedHashMap<>();
        tiempos.put(contexto.participantes.get(0), 20.0);
        tiempos.put(contexto.participantes.get(1), 21.0);
        tiempos.put(contexto.participantes.get(2), 22.0);
        contexto.sistema.registrarResultado(contexto.evento.getId(), new ResultadoRelevos(tiempos));
        comprobar(contexto.torneo.getEstado() == EstadoTorneo.FINALIZADO, "Finalizacion automatica");
        comprobarCerca(contexto.torneo.getPremio(), 450.0, "Premio automatico");
    }

    private static Contexto contexto(Disciplina disciplina, int cantidad) {
        Sistema sistema = new Sistema();
        for (int i = 1; i <= cantidad; i++) {
            sistema.registrarParticipante("P" + i, "Participante " + i, "Empresa " + i);
        }
        Torneo torneo = sistema.crearTorneo("T1", "Torneo de prueba", disciplina);
        for (int i = 1; i <= cantidad; i++) {
            sistema.inscribir("T1", "P" + i);
        }
        sistema.cerrarInscripciones("T1");
        Evento evento = torneo.getEventos().get(0);
        sistema.programarEvento(evento.getId(), LocalDate.of(2026, 12, 2), 10, "Sede Prueba");
        return new Contexto(sistema, torneo, evento, torneo.getParticipantes());
    }

    private static void inscribirRango(Sistema sistema, Torneo torneo, int desde, int hasta) {
        for (int i = desde; i <= hasta; i++) {
            sistema.inscribir(torneo.getCodigo(), "P" + i);
        }
    }

    private static FilaTabla fila(Torneo torneo, Participante participante) {
        for (FilaTabla fila : torneo.tablaOrdenada()) {
            if (fila.getParticipante().equals(participante)) {
                return fila;
            }
        }
        throw new IllegalArgumentException("Fila no encontrada");
    }

    private static void comprobar(boolean condicion, String nombre) {
        if (!condicion) {
            throw new IllegalStateException("Fallo: " + nombre);
        }
        aprobadas++;
    }

    private static void comprobarCerca(double obtenido, double esperado, String nombre) {
        comprobar(Math.abs(obtenido - esperado) < 0.001, nombre);
    }

    private static class Contexto {
        private final Sistema sistema;
        private final Torneo torneo;
        private final Evento evento;
        private final List<Participante> participantes;

        private Contexto(Sistema sistema, Torneo torneo, Evento evento, List<Participante> participantes) {
            this.sistema = sistema;
            this.torneo = torneo;
            this.evento = evento;
            this.participantes = participantes;
        }
    }
}
