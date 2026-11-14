import java.util.List;

public class FutbolSala extends DisciplinaPartido {
    public FutbolSala() {
        super(TipoDisciplina.FUTBOL_SALA, 4, 8, 600.0);
    }

    @Override
    protected void validarMarcador(ResultadoPartido resultado) {
        if (resultado.getPenalesA() != null || resultado.getPenalesB() != null) {
            throw new IllegalArgumentException("Futbol sala no utiliza penales");
        }
    }

    @Override
    protected List<Puntos> calcularPuntosPartido(Evento encuentro, ResultadoPartido resultado) {
        Participante a = encuentro.getParticipantes().get(0);
        Participante b = encuentro.getParticipantes().get(1);
        int diferencia = resultado.getMarcadorA() - resultado.getMarcadorB();
        if (diferencia > 0) {
            return lista(new Puntos(a, 3, diferencia, true), new Puntos(b, 0, -diferencia, false));
        }
        if (diferencia < 0) {
            return lista(new Puntos(a, 0, diferencia, false), new Puntos(b, 3, -diferencia, true));
        }
        return lista(new Puntos(a, 1, 0, false), new Puntos(b, 1, 0, false));
    }
}
