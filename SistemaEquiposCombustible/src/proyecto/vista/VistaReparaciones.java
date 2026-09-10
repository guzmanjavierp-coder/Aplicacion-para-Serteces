package proyecto.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import proyecto.gestion.GestionEquipos;
import proyecto.gestion.GestionReparaciones;
import proyecto.modelo.Equipo;
import proyecto.modelo.Reparacion;
import proyecto.util.FechaUtil;
import proyecto.util.GeneradorId;

/**
 * Ventana para administrar reparaciones: registrar, modificar,
 * eliminar, listar y buscar por equipo.
 */
public class VistaReparaciones extends JFrame {

    private GestionReparaciones gestionReparaciones;
    private GestionEquipos gestionEquipos;

    private JTextField txtId;
    private JComboBox<Equipo> comboEquipo;
    private JTextField txtFecha;
    private JTextField txtDescripcion;
    private JTextField txtCosto;
    private JTextField txtObservaciones;

    private JButton btnRegistrar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnListar;
    private JButton btnBuscarEquipo;
    private JButton btnLimpiar;
    private JButton btnCargarSeleccionado;

    private JTable tablaReparaciones;
    private DefaultTableModel modeloTabla;

    public VistaReparaciones() {
        gestionReparaciones = new GestionReparaciones();
        gestionEquipos = new GestionEquipos();
        configurarVentana();
        construirInterfaz();
        cargarComboEquipos();
        cargarTabla(gestionReparaciones.listar());
    }

