package proyecto.reportes;

import java.util.*;
import proyecto.gestion.*;
import proyecto.modelo.*;
import proyecto.util.FechaUtil;

/** RF11: reportes generales; RF16 queda pendiente. */
public class ReporteInventario extends Reporte {
    public enum Seccion { EQUIPOS, COMBUSTIBLE, MANTENIMIENTOS, REPARACIONES }
    private final Seccion seccion;
    private final List<String[]> filas = new ArrayList<>();
    public ReporteInventario(Seccion seccion) {
        this.seccion = Objects.requireNonNull(seccion);
        switch (seccion) {
            case EQUIPOS:
                for (Equipo e : new GestionEquipos().listar()) filas.add(new String[]{e.getCodigo(), e.getNombre(), e.getDescripcion(), e.getCodigoCategoria(), ""+e.getCantidad(), e.getUbicacion(), ""+e.getEstado(), e.getResponsable()});
                break;
            case COMBUSTIBLE:
                GestionCombustible gestion = new GestionCombustible();
                for (TipoCombustible t : new GestionTiposCombustible().listar()) filas.add(new String[]{t.getCodigo(), t.getNombre(), ""+gestion.calcularExistencia(t.getCodigo()), ""+t.getNivelMinimo()});
                break;
            case MANTENIMIENTOS:
                for (Mantenimiento m : new GestionMantenimientos().listar()) filas.add(new String[]{m.getId(), m.getCodigoEquipo(), FechaUtil.formatear(m.getFecha()), ""+m.getTipo(), m.getObservaciones()});
                break;
            case REPARACIONES:
                for (Reparacion r : new GestionReparaciones().listar()) filas.add(new String[]{r.getId(), r.getCodigoEquipo(), FechaUtil.formatear(r.getFecha()), r.getDescripcion(), ""+r.getCosto(), r.getObservaciones()});
                break;
        }
    }
    @Override public String getTitulo() { return "Inventario - " + seccion; }
    @Override public String[] getColumnas() {
        switch (seccion) {
            case EQUIPOS: return new String[]{"Codigo", "Nombre", "Descripcion", "Categoria", "Cantidad", "Ubicacion", "Estado", "Responsable"};
            case COMBUSTIBLE: return new String[]{"Codigo", "Combustible", "Existencia", "Nivel minimo"};
            case MANTENIMIENTOS: return new String[]{"ID", "Equipo", "Fecha", "Tipo", "Observaciones"};
            default: return new String[]{"ID", "Equipo", "Fecha", "Descripcion", "Costo", "Observaciones"};
        }
    }
    @Override public List<String[]> getFilas() { return Collections.unmodifiableList(filas); }
}
