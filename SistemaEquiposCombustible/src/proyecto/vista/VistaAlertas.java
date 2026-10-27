package proyecto.vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import proyecto.gestion.*;
import proyecto.modelo.TipoCombustible;

public class VistaAlertas extends JFrame {
    private final JComboBox<String> filtro = new JComboBox<>();
    private final DefaultTableModel modelo = Componentes.tabla("Codigo", "Combustible", "Existencia (galones)", "Nivel minimo (galones)", "Faltante para el minimo");
    private final JLabel resumen = new JLabel();
    private boolean actualizando;
    public VistaAlertas() {
        setTitle("Alertas de bajo inventario"); setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(950, 450); setLocationRelativeTo(null);
        JPanel superior = new JPanel(new FlowLayout()); superior.add(new JLabel("Tipo de combustible:")); superior.add(filtro);
        superior.add(Componentes.boton("Actualizar", this, this::actualizar));
        add(superior, BorderLayout.NORTH); add(new JScrollPane(new JTable(modelo))); add(resumen, BorderLayout.SOUTH);
        filtro.addActionListener(e -> { if (!actualizando) Componentes.ejecutar(this, this::cargarTabla); });
        actualizar();
        Estilo.aplicar(this);
    }
    private void actualizar() {
        Object seleccionado = filtro.getSelectedItem(); actualizando = true;
        try {
            filtro.removeAllItems(); filtro.addItem("Todos");
            for (TipoCombustible t : new GestionTiposCombustible().listar()) filtro.addItem(t.getCodigo() + " - " + t.getNombre());
            if (seleccionado != null) for (int i = 0; i < filtro.getItemCount(); i++) if (seleccionado.equals(filtro.getItemAt(i))) filtro.setSelectedIndex(i);
        } finally { actualizando = false; }
        cargarTabla();
    }
    private void cargarTabla() {
        modelo.setRowCount(0); GestionCombustible gestion = new GestionCombustible();
        for (TipoCombustible t : new GestionTiposCombustible().listar()) {
            if (filtro.getSelectedIndex() > 0 && !(t.getCodigo()+" - "+t.getNombre()).equals(filtro.getSelectedItem())) continue;
            double existencia = gestion.calcularExistencia(t.getCodigo());
            if (existencia <= t.getNivelMinimo()) modelo.addRow(new Object[]{t.getCodigo(), t.getNombre(), existencia, t.getNivelMinimo(), Math.max(0, t.getNivelMinimo()-existencia)});
        }
        resumen.setText(modelo.getRowCount() == 0 ? "No hay combustibles en nivel bajo para este filtro." : modelo.getRowCount() + " tipo(s) de combustible en nivel minimo o inferior.");
    }
}
