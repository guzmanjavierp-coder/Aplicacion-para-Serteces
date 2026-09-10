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
import proyecto.gestion.GestionMantenimientos;
import proyecto.modelo.Equipo;
import proyecto.modelo.Mantenimiento;
import proyecto.modelo.TipoMantenimiento;
import proyecto.util.FechaUtil;
import proyecto.util.GeneradorId;

/**
 * Ventana para administrar mantenimientos: registrar, modificar,
 * eliminar, listar, buscar por equipo y consultar por rango de fechas.
 */
public class VistaMantenimientos extends JFrame {

    private GestionMantenimientos gestionMantenimientos;
    private GestionEquipos gestionEquipos;

    private JTextField txtId;
    private JComboBox<Equipo> comboEquipo;
    private JTextField txtFecha;
    private JComboBox<TipoMantenimiento> comboTipo;
    private JTextField txtObservaciones;

    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;

    private JButton btnRegistrar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnListar;
    private JButton btnBuscarEquipo;
    private JButton btnBuscarPeriodo;
    private JButton btnLimpiar;
    private JButton btnCargarSeleccionado;

    private JTable tablaMantenimientos;
    private DefaultTableModel modeloTabla;

    public VistaMantenimientos() {
        gestionMantenimientos = new GestionMantenimientos();
        gestionEquipos = new GestionEquipos();
        configurarVentana();
        construirInterfaz();
        cargarComboEquipos();
        cargarTabla(gestionMantenimientos.listar());
    }

    private void configurarVentana() {
        setTitle("Gestion de Mantenimientos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 550);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 5, 5));
        panelFormulario.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtId = new JTextField();
        txtId.setEditable(false);
        comboEquipo = new JComboBox<Equipo>();
        txtFecha = new JTextField();
        comboTipo = new JComboBox<TipoMantenimiento>(TipoMantenimiento.values());
        txtObservaciones = new JTextField();

        panelFormulario.add(new JLabel("ID (autogenerado):"));
        panelFormulario.add(txtId);
        panelFormulario.add(new JLabel("Equipo:"));
        panelFormulario.add(comboEquipo);
        panelFormulario.add(new JLabel("Fecha (dd/MM/yyyy):"));
        panelFormulario.add(txtFecha);
        panelFormulario.add(new JLabel("Tipo:"));
        panelFormulario.add(comboTipo);
        panelFormulario.add(new JLabel("Observaciones:"));
        panelFormulario.add(txtObservaciones);

        JPanel panelPeriodo = new JPanel(new FlowLayout());
        txtFechaInicio = new JTextField(10);
        txtFechaFin = new JTextField(10);
        panelPeriodo.add(new JLabel("Desde (dd/MM/yyyy):"));
        panelPeriodo.add(txtFechaInicio);
        panelPeriodo.add(new JLabel("Hasta (dd/MM/yyyy):"));
        panelPeriodo.add(txtFechaFin);

        JPanel panelBotones = new JPanel(new FlowLayout());
        btnRegistrar = new JButton("Registrar");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        btnListar = new JButton("Listar Todos");
        btnBuscarEquipo = new JButton("Buscar por Equipo");
        btnBuscarPeriodo = new JButton("Buscar por Periodo");
        btnLimpiar = new JButton("Limpiar");
        btnCargarSeleccionado = new JButton("Cargar Seleccionado");

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnListar);
        panelBotones.add(btnBuscarEquipo);
        panelBotones.add(btnBuscarPeriodo);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnCargarSeleccionado);

