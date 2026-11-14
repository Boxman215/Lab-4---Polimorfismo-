public class ResultadoPartido implements Resultado {
    private final int marcadorA;
    private final int marcadorB;
    private final Integer penalesA;
    private final Integer penalesB;

    public ResultadoPartido(int marcadorA, int marcadorB, Integer penalesA, Integer penalesB) {
        this.marcadorA = marcadorA;
        this.marcadorB = marcadorB;
        this.penalesA = penalesA;
        this.penalesB = penalesB;
    }

    public int getMarcadorA() {
        return marcadorA;
    }

    public int getMarcadorB() {
        return marcadorB;
    }

    public Integer getPenalesA() {
        return penalesA;
    }

    public Integer getPenalesB() {
        return penalesB;
    }
}
