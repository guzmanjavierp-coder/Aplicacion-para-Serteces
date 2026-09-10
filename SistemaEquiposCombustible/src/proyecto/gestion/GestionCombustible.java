package proyecto.gestion;

import java.util.ArrayList;
import java.util.List;
import proyecto.modelo.MovimientoCombustible;
import proyecto.modelo.TipoCombustible;
import proyecto.modelo.TipoMovimientoCombustible;
import proyecto.persistencia.ArchivoCombustible;
import proyecto.persistencia.ArchivoTiposCombustible;
import proyecto.util.Validador;

public class GestionCombustible {
    private final ArchivoCombustible archivoCombustible = new ArchivoCombustible();
    private final ArchivoTiposCombustible archivoTiposCombustible = new ArchivoTiposCombustible();
    public String registrarEntrada(MovimientoCombustible movimiento) {
        if (movimiento == null) return "El movimiento es obligatorio.";
        movimiento.setTipoMovimiento(TipoMovimientoCombustible.ENTRADA);
        String error = validarDatosBasicos(movimiento); if (error != null) return error;
        List<MovimientoCombustible> movimientos = archivoCombustible.leer(); movimientos.add(movimiento); archivoCombustible.guardar(movimientos); return null;
    }
    public String registrarSalida(MovimientoCombustible movimiento) {
        if (movimiento == null) return "El movimiento es obligatorio.";
        movimiento.setTipoMovimiento(TipoMovimientoCombustible.SALIDA);
        String error = validarDatosBasicos(movimiento); if (error != null) return error;
        double existenciaActual = calcularExistencia(movimiento.getCodigoTipoCombustible());
        if (movimiento.getCantidad() > existenciaActual) return "No hay existencia suficiente para realizar la salida.";
        List<MovimientoCombustible> movimientos = archivoCombustible.leer(); movimientos.add(movimiento); archivoCombustible.guardar(movimientos); return null;
    }
    public List<MovimientoCombustible> listar() { return archivoCombustible.leer(); }
    public List<MovimientoCombustible> buscarPorTipoCombustible(String codigo) {
        List<MovimientoCombustible> r = new ArrayList<>();
        for (MovimientoCombustible m : archivoCombustible.leer()) if (m.getCodigoTipoCombustible().equals(codigo)) r.add(m);
        return r;
    }
    public double calcularExistencia(String codigo) {
        double existencia = 0;
        for (MovimientoCombustible m : archivoCombustible.leer()) if (m.getCodigoTipoCombustible().equals(codigo)) {
            if (m.getTipoMovimiento() == TipoMovimientoCombustible.ENTRADA) existencia += m.getCantidad();
            else if (m.getTipoMovimiento() == TipoMovimientoCombustible.SALIDA) existencia -= m.getCantidad();
        }
        return existencia;
    }
    private String validarDatosBasicos(MovimientoCombustible m) {
        if (!Validador.esTextoValido(m.getId())) return "El ID del movimiento es obligatorio.";
        for (MovimientoCombustible x : archivoCombustible.leer()) if (x.getId().equals(m.getId())) return "Ya existe un movimiento con ese ID.";
        if (!Validador.esTextoValido(m.getCodigoTipoCombustible())) return "El codigo de tipo de combustible es obligatorio.";
        TipoCombustible encontrado = null; for (TipoCombustible t : archivoTiposCombustible.leer()) if (t.getCodigo().equals(m.getCodigoTipoCombustible())) { encontrado=t; break; }
        if (encontrado == null) return "El tipo de combustible indicado no existe.";
        if (!Validador.esCantidadMayorQueCero(m.getCantidad())) return "La cantidad debe ser mayor que cero.";
        if (m.getFecha() == null) return "La fecha del movimiento es obligatoria.";
        if (!Validador.esTextoValido(m.getResponsable())) return "El responsable del movimiento es obligatorio.";
        return null;
    }
}
