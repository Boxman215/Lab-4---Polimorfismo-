import java.util.List;

public class Voleibol extends DisciplinaPartido {
    public Voleibol() {
        super(TipoDisciplina.VOLEIBOL, 4, 6, 450.0);
    }

    @Override
    protected void validarMarcador(ResultadoPartido resultado) {
        boolean valido = (resultado.getMarcadorA() == 3 && resultado.getMarcadorB() >= 0 && resultado.getMarcadorB() <= 2)
                || (resultado.getMarcadorB() == 3 && resultado.getMarcadorA() >= 0 && resultado.getMarcadorA() <= 2);
        if (!valido) {
            throw new IllegalArgumentException("El resultado de voleibol debe ser 3-0, 3-1 o 3-2");
        }
        if (resultado.getPenalesA() != null || resultado.getPenalesB() != null) {
            throw new IllegalArgumentException("Voleibol no utiliza penales");
        }
    }

    @Override
    protected List<Puntos> calcularPuntosPartido(Evento encuentro, ResultadoPartido resultado) {
        Participante a = encuentro.getParticipantes().get(0);
        Participante b = encuentro.getParticipantes().get(1);
        boolean ganaA = resultado.getMarcadorA() > resultado.getMarcadorB();
        int perdedor = ganaA ? resultado.getMarcadorB() : resultado.getMarcadorA();
        int puntosGanador = perdedor == 2 ? 2 : 3;
        int puntosPerdedor = perdedor == 2 ? 1 : 0;
        int diferencia = resultado.getMarcadorA() - resultado.getMarcadorB();
        if (ganaA) {
            return lista(new Puntos(a, puntosGanador, diferencia, true), new Puntos(b, puntosPerdedor, -diferencia, false));
        }
        return lista(new Puntos(a, puntosPerdedor, diferencia, false), new Puntos(b, puntosGanador, -diferencia, true));
    }
}
