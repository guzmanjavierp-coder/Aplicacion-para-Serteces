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
import proyecto.gestion.GestionCombustible;
import proyecto.gestion.GestionTiposCombustible;
import proyecto.modelo.MovimientoCombustible;
import proyecto.modelo.TipoCombustible;
import proyecto.util.FechaUtil;
import proyecto.util.GeneradorId;

/**
 * Ventana para administrar movimientos de combustible: registrar
 * entradas y salidas, listar, buscar por tipo y consultar existencia.
 */
public class VistaCombustible extends JFrame {

    private GestionCombustible gestionCombustible;
    private GestionTiposCombustible gestionTiposCombustible;

    private JTextField txtId;
    private JComboBox<TipoCombustible> comboTipoCombustible;
    private JTextField txtCantidad;
    private JTextField txtFecha;
    private JTextField txtResponsable;

    private JButton btnRegistrarEntrada;
    private JButton btnRegistrarSalida;
    private JButton btnListar;
    private JButton btnBuscarTipo;
    private JButton btnConsultarExistencia;
    private JButton btnLimpiar;

    private JTable tablaMovimientos;
    private DefaultTableModel modeloTabla;

    public VistaCombustible() {
        gestionCombustible = new GestionCombustible();
        gestionTiposCombustible = new GestionTiposCombustible();
        configurarVentana();
        construirInterfaz();
        cargarComboTipos();
        cargarTabla(gestionCombustible.listar());
    }

