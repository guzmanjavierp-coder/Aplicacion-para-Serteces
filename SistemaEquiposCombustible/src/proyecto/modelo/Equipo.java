package proyecto.modelo;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa un equipo administrado por el sistema.
 * El atributo "codigo" identifica de forma unica a cada equipo,
 * por lo que se utiliza como base para equals() y hashCode().
 */
public class Equipo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String codigo;
    private String nombre;
    private String descripcion;
    private String codigoCategoria;
    private int cantidad;
    private String ubicacion;
    private EstadoEquipo estado;
    private String responsable;

    public Equipo(String codigo, String nombre, String descripcion, String codigoCategoria,
                  int cantidad, String ubicacion, EstadoEquipo estado, String responsable) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.codigoCategoria = codigoCategoria;
        this.cantidad = cantidad;
        this.ubicacion = ubicacion;
        this.estado = estado;
        this.responsable = responsable;
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

    public String getCodigoCategoria() {
        return codigoCategoria;
    }

    public void setCodigoCategoria(String codigoCategoria) {
        this.codigoCategoria = codigoCategoria;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public EstadoEquipo getEstado() {
        return estado;
    }

    public void setEstado(EstadoEquipo estado) {
        this.estado = estado;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    @Override
    public String toString() {
        return "Equipo{" +
                "codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", codigoCategoria='" + codigoCategoria + '\'' +
                ", cantidad=" + cantidad +
                ", ubicacion='" + ubicacion + '\'' +
                ", estado=" + estado +
                ", responsable='" + responsable + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Equipo)) {
            return false;
        }
        Equipo otro = (Equipo) obj;
        return Objects.equals(codigo, otro.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}
