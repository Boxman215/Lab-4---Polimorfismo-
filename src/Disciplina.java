import java.util.ArrayList;
import java.util.List;

public abstract class Disciplina implements ReglasDisciplina {
    private final TipoDisciplina tipo;
    private final int minimo;
    private final int maximo;
    private final double cuota;

    protected Disciplina(TipoDisciplina tipo, int minimo, int maximo, double cuota) {
        this.tipo = tipo;
        this.minimo = minimo;
        this.maximo = maximo;
        this.cuota = cuota;
    }

    @Override
    public TipoDisciplina getTipo() {
        return tipo;
    }

    @Override
    public int getMinimo() {
        return minimo;
    }

    @Override
    public int getMaximo() {
        return maximo;
    }

    @Override
    public double getCuota() {
        return cuota;
    }

    protected void validarResultadoComun(Evento evento, Resultado resultado, Class<?> tipoResultado) {
        if (evento == null || resultado == null) {
            throw new IllegalArgumentException("El evento y el resultado son obligatorios");
        }
        if (!tipoResultado.isInstance(resultado)) {
            throw new IllegalArgumentException("El resultado no corresponde a la disciplina");
        }
    }

    protected List<Puntos> lista(Puntos... puntos) {
        List<Puntos> resultado = new ArrayList<>();
        for (Puntos punto : puntos) {
            resultado.add(punto);
        }
        return resultado;
    }
}
