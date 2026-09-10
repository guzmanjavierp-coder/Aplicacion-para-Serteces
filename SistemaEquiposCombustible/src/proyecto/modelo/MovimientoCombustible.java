package proyecto.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa un movimiento de combustible: una entrada (ingreso) o
 * una salida (retiro) de una cantidad especifica de un tipo de combustible.
 * El atributo "id" identifica de forma unica a cada movimiento.
 */
public class MovimientoCombustible implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String codigoTipoCombustible;
    private TipoMovimientoCombustible tipoMovimiento;
    private double cantidad;
    private LocalDate fecha;
    private String responsable;

    public MovimientoCombustible(String id, String codigoTipoCombustible,
                                  TipoMovimientoCombustible tipoMovimiento, double cantidad,
                                  LocalDate fecha, String responsable) {
        this.id = id;
        this.codigoTipoCombustible = codigoTipoCombustible;
        this.tipoMovimiento = tipoMovimiento;
        setCantidad(cantidad);
        this.fecha = fecha;
        this.responsable = responsable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigoTipoCombustible() {
        return codigoTipoCombustible;
    }

    public void setCodigoTipoCombustible(String codigoTipoCombustible) {
        this.codigoTipoCombustible = codigoTipoCombustible;
    }

    public TipoMovimientoCombustible getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimientoCombustible tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public double getCantidad() {
        return cantidad;
    }

    // La cantidad debe ser mayor que cero: un movimiento de cantidad
    // cero o negativa no representa una entrada ni una salida valida.
    public void setCantidad(double cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad del movimiento debe ser mayor que cero.");
        }
        this.cantidad = cantidad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    @Override
    public String toString() {
        return "MovimientoCombustible{" +
                "id='" + id + '\'' +
                ", codigoTipoCombustible='" + codigoTipoCombustible + '\'' +
                ", tipoMovimiento=" + tipoMovimiento +
                ", cantidad=" + cantidad +
                ", fecha=" + fecha +
                ", responsable='" + responsable + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MovimientoCombustible)) {
            return false;
        }
        MovimientoCombustible otro = (MovimientoCombustible) obj;
        return Objects.equals(id, otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
