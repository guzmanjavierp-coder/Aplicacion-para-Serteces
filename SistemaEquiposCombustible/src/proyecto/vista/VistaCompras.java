package proyecto.vista;

import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import proyecto.gestion.*;
import proyecto.modelo.*;
import proyecto.util.*;

public class VistaCompras extends JFrame {
    private final JComboBox<Proveedor> proveedor = new JComboBox<>();
    private final JComboBox<TipoCombustible> tipo = new JComboBox<>();
    private final JTextField cantidad = new JTextField(), costo = new JTextField();
    private final SelectorResponsable responsable = new SelectorResponsable();
    private final JLabel total = new JLabel("0.00");
    private final JLabel estado = new JLabel("Nueva compra. La fecha se registra automaticamente.");
    private final DefaultTableModel tabla = Componentes.tabla("Codigo", "Empresa", "Combustible", "Galones", "Costo por galon", "Costo total", "Fecha", "Responsable");
    private final JTable tablaCompras = new JTable(tabla);
    private final GestionCompras gestion = new GestionCompras();
    private String idSeleccionado;
    public VistaCompras() {
        setTitle("Compras de combustible"); setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1050, 580); setLocationRelativeTo(null);
        JPanel formulario = new JPanel(new GridLayout(5, 2, 6, 6));
        formulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formulario.add(new JLabel("Empresa proveedora")); formulario.add(proveedor);
        formulario.add(new JLabel("Tipo de combustible")); formulario.add(tipo);
        formulario.add(new JLabel("Cantidad (galones)")); formulario.add(cantidad);
        formulario.add(new JLabel("Costo por galon")); formulario.add(costo);
        formulario.add(new JLabel("Responsable")); formulario.add(responsable);
        JPanel botones = new JPanel(new FlowLayout());
        botones.add(Componentes.boton("Registrar compra y entrada", this, this::registrar));
        botones.add(Componentes.boton("Guardar cambios", this, this::modificar));
        botones.add(Componentes.boton("Nueva / limpiar", this, this::limpiar));
        botones.add(Componentes.boton("Actualizar", this, this::actualizar));
        JPanel calculo = new JPanel(new BorderLayout(16,0));
        JPanel importe = new JPanel(new FlowLayout(FlowLayout.LEFT)); importe.add(new JLabel("Total calculado:")); importe.add(total);
        calculo.add(importe,BorderLayout.WEST); calculo.add(estado,BorderLayout.EAST);
        JPanel inferior = new JPanel(new GridLayout(2, 1)); inferior.add(calculo); inferior.add(botones);
        JPanel superior = new JPanel(new BorderLayout()); superior.add(formulario); superior.add(inferior, BorderLayout.SOUTH);
        add(superior, BorderLayout.NORTH); add(new JScrollPane(tablaCompras));
        tablaCompras.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCompras.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaCompras.getSelectedRow() >= 0)
                Componentes.ejecutar(this, this::cargarSeleccionada);
        });
        DocumentListener recalcular = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calcularTotal(); }
            public void removeUpdate(DocumentEvent e) { calcularTotal(); }
            public void changedUpdate(DocumentEvent e) { calcularTotal(); }
        };
        cantidad.getDocument().addDocumentListener(recalcular); costo.getDocument().addDocumentListener(recalcular);
        actualizar();
        tipo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice, boolean seleccionado, boolean foco) {
                if (valor instanceof TipoCombustible) {
                    TipoCombustible combustible = (TipoCombustible) valor;
                    valor = combustible.getCodigo() + " - " + combustible.getNombre();
                }
                return super.getListCellRendererComponent(lista, valor, indice, seleccionado, foco);
            }
        });
        Estilo.aplicar(this);
        total.setFont(new Font("Segoe UI",Font.BOLD,22)); total.setForeground(Estilo.VERDE);
    }
    private void actualizar() {
        proveedor.removeAllItems(); for (Proveedor p : new GestionProveedores().listar()) proveedor.addItem(p);
        tipo.removeAllItems(); for (TipoCombustible t : new GestionTiposCombustible().listar()) tipo.addItem(t);
        responsable.actualizar(); listar(); limpiar();
    }
    private void listar() {
        tabla.setRowCount(0);
        for (CompraCombustible c : gestion.listar()) {
            Proveedor p = new GestionProveedores().buscarPorCodigo(c.getCodigoProveedor());
            TipoCombustible t = new GestionTiposCombustible().buscarPorCodigo(c.getCodigoTipoCombustible());
            tabla.addRow(new Object[]{c.getId(), p == null ? c.getCodigoProveedor() : p.getEmpresa(), t == null ? c.getCodigoTipoCombustible() : t.getNombre(), c.getCantidad(), c.getCostoPorGalon(), c.getCostoTotal(), FechaUtil.formatear(c.getFecha()), c.getResponsable()});
        }
    }
    private void calcularTotal() {
        try {
            double galones = Double.parseDouble(cantidad.getText().trim()), precio = Double.parseDouble(costo.getText().trim());
            CompraCombustible c = CompraCombustible.porGalones("VISTA", "", galones, null, "", "", precio);
            total.setText(String.format(java.util.Locale.ROOT, "%.2f", c.getCostoTotal()));
        } catch (IllegalArgumentException ex) { total.setText("--"); }
    }
    private CompraCombustible construir(String id, LocalDate fecha) {
        Proveedor p = (Proveedor) proveedor.getSelectedItem(); TipoCombustible t = (TipoCombustible) tipo.getSelectedItem();
        if (p == null || t == null) throw new IllegalArgumentException("Registre y seleccione una empresa y un tipo de combustible.");
        double galones, precio;
        try { galones = Double.parseDouble(cantidad.getText().trim()); precio = Double.parseDouble(costo.getText().trim()); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException("Cantidad y costo por galon deben ser numeros validos."); }
        return CompraCombustible.porGalones(id, t.getCodigo(), galones, fecha, responsable.getText(), p.getCodigo(), precio);
    }
    private void registrar() {
        if (idSeleccionado != null) throw new IllegalArgumentException("Esta editando una compra. Use Guardar cambios o Nueva / limpiar.");
        CompraCombustible c = construir(GeneradorId.generarId("COMPRA"), LocalDate.now(ZoneId.of("America/Mexico_City")));
        Componentes.comprobar(gestion.registrar(c)); listar(); limpiar(); estado.setText("Compra registrada correctamente.");
    }
    private void modificar() {
        CompraCombustible anterior = gestion.buscarPorId(idSeleccionado);
        if (anterior == null) throw new IllegalArgumentException("Seleccione una compra de la tabla para editar.");
        Componentes.comprobar(gestion.modificar(construir(anterior.getId(), anterior.getFecha())));
        listar(); limpiar(); estado.setText("Compra actualizada; existencias recalculadas.");
    }
    private void cargarSeleccionada() {
        CompraCombustible c = gestion.buscarPorId("" + tabla.getValueAt(tablaCompras.getSelectedRow(), 0));
        if (c == null) throw new IllegalArgumentException("La compra ya no existe. Actualice la lista.");
        idSeleccionado = c.getId();
        for (int i = 0; i < proveedor.getItemCount(); i++) if (proveedor.getItemAt(i).getCodigo().equals(c.getCodigoProveedor())) proveedor.setSelectedIndex(i);
        for (int i = 0; i < tipo.getItemCount(); i++) if (tipo.getItemAt(i).getCodigo().equals(c.getCodigoTipoCombustible())) tipo.setSelectedIndex(i);
        cantidad.setText(""+c.getCantidad()); costo.setText(""+c.getCostoPorGalon()); responsable.setText(c.getResponsable());
        estado.setText("Editando compra del " + FechaUtil.formatear(c.getFecha()));
    }
    private void limpiar() {
        idSeleccionado = null; tablaCompras.clearSelection(); cantidad.setText(""); costo.setText(""); responsable.setText("");
        total.setText("0.00"); estado.setText("Nueva compra. La fecha se registra automaticamente.");
    }
}
