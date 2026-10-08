package proyecto.gestion;

import java.util.List;
import java.util.stream.Collectors;
import proyecto.modelo.CompraCombustible;

public class GestionCompras {
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
