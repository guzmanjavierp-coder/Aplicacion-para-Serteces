package proyecto.modelo;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa un tipo de combustible administrado por el sistema
 * (por ejemplo: diesel, gasolina regular, gasolina superior).
 *
 * Esta clase NO almacena la existencia actual del combustible.
 * La existencia se calcula posteriormente a partir de la suma de
 * entradas y salidas registradas en MovimientoCombustible, para
 * evitar que un valor guardado se desincronice de los movimientos reales.
 */
public class TipoCombustible implements Serializable {

    private static final long serialVersionUID = 1L;

    private String codigo;
    private String nombre;
    private double nivelMinimo;

    public TipoCombustible(String codigo, String nombre, double nivelMinimo) {
        this.codigo = codigo;
        this.nombre = nombre;
        setNivelMinimo(nivelMinimo);
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

    public double getNivelMinimo() {
        return nivelMinimo;
    }

    // El nivel minimo no puede ser negativo, ya que representa una
    // cantidad de referencia para generar alertas de inventario bajo.
    public void setNivelMinimo(double nivelMinimo) {
        if (nivelMinimo < 0) {
            throw new IllegalArgumentException("El nivel minimo no puede ser negativo.");
        }
        this.nivelMinimo = nivelMinimo;
    }

    @Override
    public String toString() {
        return "TipoCombustible{" +
                "codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", nivelMinimo=" + nivelMinimo +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TipoCombustible)) {
            return false;
        }
        TipoCombustible otro = (TipoCombustible) obj;
        return Objects.equals(codigo, otro.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}
