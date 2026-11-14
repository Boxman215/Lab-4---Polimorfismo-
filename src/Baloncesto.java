import java.util.List;

public class Baloncesto extends DisciplinaPartido {
    public Baloncesto() {
        super(TipoDisciplina.BALONCESTO, 4, 6, 500.0);
    }

    @Override
    protected void validarMarcador(ResultadoPartido resultado) {
        if (resultado.getMarcadorA() == resultado.getMarcadorB()) {
            throw new IllegalArgumentException("Baloncesto no acepta empates");
        }
        if (resultado.getPenalesA() != null || resultado.getPenalesB() != null) {
            throw new IllegalArgumentException("Baloncesto no utiliza penales");
        }
    }

    @Override
    protected List<Puntos> calcularPuntosPartido(Evento encuentro, ResultadoPartido resultado) {
        Participante a = encuentro.getParticipantes().get(0);
        Participante b = encuentro.getParticipantes().get(1);
        int diferencia = resultado.getMarcadorA() - resultado.getMarcadorB();
        if (diferencia > 0) {
            return lista(new Puntos(a, 2, diferencia, true), new Puntos(b, 1, -diferencia, false));
        }
        return lista(new Puntos(a, 1, diferencia, false), new Puntos(b, 2, -diferencia, true));
    }
}
