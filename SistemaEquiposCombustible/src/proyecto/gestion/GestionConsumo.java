package proyecto.gestion;

import java.time.LocalDate;
import java.util.*;
import proyecto.modelo.*;
import proyecto.util.FechaUtil;

public class GestionConsumo {
    public List<MovimientoCombustible> consultarSalidas(LocalDate inicio, LocalDate fin, String codigo) {
        if (inicio == null || fin == null || inicio.isAfter(fin))
            throw new IllegalArgumentException("Indique un periodo valido: inicio no posterior a fin.");
        if (codigo != null && new GestionTiposCombustible().buscarPorCodigo(codigo) == null)
            throw new IllegalArgumentException("El tipo de combustible no existe.");
        List<MovimientoCombustible> resultado = new ArrayList<>();
        for (MovimientoCombustible m : new GestionCombustible().listar())
            if (m.getTipoMovimiento() == TipoMovimientoCombustible.SALIDA
                    && (codigo == null || codigo.equals(m.getCodigoTipoCombustible()))
                    && FechaUtil.estaEnRango(m.getFecha(), inicio, fin)) resultado.add(m);
        resultado.sort(Comparator.comparing(MovimientoCombustible::getFecha).thenComparing(MovimientoCombustible::getId));
        return resultado;
    }
    public List<ConsumoCombustible> consultar(LocalDate inicio, LocalDate fin, String codigo) {
        List<MovimientoCombustible> salidas = consultarSalidas(inicio, fin, codigo);
        Map<String, Double> totales = new LinkedHashMap<>();
        Map<String, String> nombres = new LinkedHashMap<>();
        for (TipoCombustible t : new GestionTiposCombustible().listar())
            if (codigo == null || codigo.equals(t.getCodigo())) {
                totales.put(t.getCodigo(), 0.0); nombres.put(t.getCodigo(), t.getNombre());
            }
        for (MovimientoCombustible m : salidas) totales.merge(m.getCodigoTipoCombustible(), m.getCantidad(), Double::sum);
        List<ConsumoCombustible> resultado = new ArrayList<>();
        for (Map.Entry<String, Double> e : totales.entrySet())
            resultado.add(new ConsumoCombustible(e.getKey(), nombres.getOrDefault(e.getKey(), e.getKey()), e.getValue()));
        return resultado;
    }
}
