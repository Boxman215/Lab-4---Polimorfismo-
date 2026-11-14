public class Puntos {
    private final Participante participante;
    private final int puntos;
    private final int diferencia;
    private final boolean victoria;

    public Puntos(Participante participante, int puntos, int diferencia, boolean victoria) {
        this.participante = participante;
        this.puntos = puntos;
        this.diferencia = diferencia;
        this.victoria = victoria;
    }

    public Participante getParticipante() {
        return participante;
    }

    public int getPuntos() {
        return puntos;
    }

    public int getDiferencia() {
        return diferencia;
    }

    public boolean isVictoria() {
        return victoria;
    }
}
