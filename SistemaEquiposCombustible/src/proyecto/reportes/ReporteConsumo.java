package proyecto.reportes;

import java.time.LocalDate;
import java.util.*;
import proyecto.gestion.*;
import proyecto.modelo.*;
import proyecto.util.FechaUtil;

public class ReporteConsumo extends Reporte {
    private final LocalDate inicio, fin;
    private final List<String[]> filas = new ArrayList<>();
    public ReporteConsumo(LocalDate inicio, LocalDate fin, String codigo) {
        this.inicio = inicio; this.fin = fin;
        GestionConsumo gestion = new GestionConsumo();
        for (ConsumoCombustible c : gestion.consultar(inicio, fin, codigo))
            filas.add(new String[]{"TOTAL", c.getCodigo(), c.getNombre(), ""+c.getCantidad(), "", "", FechaUtil.formatear(inicio), FechaUtil.formatear(fin)});
        GestionTiposCombustible tipos = new GestionTiposCombustible();
        for (MovimientoCombustible m : gestion.consultarSalidas(inicio, fin, codigo)) {
            TipoCombustible t = tipos.buscarPorCodigo(m.getCodigoTipoCombustible());
            filas.add(new String[]{m.getId(), m.getCodigoTipoCombustible(), t == null ? m.getCodigoTipoCombustible() : t.getNombre(), ""+m.getCantidad(), FechaUtil.formatear(m.getFecha()), m.getResponsable(), FechaUtil.formatear(inicio), FechaUtil.formatear(fin)});
        }
    }
    @Override public String getTitulo() { return "Consumo del " + FechaUtil.formatear(inicio) + " al " + FechaUtil.formatear(fin); }
    @Override public String[] getColumnas() { return new String[]{"Movimiento / Total", "Codigo", "Combustible", "Cantidad utilizada", "Fecha de salida", "Responsable", "Inicio del periodo", "Fin del periodo"}; }
    @Override public List<String[]> getFilas() { return Collections.unmodifiableList(filas); }
}
