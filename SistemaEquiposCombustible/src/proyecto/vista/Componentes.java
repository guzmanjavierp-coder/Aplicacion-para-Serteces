package proyecto.vista;

import java.awt.Component;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Utilidades comunes; los fallos de validacion y archivos llegan al usuario. */
final class Componentes {
    private Componentes() { }
    static DefaultTableModel tabla(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int fila, int columna) { return false; }
        };
    }
    static JButton boton(String texto, Component padre, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(e -> ejecutar(padre, accion));
        return boton;
    }
    static void ejecutar(Component padre, Runnable accion) {
        try { accion.run(); }
        catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(padre, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    static void comprobar(String error) {
        if (error != null) throw new IllegalArgumentException(error);
    }
}
