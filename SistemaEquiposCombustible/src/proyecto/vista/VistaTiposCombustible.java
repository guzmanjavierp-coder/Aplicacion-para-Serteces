package proyecto.vista;

import proyecto.util.GeneradorId;

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
import proyecto.gestion.GestionCombustible;
import proyecto.modelo.MovimientoCombustible;
import proyecto.modelo.TipoMovimientoCombustible;
import proyecto.util.FechaUtil;
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
    private JTextField txtPrecio;

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
        Estilo.aplicar(this);
        javax.swing.Timer actualizacionStock = new javax.swing.Timer(1000, e -> {
            if (isShowing()) Componentes.ejecutar(this, this::actualizarStock);
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) { actualizacionStock.stop(); }
        });
        actualizacionStock.start();
    }

    private void configurarVentana() {
        setTitle("Gestion de Tipos de Combustible");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 550);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        JPanel panelFormulario = new JPanel(new GridLayout(3, 2, 5, 5));
        panelFormulario.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtCodigo = new JTextField();
        txtNombre = new JTextField();
        txtNivelMinimo = new JTextField();
        txtPrecio = new JTextField();

        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(new JLabel("Nivel Minimo:"));
        panelFormulario.add(txtNivelMinimo);
        panelFormulario.add(new JLabel("Precio por galon:"));
        panelFormulario.add(txtPrecio);

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

        modeloTabla = new DefaultTableModel(new Object[]{"Codigo", "Nombre", "Nivel Minimo", "Precio por galon", "Stock (galones)"}, 0) {
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaTipos = new JTable(modeloTabla);
        tablaTipos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaTipos.getSelectedRow() >= 0) cargarSeleccionado();
        });
        JScrollPane scrollTabla = new JScrollPane(tablaTipos);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        getContentPane().add(panelSuperior, BorderLayout.NORTH);
        getContentPane().add(scrollTabla, BorderLayout.CENTER);
        JPanel panelStock = new JPanel(new FlowLayout());
        panelStock.add(new JLabel("Seleccione un combustible para registrar:"));
        panelStock.add(Componentes.boton("Bajar galones", this, this::registrarConsumo));
        panelStock.add(new JLabel("Las compras suman stock automaticamente."));
        getContentPane().add(panelStock, BorderLayout.SOUTH);

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
        txtCodigo.setText(GeneradorId.generarId("COMB"));
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
        String codigo = JOptionPane.showInputDialog(this, "Codigo que desea consultar:");
        if (codigo == null) return;
        codigo = codigo.trim();
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
        txtPrecio.setText(tipo.getCostoPorGalon() == null ? "" : tipo.getCostoPorGalon().toString());
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
        txtPrecio.setText(tipo.getCostoPorGalon() == null ? "" : tipo.getCostoPorGalon().toString());
    }

    private TipoCombustible construirDesdeFormulario() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        if (codigo.isEmpty() || nombre.isEmpty()) {
            mostrarError("Seleccione un registro para editar y complete el nombre.");
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
        TipoCombustible resultado = new TipoCombustible(codigo, nombre, nivelMinimo);
        try {
            if (!txtPrecio.getText().trim().isEmpty()) resultado.setCostoPorGalon(Double.valueOf(txtPrecio.getText().trim()));
        } catch (IllegalArgumentException ex) {
            mostrarError("El precio por galon debe ser un numero finito y no negativo.");
            return null;
        }
        return resultado;
    }

    private void cargarTabla(List<TipoCombustible> lista) {
        modeloTabla.setRowCount(0);
        for (int i = 0; i < lista.size(); i++) {
            TipoCombustible tipo = lista.get(i);
            modeloTabla.addRow(new Object[]{
                    tipo.getCodigo(),
                    tipo.getNombre(),
                    tipo.getNivelMinimo(),
                    tipo.getCostoPorGalon() == null ? "No configurado" : tipo.getCostoPorGalon(),
                    new GestionCombustible().calcularExistencia(tipo.getCodigo())
            });
        }
    }

    private void actualizarStock() {
        GestionCombustible combustible = new GestionCombustible();
        for (int fila = 0; fila < modeloTabla.getRowCount(); fila++) {
            double stock = combustible.calcularExistencia((String) modeloTabla.getValueAt(fila, 0));
            if (!Double.valueOf(stock).equals(modeloTabla.getValueAt(fila, 4)))
                modeloTabla.setValueAt(stock, fila, 4);
        }
    }

    private void registrarConsumo() {
        int fila = tablaTipos.getSelectedRow();
        if (fila < 0) throw new IllegalArgumentException("Seleccione un tipo de combustible en la tabla.");
        int modelo = tablaTipos.convertRowIndexToModel(fila);
        String codigo = (String) modeloTabla.getValueAt(modelo, 0);
        GestionCombustible combustible = new GestionCombustible();
        JTextField cantidad = new JTextField();
        SelectorResponsable responsable = new SelectorResponsable();
        JPanel formulario = new JPanel(new GridLayout(0, 2, 8, 8));
        formulario.add(new JLabel("Combustible:")); formulario.add(new JLabel("" + modeloTabla.getValueAt(modelo, 1)));
        formulario.add(new JLabel("Stock actual (galones):")); formulario.add(new JLabel("" + combustible.calcularExistencia(codigo)));
        formulario.add(new JLabel("Galones a retirar:")); formulario.add(cantidad);
        formulario.add(new JLabel("Responsable:")); formulario.add(responsable);
        if (JOptionPane.showConfirmDialog(this, formulario,
                "Consumo de galones",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        double galones;
        try { galones = Double.parseDouble(cantidad.getText().trim()); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException("Ingrese una cantidad valida de galones."); }
        if (!Double.isFinite(galones) || galones <= 0)
            throw new IllegalArgumentException("Los galones deben ser un numero finito mayor que cero.");
        if (responsable.getText().trim().isEmpty()) throw new IllegalArgumentException("Indique el responsable.");
        MovimientoCombustible movimiento = new MovimientoCombustible(GeneradorId.generarId("MOV"), codigo,
                TipoMovimientoCombustible.SALIDA, galones, FechaUtil.hoy(), responsable.getText().trim());
        Componentes.comprobar(combustible.registrarSalida(movimiento));
        actualizarStock();
        JOptionPane.showMessageDialog(this, "Movimiento registrado. Stock disponible: "
                + combustible.calcularExistencia(codigo) + " galones.");
    }

    private void limpiarCampos() {
        tablaTipos.clearSelection();
        txtCodigo.setText("");
        txtNombre.setText("");
        txtNivelMinimo.setText("");
        txtPrecio.setText("");
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
