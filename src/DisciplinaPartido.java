import java.util.List;

public abstract class DisciplinaPartido extends Disciplina {
    protected DisciplinaPartido(TipoDisciplina tipo, int minimo, int maximo, double cuota) {
        super(tipo, minimo, maximo, cuota);
    }

    @Override
    public boolean esCarrera() {
        return false;
    }

    @Override
    public void validarResultado(Evento evento, Resultado resultado) {
        validarResultadoComun(evento, resultado, ResultadoPartido.class);
        ResultadoPartido partido = (ResultadoPartido) resultado;
        if (partido.getMarcadorA() < 0 || partido.getMarcadorB() < 0) {
            throw new IllegalArgumentException("Los marcadores no pueden ser negativos");
        }
        validarMarcador(partido);
    }

    @Override
    public List<Puntos> calcularPuntos(Evento evento, Resultado resultado) {
        validarResultado(evento, resultado);
        return calcularPuntosPartido(evento, (ResultadoPartido) resultado);
    }

    protected abstract void validarMarcador(ResultadoPartido resultado);

    protected abstract List<Puntos> calcularPuntosPartido(Evento encuentro, ResultadoPartido resultado);
}
