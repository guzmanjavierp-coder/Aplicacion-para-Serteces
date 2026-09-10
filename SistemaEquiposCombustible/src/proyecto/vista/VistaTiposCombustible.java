package proyecto.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import proyecto.gestion.GestionTiposCombustible;
import proyecto.modelo.TipoCombustible;

/**
 * Ventana para administrar tipos de combustible: registrar, modificar,
 * eliminar, listar y buscar por codigo.
 */
public class VistaTiposCombustible extends JFrame {

    private GestionTiposCombustible gestionTiposCombustible;

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtNivelMinimo;

    private JButton btnRegistrar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnBuscar;
    private JButton btnListar;
    private JButton btnLimpiar;
    private JButton btnCargarSeleccionado;

    private JTable tablaTipos;
    private DefaultTableModel modeloTabla;

    public VistaTiposCombustible() {
        gestionTiposCombustible = new GestionTiposCombustible();
        configurarVentana();
        construirInterfaz();
        cargarTabla(gestionTiposCombustible.listar());
    }

    private void configurarVentana() {
        setTitle("Gestion de Tipos de Combustible");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        JPanel panelFormulario = new JPanel(new GridLayout(3, 2, 5, 5));
        panelFormulario.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtCodigo = new JTextField();
        txtNombre = new JTextField();
        txtNivelMinimo = new JTextField();

        panelFormulario.add(new JLabel("Codigo:"));
        panelFormulario.add(txtCodigo);
        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(new JLabel("Nivel Minimo:"));
        panelFormulario.add(txtNivelMinimo);

        JPanel panelBotones = new JPanel(new FlowLayout());
        btnRegistrar = new JButton("Registrar");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        btnBuscar = new JButton("Buscar");
        btnListar = new JButton("Listar");
        btnLimpiar = new JButton("Limpiar");
        btnCargarSeleccionado = new JButton("Cargar Seleccionado");

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnBuscar);
        panelBotones.add(btnListar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnCargarSeleccionado);

        modeloTabla = new DefaultTableModel(new Object[]{"Codigo", "Nombre", "Nivel Minimo"}, 0) {
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaTipos = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaTipos);

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
        btnBuscar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buscar();
            }
        });
        btnListar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cargarTabla(gestionTiposCombustible.listar());
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

    private void registrar() {
        TipoCombustible tipo = construirDesdeFormulario();
        if (tipo == null) {
            return;
        }
        String error = gestionTiposCombustible.registrar(tipo);
        if (error != null) {
            mostrarError(error);
            return;
        }
        JOptionPane.showMessageDialog(this, "Tipo de combustible registrado correctamente.");
        cargarTabla(gestionTiposCombustible.listar());
        limpiarCampos();
    }

    private void modificar() {
        TipoCombustible tipo = construirDesdeFormulario();
        if (tipo == null) {
            return;
        }
        String error = gestionTiposCombustible.modificar(tipo);
        if (error != null) {
            mostrarError(error);
            return;
        }
        JOptionPane.showMessageDialog(this, "Tipo de combustible modificado correctamente.");
        cargarTabla(gestionTiposCombustible.listar());
        limpiarCampos();
    }

    private void eliminar() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            mostrarError("Debe indicar el codigo del tipo de combustible a eliminar.");
            return;
        }
        String error = gestionTiposCombustible.eliminar(codigo);
        if (error != null) {
            mostrarError(error);
            return;
        }
        JOptionPane.showMessageDialog(this, "Tipo de combustible eliminado correctamente.");
        cargarTabla(gestionTiposCombustible.listar());
        limpiarCampos();
    }

    private void buscar() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            mostrarError("Debe ingresar un codigo para buscar.");
            return;
        }
        TipoCombustible tipo = gestionTiposCombustible.buscarPorCodigo(codigo);
        if (tipo == null) {
            mostrarError("No se encontro un tipo de combustible con ese codigo.");
            return;
        }
        txtCodigo.setText(tipo.getCodigo());
        txtNombre.setText(tipo.getNombre());
        txtNivelMinimo.setText(String.valueOf(tipo.getNivelMinimo()));
    }

    private void cargarSeleccionado() {
        int filaSeleccionada = tablaTipos.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarError("Debe seleccionar un tipo de combustible de la tabla.");
            return;
        }
        String codigo = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        TipoCombustible tipo = gestionTiposCombustible.buscarPorCodigo(codigo);
        if (tipo == null) {
            mostrarError("No se pudo encontrar el tipo de combustible seleccionado.");
            return;
        }
        txtCodigo.setText(tipo.getCodigo());
        txtNombre.setText(tipo.getNombre());
        txtNivelMinimo.setText(String.valueOf(tipo.getNivelMinimo()));
    }

    private TipoCombustible construirDesdeFormulario() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        if (codigo.isEmpty() || nombre.isEmpty()) {
            mostrarError("Debe completar el codigo y el nombre.");
            return null;
        }
        double nivelMinimo;
        try {
            nivelMinimo = Double.parseDouble(txtNivelMinimo.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarError("El nivel minimo debe ser un numero valido.");
            return null;
        }
        if (nivelMinimo < 0) {
            mostrarError("El nivel minimo no puede ser negativo.");
            return null;
        }
        return new TipoCombustible(codigo, nombre, nivelMinimo);
    }

    private void cargarTabla(List<TipoCombustible> lista) {
        modeloTabla.setRowCount(0);
        for (int i = 0; i < lista.size(); i++) {
            TipoCombustible tipo = lista.get(i);
            modeloTabla.addRow(new Object[]{
                    tipo.getCodigo(),
                    tipo.getNombre(),
                    tipo.getNivelMinimo()
            });
        }
    }

    private void limpiarCampos() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtNivelMinimo.setText("");
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