    private void configurarVentana() {
        setTitle("Gestion de Combustible");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 550);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 5, 5));
        panelFormulario.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtId = new JTextField();
        txtId.setEditable(false);
        comboTipoCombustible = new JComboBox<TipoCombustible>();
        txtCantidad = new JTextField();
        txtFecha = new JTextField();
        txtResponsable = new JTextField();

        panelFormulario.add(new JLabel("ID (autogenerado):"));
        panelFormulario.add(txtId);
        panelFormulario.add(new JLabel("Tipo de Combustible:"));
        panelFormulario.add(comboTipoCombustible);
        panelFormulario.add(new JLabel("Cantidad:"));
        panelFormulario.add(txtCantidad);
        panelFormulario.add(new JLabel("Fecha (dd/MM/yyyy):"));
        panelFormulario.add(txtFecha);
        panelFormulario.add(new JLabel("Responsable:"));
        panelFormulario.add(txtResponsable);

        JPanel panelBotones = new JPanel(new FlowLayout());
        btnRegistrarEntrada = new JButton("Registrar Entrada");
        btnRegistrarSalida = new JButton("Registrar Salida");
        btnListar = new JButton("Listar Todos");
        btnBuscarTipo = new JButton("Buscar por Tipo");
        btnConsultarExistencia = new JButton("Consultar Existencia");
        btnLimpiar = new JButton("Limpiar");

        panelBotones.add(btnRegistrarEntrada);
        panelBotones.add(btnRegistrarSalida);
        panelBotones.add(btnListar);
        panelBotones.add(btnBuscarTipo);
        panelBotones.add(btnConsultarExistencia);
        panelBotones.add(btnLimpiar);

        modeloTabla = new DefaultTableModel(new Object[]{
                "ID", "Tipo de Combustible", "Movimiento", "Cantidad", "Fecha", "Responsable"
        }, 0) {
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaMovimientos = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaMovimientos);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        getContentPane().add(panelSuperior, BorderLayout.NORTH);
        getContentPane().add(scrollTabla, BorderLayout.CENTER);

        btnRegistrarEntrada.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registrarEntrada();
            }
        });
        btnRegistrarSalida.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registrarSalida();
            }
        });
        btnListar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cargarTabla(gestionCombustible.listar());
            }
        });
        btnBuscarTipo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buscarPorTipo();
            }
        });
        btnConsultarExistencia.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                consultarExistencia();
            }
        });
        btnLimpiar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });
    }

    private void cargarComboTipos() {
        comboTipoCombustible.removeAllItems();
        List<TipoCombustible> tipos = gestionTiposCombustible.listar();
        for (int i = 0; i < tipos.size(); i++) {
            comboTipoCombustible.addItem(tipos.get(i));
        }
    }

    private void registrarEntrada() {
        MovimientoCombustible movimiento = construirDesdeFormulario();
        if (movimiento == null) {
            return;
        }
        String error = gestionCombustible.registrarEntrada(movimiento);
        if (error != null) {
            mostrarError(error);
            return;
        }
        JOptionPane.showMessageDialog(this, "Entrada de combustible registrada correctamente.");
        cargarTabla(gestionCombustible.listar());
        limpiarCampos();
    }

    private void registrarSalida() {
        MovimientoCombustible movimiento = construirDesdeFormulario();
        if (movimiento == null) {
            return;
        }
        String error = gestionCombustible.registrarSalida(movimiento);
        if (error != null) {
            mostrarError(error);
            return;
        }
        JOptionPane.showMessageDialog(this, "Salida de combustible registrada correctamente.");
        cargarTabla(gestionCombustible.listar());
        limpiarCampos();
    }

    private void buscarPorTipo() {
        TipoCombustible tipoSeleccionado = (TipoCombustible) comboTipoCombustible.getSelectedItem();
        if (tipoSeleccionado == null) {
            mostrarError("Debe seleccionar un tipo de combustible.");
            return;
        }
        List<MovimientoCombustible> resultado = gestionCombustible.buscarPorTipoCombustible(tipoSeleccionado.getCodigo());
        if (resultado.isEmpty()) {
            mostrarError("No se encontraron movimientos para ese tipo de combustible.");
        }
        cargarTabla(resultado);
    }

    private void consultarExistencia() {
        TipoCombustible tipoSeleccionado = (TipoCombustible) comboTipoCombustible.getSelectedItem();
        if (tipoSeleccionado == null) {
            mostrarError("Debe seleccionar un tipo de combustible.");
            return;
        }
        double existencia = gestionCombustible.calcularExistencia(tipoSeleccionado.getCodigo());
        JOptionPane.showMessageDialog(this, "Existencia actual: " + existencia);
    }

    private MovimientoCombustible construirDesdeFormulario() {
        TipoCombustible tipoSeleccionado = (TipoCombustible) comboTipoCombustible.getSelectedItem();
        if (tipoSeleccionado == null) {
            mostrarError("Debe seleccionar un tipo de combustible.");
            return null;
        }
        double cantidad;
        try {
            cantidad = Double.parseDouble(txtCantidad.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarError("La cantidad debe ser un numero valido.");
            return null;
        }
        if (cantidad <= 0) {
            mostrarError("La cantidad debe ser mayor que cero.");
            return null;
        }
        LocalDate fecha = FechaUtil.parsear(txtFecha.getText().trim());
        if (fecha == null) {
            mostrarError("La fecha no es valida. Use el formato dd/MM/yyyy.");
            return null;
        }
        String responsable = txtResponsable.getText().trim();
        if (responsable.isEmpty()) {
            mostrarError("Debe ingresar el responsable.");
            return null;
        }
        String id = GeneradorId.generarId("MOV");
        return new MovimientoCombustible(id, tipoSeleccionado.getCodigo(), null, cantidad, fecha, responsable);
    }

    private void cargarTabla(List<MovimientoCombustible> lista) {
        modeloTabla.setRowCount(0);
        for (int i = 0; i < lista.size(); i++) {
            MovimientoCombustible movimiento = lista.get(i);
            modeloTabla.addRow(new Object[]{
                    movimiento.getId(),
                    movimiento.getCodigoTipoCombustible(),
                    movimiento.getTipoMovimiento(),
                    movimiento.getCantidad(),
                    FechaUtil.formatear(movimiento.getFecha()),
                    movimiento.getResponsable()
            });
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtCantidad.setText("");
        txtFecha.setText("");
        txtResponsable.setText("");
        if (comboTipoCombustible.getItemCount() > 0) {
            comboTipoCombustible.setSelectedIndex(0);
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
