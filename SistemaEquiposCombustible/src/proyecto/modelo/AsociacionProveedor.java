package proyecto.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import proyecto.util.FechaUtil;

/** Relacion separada para conservar compatibilidad con los archivos de entregas anteriores. */
public class AsociacionProveedor implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String codigoProveedor;
    private final TipoRegistroProveedor tipo;
    private final String idRegistro;
    private final LocalDate fechaAsociacion;
    public AsociacionProveedor(String proveedor, TipoRegistroProveedor tipo, String id) {
        this.fechaAsociacion = FechaUtil.hoy();
        this.codigoProveedor = proveedor; this.tipo = tipo; this.idRegistro = id;
    }
    public LocalDate getFechaAsociacion() { return fechaAsociacion; }
    public String getCodigoProveedor() { return codigoProveedor; }
    public TipoRegistroProveedor getTipo() { return tipo; }
    public String getIdRegistro() { return idRegistro; }
}
