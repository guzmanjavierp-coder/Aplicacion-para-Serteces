package proyecto.vista;

import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import proyecto.gestion.*;
import proyecto.modelo.*;
import proyecto.util.*;

public class VistaCompras extends JFrame {
    private final JComboBox<Proveedor> proveedor = new JComboBox<>();
    private final JComboBox<TipoCombustible> tipo = new JComboBox<>();
    private final JTextField cantidad = new JTextField(), costo = new JTextField(), fecha = new JTextField(), responsable = new JTextField();
    private final DefaultTableModel tabla = Componentes.tabla("ID", "Proveedor", "Combustible", "Cantidad", "Costo total", "Fecha", "Responsable");
    private final GestionCompras gestion = new GestionCompras();
    public VistaCompras() {
        setTitle("Compras de combustible"); setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(950, 550); setLocationRelativeTo(null);
        JPanel formulario = new JPanel(new GridLayout(6, 2, 6, 6));
        formulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formulario.add(new JLabel("Proveedor")); formulario.add(proveedor);
        formulario.add(new JLabel("Tipo de combustible")); formulario.add(tipo);
        formulario.add(new JLabel("Cantidad")); formulario.add(cantidad);
        formulario.add(new JLabel("Costo TOTAL de la compra")); formulario.add(costo);
        formulario.add(new JLabel("Fecha (dd/MM/yyyy)")); formulario.add(fecha);
        formulario.add(new JLabel("Responsable")); formulario.add(responsable);
        JPanel botones = new JPanel(new FlowLayout());
        botones.add(Componentes.boton("Registrar compra y entrada", this, this::registrar));
        botones.add(Componentes.boton("Actualizar proveedores, tipos y compras", this, this::actualizar));
        JPanel superior = new JPanel(new BorderLayout()); superior.add(formulario); superior.add(botones, BorderLayout.SOUTH);
        add(superior, BorderLayout.NORTH); add(new JScrollPane(new JTable(tabla)));
        actualizar(); fecha.setText(FechaUtil.formatear(LocalDate.now()));
    }
    private void actualizar() {
        proveedor.removeAllItems(); for (Proveedor p : new GestionProveedores().listar()) proveedor.addItem(p);
        tipo.removeAllItems(); for (TipoCombustible t : new GestionTiposCombustible().listar()) tipo.addItem(t);
        listar();
    }
    private void listar() {
        tabla.setRowCount(0);
        for (CompraCombustible c : gestion.listar()) tabla.addRow(new Object[]{c.getId(), c.getCodigoProveedor(), c.getCodigoTipoCombustible(), c.getCantidad(), c.getCostoTotal(), FechaUtil.formatear(c.getFecha()), c.getResponsable()});
    }
    private void registrar() {
        Proveedor p = (Proveedor) proveedor.getSelectedItem(); TipoCombustible t = (TipoCombustible) tipo.getSelectedItem();
        if (p == null || t == null) throw new IllegalArgumentException("Registre y seleccione un proveedor y un tipo de combustible.");
        double litros, total;
        try { litros = Double.parseDouble(cantidad.getText().trim()); total = Double.parseDouble(costo.getText().trim()); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException("Cantidad y costo deben ser numeros validos."); }
        LocalDate dia = FechaUtil.parsear(fecha.getText().trim());
        if (dia == null) throw new IllegalArgumentException("Fecha invalida. Use dd/MM/yyyy.");
        CompraCombustible compra = new CompraCombustible(GeneradorId.generarId("COMPRA"), t.getCodigo(), litros, dia, responsable.getText().trim(), p.getCodigo(), total);
        Componentes.comprobar(gestion.registrar(compra)); listar(); cantidad.setText(""); costo.setText("");
        JOptionPane.showMessageDialog(this, "Compra registrada. Existencia actual: " + new GestionCombustible().calcularExistencia(t.getCodigo()));
    }
}
