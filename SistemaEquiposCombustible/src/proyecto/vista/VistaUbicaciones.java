package proyecto.vista;

import java.awt.*;
import javax.swing.*;
import proyecto.gestion.GestionUbicaciones;

/** Administracion del catalogo de ubicaciones. */
public class VistaUbicaciones extends JFrame {
    private final GestionUbicaciones gestion = new GestionUbicaciones();
    private final DefaultListModel<String> modelo = new DefaultListModel<>();
    private final JList<String> lista = new JList<>(modelo);
    private final JTextField nombre = new JTextField();

    public VistaUbicaciones() {
        setTitle("Gestion de Ubicaciones");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);
        JPanel formulario = new JPanel(new BorderLayout(10, 10));
        formulario.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        formulario.add(new JLabel("Nombre de la ubicacion:"), BorderLayout.WEST);
        formulario.add(nombre);
        add(formulario, BorderLayout.NORTH);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && lista.getSelectedValue() != null)
                nombre.setText(lista.getSelectedValue());
        });
        add(new JScrollPane(lista));
        JPanel botones = new JPanel(new FlowLayout());
        JButton agregar = new JButton("Agregar"), editar = new JButton("Guardar cambios"),
                eliminar = new JButton("Eliminar"), limpiar = new JButton("Nueva / limpiar");
        for (JButton boton : new JButton[]{agregar, editar, eliminar, limpiar}) botones.add(boton);
        add(botones, BorderLayout.SOUTH);
        agregar.addActionListener(e -> ejecutar(() -> gestion.registrar(nombre.getText())));
        editar.addActionListener(e -> ejecutar(() -> gestion.modificar(lista.getSelectedValue(), nombre.getText())));
        eliminar.addActionListener(e -> {
            if (lista.getSelectedValue() == null) {
                JOptionPane.showMessageDialog(this, "Seleccione una ubicacion.");
            } else if (JOptionPane.showConfirmDialog(this, "¿Eliminar la ubicacion seleccionada?",
                    "Eliminar ubicacion", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                ejecutar(() -> gestion.eliminar(lista.getSelectedValue()));
            }
        });
        limpiar.addActionListener(e -> { lista.clearSelection(); nombre.setText(""); });
        recargar();
        Estilo.aplicar(this);
    }

    private void ejecutar(java.util.function.Supplier<String> accion) {
        try {
            String error = accion.get();
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Ubicaciones", JOptionPane.ERROR_MESSAGE);
                return;
            }
            recargar();
            nombre.setText("");
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void recargar() {
        modelo.clear();
        for (String ubicacion : gestion.listar()) modelo.addElement(ubicacion);
    }
}
