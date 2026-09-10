package proyecto.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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
import proyecto.gestion.GestionCategorias;
import proyecto.modelo.Categoria;

/**
 * Ventana para administrar categorias: registrar, modificar, eliminar,
 * consultar y buscar por codigo. Utiliza GestionCategorias para toda
 * la logica de negocio; esta clase solo se encarga de la interfaz.
 */
public class VistaCategorias extends JFrame {

    private final GestionCategorias gestionCategorias;

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtDescripcion;

    private JButton btnRegistrar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnBuscar;
    private JButton btnLimpiar;

    private JTable tablaCategorias;
    private DefaultTableModel modeloTabla;

    public VistaCategorias() {
        this.gestionCategorias = new GestionCategorias();
        configurarVentana();
        construirInterfaz();
        cargarTabla();
    }

    private void configurarVentana() {
        setTitle("Gestion de Categorias");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        JPanel panelFormulario = new JPanel(new GridLayout(3, 2, 5, 5));
        panelFormulario.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtCodigo = new JTextField();
        txtNombre = new JTextField();
        txtDescripcion = new JTextField();

        panelFormulario.add(new JLabel("Codigo:"));
        panelFormulario.add(txtCodigo);
        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(new JLabel("Descripcion:"));
        panelFormulario.add(txtDescripcion);

        JPanel panelBotones = new JPanel(new FlowLayout());
        btnRegistrar = new JButton("Registrar");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        btnBuscar = new JButton("Buscar");
        btnLimpiar = new JButton("Limpiar");

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnBuscar);
        panelBotones.add(btnLimpiar);

        modeloTabla = new DefaultTableModel(new Object[]{"Codigo", "Nombre", "Descripcion"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaCategorias = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaCategorias);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        getContentPane().add(panelSuperior, BorderLayout.NORTH);
        getContentPane().add(scrollTabla, BorderLayout.CENTER);

        btnRegistrar.addActionListener(e -> registrar());
        btnModificar.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
        btnBuscar.addActionListener(e -> buscar());
        btnLimpiar.addActionListener(e -> limpiarCampos());
    }

    private void registrar() {
        Categoria categoria = new Categoria(
                txtCodigo.getText().trim(),
                txtNombre.getText().trim(),
                txtDescripcion.getText().trim());
        String error = gestionCategorias.registrar(categoria);
        if (error != null) {
            mostrarError(error);
            return;
        }
        JOptionPane.showMessageDialog(this, "Categoria registrada correctamente.");
        cargarTabla();
        limpiarCampos();
    }

    private void modificar() {
        Categoria categoria = new Categoria(
                txtCodigo.getText().trim(),
                txtNombre.getText().trim(),
                txtDescripcion.getText().trim());
        String error = gestionCategorias.modificar(categoria);
        if (error != null) {
            mostrarError(error);
            return;
        }
        JOptionPane.showMessageDialog(this, "Categoria modificada correctamente.");
        cargarTabla();
        limpiarCampos();
    }

    private void eliminar() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            mostrarError("Debe indicar el codigo de la categoria a eliminar.");
            return;
        }
        String error = gestionCategorias.eliminar(codigo);
        if (error != null) {
            mostrarError(error);
            return;
        }
        JOptionPane.showMessageDialog(this, "Categoria eliminada correctamente.");
        cargarTabla();
        limpiarCampos();
    }

    private void buscar() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            mostrarError("Debe ingresar un codigo para buscar.");
            return;
        }
        Categoria categoria = gestionCategorias.buscarPorCodigo(codigo);
        if (categoria == null) {
            mostrarError("No se encontro una categoria con ese codigo.");
            return;
        }
        txtCodigo.setText(categoria.getCodigo());
        txtNombre.setText(categoria.getNombre());
        txtDescripcion.setText(categoria.getDescripcion());
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<Categoria> categorias = gestionCategorias.listar();
        for (Categoria categoria : categorias) {
            modeloTabla.addRow(new Object[]{
                    categoria.getCodigo(),
                    categoria.getNombre(),
                    categoria.getDescripcion()
            });
        }
    }

    private void limpiarCampos() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
