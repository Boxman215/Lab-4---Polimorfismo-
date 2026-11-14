import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Relevos extends Disciplina {
    public Relevos() {
        super(TipoDisciplina.RELEVOS, 3, 8, 300.0);
    }

    @Override
    public boolean esCarrera() {
        return true;
    }

    @Override
    public void validarResultado(Evento evento, Resultado resultado) {
        validarResultadoComun(evento, resultado, ResultadoRelevos.class);
        ResultadoRelevos carrera = (ResultadoRelevos) resultado;
        List<Participante> participantes = evento.getParticipantes();
        Map<Participante, Double> tiempos = carrera.getTiempos();
        if (tiempos.size() != participantes.size() || !tiempos.keySet().containsAll(participantes)) {
            throw new IllegalArgumentException("Debe existir un tiempo o descalificacion por participante");
        }
        Set<Double> repetidos = new HashSet<>();
        for (Double tiempo : tiempos.values()) {
            if (tiempo != null) {
                if (tiempo <= 0 || tiempo.isNaN() || tiempo.isInfinite()) {
                    throw new IllegalArgumentException("Los tiempos deben ser positivos");
                }
                if (!repetidos.add(tiempo)) {
                    throw new IllegalArgumentException("Los tiempos no pueden repetirse");
                }
            }
        }
    }

    @Override
    public List<Puntos> calcularPuntos(Evento evento, Resultado resultado) {
        validarResultado(evento, resultado);
        ResultadoRelevos carrera = (ResultadoRelevos) resultado;
        List<Participante> ordenados = new ArrayList<>();
        for (Participante participante : evento.getParticipantes()) {
            if (carrera.getTiempos().get(participante) != null) {
                ordenados.add(participante);
            }
        }
        ordenados.sort(Comparator.comparing(carrera.getTiempos()::get));
        int[] valores = {10, 8, 6, 5, 4, 3, 2, 1};
        List<Puntos> resultadoPuntos = new ArrayList<>();
        for (int i = 0; i < ordenados.size(); i++) {
            int puntos = i < valores.length ? valores[i] : 0;
            resultadoPuntos.add(new Puntos(ordenados.get(i), puntos, 0, i == 0));
        }
        for (Participante participante : evento.getParticipantes()) {
            if (carrera.getTiempos().get(participante) == null) {
                resultadoPuntos.add(new Puntos(participante, 0, 0, false));
            }
        }
        return resultadoPuntos;
    }
}
