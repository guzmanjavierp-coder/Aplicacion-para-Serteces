package proyecto.vista;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import proyecto.gestion.*;
import proyecto.modelo.*;
import proyecto.reportes.*;
import proyecto.util.FechaUtil;

/** RF11, RF19 y RF20: vista previa y exportacion CSV. */
public class VistaReportes extends JFrame {
    private final JComboBox<String> seccion = new JComboBox<>(new String[]{"Equipos", "Existencias de combustible", "Mantenimientos (general)", "Reparaciones (general)", "Consumo de combustible"});
    private final JComboBox<String> tipo = new JComboBox<>();
    private final JTextField inicio = new JTextField(10), fin = new JTextField(10);
    private final DefaultTableModel resumen = Componentes.tabla("Codigo", "Combustible", "Cantidad utilizada");
    private final JTable detalle = new JTable();
    private final JLabel titulo = new JLabel("Seleccione un reporte y pulse Generar / consultar.");
    private final JPanel filtros = new JPanel(new FlowLayout());
    private Reporte reporte;
    public VistaReportes() {
        setTitle("Reportes de inventario y consumo"); setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1100, 650); setLocationRelativeTo(null);
        JPanel botones = new JPanel(new FlowLayout()); botones.add(new JLabel("Reporte:")); botones.add(seccion);
        botones.add(Componentes.boton("Generar / consultar", this, this::generar));
        botones.add(Componentes.boton("Exportar CSV", this, this::exportar));
        botones.add(Componentes.boton("Actualizar tipos", this, this::cargarTipos));
        filtros.add(new JLabel("Inicio (dd/MM/yyyy):")); filtros.add(inicio); filtros.add(new JLabel("Fin:")); filtros.add(fin);
        filtros.add(new JLabel("Tipo (codigo):")); filtros.add(tipo);
        inicio.setText(FechaUtil.formatear(LocalDate.now().withDayOfMonth(1))); fin.setText(FechaUtil.formatear(LocalDate.now()));
        JPanel superior = new JPanel(new GridLayout(3, 1)); superior.add(botones); superior.add(filtros); superior.add(titulo);
        add(superior, BorderLayout.NORTH);
        JSplitPane division = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(new JTable(resumen)), new JScrollPane(detalle));
        division.setResizeWeight(0.3); add(division);
        seccion.addActionListener(e -> { filtros.setVisible(seccion.getSelectedIndex() == 4); reporte = null; resumen.setRowCount(0); detalle.setModel(Componentes.tabla()); titulo.setText("Pulse Generar / consultar para actualizar."); });
        filtros.setVisible(false); cargarTipos();
    }
    private void cargarTipos() {
        tipo.removeAllItems(); tipo.addItem("Todos");
        for (TipoCombustible t : new GestionTiposCombustible().listar()) tipo.addItem(t.getCodigo());
    }
    private void generar() {
        reporte = null; resumen.setRowCount(0); detalle.setModel(Componentes.tabla());
        if (seccion.getSelectedIndex() == 4) {
            LocalDate desde = FechaUtil.parsear(inicio.getText().trim()), hasta = FechaUtil.parsear(fin.getText().trim());
            String codigo = tipo.getSelectedIndex() <= 0 ? null : (String) tipo.getSelectedItem();
            for (ConsumoCombustible c : new GestionConsumo().consultar(desde, hasta, codigo)) resumen.addRow(new Object[]{c.getCodigo(), c.getNombre(), c.getCantidad()});
            reporte = new ReporteConsumo(desde, hasta, codigo);
        } else reporte = new ReporteInventario(ReporteInventario.Seccion.values()[seccion.getSelectedIndex()]);
        DefaultTableModel modelo = Componentes.tabla(reporte.getColumnas());
        for (String[] fila : reporte.getFilas()) modelo.addRow(fila);
        detalle.setModel(modelo); titulo.setText(reporte.getTitulo() + " | " + modelo.getRowCount() + " filas. Consumo = salidas del periodo, con ambos extremos incluidos.");
    }
    private void exportar() {
        // Siempre actualiza desde los filtros visibles; nunca exporta un periodo anterior.
        generar();
        JFileChooser selector = new JFileChooser(); selector.setSelectedFile(new java.io.File("reporte.csv"));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path destino = selector.getSelectedFile().toPath();
        if (!destino.toString().toLowerCase(java.util.Locale.ROOT).endsWith(".csv")) destino = destino.resolveSibling(destino.getFileName()+".csv");
        if (java.nio.file.Files.exists(destino) && JOptionPane.showConfirmDialog(this, "Reemplazar " + destino.getFileName() + "?", "Exportar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { reporte.exportar(destino); JOptionPane.showMessageDialog(this, "Reporte guardado en " + destino.toAbsolutePath()); }
        catch (IOException ex) { throw new IllegalStateException("No se pudo exportar el reporte: " + ex.getMessage(), ex); }
    }
}
