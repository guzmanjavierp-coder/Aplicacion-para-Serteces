package proyecto.modelo;

import java.io.Serializable;

/** Relacion separada para conservar compatibilidad con los archivos de entregas anteriores. */
public class AsociacionProveedor implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String codigoProveedor;
    private final TipoRegistroProveedor tipo;
    private final String idRegistro;
    public AsociacionProveedor(String proveedor, TipoRegistroProveedor tipo, String id) {
        this.codigoProveedor = proveedor; this.tipo = tipo; this.idRegistro = id;
    }
    public String getCodigoProveedor() { return codigoProveedor; }
    public TipoRegistroProveedor getTipo() { return tipo; }
    public String getIdRegistro() { return idRegistro; }
}
