package proyecto.vista;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import proyecto.gestion.*;
import proyecto.modelo.*;
import proyecto.reportes.*;
import proyecto.util.FechaUtil;

/** RF11, RF16, RF19 y RF20: vista previa, filtros y exportacion CSV. */
public class VistaReportes extends JFrame {
    private final JComboBox<String> seccion = new JComboBox<>(new String[]{"Equipos", "Existencias de combustible", "Mantenimientos (general)", "Reparaciones (general)", "Consumo de combustible", "Mantenimientos y reparaciones (detallado)"});
    private final JComboBox<String> tipo = new JComboBox<>();
    private final JTextField inicio = new JTextField(10), fin = new JTextField(10);
    private final DefaultTableModel resumen = Componentes.tabla("Codigo", "Combustible", "Cantidad utilizada");
    private final JTable detalle = new JTable();
    private final JLabel titulo = new JLabel("Seleccione un reporte y pulse Generar / consultar.");
    private final JPanel filtros = new JPanel(new FlowLayout());
    private final JLabel etiquetaCombustible = new JLabel("Tipo (codigo):");
    private final JPanel filtrosServicios = new JPanel(new FlowLayout());
    private final JComboBox<String> equipoServicio = new JComboBox<>();
    private final List<String> codigosEquipos = new ArrayList<>();
    private final JComboBox<ReporteServicios.FiltroServicio> servicio = new JComboBox<>(ReporteServicios.FiltroServicio.values());
    private final JCheckBox todoHistorial = new JCheckBox("Todo el historial", true);
    private Reporte reporte;
    public VistaReportes() {
        setTitle("Reportes de inventario, consumo y servicios"); setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1100, 650); setLocationRelativeTo(null);
        JPanel botones = new JPanel(new FlowLayout()); botones.add(new JLabel("Reporte:")); botones.add(seccion);
        botones.add(Componentes.boton("Generar / consultar", this, this::generar));
        botones.add(Componentes.boton("Exportar CSV", this, this::exportar));
        botones.add(Componentes.boton("Actualizar catalogos", this, () -> { cargarTipos(); cargarEquiposServicios(); limpiarVista(); }));
        filtros.add(new JLabel("Inicio (dd/MM/yyyy):")); filtros.add(inicio); filtros.add(new JLabel("Fin:")); filtros.add(fin);
        filtros.add(etiquetaCombustible); filtros.add(tipo);
        inicio.setText(FechaUtil.formatear(FechaUtil.hoy().withDayOfMonth(1))); fin.setText(FechaUtil.formatear(FechaUtil.hoy()));
        filtrosServicios.add(new JLabel("Equipo:")); filtrosServicios.add(equipoServicio);
        filtrosServicios.add(new JLabel("Servicios:")); filtrosServicios.add(servicio); filtrosServicios.add(todoHistorial);
        JPanel superior = new JPanel(); superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
        superior.add(botones); superior.add(filtros); superior.add(filtrosServicios); superior.add(titulo);
        add(superior, BorderLayout.NORTH);
        JSplitPane division = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(new JTable(resumen)), new JScrollPane(detalle));
        division.setResizeWeight(0.3); add(division);
        seccion.addActionListener(e -> actualizarFiltros());
        todoHistorial.addActionListener(e -> actualizarFiltros());
        equipoServicio.addActionListener(e -> limpiarVista()); servicio.addActionListener(e -> limpiarVista()); tipo.addActionListener(e -> limpiarVista());
        DocumentListener cambios = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { limpiarVista(); }
            public void removeUpdate(DocumentEvent e) { limpiarVista(); }
            public void changedUpdate(DocumentEvent e) { limpiarVista(); }
        };
        inicio.getDocument().addDocumentListener(cambios); fin.getDocument().addDocumentListener(cambios);
        cargarTipos(); cargarEquiposServicios(); actualizarFiltros();
        Estilo.aplicar(this);
        seccion.setPreferredSize(new Dimension(360,36));
    }
    private void limpiarVista() {
        reporte = null;
        resumen.setColumnIdentifiers(seccion.getSelectedIndex() == 5
                ? new String[]{"Servicio", "Registros", "Costo registrado", "Sin costo registrado"}
                : new String[]{"Codigo", "Combustible", "Cantidad utilizada"});
        resumen.setRowCount(0); detalle.setModel(Componentes.tabla());
        titulo.setText("Pulse Generar / consultar para actualizar el reporte.");
    }
    private void actualizarFiltros() {
        boolean detallado = seccion.getSelectedIndex() == 5;
        filtros.setVisible(detallado || seccion.getSelectedIndex() == 4);
        etiquetaCombustible.setVisible(!detallado); tipo.setVisible(!detallado); filtrosServicios.setVisible(detallado);
        inicio.setEnabled(!detallado || !todoHistorial.isSelected()); fin.setEnabled(inicio.isEnabled());
        limpiarVista(); revalidate();
    }
    private void cargarEquiposServicios() {
        String anterior = equipoServicio.getSelectedIndex() > 0 ? codigosEquipos.get(equipoServicio.getSelectedIndex()-1) : null;
        Map<String,String> equipos = new LinkedHashMap<>();
        for (Equipo e : new GestionEquipos().listar()) equipos.put(e.getCodigo(), e.getNombre());
        for (Mantenimiento m : new GestionMantenimientos().listar()) equipos.putIfAbsent(m.getCodigoEquipo(), "Equipo no disponible");
        for (Reparacion r : new GestionReparaciones().listar()) equipos.putIfAbsent(r.getCodigoEquipo(), "Equipo no disponible");
        equipoServicio.removeAllItems(); codigosEquipos.clear(); equipoServicio.addItem("Todos los equipos");
        for (Map.Entry<String,String> e : equipos.entrySet()) {
            codigosEquipos.add(e.getKey()); equipoServicio.addItem(e.getValue()+" ("+e.getKey()+")");
        }
        if (anterior != null && codigosEquipos.contains(anterior)) equipoServicio.setSelectedIndex(codigosEquipos.indexOf(anterior)+1);
    }
    private void cargarTipos() {
        tipo.removeAllItems(); tipo.addItem("Todos");
        for (TipoCombustible t : new GestionTiposCombustible().listar()) tipo.addItem(t.getCodigo());
    }
    private void generar() {
        limpiarVista();
        if (seccion.getSelectedIndex() == 4) {
            LocalDate desde = FechaUtil.parsear(inicio.getText().trim()), hasta = FechaUtil.parsear(fin.getText().trim());
            String codigo = tipo.getSelectedIndex() <= 0 ? null : (String) tipo.getSelectedItem();
            for (ConsumoCombustible c : new GestionConsumo().consultar(desde, hasta, codigo)) resumen.addRow(new Object[]{c.getCodigo(), c.getNombre(), c.getCantidad()});
            reporte = new ReporteConsumo(desde, hasta, codigo);
        } else if (seccion.getSelectedIndex() == 5) {
            LocalDate desde = null, hasta = null;
            if (!todoHistorial.isSelected()) {
                desde = FechaUtil.parsear(inicio.getText().trim()); hasta = FechaUtil.parsear(fin.getText().trim());
                if (desde == null || hasta == null) throw new IllegalArgumentException("Ingrese fechas validas en formato dd/MM/yyyy o seleccione Todo el historial.");
            }
            String codigo = equipoServicio.getSelectedIndex() <= 0 ? null : codigosEquipos.get(equipoServicio.getSelectedIndex()-1);
            ReporteServicios servicios = new ReporteServicios(desde, hasta, codigo, (ReporteServicios.FiltroServicio)servicio.getSelectedItem());
            reporte = servicios;
            for (String[] fila : servicios.getResumen()) resumen.addRow(fila);
            DefaultTableModel modelo = Componentes.tabla(servicios.getColumnasDetalle());
            for (String[] fila : servicios.getDetalle()) modelo.addRow(fila);
            detalle.setModel(modelo);
            titulo.setText("<html>" + servicios.getCantidadRegistros() + " servicios · Total de costos registrados: " + servicios.getCostoTotalRegistrado().toPlainString()
                    + " · Sin costo registrado: " + servicios.getSinCostoRegistrado()
                    + (servicios.getCantidadRegistros() == 0 ? " · No hay servicios para estos filtros." : "") + "</html>");
            return;
        } else reporte = new ReporteInventario(ReporteInventario.Seccion.values()[seccion.getSelectedIndex()]);
        DefaultTableModel modelo = Componentes.tabla(reporte.getColumnas());
        for (String[] fila : reporte.getFilas()) modelo.addRow(fila);
        detalle.setModel(modelo); titulo.setText(reporte.getTitulo() + " | " + modelo.getRowCount() + " filas."
                + (seccion.getSelectedIndex() == 4 ? " Consumo = salidas del periodo, con ambos extremos incluidos." : ""));
    }
    private void exportar() {
        // Siempre actualiza desde los filtros visibles; nunca exporta un periodo anterior.
        generar();
        JFileChooser selector = new JFileChooser(); selector.setSelectedFile(new java.io.File(seccion.getSelectedIndex() == 5 ? "mantenimientos-reparaciones.csv" : "reporte.csv"));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path destino = selector.getSelectedFile().toPath();
        if (!destino.toString().toLowerCase(java.util.Locale.ROOT).endsWith(".csv")) destino = destino.resolveSibling(destino.getFileName()+".csv");
        if (java.nio.file.Files.exists(destino) && JOptionPane.showConfirmDialog(this, "Reemplazar " + destino.getFileName() + "?", "Exportar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { reporte.exportar(destino); JOptionPane.showMessageDialog(this, "Reporte guardado en " + destino.toAbsolutePath()); }
        catch (IOException ex) { throw new IllegalStateException("No se pudo exportar el reporte: " + ex.getMessage(), ex); }
    }
}
