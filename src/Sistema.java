import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Sistema {
    private final Map<String, Participante> participantes;
    private final Map<String, Torneo> torneos;
    private int siguienteEvento;

    public Sistema() {
        participantes = new LinkedHashMap<>();
        torneos = new LinkedHashMap<>();
        siguienteEvento = 1;
    }

    public Participante registrarParticipante(String codigo, String nombre, String empresa) {
        String clave = clave(codigo);
        if (clave.isEmpty()) {
            throw new IllegalArgumentException("El codigo no puede estar vacio");
        }
        if (participantes.containsKey(clave)) {
            throw new IllegalArgumentException("El codigo del participante ya existe");
        }
        Participante participante = new Participante(codigo, nombre, empresa);
        participantes.put(clave, participante);
        return participante;
    }

    public Torneo crearTorneo(String codigo, String nombre, Disciplina disciplina) {
        String clave = clave(codigo);
        if (clave.isEmpty()) {
            throw new IllegalArgumentException("El codigo no puede estar vacio");
        }
        if (torneos.containsKey(clave)) {
            throw new IllegalArgumentException("El codigo del torneo ya existe");
        }
        Torneo torneo = new Torneo(codigo, nombre, disciplina);
        torneos.put(clave, torneo);
        return torneo;
    }

    public double cotizar(TipoDisciplina tipo, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        Disciplina disciplina = crearDisciplina(tipo);
        if (cantidad < disciplina.getMinimo() || cantidad > disciplina.getMaximo()) {
            throw new IllegalArgumentException("La cantidad esta fuera del rango de la disciplina");
        }
        return disciplina.getCuota() * cantidad;
    }

    public void inscribir(String codigoTorneo, String codigoParticipante) {
        Torneo torneo = obtenerTorneo(codigoTorneo);
        Participante participante = obtenerParticipante(codigoParticipante);
        torneo.inscribir(participante);
    }

    public void cerrarInscripciones(String codigoTorneo) {
        Torneo torneo = obtenerTorneo(codigoTorneo);
        List<Evento> eventos = torneo.cerrarInscripciones(siguienteEvento);
        siguienteEvento += eventos.size();
    }

    public void programarEvento(int id, LocalDate fecha, int hora, String sede) {
        Evento evento = obtenerEvento(id);
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        if (hora < 0 || hora > 23) {
            throw new IllegalArgumentException("La hora debe estar entre 0 y 23");
        }
        if (sede == null || sede.trim().isEmpty()) {
            throw new IllegalArgumentException("La sede no puede estar vacia");
        }
        if (evento.getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede reprogramar un evento finalizado");
        }
        for (Evento otro : todosLosEventos()) {
            if (otro == evento || otro.getEstado() == EstadoEvento.SIN_PROGRAMAR || !otro.estaEn(fecha, hora)) {
                continue;
            }
            if (otro.getSede().equalsIgnoreCase(sede.trim())) {
                throw new IllegalArgumentException("La sede ya esta ocupada en esa fecha y hora");
            }
            for (Participante participante : evento.getParticipantes()) {
                if (otro.tieneParticipante(participante)) {
                    throw new IllegalArgumentException("Un participante ya tiene otro evento en esa fecha y hora");
                }
            }
        }
        evento.programar(fecha, hora, sede.trim());
    }

    public void registrarResultado(int id, Resultado resultado) {
        Evento evento = obtenerEvento(id);
        evento.getTorneo().registrarResultado(evento, resultado);
    }

    public Participante obtenerParticipante(String codigo) {
        Participante participante = participantes.get(clave(codigo));
        if (participante == null) {
            throw new IllegalArgumentException("No existe el participante");
        }
        return participante;
    }

    public Torneo obtenerTorneo(String codigo) {
        Torneo torneo = torneos.get(clave(codigo));
        if (torneo == null) {
            throw new IllegalArgumentException("No existe el torneo");
        }
        return torneo;
    }

    public Evento obtenerEvento(int id) {
        for (Evento evento : todosLosEventos()) {
            if (evento.getId() == id) {
                return evento;
            }
        }
        throw new IllegalArgumentException("No existe el evento");
    }

    public List<Participante> getParticipantes() {
        return Collections.unmodifiableList(new ArrayList<>(participantes.values()));
    }

    public List<Torneo> getTorneos() {
        return Collections.unmodifiableList(new ArrayList<>(torneos.values()));
    }

    public List<Evento> todosLosEventos() {
        List<Evento> eventos = new ArrayList<>();
        for (Torneo torneo : torneos.values()) {
            eventos.addAll(torneo.getEventos());
        }
        return eventos;
    }

    public List<Evento> calendarioGeneral() {
        List<Evento> eventos = eventosProgramados();
        eventos.sort(Comparator.naturalOrder());
        return eventos;
    }

    public List<Evento> calendarioTorneo(String codigoTorneo) {
        Torneo torneo = obtenerTorneo(codigoTorneo);
        List<Evento> eventos = new ArrayList<>();
        for (Evento evento : torneo.getEventos()) {
            if (evento.getEstado() != EstadoEvento.SIN_PROGRAMAR) {
                eventos.add(evento);
            }
        }
        eventos.sort(Comparator.naturalOrder());
        return eventos;
    }

    public List<Evento> calendarioParticipante(String codigoParticipante) {
        Participante participante = obtenerParticipante(codigoParticipante);
        List<Evento> eventos = new ArrayList<>();
        for (Evento evento : eventosProgramados()) {
            if (evento.tieneParticipante(participante)) {
                eventos.add(evento);
            }
        }
        eventos.sort(Comparator.naturalOrder());
        return eventos;
    }

    public String reportes() {
        StringBuilder texto = new StringBuilder();
        texto.append("INGRESOS Y PREMIOS\n");
        double total = 0;
        Map<TipoDisciplina, Double> porDisciplina = new LinkedHashMap<>();
        double premios = 0;
        for (Torneo torneo : torneos.values()) {
            total += torneo.getIngresos();
            porDisciplina.put(torneo.getDisciplina().getTipo(),
                    porDisciplina.getOrDefault(torneo.getDisciplina().getTipo(), 0.0) + torneo.getIngresos());
            premios += torneo.getPremio();
        }
        texto.append("Total: ").append(dinero(total)).append("\n");
        for (Map.Entry<TipoDisciplina, Double> entrada : porDisciplina.entrySet()) {
            texto.append(entrada.getKey().getNombre()).append(": ").append(dinero(entrada.getValue())).append("\n");
        }
        texto.append("Premios entregados: ").append(dinero(premios)).append("\n\n");
        texto.append(rankingEmpresas());
        return texto.toString();
    }

    public String rankingEmpresas() {
        Map<String, Integer> puntos = new LinkedHashMap<>();
        for (Torneo torneo : torneos.values()) {
            for (FilaTabla fila : torneo.tablaOrdenada()) {
                String empresa = fila.getParticipante().getEmpresa();
                puntos.put(empresa, puntos.getOrDefault(empresa, 0) + fila.getPuntos());
            }
        }
        List<Map.Entry<String, Integer>> ranking = new ArrayList<>(puntos.entrySet());
        ranking.sort(Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue).reversed()
                .thenComparing(Map.Entry::getKey, String.CASE_INSENSITIVE_ORDER));
        StringBuilder texto = new StringBuilder("RANKING DE EMPRESAS\n");
        int posicion = 1;
        for (Map.Entry<String, Integer> entrada : ranking) {
            texto.append(posicion++).append(". ").append(entrada.getKey()).append(" - ")
                    .append(entrada.getValue()).append(" puntos\n");
        }
        return texto.toString();
    }

    public String dinero(double valor) {
        return String.format(Locale.US, "Q%.2f", valor);
    }

    public String calendarioTexto(List<Evento> eventos) {
        StringBuilder texto = new StringBuilder();
        if (eventos.isEmpty()) {
            return "No hay eventos programados";
        }
        for (Evento evento : eventos) {
            texto.append(evento).append("\n");
        }
        return texto.toString();
    }

    public String torneosTexto() {
        StringBuilder texto = new StringBuilder();
        for (Torneo torneo : torneos.values()) {
            texto.append(torneo.getCodigo()).append(" | ").append(torneo.getNombre()).append(" | ")
                    .append(torneo.getDisciplina().getTipo().getNombre()).append(" | ")
                    .append(torneo.getEstado()).append(" | Participantes: ")
                    .append(torneo.getParticipantes().size()).append("\n");
        }
        return texto.toString();
    }

    private List<Evento> eventosProgramados() {
        List<Evento> eventos = new ArrayList<>();
        for (Evento evento : todosLosEventos()) {
            if (evento.getEstado() != EstadoEvento.SIN_PROGRAMAR) {
                eventos.add(evento);
            }
        }
        return eventos;
    }

    private String clave(String valor) {
        return valor == null ? "" : valor.trim().toUpperCase();
    }

    private Disciplina crearDisciplina(TipoDisciplina tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("La disciplina es obligatoria");
        }
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
                throw new IllegalArgumentException("Disciplina no disponible");
        }
    }
}
