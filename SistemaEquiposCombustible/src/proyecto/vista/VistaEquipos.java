package proyecto.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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
import proyecto.gestion.GestionCategorias;
import proyecto.gestion.GestionEquipos;
import proyecto.modelo.Categoria;
import proyecto.modelo.EstadoEquipo;
import proyecto.modelo.Equipo;

/**
 * Ventana para administrar equipos: registrar, modificar, eliminar,
 * consultar y buscar por codigo o por categoria. La logica de negocio
 * permanece en GestionEquipos y GestionCategorias.
 */
public class VistaEquipos extends JFrame {

    private final GestionEquipos gestionEquipos;
    private final GestionCategorias gestionCategorias;

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JComboBox<Categoria> comboCategoria;
    private JTextField txtCantidad;
    private JTextField txtUbicacion;
    private JComboBox<EstadoEquipo> comboEstado;
    private JTextField txtResponsable;

    private JButton btnRegistrar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnBuscarCodigo;
    private JButton btnBuscarCategoria;
    private JButton btnLimpiar;

    private JTable tablaEquipos;
    private DefaultTableModel modeloTabla;

    public VistaEquipos() {
        this.gestionEquipos = new GestionEquipos();
        this.gestionCategorias = new GestionCategorias();
        configurarVentana();
        construirInterfaz();
        cargarComboCategorias();
        cargarTabla(gestionEquipos.listar());
    }

