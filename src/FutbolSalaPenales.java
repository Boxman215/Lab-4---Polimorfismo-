import java.util.List;

public class FutbolSalaPenales extends DisciplinaPartido {
    public FutbolSalaPenales() {
        super(TipoDisciplina.FUTBOL_SALA_PENALES, 4, 8, 750.0);
    }

    @Override
    protected void validarMarcador(ResultadoPartido resultado) {
        boolean hayPenalA = resultado.getPenalesA() != null;
        boolean hayPenalB = resultado.getPenalesB() != null;
        if (resultado.getMarcadorA() == resultado.getMarcadorB()) {
            if (!hayPenalA || !hayPenalB) {
                throw new IllegalArgumentException("El empate necesita penales");
            }
            if (resultado.getPenalesA() < 0 || resultado.getPenalesB() < 0) {
                throw new IllegalArgumentException("Los penales no pueden ser negativos");
            }
            if (resultado.getPenalesA().equals(resultado.getPenalesB())) {
                throw new IllegalArgumentException("Los penales no pueden quedar empatados");
            }
        } else if (hayPenalA || hayPenalB) {
            throw new IllegalArgumentException("No se registran penales si los goles son diferentes");
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
        if (resultado.getPenalesA() > resultado.getPenalesB()) {
            return lista(new Puntos(a, 2, 0, true), new Puntos(b, 1, 0, false));
        }
        return lista(new Puntos(a, 1, 0, false), new Puntos(b, 2, 0, true));
    }
}
