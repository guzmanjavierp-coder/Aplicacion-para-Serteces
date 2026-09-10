package proyecto.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa un mantenimiento realizado a un equipo especifico.
 * El atributo "id" identifica de forma unica a cada mantenimiento.
 */
public class Mantenimiento implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String codigoEquipo;
    private LocalDate fecha;
    private TipoMantenimiento tipo;
    private String observaciones;

    public Mantenimiento(String id, String codigoEquipo, LocalDate fecha,
                          TipoMantenimiento tipo, String observaciones) {
        this.id = id;
        this.codigoEquipo = codigoEquipo;
        this.fecha = fecha;
        this.tipo = tipo;
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

    public TipoMantenimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMantenimiento tipo) {
        this.tipo = tipo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "Mantenimiento{" +
                "id='" + id + '\'' +
                ", codigoEquipo='" + codigoEquipo + '\'' +
                ", fecha=" + fecha +
                ", tipo=" + tipo +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Mantenimiento)) {
            return false;
        }
        Mantenimiento otro = (Mantenimiento) obj;
        return Objects.equals(id, otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
