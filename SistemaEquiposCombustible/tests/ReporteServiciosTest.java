import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import proyecto.gestion.*;
import proyecto.modelo.*;
import proyecto.persistencia.*;
import proyecto.reportes.*;
import proyecto.reportes.ReporteServicios.FiltroServicio;
import proyecto.util.FechaUtil;
import proyecto.vista.*;

/** RF16: compatibilidad real con el esquema anterior, costos, filtros, CSV y Swing. */
public class ReporteServiciosTest {
    private static int comprobaciones;
    private static final LocalDate INICIO = LocalDate.of(2026,1,1), FIN = LocalDate.of(2026,1,31);
    private static void verificar(boolean valor, String mensaje) { comprobaciones++; if (!valor) throw new AssertionError(mensaje); }
    private static void correcto(String error) { verificar(error == null, "Operacion fallida: "+error); }
    private static void rechaza(Runnable accion) {
        boolean rechazo=false; try { accion.run(); } catch(IllegalArgumentException | IllegalStateException ex) { rechazo=true; }
        verificar(rechazo, "Se esperaba un error controlado.");
    }
    private static Mantenimiento mantenimiento(String id, String equipo, LocalDate fecha, double costo) {
        return new Mantenimiento(id,equipo,fecha,TipoMantenimiento.PREVENTIVO,"Revision de prueba",costo);
    }
    private static ReporteServicios reporte(String equipo, FiltroServicio filtro) { return new ReporteServicios(INICIO,FIN,equipo,filtro); }
    private static Object campo(Object objeto,String nombre) throws Exception { Field f=objeto.getClass().getDeclaredField(nombre); f.setAccessible(true); return f.get(objeto); }
    private static void metodo(Object objeto,String nombre) throws Exception { Method m=objeto.getClass().getDeclaredMethod(nombre); m.setAccessible(true); m.invoke(objeto); }
    private static java.util.List<Component> componentes(Container c) {
        java.util.List<Component> resultado=new ArrayList<>();
        for(Component hijo:c.getComponents()) { resultado.add(hijo); if(hijo instanceof Container) resultado.addAll(componentes((Container)hijo)); }
        return resultado;
    }
    private static void distribuir(Container c) { c.doLayout(); for(Component hijo:c.getComponents()) if(hijo instanceof Container) distribuir((Container)hijo); }
    private static void imagen(JFrame ventana,String nombre) throws Exception {
        for(Component c:componentes(ventana.getContentPane())) if(c instanceof JScrollPane) {
            JScrollPane s=(JScrollPane)c; if(s.getViewport().getView() instanceof JTable) s.setColumnHeaderView(((JTable)s.getViewport().getView()).getTableHeader());
        }
        ventana.getContentPane().setSize(ventana.getSize()); distribuir(ventana.getContentPane());
        BufferedImage imagen=new BufferedImage(ventana.getWidth(),ventana.getHeight(),BufferedImage.TYPE_INT_RGB);
        Graphics2D g=imagen.createGraphics(); ventana.getContentPane().printAll(g); g.dispose(); ImageIO.write(imagen,"png",Paths.get(nombre+".png").toFile());
    }
    public static void main(String[] args) throws Exception {
        verificar(Files.exists(Paths.get(".pruebas-aisladas")),"Directorio aislado");
        try {
            correcto(new GestionCategorias().registrar(new Categoria("CAT","Maquinaria","Pruebas")));
            GestionEquipos equipos=new GestionEquipos();
            for(String codigo:new String[]{"EQ1","EQ2","EQ3"}) correcto(equipos.registrar(new Equipo(codigo,"Equipo "+codigo,"Descripcion","CAT",1,"Bodega",EstadoEquipo.DISPONIBLE,"Javier")));
            // Fixture serializado con la clase Mantenimiento ANTERIOR a incorporar costo.
            Path fixture=Paths.get(System.getProperty("proyecto.pruebas.fixtures"),"mantenimiento-anterior.dat");
            Files.copy(fixture,Paths.get("mantenimientos.dat"));
            GestionMantenimientos gestion=new GestionMantenimientos();
            Mantenimiento anterior=gestion.listar().get(0);
            verificar(anterior.getId().equals("LEGACY") && !anterior.tieneCostoRegistrado() && anterior.getCosto()==null,"Archivo anterior conserva costo desconocido");
            verificar(anterior.getFecha().equals(INICIO),"Fecha anterior conservada");
            verificar(gestion.registrar(new Mantenimiento("SIN-COSTO","EQ1",INICIO,TipoMantenimiento.PREVENTIVO,"Nuevo"))!=null,"Costo requerido en nuevos registros");
            verificar(gestion.registrar(null)!=null,"Mantenimiento nulo");
            verificar(equipos.buscarPorCodigo("EQ1").getEstado() == EstadoEquipo.DISPONIBLE, "Registro invalido conserva estado");
            correcto(gestion.registrar(mantenimiento("M2","EQ1",INICIO.plusDays(9),125.50)));
            verificar(new GestionEquipos().buscarPorCodigo("EQ1").getEstado() == EstadoEquipo.EN_MANTENIMIENTO, "Nuevo mantenimiento actualiza y persiste estado del equipo");
            verificar(equipos.buscarPorCodigo("EQ2").getEstado() == EstadoEquipo.DISPONIBLE, "Otros equipos conservan estado");
            verificar(gestion.registrar(mantenimiento("M2","EQ2",INICIO,10)) != null, "Rechaza mantenimiento duplicado");
            verificar(equipos.buscarPorCodigo("EQ2").getEstado() == EstadoEquipo.DISPONIBLE, "Duplicado no cambia estado del equipo");
            correcto(gestion.registrar(mantenimiento("M3","EQ2",INICIO.plusDays(14),0)));
            correcto(gestion.registrar(mantenimiento("M4","EQ1",FIN.plusDays(1),40)));
            verificar(gestion.listar().stream().filter(m->m.getId().equals("M3")).findFirst().get().tieneCostoRegistrado(),"Costo cero es un dato conocido");
            rechaza(()->mantenimiento("INVALIDO","EQ1",INICIO,-1));
            rechaza(()->mantenimiento("INVALIDO","EQ1",INICIO,Double.NaN));
            rechaza(()->mantenimiento("INVALIDO","EQ1",INICIO,Double.POSITIVE_INFINITY));
            verificar(mantenimiento("REDONDEO","EQ1",INICIO,1.005).getCosto()==1.01,"Redondeo monetario de mantenimiento");
            GestionReparaciones reparaciones=new GestionReparaciones();
            Equipo equipoReparacion = equipos.buscarPorCodigo("EQ1");
            equipoReparacion.setEstado(EstadoEquipo.DISPONIBLE);
            correcto(equipos.modificar(equipoReparacion));
            verificar(reparaciones.registrar(null) != null, "Rechaza reparacion nula");
            verificar(reparaciones.registrar(new Reparacion("INVALIDA","EQ1",FIN,"",1,"Prueba")) != null, "Rechaza reparacion incompleta");
            verificar(equipos.buscarPorCodigo("EQ1").getEstado() == EstadoEquipo.DISPONIBLE, "Reparacion invalida conserva disponible");
            correcto(reparaciones.registrar(new Reparacion("R1","EQ1",FIN,"Cambio; \"sello\"",74.50,"Revisado\nSin fugas")));
            verificar(new GestionEquipos().buscarPorCodigo("EQ1").getEstado() == EstadoEquipo.REPARACION, "Reparacion cambia disponible y persiste estado");
            verificar(equipos.buscarPorCodigo("EQ3").getEstado() == EstadoEquipo.DISPONIBLE, "Reparacion no cambia otros equipos");
            verificar(reparaciones.registrar(new Reparacion("R1","EQ3",FIN,"Duplicado",1,"Prueba")) != null, "Rechaza reparacion duplicada");
            verificar(equipos.buscarPorCodigo("EQ3").getEstado() == EstadoEquipo.DISPONIBLE, "Duplicado conserva estado");
            correcto(reparaciones.registrar(new Reparacion("R2","EQ2",INICIO.plusDays(14),"Ajuste",20.10,"Correcto")));
            correcto(reparaciones.registrar(new Reparacion("R3","EQ1",INICIO.minusDays(1),"Servicio anterior",10,"Correcto")));
            rechaza(()->new Reparacion("INVALIDO","EQ1",INICIO,"Invalido",Double.NaN,"Obs"));
            rechaza(()->new Reparacion("INVALIDO","EQ1",INICIO,"Invalido",Double.POSITIVE_INFINITY,"Obs"));

            ReporteServicios ambos=reporte("EQ1",FiltroServicio.TODOS);
            verificar(ambos.getCantidadRegistros()==3,"Filtros simultaneos y limites inclusivos");
            verificar(ambos.getCostoTotalRegistrado().equals(new BigDecimal("200.00")),"Total conocido de ambos servicios");
            verificar(ambos.getSinCostoRegistrado()==1,"Costo desconocido visible");
            verificar(ambos.getDetalle().get(0)[1].equals("LEGACY") && ambos.getDetalle().get(0)[6].equals("No registrado"),"Orden de fechas y texto para costo desconocido");
            verificar(ambos.getDetalle().get(2)[4].equals("31/01/2026"),"Incluye el ultimo dia");
            verificar(ambos.getDetalle().get(0)[3].equals("Equipo EQ1"),"Nombre de equipo");
            verificar(ambos.getResumen().get(0)[2].equals("125.50") && ambos.getResumen().get(1)[2].equals("74.50"),"Subtotales por servicio");
            verificar(ambos.getResumen().get(2)[1].equals("3") && ambos.getResumen().get(2)[3].equals("1"),"Resumen incluye conteos y costos faltantes");
            verificar(reporte("EQ1",FiltroServicio.MANTENIMIENTOS).getCantidadRegistros()==2,"Filtro solo mantenimientos");
            verificar(reporte("EQ1",FiltroServicio.REPARACIONES).getCantidadRegistros()==1,"Filtro solo reparaciones");
            verificar(reporte("EQ2",FiltroServicio.TODOS).getCostoTotalRegistrado().equals(new BigDecimal("20.10")),"Total por otro equipo con servicio gratuito");
            verificar(reporte(null,FiltroServicio.TODOS).getCantidadRegistros()==5,"Todos los equipos");
            verificar(reporte(null,FiltroServicio.TODOS).getCostoTotalRegistrado().equals(new BigDecimal("220.10")),"Suma decimal exacta");
            verificar(new ReporteServicios(null,null,null,FiltroServicio.TODOS).getCantidadRegistros()==7,"Todo el historial");
            verificar(new ReporteServicios(INICIO,INICIO,"EQ1",FiltroServicio.TODOS).getSinCostoRegistrado()==1,"Periodo de un dia");
            rechaza(()->new ReporteServicios(FIN,INICIO,null,FiltroServicio.TODOS));
            rechaza(()->new ReporteServicios(INICIO,null,null,FiltroServicio.TODOS));
            rechaza(()->reporte("NO-EXISTE",FiltroServicio.TODOS));
            ReporteServicios vacio=new ReporteServicios(FIN.plusMonths(2),FIN.plusMonths(3),null,FiltroServicio.TODOS);
            verificar(vacio.getCantidadRegistros()==0 && vacio.getCostoTotalRegistrado().equals(new BigDecimal("0.00")),"Reporte sin registros");
            verificar(vacio.getResumen().size()==3 && vacio.getSinCostoRegistrado()==0,"Resumen vacio consistente");
            vacio.exportar(Paths.get("vacio.csv"));
            verificar(Files.readString(Paths.get("vacio.csv")).contains("TOTAL"),"CSV vacio con contexto y totales cero");
            ambos.exportar(Paths.get("servicios.csv"));
            String csv=Files.readString(Paths.get("servicios.csv"));
            verificar(csv.startsWith("\uFEFF"),"CSV UTF-8 con BOM");
            verificar(csv.contains("Cambio; \"\"sello\"\"") && csv.contains("Revisado\nSin fugas"),"CSV escapa separadores, comillas y saltos");
            verificar(csv.contains("01/01/2026") && csv.contains("31/01/2026") && csv.contains("EQ1"),"CSV incluye filtros y fechas");
            verificar(csv.contains("SUBTOTAL") && csv.contains("TOTAL") && csv.contains("No registrado"),"CSV diferencia detalle, totales y costo desconocido");
            for(String[] fila:ambos.getFilas()) verificar(fila.length==ambos.getColumnas().length,"CSV rectangular");
            for(String[] fila:ambos.getDetalle()) verificar(fila.length==ambos.getColumnasDetalle().length,"Tabla detalle rectangular");
            String[] copia=ambos.getDetalle().get(0); copia[6]="999";
            verificar(ambos.getDetalle().get(0)[6].equals("No registrado"),"Vista previa no permite alterar el reporte");

            correcto(gestion.modificar(mantenimiento("M2","EQ1",INICIO.plusDays(9),150.55)));
            verificar(ambos.getCostoTotalRegistrado().equals(new BigDecimal("200.00")),"Reporte es una captura estable");
            verificar(reporte("EQ1",FiltroServicio.TODOS).getCostoTotalRegistrado().equals(new BigDecimal("225.05")),"Reporte actualizado refleja edicion de costo");
            verificar(gestion.modificar(new Mantenimiento("M2","EQ1",INICIO,TipoMantenimiento.PREVENTIVO,"Quitar costo"))!=null,"Impide borrar costo conocido");
            correcto(gestion.modificar(mantenimiento("M2","EQ1",INICIO.plusDays(9),125.50)));
            anterior.setCosto(12.75); correcto(gestion.modificar(anterior));
            verificar(gestion.listar().get(0).getCosto()==12.75 && gestion.listar().get(0).getFecha().equals(INICIO),"Actualizar costo antiguo conserva ID y fecha");
            verificar(reporte("EQ1",FiltroServicio.TODOS).getCostoTotalRegistrado().equals(new BigDecimal("212.75")) && reporte("EQ1",FiltroServicio.TODOS).getSinCostoRegistrado()==0,"Costo historico completado se suma");
            correcto(reparaciones.registrar(new Reparacion("HUERFANO","EQ3",INICIO.plusDays(19),"Servicio",10,"Historial")));
            correcto(equipos.eliminar("EQ3"));
            ReporteServicios historico=reporte("EQ3",FiltroServicio.TODOS);
            verificar(historico.getCantidadRegistros()==1 && historico.getDetalle().get(0)[3].equals("Equipo no disponible"),"Conserva historial de equipo eliminado");
            byte[] datos=Files.readAllBytes(Paths.get("mantenimientos.dat"));
            Files.writeString(Paths.get("mantenimientos.dat"),"Archivo danado");
            rechaza(()->reporte(null,FiltroServicio.TODOS));
            verificar(Files.readString(Paths.get("mantenimientos.dat")).equals("Archivo danado"),"No oculta ni sobrescribe un archivo danado");
            Files.write(Paths.get("mantenimientos.dat"),datos);

            SwingUtilities.invokeAndWait(()-> {
                VistaReportes vista=null; VistaMantenimientos mant=null;
                try {
                    vista=new VistaReportes();
                    ((JComboBox<?>)campo(vista,"seccion")).setSelectedIndex(5); metodo(vista,"generar");
                    verificar(campo(vista,"reporte") instanceof ReporteServicios,"RF16 accesible desde selector de reportes");
                    verificar(((JTable)campo(vista,"detalle")).getRowCount()==8,"Vista previa de todo el historial");
                    verificar(!((JTextField)campo(vista,"inicio")).isEnabled(),"Todo el historial desactiva fechas");
                    ((JCheckBox)campo(vista,"todoHistorial")).doClick();
                    ((JTextField)campo(vista,"inicio")).setText("01/01/2026"); ((JTextField)campo(vista,"fin")).setText("31/01/2026");
                    ((JComboBox<?>)campo(vista,"equipoServicio")).setSelectedIndex(1); metodo(vista,"generar");
                    ReporteServicios filtrado=(ReporteServicios)campo(vista,"reporte");
                    verificar(filtrado.getCantidadRegistros()==3 && filtrado.getCostoTotalRegistrado().equals(new BigDecimal("212.75")),"Filtros de periodo y equipo desde Swing");
                    ((JComboBox<?>)campo(vista,"servicio")).setSelectedIndex(2);
                    verificar(campo(vista,"reporte")==null && ((JTable)campo(vista,"detalle")).getRowCount()==0,"Cambiar filtro invalida vista previa anterior");
                    metodo(vista,"generar"); verificar(((ReporteServicios)campo(vista,"reporte")).getCantidadRegistros()==1,"Filtro por servicio desde Swing");
                    ((JComboBox<?>)campo(vista,"servicio")).setSelectedIndex(0); ((JComboBox<?>)campo(vista,"equipoServicio")).setSelectedIndex(0); metodo(vista,"generar");
                    imagen(vista,"reporte-rf16");
                    mant=new VistaMantenimientos();
                    JTable tabla=(JTable)campo(mant,"tablaMantenimientos"); tabla.setRowSelectionInterval(0,0);
                    verificar(((JTextField)campo(mant,"txtCosto")).getText().equals("12.75"),"Seleccion carga costo del mantenimiento");
                    ((JTextField)campo(mant,"txtCosto")).setText("15.00");
                    Method construir=mant.getClass().getDeclaredMethod("construirDesdeFormulario",String.class); construir.setAccessible(true);
                    Mantenimiento editado=(Mantenimiento)construir.invoke(mant,"LEGACY");
                    verificar(editado.getCosto()==15 && editado.getFecha().equals(INICIO),"Formulario edita costo conservando fecha original");
                    Mantenimiento nuevo=(Mantenimiento)construir.invoke(mant,"NUEVO-UI");
                    verificar(nuevo.getCosto()==15 && nuevo.getFecha().equals(FechaUtil.hoy()),"Formulario nuevo usa fecha automatica");
                    imagen(mant,"mantenimiento-con-costo");
                } catch(Exception ex) { throw new RuntimeException(ex); }
                finally { if(vista!=null) vista.dispose(); if(mant!=null) mant.dispose(); }
            });
            System.out.println("OK: "+comprobaciones+" comprobaciones del RF16.");
            System.out.println("Capturas RF16: "+Paths.get("").toAbsolutePath());
            System.exit(0);
        } catch(Throwable ex) { ex.printStackTrace(); System.exit(1); }
    }
}
