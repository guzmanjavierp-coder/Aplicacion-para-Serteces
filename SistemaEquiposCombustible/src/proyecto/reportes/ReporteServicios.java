package proyecto.reportes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import proyecto.gestion.*;
import proyecto.modelo.*;
import proyecto.util.FechaUtil;

/** RF16. Captura un conjunto de datos consistente para vista previa y exportacion. */
public class ReporteServicios extends Reporte {
    public enum FiltroServicio {
        TODOS("Ambos servicios"), MANTENIMIENTOS("Mantenimientos"), REPARACIONES("Reparaciones");
        private final String etiqueta;
        FiltroServicio(String etiqueta) { this.etiqueta = etiqueta; }
        @Override public String toString() { return etiqueta; }
    }
    private static final class Registro {
        final boolean mantenimiento;
        final String id, codigoEquipo, nombreEquipo, descripcion, observaciones;
        final LocalDate fecha;
        final BigDecimal costo;
        Registro(boolean mantenimiento, String id, String equipo, String nombre, LocalDate fecha,
                 String descripcion, String observaciones, Double costo) {
            if (fecha == null) throw new IllegalStateException("El servicio " + id + " no tiene una fecha valida.");
            if (costo != null && (!Double.isFinite(costo) || costo < 0))
                throw new IllegalStateException("El servicio " + id + " tiene un costo invalido. Corrija el registro antes de generar el reporte.");
            this.mantenimiento = mantenimiento; this.id = id; this.codigoEquipo = equipo;
            this.nombreEquipo = nombre; this.fecha = fecha; this.descripcion = descripcion;
            this.observaciones = observaciones;
            this.costo = costo == null ? null : BigDecimal.valueOf(costo).setScale(2, RoundingMode.HALF_UP);
        }
    }
    private final LocalDate inicio, fin;
    private final String equipo;
    private final FiltroServicio filtro;
    private final List<Registro> registros = new ArrayList<>();

