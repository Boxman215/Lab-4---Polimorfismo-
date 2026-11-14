import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Consola {
    private final Sistema sistema;
    private final Scanner scanner;

    public Consola(Sistema sistema) {
        this.sistema = sistema;
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            String opcion = leer("Opcion: ");
            try {
                switch (opcion) {
                    case "1":
                        registrarParticipante();
                        break;
                    case "2":
                        crearTorneo();
                        break;
                    case "3":
                        cotizar();
                        break;
                    case "4":
                        inscribir();
                        break;
                    case "5":
                        cerrarInscripciones();
                        break;
                    case "6":
                        programarEvento();
                        break;
                    case "7":
                        reprogramarEvento();
                        break;
                    case "8":
                        registrarResultado();
                        break;
                    case "9":
                        consultarTorneos();
                        break;
                    case "10":
                        consultarCalendario();
                        break;
                    case "11":
                        consultarReportes();
                        break;
                    case "0":
                        continuar = false;
                        System.out.println("Programa finalizado");
                        break;
                    default:
                        System.out.println("Opcion no valida");
                }
            } catch (IllegalArgumentException | DateTimeParseException ex) {
                System.out.println("Operacion rechazada: " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("Entrada no valida. Intente de nuevo");
            }
        }
    }

    private void mostrarMenu() {
        System.out.println("\nARENA GT");
        System.out.println("1. Registrar participante");
        System.out.println("2. Crear torneo");
        System.out.println("3. Cotizar inscripcion");
        System.out.println("4. Inscribir participante");
        System.out.println("5. Cerrar inscripciones");
        System.out.println("6. Programar evento");
        System.out.println("7. Reprogramar evento");
        System.out.println("8. Registrar resultado");
        System.out.println("9. Consultar torneos y tablas");
        System.out.println("10. Consultar calendarios");
        System.out.println("11. Generar reportes");
        System.out.println("0. Salir");
    }

    private void registrarParticipante() {
        Participante participante = sistema.registrarParticipante(
                leer("Codigo: "), leer("Nombre: "), leer("Empresa: "));
        System.out.println("Registrado: " + participante);
    }

    private void crearTorneo() {
        mostrarDisciplinas();
        int tipo = entero("Disciplina: ");
        TipoDisciplina disciplina = disciplina(tipo);
        Torneo torneo = sistema.crearTorneo(leer("Codigo del torneo: "), leer("Nombre: "), crearDisciplina(disciplina));
        System.out.println("Torneo creado: " + torneo.getCodigo());
    }

    private void cotizar() {
        mostrarDisciplinas();
        TipoDisciplina disciplina = disciplina(entero("Disciplina: "));
        double total = sistema.cotizar(disciplina, entero("Cantidad de participantes: "));
        System.out.println("Cotizacion: " + sistema.dinero(total));
    }

    private void inscribir() {
        sistema.inscribir(leer("Codigo del torneo: "), leer("Codigo del participante: "));
        System.out.println("Inscripcion realizada");
    }

    private void cerrarInscripciones() {
        sistema.cerrarInscripciones(leer("Codigo del torneo: "));
        System.out.println("Inscripciones cerradas y eventos generados");
    }

    private void programarEvento() {
        int id = entero("Id del evento: ");
        LocalDate fecha = fecha(leer("Fecha AAAA-MM-DD: "));
        int hora = entero("Hora 0-23: ");
        sistema.programarEvento(id, fecha, hora, leer("Sede: "));
        System.out.println("Evento programado");
    }

    private void reprogramarEvento() {
        programarEvento();
    }

    private void registrarResultado() {
        Evento evento = sistema.obtenerEvento(entero("Id del evento: "));
        if (!evento.getTorneo().getDisciplina().esCarrera()) {
            int a = entero("Marcador de " + evento.getParticipantes().get(0).getNombre() + ": ");
            int b = entero("Marcador de " + evento.getParticipantes().get(1).getNombre() + ": ");
            Integer penalA = null;
            Integer penalB = null;
            if (evento.getTorneo().getDisciplina().getTipo() == TipoDisciplina.FUTBOL_SALA_PENALES && a == b) {
                penalA = entero("Penales de " + evento.getParticipantes().get(0).getNombre() + ": ");
                penalB = entero("Penales de " + evento.getParticipantes().get(1).getNombre() + ": ");
            }
            sistema.registrarResultado(evento.getId(), new ResultadoPartido(a, b, penalA, penalB));
        } else {
            Map<Participante, Double> tiempos = new LinkedHashMap<>();
            for (Participante participante : evento.getParticipantes()) {
                String entrada = leer("Tiempo de " + participante.getNombre() + " o DQ: ");
                if (entrada.equalsIgnoreCase("DQ")) {
                    tiempos.put(participante, null);
                } else {
                    tiempos.put(participante, Double.parseDouble(entrada));
                }
            }
            sistema.registrarResultado(evento.getId(), new ResultadoRelevos(tiempos));
        }
        System.out.println("Resultado registrado");
    }

    private void consultarTorneos() {
        System.out.println(sistema.torneosTexto());
        String codigo = leer("Codigo de torneo o ENTER para terminar: ");
        if (!codigo.isEmpty()) {
            System.out.println(sistema.obtenerTorneo(codigo).tablaTexto());
            for (Evento evento : sistema.obtenerTorneo(codigo).getEventos()) {
                System.out.println(evento);
            }
        }
    }

    private void consultarCalendario() {
        System.out.println("1. General");
        System.out.println("2. Torneo");
        System.out.println("3. Participante");
        int opcion = entero("Opcion: ");
        if (opcion == 1) {
            System.out.println(sistema.calendarioTexto(sistema.calendarioGeneral()));
        } else if (opcion == 2) {
            System.out.println(sistema.calendarioTexto(sistema.calendarioTorneo(leer("Codigo del torneo: "))));
        } else if (opcion == 3) {
            System.out.println(sistema.calendarioTexto(sistema.calendarioParticipante(leer("Codigo del participante: "))));
        } else {
            System.out.println("Opcion no valida");
        }
    }

    private void consultarReportes() {
        for (Torneo torneo : sistema.getTorneos()) {
            System.out.println(torneo.tablaTexto());
        }
        System.out.println(sistema.reportes());
    }

    private void mostrarDisciplinas() {
        System.out.println("1. Futbol sala");
        System.out.println("2. Futbol sala con penales");
        System.out.println("3. Baloncesto");
        System.out.println("4. Voleibol");
        System.out.println("5. Relevos");
    }

    private TipoDisciplina disciplina(int opcion) {
        switch (opcion) {
            case 1:
                return TipoDisciplina.FUTBOL_SALA;
            case 2:
                return TipoDisciplina.FUTBOL_SALA_PENALES;
            case 3:
                return TipoDisciplina.BALONCESTO;
            case 4:
                return TipoDisciplina.VOLEIBOL;
            case 5:
                return TipoDisciplina.RELEVOS;
            default:
                throw new IllegalArgumentException("Disciplina no valida");
        }
    }

    private Disciplina crearDisciplina(TipoDisciplina tipo) {
        switch (tipo) {
            case FUTBOL_SALA:
                return new FutbolSala();
            case FUTBOL_SALA_PENALES:
                return new FutbolSalaPenales();
            case BALONCESTO:
                return new Baloncesto();
            case VOLEIBOL:
                return new Voleibol();
            case RELEVOS:
                return new Relevos();
            default:
                throw new IllegalArgumentException("Disciplina no valida");
        }
    }

    private String leer(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private int entero(String mensaje) {
        return Integer.parseInt(leer(mensaje));
    }

    private LocalDate fecha(String texto) {
        return LocalDate.parse(texto);
    }
}
