import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResultadoRelevos implements Resultado {
    private final Map<Participante, Double> tiempos;

    public ResultadoRelevos(Map<Participante, Double> tiempos) {
        this.tiempos = new LinkedHashMap<>(tiempos);
    }

    public Map<Participante, Double> getTiempos() {
        return Collections.unmodifiableMap(tiempos);
    }
}