    private void configurarVentana() {
        setTitle("Gestion de Equipos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(750, 550);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        JPanel panelFormulario = new JPanel(new GridLayout(8, 2, 5, 5));
        panelFormulario.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtCodigo = new JTextField();
        txtNombre = new JTextField();
        txtDescripcion = new JTextField();
        comboCategoria = new JComboBox<>();
        txtCantidad = new JTextField();
        txtUbicacion = new JTextField();
        comboEstado = new JComboBox<>(EstadoEquipo.values());
        txtResponsable = new JTextField();

        panelFormulario.add(new JLabel("Codigo:"));
        panelFormulario.add(txtCodigo);
        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(new JLabel("Descripcion:"));
        panelFormulario.add(txtDescripcion);
        panelFormulario.add(new JLabel("Categoria:"));
        panelFormulario.add(comboCategoria);
        panelFormulario.add(new JLabel("Cantidad:"));
        panelFormulario.add(txtCantidad);
        panelFormulario.add(new JLabel("Ubicacion:"));
        panelFormulario.add(txtUbicacion);
        panelFormulario.add(new JLabel("Estado:"));
        panelFormulario.add(comboEstado);
        panelFormulario.add(new JLabel("Responsable:"));
        panelFormulario.add(txtResponsable);

        JPanel panelBotones = new JPanel(new FlowLayout());
        btnRegistrar = new JButton("Registrar");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        btnBuscarCodigo = new JButton("Buscar por Codigo");
        btnBuscarCategoria = new JButton("Buscar por Categoria");
        btnLimpiar = new JButton("Limpiar");

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnBuscarCodigo);
        panelBotones.add(btnBuscarCategoria);
        panelBotones.add(btnLimpiar);

        modeloTabla = new DefaultTableModel(new Object[]{
                "Codigo", "Nombre", "Categoria", "Cantidad", "Ubicacion", "Estado", "Responsable"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaEquipos = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaEquipos);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        getContentPane().add(panelSuperior, BorderLayout.NORTH);
        getContentPane().add(scrollTabla, BorderLayout.CENTER);

        btnRegistrar.addActionListener(e -> registrar());
        btnModificar.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
        btnBuscarCodigo.addActionListener(e -> buscarPorCodigo());
        btnBuscarCategoria.addActionListener(e -> buscarPorCategoria());
        btnLimpiar.addActionListener(e -> limpiarCampos());
    }

    private void cargarComboCategorias() {
        comboCategoria.removeAllItems();
        List<Categoria> categorias = gestionCategorias.listar();
        for (Categoria categoria : categorias) {
            comboCategoria.addItem(categoria);
        }
    }

    private void registrar() {
        Equipo equipo = construirEquipoDesdeFormulario();
        if (equipo == null) {
            return;
        }
        String error = gestionEquipos.registrar(equipo);
        if (error != null) {
            mostrarError(error);
            return;
        }
        JOptionPane.showMessageDialog(this, "Equipo registrado correctamente.");
        cargarTabla(gestionEquipos.listar());
        limpiarCampos();
    }

    private void modificar() {
        Equipo equipo = construirEquipoDesdeFormulario();
        if (equipo == null) {
            return;
        }
        String error = gestionEquipos.modificar(equipo);
        if (error != null) {
            mostrarError(error);
            return;
        }
        JOptionPane.showMessageDialog(this, "Equipo modificado correctamente.");
        cargarTabla(gestionEquipos.listar());
        limpiarCampos();
    }

    private void eliminar() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            mostrarError("Debe indicar el codigo del equipo a eliminar.");
            return;
        }
        String error = gestionEquipos.eliminar(codigo);
        if (error != null) {
            mostrarError(error);
            return;
        }
        JOptionPane.showMessageDialog(this, "Equipo eliminado correctamente.");
        cargarTabla(gestionEquipos.listar());
        limpiarCampos();
    }

    private void buscarPorCodigo() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            mostrarError("Debe ingresar un codigo para buscar.");
            return;
        }
        Equipo equipo = gestionEquipos.buscarPorCodigo(codigo);
        if (equipo == null) {
            mostrarError("No se encontro un equipo con ese codigo.");
            return;
        }
        cargarFormularioConEquipo(equipo);
    }

    private void buscarPorCategoria() {
        Categoria categoriaSeleccionada = (Categoria) comboCategoria.getSelectedItem();
        if (categoriaSeleccionada == null) {
            mostrarError("Debe seleccionar una categoria para buscar.");
            return;
        }
        List<Equipo> equipos = gestionEquipos.buscarPorCategoria(categoriaSeleccionada.getCodigo());
        if (equipos.isEmpty()) {
            mostrarError("No se encontraron equipos para la categoria seleccionada.");
        }
        cargarTabla(equipos);
    }

    private Equipo construirEquipoDesdeFormulario() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String ubicacion = txtUbicacion.getText().trim();
        String responsable = txtResponsable.getText().trim();

        if (codigo.isEmpty() || nombre.isEmpty() || ubicacion.isEmpty() || responsable.isEmpty()) {
            mostrarError("Debe completar todos los campos obligatorios.");
            return null;
        }

        Categoria categoriaSeleccionada = (Categoria) comboCategoria.getSelectedItem();
        if (categoriaSeleccionada == null) {
            mostrarError("Debe seleccionar una categoria.");
            return null;
        }

        EstadoEquipo estadoSeleccionado = (EstadoEquipo) comboEstado.getSelectedItem();

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarError("La cantidad debe ser un numero entero valido.");
            return null;
        }

        return new Equipo(codigo, nombre, descripcion, categoriaSeleccionada.getCodigo(),
                cantidad, ubicacion, estadoSeleccionado, responsable);
    }

    private void cargarFormularioConEquipo(Equipo equipo) {
        txtCodigo.setText(equipo.getCodigo());
        txtNombre.setText(equipo.getNombre());
        txtDescripcion.setText(equipo.getDescripcion());
        txtCantidad.setText(String.valueOf(equipo.getCantidad()));
        txtUbicacion.setText(equipo.getUbicacion());
        txtResponsable.setText(equipo.getResponsable());
        comboEstado.setSelectedItem(equipo.getEstado());

        for (int i = 0; i < comboCategoria.getItemCount(); i++) {
            Categoria categoria = comboCategoria.getItemAt(i);
            if (categoria.getCodigo().equals(equipo.getCodigoCategoria())) {
                comboCategoria.setSelectedIndex(i);
                break;
            }
        }
    }

    private void cargarTabla(List<Equipo> equipos) {
        modeloTabla.setRowCount(0);
        for (Equipo equipo : equipos) {
            modeloTabla.addRow(new Object[]{
                    equipo.getCodigo(),
                    equipo.getNombre(),
                    equipo.getCodigoCategoria(),
                    equipo.getCantidad(),
                    equipo.getUbicacion(),
                    equipo.getEstado(),
                    equipo.getResponsable()
            });
        }
    }

    private void limpiarCampos() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtCantidad.setText("");
        txtUbicacion.setText("");
        txtResponsable.setText("");
        if (comboCategoria.getItemCount() > 0) {
            comboCategoria.setSelectedIndex(0);
        }
        comboEstado.setSelectedIndex(0);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
