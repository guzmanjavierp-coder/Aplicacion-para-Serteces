package proyecto.vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import proyecto.gestion.GestionResponsables;

public class VistaResponsables extends JFrame {
    private final GestionResponsables gestion = new GestionResponsables();
    private final DefaultTableModel modelo = Componentes.tabla("Responsables registrados");
    private final JTextField nombre = new JTextField(25);
    public VistaResponsables() {
        setTitle("Responsables disponibles"); setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(650, 420); setLocationRelativeTo(null);
        JPanel superior = new JPanel(new FlowLayout());
        superior.add(new JLabel("Nombre:")); superior.add(nombre);
        superior.add(Componentes.boton("Agregar", this, () -> {
            Componentes.comprobar(gestion.registrar(nombre.getText())); nombre.setText(""); cargar();
        }));
        superior.add(Componentes.boton("Actualizar", this, this::cargar));
        add(superior, BorderLayout.NORTH); add(new JScrollPane(new JTable(modelo)));
        cargar();
        Estilo.aplicar(this);
    }
    private void cargar() { modelo.setRowCount(0); for (String n : gestion.listar()) modelo.addRow(new Object[]{n}); }
}
