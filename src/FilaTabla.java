public class FilaTabla {
    private final Participante participante;
    private int puntos;
    private int diferencia;
    private int victorias;

    public FilaTabla(Participante participante) {
        this.participante = participante;
    }

    public void acumular(Puntos puntosEvento) {
        puntos += puntosEvento.getPuntos();
        diferencia += puntosEvento.getDiferencia();
        if (puntosEvento.isVictoria()) {
            victorias++;
        }
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

    public int getVictorias() {
        return victorias;
    }
}
