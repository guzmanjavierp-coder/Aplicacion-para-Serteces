package proyecto.modelo;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.math.RoundingMode;

/** Una compra ES una entrada: se guarda una sola vez y aumenta el stock una sola vez. */
public class CompraCombustible extends MovimientoCombustible {
    private static final long serialVersionUID = 1L;
    private final String codigoProveedor;
    private final double costoTotal;
    private Double costoPorGalon;

    public CompraCombustible(String id, String tipo, double cantidad, LocalDate fecha,
                            String responsable, String proveedor, double costoTotal) {
        super(id, tipo, TipoMovimientoCombustible.ENTRADA, cantidad, fecha, responsable);
        if (!Double.isFinite(costoTotal) || costoTotal < 0)
            throw new IllegalArgumentException("El costo total debe ser finito y no negativo.");
        this.codigoProveedor = proveedor; this.costoTotal = costoTotal;
    }
    public String getCodigoProveedor() { return codigoProveedor; }
    public double getCostoTotal() { return costoTotal; }
    public double getCostoPorGalon() {
        return costoPorGalon == null ? costoTotal / getCantidad() : costoPorGalon;
    }
    public static CompraCombustible porGalones(String id, String tipo, double galones,
            LocalDate fecha, String responsable, String proveedor, double precio) {
        if (!Double.isFinite(galones) || galones <= 0 || !Double.isFinite(precio) || precio < 0)
            throw new IllegalArgumentException("Galones y costo por galon deben ser numeros validos; cantidad mayor que cero y costo no negativo.");
        double total = BigDecimal.valueOf(galones).multiply(BigDecimal.valueOf(precio))
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
        CompraCombustible compra = new CompraCombustible(id, tipo, galones, fecha, responsable, proveedor, total);
        compra.costoPorGalon = precio;
        return compra;
    }
}
