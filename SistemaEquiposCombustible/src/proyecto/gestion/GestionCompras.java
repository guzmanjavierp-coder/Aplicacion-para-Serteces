package proyecto.gestion;

import java.util.List;
import java.util.stream.Collectors;
import proyecto.modelo.CompraCombustible;
import proyecto.modelo.MovimientoCombustible;
import proyecto.modelo.TipoMovimientoCombustible;
import proyecto.persistencia.ArchivoCombustible;
import proyecto.util.Validador;

public class GestionCompras {
    public CompraCombustible buscarPorId(String id) {
        for (CompraCombustible c : listar()) if (c.getId().equals(id)) return c;
        return null;
    }
    /** Reemplaza la entrada original, nunca agrega una segunda entrada. */
    public String modificar(CompraCombustible compra) {
        if (compra == null) return "La compra es obligatoria.";
        CompraCombustible anterior = buscarPorId(compra.getId());
        if (anterior == null) return "La compra no existe.";
        if (new GestionProveedores().buscarPorCodigo(compra.getCodigoProveedor()) == null)
            return "El proveedor indicado no existe.";
        if (new GestionTiposCombustible().buscarPorCodigo(compra.getCodigoTipoCombustible()) == null)
            return "El tipo de combustible no existe.";
        if (!Validador.esTextoValido(compra.getResponsable())) return "El responsable es obligatorio.";
        if (compra.getFecha() == null || !compra.getFecha().equals(anterior.getFecha()))
            return "La edicion debe conservar la fecha original de la compra.";
        if (!Double.isFinite(compra.getCantidad()) || compra.getCantidad() <= 0
                || !Double.isFinite(compra.getCostoTotal()) || compra.getCostoTotal() < 0)
            return "Cantidad o costo invalido.";
        ArchivoCombustible archivo = new ArchivoCombustible();
        List<MovimientoCombustible> movimientos = archivo.leer();
        compra.setTipoMovimiento(TipoMovimientoCombustible.ENTRADA);
        for (int i = 0; i < movimientos.size(); i++)
            if (movimientos.get(i).getId().equals(compra.getId())) movimientos.set(i, compra);
        for (String codigo : new String[]{anterior.getCodigoTipoCombustible(), compra.getCodigoTipoCombustible()}) {
            java.math.BigDecimal saldo = java.math.BigDecimal.ZERO;
            for (MovimientoCombustible m : movimientos) if (codigo.equals(m.getCodigoTipoCombustible())) {
                java.math.BigDecimal cantidad = java.math.BigDecimal.valueOf(m.getCantidad());
                saldo = m.getTipoMovimiento() == TipoMovimientoCombustible.ENTRADA ? saldo.add(cantidad) : saldo.subtract(cantidad);
            }
            if (saldo.signum() < 0) return "No se puede editar: la compra respalda combustible que ya fue retirado (" + codigo + ").";
        }
        archivo.guardar(movimientos);
        return null;
    }
    public String registrar(CompraCombustible compra) {
        if (compra == null) return "La compra es obligatoria.";
        if (new GestionProveedores().buscarPorCodigo(compra.getCodigoProveedor()) == null)
            return "El proveedor indicado no existe.";
        if (!Double.isFinite(compra.getCostoTotal()) || compra.getCostoTotal() < 0)
            return "El costo total debe ser finito y no negativo.";
        return new GestionCombustible().registrarEntrada(compra);
    }
    public List<CompraCombustible> listar() {
        return new GestionCombustible().listar().stream().filter(m -> m instanceof CompraCombustible)
                .map(m -> (CompraCombustible) m).collect(Collectors.toList());
    }
    public List<CompraCombustible> buscarPorProveedor(String codigo) {
        return listar().stream().filter(c -> c.getCodigoProveedor().equals(codigo)).collect(Collectors.toList());
    }
}
