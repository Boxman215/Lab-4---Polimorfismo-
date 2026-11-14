import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Evento implements Comparable<Evento> {
    private final int id;
    private final Torneo torneo;
    private final List<Participante> participantes;
    private EstadoEvento estado;
    private LocalDate fecha;
    private int hora;
    private String sede;
    private Resultado resultado;

    protected Evento(int id, Torneo torneo, List<Participante> participantes) {
        this.id = id;
        this.torneo = torneo;
        this.participantes = new ArrayList<>(participantes);
        this.estado = EstadoEvento.SIN_PROGRAMAR;
    }

    public int getId() {
        return id;
    }

    public Torneo getTorneo() {
        return torneo;
    }

    public List<Participante> getParticipantes() {
        return Collections.unmodifiableList(participantes);
    }

    public EstadoEvento getEstado() {
        return estado;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public int getHora() {
        return hora;
    }

    public String getSede() {
        return sede;
    }

    public Resultado getResultado() {
        return resultado;
    }

    public boolean tieneParticipante(Participante participante) {
        return participantes.contains(participante);
    }

    public boolean estaEn(LocalDate fecha, int hora) {
        return estado != EstadoEvento.SIN_PROGRAMAR && this.fecha.equals(fecha) && this.hora == hora;
    }

    public void programar(LocalDate fecha, int hora, String sede) {
        if (estado == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("El evento ya esta finalizado");
        }
        this.fecha = fecha;
        this.hora = hora;
        this.sede = sede;
        this.estado = EstadoEvento.PROGRAMADO;
    }

    public void finalizar(Resultado resultado) {
        if (estado != EstadoEvento.PROGRAMADO) {
            throw new IllegalArgumentException("El evento debe estar programado");
        }
        this.resultado = resultado;
        this.estado = EstadoEvento.FINALIZADO;
    }

    protected String ubicacion() {
        if (fecha == null) {
            return "Sin programar";
        }
        return fecha.format(DateTimeFormatter.ISO_DATE) + " " + String.format("%02d:00", hora) + " - " + sede;
    }

    protected abstract String tipoEvento();

    public abstract String resultadoTexto();

    @Override
    public int compareTo(Evento otro) {
        int comparacion = Boolean.compare(fecha == null, otro.fecha == null);
        if (comparacion != 0) {
            return comparacion;
        }
        if (fecha != null) {
            comparacion = fecha.compareTo(otro.fecha);
            if (comparacion != 0) {
                return comparacion;
            }
            comparacion = Integer.compare(hora, otro.hora);
            if (comparacion != 0) {
                return comparacion;
            }
            comparacion = sede.compareToIgnoreCase(otro.sede);
            if (comparacion != 0) {
                return comparacion;
            }
        }
        return Integer.compare(id, otro.id);
    }

    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder();
        texto.append("Evento ").append(id).append(" | ").append(tipoEvento()).append(" | ");
        texto.append(torneo.getNombre()).append(" | ").append(estado).append(" | ").append(ubicacion());
        texto.append(" | ").append(participantesTexto());
        if (resultado != null) {
            texto.append(" | ").append(resultadoTexto());
        }
        return texto.toString();
    }

    private String participantesTexto() {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < participantes.size(); i++) {
            if (i > 0) {
                texto.append(" vs ");
            }
            texto.append(participantes.get(i).getNombre());
        }
        return texto.toString();
    }
}
