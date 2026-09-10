package proyecto.modelo;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa una categoria a la que pueden pertenecer los equipos.
 * El atributo "codigo" identifica de forma unica a cada categoria.
 */
public class Categoria implements Serializable {

    private static final long serialVersionUID = 1L;

    private String codigo;
    private String nombre;
    private String descripcion;

    public Categoria(String codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Categoria)) {
            return false;
        }
        Categoria otra = (Categoria) obj;
        return Objects.equals(codigo, otra.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}
