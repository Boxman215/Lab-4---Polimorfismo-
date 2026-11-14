import java.util.List;

public class CarreraRelevos extends Evento {
    public CarreraRelevos(int id, Torneo torneo, List<Participante> participantes) {
        super(id, torneo, participantes);
    }

    @Override
    protected String tipoEvento() {
        return "Carrera de relevos";
    }

    @Override
    public String resultadoTexto() {
        ResultadoRelevos carrera = (ResultadoRelevos) getResultado();
        StringBuilder texto = new StringBuilder();
        for (Participante participante : getParticipantes()) {
            if (texto.length() > 0) {
                texto.append(", ");
            }
            Double tiempo = carrera.getTiempos().get(participante);
            texto.append(participante.getNombre()).append("=").append(tiempo == null ? "DQ" : tiempo);
        }
        return texto.toString();
    }
}
