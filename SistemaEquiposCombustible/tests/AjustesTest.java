import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import proyecto.gestion.*;
import proyecto.modelo.*;
import proyecto.persistencia.ArchivoCombustible;
import proyecto.util.FechaUtil;
import proyecto.vista.*;

public class AjustesTest {
    private static int comprobaciones;
    private static void verificar(boolean valor, String descripcion) {
        comprobaciones++; if (!valor) throw new AssertionError(descripcion);
    }
    private static void correcto(String error) { verificar(error == null, "Operacion: " + error); }
    private static Object campo(Object objeto, String nombre) throws Exception {
        Field campo = objeto.getClass().getDeclaredField(nombre); campo.setAccessible(true); return campo.get(objeto);
    }
    private static java.util.List<Component> componentes(Container padre) {
        java.util.List<Component> resultado = new ArrayList<>();
        for (Component c : padre.getComponents()) { resultado.add(c); if (c instanceof Container) resultado.addAll(componentes((Container)c)); }
        return resultado;
    }
    private static void boton(JFrame ventana, String texto) {
        for (Component c : componentes(ventana.getContentPane())) if (c instanceof JButton && ((JButton)c).getText().equals(texto)) {
            ((JButton)c).doClick(); return;
        }
        throw new AssertionError("Boton no encontrado: " + texto);
    }
    private static void texto(Object objeto, String campo, String valor) throws Exception { ((JTextField)campo(objeto, campo)).setText(valor); }
    private static void responsable(Object objeto, String campo, String valor) throws Exception { ((JComboBox<?>)campo(objeto, campo)).setSelectedItem(valor); }
    private static void imagen(JFrame ventana, String nombre) throws Exception {
        for (Component c : componentes(ventana.getContentPane())) if (c instanceof JScrollPane) {
            JScrollPane scroll = (JScrollPane)c;
            if (scroll.getViewport().getView() instanceof JTable) scroll.setColumnHeaderView(((JTable)scroll.getViewport().getView()).getTableHeader());
        }
        ventana.getContentPane().setSize(ventana.getWidth(), ventana.getHeight());
        distribuir(ventana.getContentPane());
        BufferedImage imagen = new BufferedImage(ventana.getWidth(), ventana.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagen.createGraphics(); ventana.getContentPane().printAll(g); g.dispose();
        ImageIO.write(imagen, "png", Paths.get(nombre + ".png").toFile());
    }
    private static void distribuir(Container c) { c.doLayout(); for (Component hijo : c.getComponents()) if (hijo instanceof Container) distribuir((Container)hijo); }
    public static void main(String[] args) throws Exception {
        verificar(Files.exists(Paths.get(".pruebas-aisladas")), "Datos aislados");
        try {
            correcto(new GestionProveedores().registrar(new Proveedor("P", "Empresa Uno", "Contacto", "123", "a@b.com", "Ciudad", "Combustible")));
            correcto(new GestionProveedores().registrar(new Proveedor("P2", "Empresa Dos", "Contacto", "123", "a@b.com", "Ciudad", "Combustible")));
            correcto(new GestionTiposCombustible().registrar(new TipoCombustible("D", "Diesel", 20)));
            correcto(new GestionTiposCombustible().registrar(new TipoCombustible("G", "Gasolina", 10)));
            LocalDate fecha = LocalDate.of(2026, 1, 1);
            GestionCompras compras = new GestionCompras();
            correcto(compras.registrar(new CompraCombustible("ANTERIOR", "D", 100, fecha, "Javier", "P", 250)));
            verificar(compras.buscarPorId("ANTERIOR").getCostoPorGalon() == 2.5, "Precio derivado de compra anterior");
            CompraCombustible nueva = CompraCombustible.porGalones("NUEVA", "G", 3, fecha, "Agustin", "P", 0.335);
            verificar(nueva.getCostoTotal() == 1.01, "Redondeo decimal del total");
            correcto(compras.registrar(nueva));
            verificar(compras.buscarPorId("NUEVA").getCostoPorGalon() == 0.335, "Persistencia del precio unitario");
            GestionCombustible combustible = new GestionCombustible();
            correcto(combustible.registrarSalida(new MovimientoCombustible("S", "D", null, 60, fecha, "Esteban")));
            int cantidad = combustible.listar().size();
            correcto(compras.modificar(CompraCombustible.porGalones("ANTERIOR", "D", 80, fecha, "Agustin", "P2", 4.5)));
            verificar(combustible.calcularExistencia("D") == 20, "Edicion actualiza stock");
            verificar(combustible.listar().size() == cantidad, "Edicion no duplica entrada");
            verificar(compras.buscarPorId("ANTERIOR").getCostoTotal() == 360, "Total editado");
            verificar(compras.buscarPorProveedor("P2").size() == 1, "Edicion cambia proveedor");
            verificar(compras.modificar(CompraCombustible.porGalones("ANTERIOR", "D", 59, fecha, "A", "P", 3)) != null, "Impide reducir por debajo de lo retirado");
            verificar(combustible.calcularExistencia("D") == 20, "Edicion rechazada conserva saldo");
            verificar(compras.modificar(CompraCombustible.porGalones("ANTERIOR", "G", 80, fecha, "A", "P", 3)) != null, "Impide cambiar tipo con combustible usado");
            verificar(compras.modificar(CompraCombustible.porGalones("NO", "D", 80, fecha, "A", "P", 3)) != null, "Compra inexistente");
            verificar(compras.modificar(CompraCombustible.porGalones("ANTERIOR", "D", 80, fecha.plusDays(1), "A", "P", 3)) != null, "Preserva fecha original");
            verificar(compras.modificar(CompraCombustible.porGalones("ANTERIOR", "D", 80, fecha, "", "P", 3)) != null, "Responsable obligatorio al editar");
            verificar(compras.modificar(CompraCombustible.porGalones("ANTERIOR", "D", 80, fecha, "A", "NO", 3)) != null, "Proveedor valido al editar");
            correcto(compras.modificar(CompraCombustible.porGalones("NUEVA", "D", 3, fecha, "Agustin", "P", 0.335)));
            verificar(combustible.calcularExistencia("G") == 0 && combustible.calcularExistencia("D") == 23, "Cambio de tipo sin salidas actualiza ambos saldos");
            correcto(new GestionTiposCombustible().modificar(new TipoCombustible("D", "Diesel", 23)));
            GestionResponsables responsables = new GestionResponsables();
            verificar(responsables.listar().containsAll(Arrays.asList("Agustin", "Esteban")), "Responsables de movimientos anteriores");
            correcto(responsables.registrar("  Carlos  "));
            verificar(new GestionResponsables().listar().contains("Carlos"), "Persistencia y normalizacion de responsable");
            verificar(responsables.registrar("carlos") != null, "Responsable duplicado sin distinguir mayusculas");
            verificar(responsables.registrar(" ") != null, "Responsable vacio");
            correcto(new GestionCategorias().registrar(new Categoria("CAT", "Herramientas", "Prueba")));
            correcto(new GestionEquipos().registrar(new Equipo("EQ", "Equipo", "Descripcion", "CAT", 1, "Bodega", EstadoEquipo.values()[0], "Ana")));
            verificar(proyecto.util.GeneradorId.generarId("EQ").equals("EQ0001"), "Primer consecutivo de equipo");
            correcto(new GestionEquipos().registrar(new Equipo("EQ002", "Equipo temporal", "Descripcion", "CAT", 1, "Bodega", EstadoEquipo.values()[0], "Ana")));
            verificar(proyecto.util.GeneradorId.generarId("EQ").equals("EQ0003"), "Continua despues de codigos existentes");
            correcto(new GestionEquipos().eliminar("EQ002"));
            verificar(proyecto.util.GeneradorId.generarId("EQ").equals("EQ0004"), "Consecutivo persiste sin reutilizar eliminados");
            verificar(new GestionEquipos().buscarPorCodigo("EQ").toString().equals("Equipo"), "Nombre visible del equipo");
            verificar(proyecto.util.NombreEquipo.mostrar("EQ").equals("Equipo"), "Nombre para tablas y consultas");
            verificar(proyecto.util.NombreEquipo.mostrar("NO").contains("Equipo no disponible"), "Referencia antigua identificable");
            verificar(proyecto.util.GeneradorId.generarId("MANT").equals("MANT0001"), "Formato consecutivo de mantenimiento");
            verificar(proyecto.util.GeneradorId.generarId("MANT").equals("MANT0002"), "Siguiente consecutivo de mantenimiento");
            for (String prefijo : new String[]{"CAT", "COMB", "PROV", "REP", "MOV", "COMPRA"}) {
                String primero = proyecto.util.GeneradorId.generarId(prefijo);
                String siguiente = proyecto.util.GeneradorId.generarId(prefijo);
                verificar(primero.matches(prefijo + "[0-9]{4,}"), "Cuatro digitos para " + prefijo);
                verificar(Long.parseLong(siguiente.substring(prefijo.length())) == Long.parseLong(primero.substring(prefijo.length())) + 1, "Consecutivo independiente para " + prefijo);
            }
            GestionUbicaciones ubicaciones = new GestionUbicaciones();
            verificar(ubicaciones.listar().contains("Bodega"), "Importa ubicaciones anteriores");
            verificar(ubicaciones.registrar(" bODEGA ") != null, "Evita duplicados de ubicacion");
            verificar(ubicaciones.registrar(" ") != null, "Rechaza ubicacion vacia");
            correcto(ubicaciones.registrar("Patio"));
            verificar(new GestionUbicaciones().listar().contains("Patio"), "Persiste ubicaciones");
            verificar(ubicaciones.modificar("Bodega", "Patio") != null, "Evita duplicados al editar");
            correcto(ubicaciones.modificar("Bodega", "Bodega principal"));
            verificar(new GestionEquipos().buscarPorCodigo("EQ").getUbicacion().equals("Bodega principal"), "Renombra ubicacion en equipos");
            verificar(!ubicaciones.listar().contains("Bodega"), "No reaparece el nombre anterior");
            verificar(ubicaciones.eliminar("Bodega principal") != null, "Protege ubicaciones en uso");
            correcto(ubicaciones.eliminar("Patio"));
            verificar(!new GestionUbicaciones().listar().contains("Patio"), "Persiste eliminacion");
            verificar(responsables.listar().contains("Ana"), "Responsables de equipos");

            SwingUtilities.invokeAndWait(() -> {
                java.util.List<JFrame> ventanas = new ArrayList<>();
                try {
                    GestionTiposCombustible catalogo = new GestionTiposCombustible();
                    TipoCombustible diesel = catalogo.buscarPorCodigo("D"); diesel.setCostoPorGalon(7.35); correcto(catalogo.modificar(diesel));
                    TipoCombustible gasolina = catalogo.buscarPorCodigo("G"); gasolina.setCostoPorGalon(34.0); correcto(catalogo.modificar(gasolina));
                    VistaCompras vista = new VistaCompras(); ventanas.add(vista);
                    verificar(!((JTextField)campo(vista, "costo")).isEditable(), "Precio automatico no editable");
                    verificar(((JTextField)campo(vista, "costo")).getText().equals("7.35"), "Precio cargado desde catalogo");
                    JComboBox<?> tipoCompra = (JComboBox<?>)campo(vista, "tipo");
                    tipoCompra.setSelectedIndex(1);
                    verificar(((JTextField)campo(vista, "costo")).getText().equals("34.00"), "Precio cambia con combustible");
                    tipoCompra.setSelectedIndex(0);
                    texto(vista, "cantidad", "2.5"); responsable(vista, "responsable", "Carlos");
                    verificar(((JLabel)campo(vista, "total")).getText().equals("18.38"), "Total automatico visible");
                    boton(vista, "Registrar compra y entrada");
                    CompraCombustible registrada = compras.listar().get(compras.listar().size()-1);
                    verificar(registrada.getFecha().equals(FechaUtil.hoy()), "Fecha automatica de compra");
                    verificar(registrada.getCostoTotal() == 18.38, "Total guardado desde formulario");
                    JTable tabla = (JTable)campo(vista, "tablaCompras"); tabla.setRowSelectionInterval(tabla.getRowCount()-1, tabla.getRowCount()-1);
                    verificar(campo(vista, "idSeleccionado").equals(registrada.getId()), "Seleccion carga compra para editar");
                    texto(vista, "cantidad", "5"); boton(vista, "Guardar cambios");
                    verificar(compras.buscarPorId(registrada.getId()).getCostoTotal() == 36.75, "Boton modifica compra seleccionada");
                    verificar(compras.listar().size() == 3, "Boton editar conserva cantidad de compras");
                    imagen(vista, "compras");
                    VistaAlertas alertas = new VistaAlertas(); ventanas.add(alertas);
                    JTable tablaAlertas = null;
                    for (Component c : componentes(alertas.getContentPane())) if (c instanceof JTable) tablaAlertas = (JTable)c;
                    verificar(tablaAlertas != null && tablaAlertas.getRowCount() == 1, "Tabla muestra todos los bajos (solo gasolina en este punto)");
                    JComboBox<?> filtro = (JComboBox<?>)campo(alertas, "filtro"); filtro.setSelectedIndex(1);
                    verificar(tablaAlertas.getRowCount() == 0, "Filtro excluye combustible suficiente");
                    filtro.setSelectedIndex(2); verificar(tablaAlertas.getRowCount() == 1, "Filtro muestra tipo bajo");
                    filtro.setSelectedIndex(0); imagen(alertas, "alertas");
                    VistaProveedores proveedores = new VistaProveedores(); ventanas.add(proveedores);
                    for (String c : new String[]{"nombre", "contacto", "telefono", "direccion", "servicios"}) texto(proveedores, c, "Empresa Prueba");
                    texto(proveedores, "correo", "empresa@example.com"); boton(proveedores, "Registrar");
                    Proveedor p = new GestionProveedores().listar().get(2);
                    verificar(p.getCodigo().matches("PROV[0-9]{4,}") && p.getEmpresa().equals("Empresa Prueba"), "Codigo automatico y empresa de proveedor");
                    @SuppressWarnings("unchecked") JComboBox<String> selectorRegistro = (JComboBox<String>) campo(proveedores, "registro");
                    Component opcionEquipo = selectorRegistro.getRenderer().getListCellRendererComponent(new JList<String>(), "EQ", 0, false, false);
                    verificar(((JLabel)opcionEquipo).getText().equals("Equipo"), "Selector de proveedor muestra nombre sin codigo tras aplicar estilo");
                    verificar(selectorRegistro.getSelectedItem().equals("EQ"), "Selector conserva codigo interno para asociar");
                    imagen(proveedores, "proveedores");
                    VistaTiposCombustible vistaTipos = new VistaTiposCombustible(); ventanas.add(vistaTipos);
                    JTable stockTipos = (JTable) campo(vistaTipos, "tablaTipos");
                    int filaDiesel = -1;
                    for (int i = 0; i < stockTipos.getRowCount(); i++) if ("D".equals(stockTipos.getValueAt(i, 0))) filaDiesel = i;
                    GestionCombustible inventario = new GestionCombustible();
                    double saldoAntes = inventario.calcularExistencia("D");
                    verificar(((Number)stockTipos.getValueAt(filaDiesel, 4)).doubleValue() == saldoAntes, "Tipos muestra stock de compras existentes");
                    stockTipos.setRowSelectionInterval(filaDiesel, filaDiesel);
                    correcto(inventario.registrarEntrada(new MovimientoCombustible("STOCK-ENT", "D", TipoMovimientoCombustible.ENTRADA, 4, FechaUtil.hoy(), "Prueba")));
                    java.lang.reflect.Method refrescar = VistaTiposCombustible.class.getDeclaredMethod("actualizarStock"); refrescar.setAccessible(true); refrescar.invoke(vistaTipos);
                    verificar(((Number)stockTipos.getValueAt(filaDiesel, 4)).doubleValue() == saldoAntes + 4, "Tipos actualiza entradas con ventana abierta");
                    correcto(inventario.registrarSalida(new MovimientoCombustible("STOCK-SAL", "D", TipoMovimientoCombustible.SALIDA, 2, FechaUtil.hoy(), "Prueba")));
                    refrescar.invoke(vistaTipos);
                    verificar(((Number)stockTipos.getValueAt(filaDiesel, 4)).doubleValue() == saldoAntes + 2, "Tipos descuenta consumo");
                    verificar(stockTipos.getSelectedRow() == filaDiesel, "Actualizar stock conserva seleccion");
                    verificar(inventario.registrarSalida(new MovimientoCombustible("STOCK-EXCESO", "D", TipoMovimientoCombustible.SALIDA, saldoAntes + 100, FechaUtil.hoy(), "Prueba")) != null, "No permite retirar mas stock del disponible");
                    imagen(vistaTipos, "tipos-stock");
                    ventanas.add(new VistaUbicaciones()); ventanas.add(new VistaCategorias()); ventanas.add(new VistaEquipos());
                    VistaMantenimientos vistaMant = new VistaMantenimientos();
                    @SuppressWarnings("unchecked") JComboBox<Equipo> selectorEquipo = (JComboBox<Equipo>) campo(vistaMant, "comboEquipo");
                    JLabel etiquetaEquipo = (JLabel) selectorEquipo.getRenderer().getListCellRendererComponent(new JList<Equipo>(), selectorEquipo.getItemAt(0), 0, false, false);
                    verificar(etiquetaEquipo.getText().equals("Equipo"), "Selector de mantenimiento muestra solo nombre");
                    ventanas.add(vistaMant); ventanas.add(new VistaReparaciones()); ventanas.add(new VistaCombustible());
                    ventanas.add(new VistaResponsables()); ventanas.add(new VistaPrincipal());
                    for (JFrame ventana : ventanas) {
                        for (Component c : componentes(ventana.getContentPane())) if (c instanceof JLabel) {
                            String etiqueta = ((JLabel)c).getText();
                            verificar(etiqueta == null || (!etiqueta.contains("autogenerado") && !etiqueta.equals("Codigo:") && !etiqueta.equals("Codigo unico") && !etiqueta.equals("Fecha (dd/MM/yyyy):")), "Formulario sin campos automaticos: "+etiqueta);
                        }
                    }
                    verificar(!((JTextField)campo(proveedores, "codigo")).isShowing(), "Codigo fuera del formulario");
                    for (JFrame v : ventanas) if (v instanceof VistaMantenimientos || v instanceof VistaReparaciones || v instanceof VistaCombustible) {
                        verificar(!componentes(v.getContentPane()).contains(campo(v, "txtId")), "ID fuera del formulario");
                        verificar(!componentes(v.getContentPane()).contains(campo(v, "txtFecha")), "Fecha fuera del formulario");
                    }
                    for (JFrame v : ventanas) {
                        if (v instanceof VistaPrincipal) imagen(v, "principal");
                        if (v instanceof VistaEquipos) imagen(v, "equipos");
                        if (v instanceof VistaMantenimientos) imagen(v, "mantenimientos");
                        if (v instanceof VistaReportes) imagen(v, "reportes");
                    }
                } catch (Exception ex) { throw new RuntimeException(ex); }
                finally { for (JFrame ventana : ventanas) ventana.dispose(); }
            });
            System.out.println("OK: " + comprobaciones + " comprobaciones de ajustes de formularios y compras.");
            System.out.println("Capturas guardadas en: " + Paths.get("").toAbsolutePath());
            System.exit(0);
        } catch (Throwable ex) { ex.printStackTrace(); System.exit(1); }
    }
}
