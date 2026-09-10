package proyecto.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa una reparacion realizada a un equipo especifico.
 * El atributo "id" identifica de forma unica a cada reparacion.
 */
public class Reparacion implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String codigoEquipo;
    private LocalDate fecha;
    private String descripcion;
    private double costo;
    private String observaciones;

    public Reparacion(String id, String codigoEquipo, LocalDate fecha,
                       String descripcion, double costo, String observaciones) {
        this.id = id;
        this.codigoEquipo = codigoEquipo;
        this.fecha = fecha;
        this.descripcion = descripcion;
        setCosto(costo);
        this.observaciones = observaciones;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigoEquipo() {
        return codigoEquipo;
    }

    public void setCodigoEquipo(String codigoEquipo) {
        this.codigoEquipo = codigoEquipo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getCosto() {
        return costo;
    }

    // El costo no puede ser negativo. Se valida directamente en el setter
    // para que la clase nunca quede en un estado invalido, sin importar
    // desde donde se modifique.
    public void setCosto(double costo) {
        if (costo < 0) {
            throw new IllegalArgumentException("El costo de la reparacion no puede ser negativo.");
        }
        this.costo = costo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "Reparacion{" +
                "id='" + id + '\'' +
                ", codigoEquipo='" + codigoEquipo + '\'' +
                ", fecha=" + fecha +
                ", descripcion='" + descripcion + '\'' +
                ", costo=" + costo +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Reparacion)) {
            return false;
        }
        Reparacion otra = (Reparacion) obj;
        return Objects.equals(id, otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
