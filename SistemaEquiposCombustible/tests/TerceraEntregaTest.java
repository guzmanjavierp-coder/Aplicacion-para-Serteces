import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import javax.swing.SwingUtilities;
import proyecto.gestion.*;
import proyecto.modelo.*;
import proyecto.persistencia.*;
import proyecto.reportes.*;
import proyecto.util.FechaUtil;
import proyecto.vista.*;

/** Ejecutar SOLO en un directorio aislado; no toca los datos reales del proyecto. */
public class TerceraEntregaTest {
    private static int comprobaciones;
    private static void verificar(boolean condicion, String mensaje) {
        comprobaciones++;
        if (!condicion) throw new AssertionError(mensaje);
    }
    private static void correcto(String error) { verificar(error == null, "Operacion fallida: " + error); }
    private static void rechaza(Runnable accion) {
        boolean fallo = false;
        try { accion.run(); } catch (IllegalArgumentException | IllegalStateException ex) { fallo = true; }
        verificar(fallo, "Se esperaba un error controlado.");
    }
    private static Proveedor proveedor(String codigo, String nombre) {
        return new Proveedor(codigo, nombre, "Contacto", "12345678", "correo@example.com", "Direccion", "Equipos y servicios");
    }
    private static MovimientoCombustible movimiento(String id, String tipo, double cantidad, LocalDate fecha) {
        return new MovimientoCombustible(id, tipo, null, cantidad, fecha, "Javier");
    }
    public static void main(String[] args) throws Exception {
        if (args.length == 1 && args[0].equals("--compatibilidad")) {
            System.out.println("Lectura de archivos existentes: " + new GestionEquipos().listar().size() + " equipos, "
                    + new GestionTiposCombustible().listar().size() + " combustibles, " + new GestionCombustible().listar().size() + " movimientos.");
            for (ReporteInventario.Seccion s : ReporteInventario.Seccion.values()) new ReporteInventario(s);
            System.out.println("OK: datos anteriores leidos sin modificarlos.");
            return;
        }
        verificar(Files.exists(Paths.get(".pruebas-aisladas")), "Falta marcador de aislamiento.");
        GestionProveedores proveedores = new GestionProveedores();
        correcto(proveedores.registrar(proveedor("P1", "Proveedor; con \"comillas\"")));
        verificar(proveedores.registrar(proveedor("P1", "Duplicado")) != null, "Codigo duplicado.");
        verificar(proveedores.registrar(proveedor("", "Invalido")) != null, "Campos obligatorios.");
        verificar(proveedores.modificar(proveedor("P9", "Inexistente")) != null, "Modificar inexistente.");
        correcto(proveedores.modificar(proveedor("P1", "Proveedor actualizado")));
        verificar(new GestionProveedores().buscarPorCodigo("P1").getNombre().equals("Proveedor actualizado"), "Persistencia proveedor.");
        correcto(proveedores.registrar(proveedor("P2", "Sin registros")));
        correcto(proveedores.eliminar("P2"));
        verificar(proveedores.buscarPorCodigo("P2") == null, "Eliminar proveedor.");

        GestionTiposCombustible tipos = new GestionTiposCombustible();
        correcto(tipos.registrar(new TipoCombustible("D", "Diesel", 10)));
        correcto(tipos.registrar(new TipoCombustible("G", "Gasolina", 5)));
        LocalDate inicio = LocalDate.of(2026, 10, 1), fin = LocalDate.of(2026, 10, 6);
        GestionCompras compras = new GestionCompras();
        correcto(compras.registrar(new CompraCombustible("C1", "D", 100, inicio, "Agustin", "P1", 500)));
        verificar(new GestionCombustible().calcularExistencia("D") == 100, "Compra aumenta stock una vez.");
        verificar(compras.registrar(new CompraCombustible("C1", "D", 100, inicio, "Agustin", "P1", 500)) != null, "Compra duplicada.");
        verificar(new GestionCombustible().calcularExistencia("D") == 100, "Duplicado no cambia stock.");
        verificar(compras.registrar(new CompraCombustible("C2", "D", 100, inicio, "Agustin", "NO", 500)) != null, "Proveedor inexistente.");
        verificar(compras.registrar(new CompraCombustible("C2", "NO", 100, inicio, "Agustin", "P1", 500)) != null, "Tipo inexistente.");
        verificar(compras.registrar(new CompraCombustible("C2", "D", 100, null, "Agustin", "P1", 500)) != null, "Fecha obligatoria.");
        verificar(compras.registrar(new CompraCombustible("C2", "D", 100, inicio, "", "P1", 500)) != null, "Responsable obligatorio.");
        rechaza(() -> new CompraCombustible("N", "D", Double.NaN, inicio, "A", "P1", 1));
        rechaza(() -> new CompraCombustible("N", "D", 1, inicio, "A", "P1", Double.POSITIVE_INFINITY));
        rechaza(() -> new CompraCombustible("N", "D", 0, inicio, "A", "P1", 1));
        rechaza(() -> new CompraCombustible("N", "D", 1, inicio, "A", "P1", -1));
        verificar(new GestionCompras().buscarPorProveedor("P1").size() == 1, "Persistencia compra con proveedor.");
        verificar(proveedores.eliminar("P1") != null, "Proteccion proveedor con compra.");

        GestionCombustible movimientos = new GestionCombustible();
        correcto(movimientos.registrarEntrada(movimiento("E1", "G", 50, inicio)));
        correcto(movimientos.registrarSalida(movimiento("S0", "D", 5, inicio.minusDays(1))));
        correcto(movimientos.registrarSalida(movimiento("S1", "D", 10, inicio)));
        correcto(movimientos.registrarSalida(movimiento("S2", "D", 20, fin)));
        correcto(movimientos.registrarSalida(movimiento("S3", "D", 7, fin.plusDays(1))));
        correcto(movimientos.registrarSalida(movimiento("S4", "G", 8, fin)));
        verificar(movimientos.registrarSalida(movimiento("EXCESO", "D", 500, fin)) != null, "Stock insuficiente.");
        verificar(movimientos.calcularExistencia("D") == 58, "Stock con compra y salidas.");
        correcto(tipos.modificar(new TipoCombustible("D", "Diesel", 58)));
        verificar(new GestionAlertas().tieneInventarioBajo("D"), "Alerta al alcanzar el minimo.");
        correcto(tipos.modificar(new TipoCombustible("D", "Diesel", 57)));
        verificar(!new GestionAlertas().tieneInventarioBajo("D"), "Sin alerta por encima del minimo.");
        GestionConsumo consumo = new GestionConsumo();
        verificar(consumo.consultar(inicio, fin, "D").get(0).getCantidad() == 30, "Limites inclusivos y solo salidas.");
        verificar(consumo.consultar(inicio, fin, null).size() == 2, "Agrupacion por combustible.");
        verificar(consumo.consultar(fin, fin, "G").get(0).getCantidad() == 8, "Un solo dia.");
        verificar(consumo.consultar(fin.plusDays(3), fin.plusDays(4), "G").get(0).getCantidad() == 0, "Periodo sin consumo.");
        rechaza(() -> consumo.consultar(fin, inicio, null));
        rechaza(() -> consumo.consultar(null, fin, null));
        rechaza(() -> consumo.consultar(inicio, fin, "NO"));
        verificar(FechaUtil.parsear("31/02/2026") == null, "Fecha imposible.");
        verificar(FechaUtil.parsear("29/02/2024") != null, "Bisiesto.");

        correcto(new GestionCategorias().registrar(new Categoria("CAT", "Maquinaria", "Pruebas")));
        correcto(new GestionEquipos().registrar(new Equipo("EQ", "Bomba", "Descripcion", "CAT", 1, "Bodega", EstadoEquipo.values()[0], "Esteban")));
        correcto(new GestionMantenimientos().registrar(new Mantenimiento("M1", "EQ", inicio, TipoMantenimiento.values()[0], "Revision", 0)));
        correcto(new GestionReparaciones().registrar(new Reparacion("R1", "EQ", fin, "Cambio", 25, "Servicio")));
        for (TipoRegistroProveedor tipo : TipoRegistroProveedor.values()) {
            String id = tipo == TipoRegistroProveedor.EQUIPO ? "EQ" : tipo == TipoRegistroProveedor.MANTENIMIENTO ? "M1" : "R1";
            correcto(proveedores.asociar("P1", tipo, id));
            verificar(proveedores.asociar("P1", tipo, id) != null, "Asociacion duplicada.");
        }
        verificar(new GestionProveedores().consultarAsociaciones("P1").size() == 3, "Persistencia de asociaciones.");
        verificar(proveedores.asociar("P1", TipoRegistroProveedor.EQUIPO, "NO") != null, "Referencia inexistente.");
        correcto(proveedores.desasociar("P1", TipoRegistroProveedor.EQUIPO, "EQ"));
        verificar(proveedores.consultarAsociaciones("P1").size() == 2, "Quitar vinculo.");
        for (ReporteInventario.Seccion seccion : ReporteInventario.Seccion.values()) {
            Reporte reporte = new ReporteInventario(seccion);
            verificar(!reporte.getFilas().isEmpty(), "Reporte " + seccion);
            for (String[] fila : reporte.getFilas()) verificar(fila.length == reporte.getColumnas().length, "Columnas alineadas.");
            reporte.exportar(Paths.get(seccion+".csv"));
            verificar(Files.size(Paths.get(seccion+".csv")) > 3, "CSV generado.");
        }
        Reporte reporte = new ReporteConsumo(inicio, fin, "D");
        verificar(reporte.getFilas().size() == 3, "Resumen y dos salidas del periodo.");
        reporte.exportar(Paths.get("consumo.csv"));
        String csv = Files.readString(Paths.get("consumo.csv"), StandardCharsets.UTF_8);
        verificar(csv.contains("30.0") && csv.contains("01/10/2026") && csv.contains("06/10/2026"), "Contenido CSV consumo.");
        Reporte especial = new Reporte() {
            public String getTitulo() { return "Prueba"; }
            public String[] getColumnas() { return new String[]{"Texto"}; }
            public List<String[]> getFilas() { return Collections.singletonList(new String[]{"=SUM(1;2) \"texto\"\nlinea"}); }
        };
        especial.exportar(Paths.get("escape.csv"));
        verificar(Files.readString(Paths.get("escape.csv")).contains("\"'=SUM(1;2) \"\"texto\"\"\nlinea\""), "Escape CSV y formulas.");
        // Archivo legado: lista de movimientos simples mas compras, mismo serialVersionUID.
        verificar(new ArchivoCombustible().leer().size() == 7, "Lectura mixta compatible.");
        byte[] original = Files.readAllBytes(Paths.get("proveedores.dat"));
        Files.writeString(Paths.get("proveedores.dat"), "archivo danado");
        rechaza(() -> proveedores.registrar(proveedor("P3", "No sobrescribir")));
        verificar(Files.readString(Paths.get("proveedores.dat")).equals("archivo danado"), "No sobrescribe archivo danado.");
        Files.write(Paths.get("proveedores.dat"), original);
        rechaza(() -> new ArchivoLista<Proveedor>("directorio-inexistente/datos.dat", Proveedor.class).guardar(proveedores.listar()));

        SwingUtilities.invokeAndWait(() -> {
            VistaPrincipal principal = new VistaPrincipal();
            VistaProveedores vp = new VistaProveedores(); VistaCompras vc = new VistaCompras(); VistaReportes vr = new VistaReportes();
            verificar(principal.getContentPane().getComponentCount() > 0 && vp.getContentPane().getComponentCount() > 0
                    && vc.getContentPane().getComponentCount() > 0 && vr.getContentPane().getComponentCount() > 0, "Construccion de ventanas Swing.");
            java.awt.Container superior = (java.awt.Container) vr.getContentPane().getComponent(0);
            java.awt.Container botones = (java.awt.Container) superior.getComponent(0);
            javax.swing.JComboBox<?> selector = (javax.swing.JComboBox<?>) botones.getComponent(1);
            javax.swing.JButton generar = (javax.swing.JButton) botones.getComponent(2);
            for (int i = 0; i < 4; i++) { selector.setSelectedIndex(i); generar.doClick(); }
            selector.setSelectedIndex(4);
            java.awt.Container filtros = (java.awt.Container) superior.getComponent(1);
            ((javax.swing.JTextField) filtros.getComponent(1)).setText("01/10/2026");
            ((javax.swing.JTextField) filtros.getComponent(3)).setText("06/10/2026");
            generar.doClick();
            javax.swing.JSplitPane division = (javax.swing.JSplitPane) vr.getContentPane().getComponent(1);
            javax.swing.JTable tablaConsumo = (javax.swing.JTable) ((javax.swing.JScrollPane) division.getTopComponent()).getViewport().getView();
            verificar(tablaConsumo.getRowCount() == 2, "Consulta desde boton Swing: resumen de dos tipos.");
            verificar(((Number) tablaConsumo.getValueAt(0, 2)).doubleValue() == 30, "Consulta Swing: total diesel.");
            principal.dispose(); vp.dispose(); vc.dispose(); vr.dispose();
        });
        System.out.println("OK: " + comprobaciones + " comprobaciones de tercera entrega.");
        System.exit(0);
    }
}