        modeloTabla = new DefaultTableModel(new Object[]{
                "ID", "Equipo", "Fecha", "Tipo", "Observaciones"
        }, 0) {
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaMantenimientos = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaMantenimientos);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelFormulario, BorderLayout.NORTH);
        panelSuperior.add(panelPeriodo, BorderLayout.CENTER);
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
                cargarTabla(gestionMantenimientos.listar());
            }
        });
        btnBuscarEquipo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buscarPorEquipo();
            }
        });
        btnBuscarPeriodo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buscarPorPeriodo();
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
        Mantenimiento mantenimiento = construirDesdeFormulario(GeneradorId.generarId("MANT"));
        if (mantenimiento == null) {
            return;
        }
        String error = gestionMantenimientos.registrar(mantenimiento);
        if (error != null) {
            mostrarError(error);
            return;
        }
        JOptionPane.showMessageDialog(this, "Mantenimiento registrado correctamente.");
        cargarTabla(gestionMantenimientos.listar());
        limpiarCampos();
    }

    private void modificar() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            mostrarError("Debe buscar o seleccionar un mantenimiento antes de modificar.");
            return;
        }
        Mantenimiento mantenimiento = construirDesdeFormulario(id);
        if (mantenimiento == null) {
            return;
        }
        String error = gestionMantenimientos.modificar(mantenimiento);
        if (error != null) {
            mostrarError(error);
            return;
        }
        JOptionPane.showMessageDialog(this, "Mantenimiento modificado correctamente.");
        cargarTabla(gestionMantenimientos.listar());
        limpiarCampos();
    }

    private void eliminar() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            mostrarError("Debe indicar el ID del mantenimiento a eliminar.");
            return;
        }
        String error = gestionMantenimientos.eliminar(id);
        if (error != null) {
            mostrarError(error);
            return;
        }
        JOptionPane.showMessageDialog(this, "Mantenimiento eliminado correctamente.");
        cargarTabla(gestionMantenimientos.listar());
        limpiarCampos();
    }

    private void buscarPorEquipo() {
        Equipo equipoSeleccionado = (Equipo) comboEquipo.getSelectedItem();
        if (equipoSeleccionado == null) {
            mostrarError("Debe seleccionar un equipo.");
            return;
        }
        List<Mantenimiento> resultado = gestionMantenimientos.buscarPorEquipo(equipoSeleccionado.getCodigo());
        if (resultado.isEmpty()) {
            mostrarError("No se encontraron mantenimientos para ese equipo.");
        }
        cargarTabla(resultado);
    }

    private void buscarPorPeriodo() {
        LocalDate inicio = FechaUtil.parsear(txtFechaInicio.getText().trim());
        LocalDate fin = FechaUtil.parsear(txtFechaFin.getText().trim());
        if (inicio == null || fin == null) {
            mostrarError("Las fechas del periodo no son validas. Use el formato dd/MM/yyyy.");
            return;
        }
        List<Mantenimiento> resultado = gestionMantenimientos.consultarPorPeriodo(inicio, fin);
        if (resultado.isEmpty()) {
            mostrarError("No se encontraron mantenimientos en ese periodo.");
        }
        cargarTabla(resultado);
    }

    private Mantenimiento construirDesdeFormulario(String id) {
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
        TipoMantenimiento tipoSeleccionado = (TipoMantenimiento) comboTipo.getSelectedItem();
        String observaciones = txtObservaciones.getText().trim();
        if (observaciones.isEmpty()) {
            mostrarError("Debe ingresar las observaciones.");
            return null;
        }
        return new Mantenimiento(id, equipoSeleccionado.getCodigo(), fecha, tipoSeleccionado, observaciones);
    }

    private void cargarSeleccionado() {
        int filaSeleccionada = tablaMantenimientos.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarError("Debe seleccionar un mantenimiento de la tabla.");
            return;
        }
        String id = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        Mantenimiento mantenimiento = null;
        List<Mantenimiento> lista = gestionMantenimientos.listar();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId().equals(id)) {
                mantenimiento = lista.get(i);
                break;
            }
        }
        if (mantenimiento == null) {
            mostrarError("No se pudo encontrar el mantenimiento seleccionado.");
            return;
        }
        txtId.setText(mantenimiento.getId());
        txtFecha.setText(FechaUtil.formatear(mantenimiento.getFecha()));
        txtObservaciones.setText(mantenimiento.getObservaciones());
        comboTipo.setSelectedItem(mantenimiento.getTipo());
        for (int i = 0; i < comboEquipo.getItemCount(); i++) {
            Equipo equipo = comboEquipo.getItemAt(i);
            if (equipo.getCodigo().equals(mantenimiento.getCodigoEquipo())) {
                comboEquipo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void cargarTabla(List<Mantenimiento> lista) {
        modeloTabla.setRowCount(0);
        for (int i = 0; i < lista.size(); i++) {
            Mantenimiento mantenimiento = lista.get(i);
            modeloTabla.addRow(new Object[]{
                    mantenimiento.getId(),
                    mantenimiento.getCodigoEquipo(),
                    FechaUtil.formatear(mantenimiento.getFecha()),
                    mantenimiento.getTipo(),
                    mantenimiento.getObservaciones()
            });
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtFecha.setText("");
        txtObservaciones.setText("");
        txtFechaInicio.setText("");
        txtFechaFin.setText("");
        if (comboEquipo.getItemCount() > 0) {
            comboEquipo.setSelectedIndex(0);
        }
        comboTipo.setSelectedIndex(0);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
