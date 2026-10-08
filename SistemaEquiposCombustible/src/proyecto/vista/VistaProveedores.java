package proyecto.vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import proyecto.gestion.*;
import proyecto.modelo.*;
import proyecto.util.FechaUtil;

/** RF12 y RF17: CRUD, consulta por codigo y registros de cada proveedor. */
public class VistaProveedores extends JFrame {
    private final GestionProveedores gestion = new GestionProveedores();
    private final JTextField codigo = new JTextField(), nombre = new JTextField(), contacto = new JTextField();
    private final JTextField telefono = new JTextField(), correo = new JTextField(), direccion = new JTextField(), servicios = new JTextField();
    private final DefaultTableModel proveedores = Componentes.tabla("Codigo", "Nombre", "Contacto", "Telefono", "Correo", "Direccion", "Servicios");
    private final DefaultTableModel asociados = Componentes.tabla("Tipo", "ID / Codigo", "Detalle", "Fecha", "Cantidad", "Costo total", "Observaciones");
    private final JTable tabla = new JTable(proveedores);
    private final JTable tablaAsociados = new JTable(asociados);
    private final JComboBox<TipoRegistroProveedor> tipo = new JComboBox<>(TipoRegistroProveedor.values());
    private final JComboBox<String> registro = new JComboBox<>();

