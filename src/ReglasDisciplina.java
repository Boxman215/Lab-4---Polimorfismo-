import java.util.List;

public interface ReglasDisciplina {
    TipoDisciplina getTipo();

    int getMinimo();

    int getMaximo();

    double getCuota();

    boolean esCarrera();

    void validarResultado(Evento evento, Resultado resultado);

    List<Puntos> calcularPuntos(Evento evento, Resultado resultado);
}
