import java.util.List;

public class Encuentro extends Evento {
    public Encuentro(int id, Torneo torneo, List<Participante> participantes) {
        super(id, torneo, participantes);
        if (participantes.size() != 2) {
            throw new IllegalArgumentException("Un encuentro necesita dos participantes");
        }
    }

    @Override
    protected String tipoEvento() {
        return "Encuentro";
    }

    @Override
    public String resultadoTexto() {
        ResultadoPartido partido = (ResultadoPartido) getResultado();
        String texto = partido.getMarcadorA() + " - " + partido.getMarcadorB();
        if (partido.getPenalesA() != null) {
            texto += " penales " + partido.getPenalesA() + " - " + partido.getPenalesB();
        }
        return texto;
    }
}