    private void configurarVentana() {
        setTitle("Gestion de Reparaciones");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 550);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 5, 5));
        panelFormulario.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtId = new JTextField();
        txtId.setEditable(false);
        comboEquipo = new JComboBox<Equipo>();
        txtFecha = new JTextField();
        txtDescripcion = new JTextField();
        txtCosto = new JTextField();
        txtObservaciones = new JTextField();

        panelFormulario.add(new JLabel("ID (autogenerado):"));
        panelFormulario.add(txtId);
        panelFormulario.add(new JLabel("Equipo:"));
        panelFormulario.add(comboEquipo);
        panelFormulario.add(new JLabel("Fecha (dd/MM/yyyy):"));
        panelFormulario.add(txtFecha);
        panelFormulario.add(new JLabel("Descripcion:"));
        panelFormulario.add(txtDescripcion);
        panelFormulario.add(new JLabel("Costo:"));
        panelFormulario.add(txtCosto);
        panelFormulario.add(new JLabel("Observaciones:"));
        panelFormulario.add(txtObservaciones);

        JPanel panelBotones = new JPanel(new FlowLayout());
        btnRegistrar = new JButton("Registrar");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        btnListar = new JButton("Listar Todas");
        btnBuscarEquipo = new JButton("Buscar por Equipo");
        btnLimpiar = new JButton("Limpiar");
        btnCargarSeleccionado = new JButton("Cargar Seleccionado");

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnListar);
        panelBotones.add(btnBuscarEquipo);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnCargarSeleccionado);

        modeloTabla = new DefaultTableModel(new Object[]{
                "ID", "Equipo", "Fecha", "Descripcion", "Costo", "Observaciones"
        }, 0) {
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaReparaciones = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaReparaciones);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        getContentPane().add(panelSuperior, BorderLayout.NORTH);
        getContentPane().add(scrollTabla, BorderLayout.CENTER);

        btnRegistrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registrar();
            }
        });
        btnModificar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modificar();
            }
        });
        btnEliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                eliminar();
            }
        });
        btnListar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cargarTabla(gestionReparaciones.listar());
            }
        });
        btnBuscarEquipo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buscarPorEquipo();
            }
        });
        btnLimpiar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });
        btnCargarSeleccionado.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cargarSeleccionado();
            }
        });
    }

    private void cargarComboEquipos() {
        comboEquipo.removeAllItems();
        List<Equipo> equipos = gestionEquipos.listar();
        for (int i = 0; i < equipos.size(); i++) {
            comboEquipo.addItem(equipos.get(i));
        }
    }

    private void registrar() {
        Reparacion reparacion = construirDesdeFormulario(GeneradorId.generarId("REP"));
        if (reparacion == null) {
            return;
        }
        String error = gestionReparaciones.registrar(reparacion);
        if (error != null) {
            mostrarError(error);
            return;
        }
        JOptionPane.showMessageDialog(this, "Reparacion registrada correctamente.");
        cargarTabla(gestionReparaciones.listar());
        limpiarCampos();
    }

    private void modificar() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            mostrarError("Debe buscar o seleccionar una reparacion antes de modificar.");
            return;
        }
        Reparacion reparacion = construirDesdeFormulario(id);
        if (reparacion == null) {
            return;
        }
        String error = gestionReparaciones.modificar(reparacion);
        if (error != null) {
            mostrarError(error);
            return;
        }
        JOptionPane.showMessageDialog(this, "Reparacion modificada correctamente.");
        cargarTabla(gestionReparaciones.listar());
        limpiarCampos();
    }

    private void eliminar() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            mostrarError("Debe indicar el ID de la reparacion a eliminar.");
            return;
        }
        String error = gestionReparaciones.eliminar(id);
        if (error != null) {
            mostrarError(error);
            return;
        }
        JOptionPane.showMessageDialog(this, "Reparacion eliminada correctamente.");
        cargarTabla(gestionReparaciones.listar());
        limpiarCampos();
    }

    private void buscarPorEquipo() {
        Equipo equipoSeleccionado = (Equipo) comboEquipo.getSelectedItem();
        if (equipoSeleccionado == null) {
            mostrarError("Debe seleccionar un equipo.");
            return;
        }
        List<Reparacion> resultado = gestionReparaciones.buscarPorEquipo(equipoSeleccionado.getCodigo());
        if (resultado.isEmpty()) {
            mostrarError("No se encontraron reparaciones para ese equipo.");
        }
        cargarTabla(resultado);
    }

    private Reparacion construirDesdeFormulario(String id) {
        Equipo equipoSeleccionado = (Equipo) comboEquipo.getSelectedItem();
        if (equipoSeleccionado == null) {
            mostrarError("Debe seleccionar un equipo.");
            return null;
        }
        LocalDate fecha = FechaUtil.parsear(txtFecha.getText().trim());
        if (fecha == null) {
            mostrarError("La fecha no es valida. Use el formato dd/MM/yyyy.");
            return null;
        }
        String descripcion = txtDescripcion.getText().trim();
        if (descripcion.isEmpty()) {
            mostrarError("Debe ingresar la descripcion de la reparacion.");
            return null;
        }
        double costo;
        try {
            costo = Double.parseDouble(txtCosto.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarError("El costo debe ser un numero valido.");
            return null;
        }
        if (costo < 0) {
            mostrarError("El costo no puede ser negativo.");
            return null;
        }
        String observaciones = txtObservaciones.getText().trim();
        if (observaciones.isEmpty()) {
            mostrarError("Debe ingresar las observaciones.");
            return null;
        }
        return new Reparacion(id, equipoSeleccionado.getCodigo(), fecha, descripcion, costo, observaciones);
    }

    private void cargarSeleccionado() {
        int filaSeleccionada = tablaReparaciones.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarError("Debe seleccionar una reparacion de la tabla.");
            return;
        }
        String id = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        Reparacion reparacion = null;
        List<Reparacion> lista = gestionReparaciones.listar();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId().equals(id)) {
                reparacion = lista.get(i);
                break;
            }
        }
        if (reparacion == null) {
            mostrarError("No se pudo encontrar la reparacion seleccionada.");
            return;
        }
        txtId.setText(reparacion.getId());
        txtFecha.setText(FechaUtil.formatear(reparacion.getFecha()));
        txtDescripcion.setText(reparacion.getDescripcion());
        txtCosto.setText(String.valueOf(reparacion.getCosto()));
        txtObservaciones.setText(reparacion.getObservaciones());
        for (int i = 0; i < comboEquipo.getItemCount(); i++) {
            Equipo equipo = comboEquipo.getItemAt(i);
            if (equipo.getCodigo().equals(reparacion.getCodigoEquipo())) {
                comboEquipo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void cargarTabla(List<Reparacion> lista) {
        modeloTabla.setRowCount(0);
        for (int i = 0; i < lista.size(); i++) {
            Reparacion reparacion = lista.get(i);
            modeloTabla.addRow(new Object[]{
                    reparacion.getId(),
                    reparacion.getCodigoEquipo(),
                    FechaUtil.formatear(reparacion.getFecha()),
                    reparacion.getDescripcion(),
                    reparacion.getCosto(),
                    reparacion.getObservaciones()
            });
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtFecha.setText("");
        txtDescripcion.setText("");
        txtCosto.setText("");
        txtObservaciones.setText("");
        if (comboEquipo.getItemCount() > 0) {
            comboEquipo.setSelectedIndex(0);
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
