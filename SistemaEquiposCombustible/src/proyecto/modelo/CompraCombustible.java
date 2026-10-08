package proyecto.modelo;

import java.time.LocalDate;

/** Una compra ES una entrada: se guarda una sola vez y aumenta el stock una sola vez. */
public class CompraCombustible extends MovimientoCombustible {
    private static final long serialVersionUID = 1L;
    private final String codigoProveedor;
    private final double costoTotal;

    public CompraCombustible(String id, String tipo, double cantidad, LocalDate fecha,
                            String responsable, String proveedor, double costoTotal) {
        super(id, tipo, TipoMovimientoCombustible.ENTRADA, cantidad, fecha, responsable);
        if (!Double.isFinite(costoTotal) || costoTotal < 0)
            throw new IllegalArgumentException("El costo total debe ser finito y no negativo.");
        this.codigoProveedor = proveedor; this.costoTotal = costoTotal;
    }
    public String getCodigoProveedor() { return codigoProveedor; }
    public double getCostoTotal() { return costoTotal; }
}
