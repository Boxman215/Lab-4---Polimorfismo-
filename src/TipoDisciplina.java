public enum TipoDisciplina {
    FUTBOL_SALA("Futbol sala"),
    FUTBOL_SALA_PENALES("Futbol sala con penales"),
    BALONCESTO("Baloncesto"),
    VOLEIBOL("Voleibol"),
    RELEVOS("Relevos");

    private final String nombre;

    TipoDisciplina(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
