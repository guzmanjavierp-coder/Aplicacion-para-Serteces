package proyecto.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import proyecto.gestion.GestionAlertas;
import proyecto.gestion.GestionCombustible;
import proyecto.gestion.GestionTiposCombustible;
import proyecto.modelo.TipoCombustible;

public class VistaAlertas extends JFrame {
    private GestionAlertas gestionAlertas;
    private GestionTiposCombustible gestionTiposCombustible;
    private GestionCombustible gestionCombustible;
    private JComboBox<TipoCombustible> comboTipoCombustible;
    private JButton btnConsultar;
    private JButton btnMostrarTodas;
    private JButton btnActualizar;
    private JTextArea areaResultados;

    public VistaAlertas() {
        gestionAlertas = new GestionAlertas();
        gestionTiposCombustible = new GestionTiposCombustible();
        gestionCombustible = new GestionCombustible();
        configurarVentana();
        construirInterfaz();
        cargarComboTipos();
    }
    private void configurarVentana() {
        setTitle("Alertas de Bajo Inventario");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(null);
    }
    private void construirInterfaz() {
        JPanel panelSuperior = new JPanel(new FlowLayout());
        comboTipoCombustible = new JComboBox<TipoCombustible>();
        btnConsultar = new JButton("Consultar");
        btnMostrarTodas = new JButton("Mostrar Todas las Alertas");
        btnActualizar = new JButton("Actualizar");
        panelSuperior.add(comboTipoCombustible);
        panelSuperior.add(btnConsultar);
        panelSuperior.add(btnMostrarTodas);
        panelSuperior.add(btnActualizar);
        areaResultados = new JTextArea();
        areaResultados.setEditable(false);
        getContentPane().add(panelSuperior, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(areaResultados), BorderLayout.CENTER);
        btnConsultar.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { consultar(); } });
        btnMostrarTodas.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { mostrarTodasLasAlertas(); } });
        btnActualizar.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { actualizar(); } });
    }
    private void cargarComboTipos() {
        comboTipoCombustible.removeAllItems();
        List<TipoCombustible> tipos = gestionTiposCombustible.listar();
        for (int i = 0; i < tipos.size(); i++) comboTipoCombustible.addItem(tipos.get(i));
    }
    private void consultar() {
        TipoCombustible tipo = (TipoCombustible) comboTipoCombustible.getSelectedItem();
        if (tipo == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un tipo de combustible.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double existencia = gestionCombustible.calcularExistencia(tipo.getCodigo());
        String estado = gestionAlertas.tieneInventarioBajo(tipo.getCodigo()) ? "INVENTARIO BAJO" : "INVENTARIO SUFICIENTE";
        areaResultados.setText("Tipo: " + tipo.getNombre() + "\nExistencia actual: " + existencia + "\nNivel minimo: " + tipo.getNivelMinimo() + "\nEstado: " + estado);
    }
    private void mostrarTodasLasAlertas() {
        List<String> alertas = gestionAlertas.obtenerTodasLasAlertas();
        if (alertas.isEmpty()) { areaResultados.setText("No hay alertas de bajo inventario."); return; }
        String texto = "";
        for (int i = 0; i < alertas.size(); i++) texto = texto + alertas.get(i) + "\n";
        areaResultados.setText(texto);
    }
    private void actualizar() { cargarComboTipos(); mostrarTodasLasAlertas(); }
}