    /** Fechas ambas null: todo el historial. Equipo null: todos los equipos. */
    public ReporteServicios(LocalDate inicio, LocalDate fin, String equipo, FiltroServicio filtro) {
        if ((inicio == null) != (fin == null) || (inicio != null && inicio.isAfter(fin)))
            throw new IllegalArgumentException("Indique un periodo valido: inicio no posterior a fin, o seleccione Todo el historial.");
        this.inicio = inicio; this.fin = fin;
        this.equipo = equipo == null || equipo.trim().isEmpty() ? null : equipo.trim();
        this.filtro = filtro == null ? FiltroServicio.TODOS : filtro;
        Map<String,String> nombres = new LinkedHashMap<>();
        for (Equipo e : new GestionEquipos().listar()) nombres.put(e.getCodigo(), e.getNombre());
        List<Mantenimiento> mantenimientos = new GestionMantenimientos().listar();
        List<Reparacion> reparaciones = new GestionReparaciones().listar();
        if (this.equipo != null && !nombres.containsKey(this.equipo)
                && mantenimientos.stream().noneMatch(m -> this.equipo.equals(m.getCodigoEquipo()))
                && reparaciones.stream().noneMatch(r -> this.equipo.equals(r.getCodigoEquipo())))
            throw new IllegalArgumentException("El equipo indicado no existe ni tiene servicios historicos.");
        if (this.filtro != FiltroServicio.REPARACIONES) for (Mantenimiento m : mantenimientos)
            if (incluir(m.getCodigoEquipo(), m.getFecha())) registros.add(new Registro(true, m.getId(), m.getCodigoEquipo(),
                    nombres.getOrDefault(m.getCodigoEquipo(), "Equipo no disponible"), m.getFecha(), ""+m.getTipo(), m.getObservaciones(), m.getCosto()));
        if (this.filtro != FiltroServicio.MANTENIMIENTOS) for (Reparacion r : reparaciones)
            if (incluir(r.getCodigoEquipo(), r.getFecha())) registros.add(new Registro(false, r.getId(), r.getCodigoEquipo(),
                    nombres.getOrDefault(r.getCodigoEquipo(), "Equipo no disponible"), r.getFecha(), r.getDescripcion(), r.getObservaciones(), r.getCosto()));
        registros.sort(Comparator.comparing((Registro r) -> r.fecha).thenComparing(r -> r.mantenimiento ? 0 : 1).thenComparing(r -> r.id));
    }
    private boolean incluir(String codigo, LocalDate fecha) {
        return (equipo == null || equipo.equals(codigo)) && (inicio == null || FechaUtil.estaEnRango(fecha, inicio, fin));
    }
    public int getCantidadRegistros() { return registros.size(); }
    public int getSinCostoRegistrado() { return (int)registros.stream().filter(r -> r.costo == null).count(); }
    public BigDecimal getCostoTotalRegistrado() { return total(null); }
    private BigDecimal total(Boolean mantenimiento) {
        BigDecimal total = BigDecimal.ZERO.setScale(2);
        for (Registro r : registros) if (r.costo != null && (mantenimiento == null || mantenimiento == r.mantenimiento)) total = total.add(r.costo);
        return total;
    }
    private String[] resumen(String nombre, Boolean mantenimiento) {
        int cantidad = 0, desconocidos = 0;
        for (Registro r : registros) if (mantenimiento == null || mantenimiento == r.mantenimiento) {
            cantidad++; if (r.costo == null) desconocidos++;
        }
        return new String[]{nombre, ""+cantidad, total(mantenimiento).toPlainString(), ""+desconocidos};
    }
    public List<String[]> getResumen() {
        return Arrays.asList(resumen("Mantenimientos", true), resumen("Reparaciones", false), resumen("TOTAL REGISTRADO", null));
    }
    public String[] getColumnasDetalle() {
        return new String[]{"Servicio", "ID", "Codigo equipo", "Equipo", "Fecha", "Descripcion / tipo", "Costo", "Observaciones"};
    }
    public List<String[]> getDetalle() {
        List<String[]> filas = new ArrayList<>();
        for (Registro r : registros) filas.add(new String[]{r.mantenimiento ? "Mantenimiento" : "Reparacion", r.id, r.codigoEquipo,
                r.nombreEquipo, FechaUtil.formatear(r.fecha), r.descripcion, r.costo == null ? "No registrado" : r.costo.toPlainString(), r.observaciones});
        return filas;
    }
    @Override public String getTitulo() {
        return "Mantenimientos y reparaciones | " + (inicio == null ? "Todo el historial" : FechaUtil.formatear(inicio)+" al "+FechaUtil.formatear(fin))
                + " | Equipo: " + (equipo == null ? "Todos" : equipo) + " | " + filtro;
    }
    @Override public String[] getColumnas() {
        return new String[]{"Clase de fila", "Servicio", "ID", "Codigo equipo", "Equipo", "Fecha", "Descripcion / tipo", "Costo registrado", "Observaciones",
                "Inicio del periodo", "Fin del periodo", "Filtro equipo", "Filtro servicio", "Cantidad de registros", "Sin costo registrado"};
    }
    private String[] completar(String clase, String servicio, String id, String codigo, String nombre, String fecha, String descripcion,
                               String costo, String observaciones, String cantidad, String sinCosto) {
        return new String[]{clase, servicio, id, codigo, nombre, fecha, descripcion, costo, observaciones,
                inicio == null ? "Todo el historial" : FechaUtil.formatear(inicio), fin == null ? "Todo el historial" : FechaUtil.formatear(fin),
                equipo == null ? "Todos" : equipo, filtro.toString(), cantidad, sinCosto};
    }
    /** CSV: detalle y resumen identificados para evitar sumar dos veces los mismos servicios. */
    @Override public List<String[]> getFilas() {
        List<String[]> filas = new ArrayList<>();
        for (Registro r : registros) filas.add(completar("DETALLE", r.mantenimiento ? "Mantenimiento" : "Reparacion", r.id, r.codigoEquipo,
                r.nombreEquipo, FechaUtil.formatear(r.fecha), r.descripcion, r.costo == null ? "No registrado" : r.costo.toPlainString(), r.observaciones, "1", r.costo == null ? "1" : "0"));
        List<String[]> resumen = getResumen();
        for (int i = 0; i < resumen.size(); i++) {
            String[] fila = resumen.get(i);
            filas.add(completar(i == 2 ? "TOTAL" : "SUBTOTAL", fila[0], "", "", "", "", "", fila[2],
                    "Solo costos registrados; los desconocidos no se consideran cero.", fila[1], fila[3]));
        }
        return filas;
    }
}
