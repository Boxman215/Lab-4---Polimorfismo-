import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Torneo {
    private final String codigo;
    private final String nombre;
    private final Disciplina disciplina;
    private final Map<String, Participante> participantes;
    private final List<Evento> eventos;
    private final Map<Participante, FilaTabla> tabla;
    private EstadoTorneo estado;
    private double premio;

    public Torneo(String codigo, String nombre, Disciplina disciplina) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo del torneo no puede estar vacio");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del torneo no puede estar vacio");
        }
        if (disciplina == null) {
            throw new IllegalArgumentException("La disciplina es obligatoria");
        }
        this.codigo = codigo.trim();
        this.nombre = nombre.trim();
        this.disciplina = disciplina;
        this.participantes = new LinkedHashMap<>();
        this.eventos = new ArrayList<>();
        this.tabla = new LinkedHashMap<>();
        this.estado = EstadoTorneo.INSCRIPCION;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public EstadoTorneo getEstado() {
        return estado;
    }

    public double getPremio() {
        return premio;
    }

    public List<Participante> getParticipantes() {
        return Collections.unmodifiableList(new ArrayList<>(participantes.values()));
    }

    public List<Evento> getEventos() {
        return Collections.unmodifiableList(eventos);
    }

    public void validarInscripcion(Participante participante) {
        if (estado != EstadoTorneo.INSCRIPCION) {
            throw new IllegalArgumentException("El torneo ya no esta en inscripcion");
        }
        if (participantes.size() >= disciplina.getMaximo()) {
            throw new IllegalArgumentException("El torneo ya alcanzo el maximo de participantes");
        }
        if (participantes.containsKey(participante.getCodigo())) {
            throw new IllegalArgumentException("El participante ya esta inscrito");
        }
        int cantidadEmpresa = 0;
        for (Participante registrado : participantes.values()) {
            if (registrado.getEmpresa().equalsIgnoreCase(participante.getEmpresa())) {
                cantidadEmpresa++;
            }
        }
        if (cantidadEmpresa >= 2) {
            throw new IllegalArgumentException("Una empresa no puede tener mas de dos participantes");
        }
    }

    public void inscribir(Participante participante) {
        validarInscripcion(participante);
        participantes.put(participante.getCodigo(), participante);
        tabla.put(participante, new FilaTabla(participante));
    }

    public List<Evento> cerrarInscripciones(int primerId) {
        if (estado != EstadoTorneo.INSCRIPCION) {
            throw new IllegalArgumentException("El torneo no esta en inscripcion");
        }
        if (participantes.size() < disciplina.getMinimo()) {
            throw new IllegalArgumentException("No se alcanzo el minimo de participantes");
        }
        List<Participante> lista = getParticipantes();
        if (disciplina.esCarrera()) {
            eventos.add(new CarreraRelevos(primerId, this, lista));
        } else {
            int id = primerId;
            for (int i = 0; i < lista.size(); i++) {
                for (int j = i + 1; j < lista.size(); j++) {
                    List<Participante> pareja = new ArrayList<>();
                    pareja.add(lista.get(i));
                    pareja.add(lista.get(j));
                    eventos.add(new Encuentro(id++, this, pareja));
                }
            }
        }
        estado = EstadoTorneo.EN_JUEGO;
        return new ArrayList<>(eventos);
    }

    public void registrarResultado(Evento evento, Resultado resultado) {
        if (!eventos.contains(evento)) {
            throw new IllegalArgumentException("El evento no pertenece al torneo");
        }
        if (evento.getEstado() != EstadoEvento.PROGRAMADO) {
            throw new IllegalArgumentException("El evento debe estar programado y sin resultado");
        }
        disciplina.validarResultado(evento, resultado);
        List<Puntos> puntos = disciplina.calcularPuntos(evento, resultado);
        for (Puntos punto : puntos) {
            tabla.get(punto.getParticipante()).acumular(punto);
        }
        evento.finalizar(resultado);
        if (todosLosEventosFinalizados()) {
            estado = EstadoTorneo.FINALIZADO;
            premio = disciplina.getCuota() * participantes.size() * 0.50;
        }
    }

    public boolean todosLosEventosFinalizados() {
        if (eventos.isEmpty()) {
            return false;
        }
        for (Evento evento : eventos) {
            if (evento.getEstado() != EstadoEvento.FINALIZADO) {
                return false;
            }
        }
        return true;
    }

    public List<FilaTabla> tablaOrdenada() {
        List<FilaTabla> filas = new ArrayList<>(tabla.values());
        filas.sort(comparadorTabla());
        return filas;
    }

    public static Comparator<FilaTabla> comparadorTabla() {
        return Comparator.comparingInt(FilaTabla::getPuntos).reversed()
                .thenComparing(Comparator.comparingInt(FilaTabla::getDiferencia).reversed())
                .thenComparing(Comparator.comparingInt(FilaTabla::getVictorias).reversed())
                .thenComparing(FilaTabla::getParticipante);
    }

    public String tablaTexto() {
        StringBuilder texto = new StringBuilder();
        texto.append("Tabla de ").append(nombre).append("\n");
        texto.append("Pos Nombre Empresa Puntos Diferencia Victorias\n");
        int posicion = 1;
        for (FilaTabla fila : tablaOrdenada()) {
            texto.append(posicion++).append(" ")
                    .append(fila.getParticipante().getNombre()).append(" ")
                    .append(fila.getParticipante().getEmpresa()).append(" ")
                    .append(fila.getPuntos()).append(" ")
                    .append(fila.getDiferencia()).append(" ")
                    .append(fila.getVictorias()).append("\n");
        }
        if (estado == EstadoTorneo.FINALIZADO && !tablaOrdenada().isEmpty()) {
            texto.append("Campeon: ").append(tablaOrdenada().get(0).getParticipante().getNombre()).append("\n");
        }
        return texto.toString();
    }

    public double getIngresos() {
        return disciplina.getCuota() * participantes.size();
    }
}
