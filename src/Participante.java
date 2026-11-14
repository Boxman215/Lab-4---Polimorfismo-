import java.util.Objects;

public class Participante implements Comparable<Participante> {
    private final String codigo;
    private final String nombre;
    private final String empresa;

    public Participante(String codigo, String nombre, String empresa) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo no puede estar vacio");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio");
        }
        if (empresa == null || empresa.trim().isEmpty()) {
            throw new IllegalArgumentException("La empresa no puede estar vacia");
        }
        this.codigo = codigo.trim();
        this.nombre = nombre.trim();
        this.empresa = empresa.trim();
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmpresa() {
        return empresa;
    }

    @Override
    public int compareTo(Participante otro) {
        int comparacion = nombre.compareToIgnoreCase(otro.nombre);
        if (comparacion != 0) {
            return comparacion;
        }
        return codigo.compareToIgnoreCase(otro.codigo);
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Participante)) {
            return false;
        }
        Participante otro = (Participante) objeto;
        return codigo.equalsIgnoreCase(otro.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo.toUpperCase());
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (" + empresa + ")";
    }
}