    public VistaProveedores() {
        setTitle("Proveedores y registros asociados"); setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1050, 720); setLocationRelativeTo(null);
        JPanel formulario = new JPanel(new GridLayout(7, 2, 6, 6));
        String[] etiquetas = {"Codigo unico", "Nombre", "Contacto", "Telefono", "Correo (opcional)", "Direccion", "Productos / servicios"};
        JTextField[] campos = {codigo, nombre, contacto, telefono, correo, direccion, servicios};
        for (int i = 0; i < campos.length; i++) { formulario.add(new JLabel(etiquetas[i])); formulario.add(campos[i]); }
        JPanel botones = new JPanel(new FlowLayout());
        botones.add(Componentes.boton("Registrar", this, () -> { Componentes.comprobar(gestion.registrar(construir())); listar(); limpiar(); }));
        botones.add(Componentes.boton("Modificar", this, () -> { Componentes.comprobar(gestion.modificar(construir())); listar(); consultar(); }));
        botones.add(Componentes.boton("Eliminar", this, () -> {
            if (JOptionPane.showConfirmDialog(this, "Eliminar proveedor " + codigo.getText() + "?", "Eliminar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                Componentes.comprobar(gestion.eliminar(codigo.getText().trim())); listar(); limpiar();
            }
        }));
        botones.add(Componentes.boton("Consultar codigo y registros", this, this::consultar));
        botones.add(Componentes.boton("Listar / actualizar", this, () -> { listar(); cargarRegistros(); }));
        botones.add(Componentes.boton("Nuevo / limpiar", this, this::limpiar));
        JPanel superior = new JPanel(new BorderLayout()); superior.add(formulario); superior.add(botones, BorderLayout.SOUTH);
        superior.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); add(superior, BorderLayout.NORTH);
        JPanel relaciones = new JPanel(new FlowLayout());
        relaciones.add(new JLabel("Vincular proveedor del formulario con:")); relaciones.add(tipo); relaciones.add(registro);
        relaciones.add(Componentes.boton("Asociar", this, () -> {
            Componentes.comprobar(gestion.asociar(codigo.getText().trim(), (TipoRegistroProveedor) tipo.getSelectedItem(), (String) registro.getSelectedItem())); consultar();
        }));
        relaciones.add(Componentes.boton("Quitar vinculo seleccionado", this, () -> {
            int fila = tablaAsociados.getSelectedRow();
            if (fila < 0) throw new IllegalArgumentException("Seleccione un registro asociado.");
            String categoria = "" + asociados.getValueAt(fila, 0);
            if (categoria.equals("COMPRA")) throw new IllegalArgumentException("Las compras conservan su proveedor para mantener el historial.");
            Componentes.comprobar(gestion.desasociar(codigo.getText().trim(), TipoRegistroProveedor.valueOf(categoria), ""+asociados.getValueAt(fila, 1))); consultar();
        }));
        JPanel inferior = new JPanel(new BorderLayout()); inferior.add(relaciones, BorderLayout.NORTH); inferior.add(new JScrollPane(tablaAsociados));
        JSplitPane division = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tabla), inferior);
        division.setResizeWeight(0.45); add(division);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) Componentes.ejecutar(this, () -> {
                codigo.setText(""+proveedores.getValueAt(tabla.getSelectedRow(), 0)); consultar();
            });
        });
        tipo.addActionListener(e -> Componentes.ejecutar(this, this::cargarRegistros));
        listar(); cargarRegistros();
    }
    private Proveedor construir() {
        return new Proveedor(codigo.getText().trim(), nombre.getText().trim(), contacto.getText().trim(), telefono.getText().trim(), correo.getText().trim(), direccion.getText().trim(), servicios.getText().trim());
    }
    private void listar() {
        proveedores.setRowCount(0);
        for (Proveedor p : gestion.listar()) proveedores.addRow(new Object[]{p.getCodigo(), p.getNombre(), p.getContacto(), p.getTelefono(), p.getCorreo(), p.getDireccion(), p.getServicios()});
    }
    private void limpiar() {
        tabla.clearSelection();
        for (JTextField c : new JTextField[]{codigo, nombre, contacto, telefono, correo, direccion, servicios}) c.setText("");
        codigo.setEditable(true); asociados.setRowCount(0); cargarRegistros();
    }
    private void consultar() {
        Proveedor p = gestion.buscarPorCodigo(codigo.getText().trim());
        if (p == null) throw new IllegalArgumentException("El proveedor no existe.");
        codigo.setText(p.getCodigo()); codigo.setEditable(false);
        nombre.setText(p.getNombre()); contacto.setText(p.getContacto()); telefono.setText(p.getTelefono());
        correo.setText(p.getCorreo()); direccion.setText(p.getDireccion()); servicios.setText(p.getServicios());
        asociados.setRowCount(0);
        for (AsociacionProveedor a : gestion.consultarAsociaciones(p.getCodigo())) {
            Object[] fila = {a.getTipo(), a.getIdRegistro(), "Registro eliminado", "", "", "", ""};
            switch (a.getTipo()) {
                case EQUIPO:
                    Equipo equipo = new GestionEquipos().buscarPorCodigo(a.getIdRegistro());
                    if (equipo != null) fila = new Object[]{a.getTipo(), a.getIdRegistro(), equipo.getNombre()+" / "+equipo.getUbicacion(), "", equipo.getCantidad(), "", equipo.getDescripcion()};
                    break;
                case MANTENIMIENTO:
                    for (Mantenimiento m : new GestionMantenimientos().listar()) if (m.getId().equals(a.getIdRegistro()))
                        fila = new Object[]{a.getTipo(), m.getId(), m.getCodigoEquipo()+" / "+m.getTipo(), FechaUtil.formatear(m.getFecha()), "", "No registrado", m.getObservaciones()};
                    break;
                case REPARACION:
                    for (Reparacion r : new GestionReparaciones().listar()) if (r.getId().equals(a.getIdRegistro()))
                        fila = new Object[]{a.getTipo(), r.getId(), r.getCodigoEquipo()+" / "+r.getDescripcion(), FechaUtil.formatear(r.getFecha()), "", r.getCosto(), r.getObservaciones()};
                    break;
            }
            asociados.addRow(fila);
        }
        for (CompraCombustible c : new GestionCompras().buscarPorProveedor(p.getCodigo()))
            asociados.addRow(new Object[]{"COMPRA", c.getId(), c.getCodigoTipoCombustible(), FechaUtil.formatear(c.getFecha()), c.getCantidad(), c.getCostoTotal(), "Responsable: "+c.getResponsable()});
    }
    private void cargarRegistros() {
        registro.removeAllItems();
        switch ((TipoRegistroProveedor) tipo.getSelectedItem()) {
            case EQUIPO: for (Equipo e : new GestionEquipos().listar()) registro.addItem(e.getCodigo()); break;
            case MANTENIMIENTO: for (Mantenimiento m : new GestionMantenimientos().listar()) registro.addItem(m.getId()); break;
            case REPARACION: for (Reparacion r : new GestionReparaciones().listar()) registro.addItem(r.getId()); break;
        }
    }
}
