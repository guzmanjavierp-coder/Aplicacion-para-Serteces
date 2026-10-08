package proyecto.vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class VistaPrincipal extends JFrame {
    private JButton btnEquipos, btnCategorias, btnMantenimientos, btnReparaciones;
    private JButton btnTiposCombustible, btnMovimientosCombustible, btnAlertas;
    public VistaPrincipal() { configurarVentana(); construirInterfaz(); }
    private void configurarVentana() {
        setTitle("Sistema de Gestión Serteces");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(560, 600));
        setLocationRelativeTo(null);
        setResizable(false);
    }
    private void construirInterfaz() {
        JLabel titulo = new JLabel("Sistema de Gestión Serteces", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 10, 15, 10));
        JPanel panelBotones = new JPanel(new GridLayout(10, 1, 10, 10));
        panelBotones.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 60, 20, 60));
        btnEquipos = new JButton("Equipos");
        btnCategorias = new JButton("Categorias");
        btnMantenimientos = new JButton("Mantenimientos");
        btnReparaciones = new JButton("Reparaciones");
        btnTiposCombustible = new JButton("Tipos de Combustible");
        btnMovimientosCombustible = new JButton("Movimientos de Combustible");
        btnAlertas = new JButton("Alertas de Bajo Inventario");
        panelBotones.add(btnEquipos); panelBotones.add(btnCategorias); panelBotones.add(btnMantenimientos);
        panelBotones.add(btnReparaciones); panelBotones.add(btnTiposCombustible); panelBotones.add(btnMovimientosCombustible); panelBotones.add(btnAlertas);
        panelBotones.add(Componentes.boton("Proveedores y registros asociados", this, () -> new VistaProveedores().setVisible(true)));
        panelBotones.add(Componentes.boton("Compras de combustible", this, () -> new VistaCompras().setVisible(true)));
        panelBotones.add(Componentes.boton("Reportes y consulta de consumo", this, () -> new VistaReportes().setVisible(true)));
        getContentPane().add(titulo, BorderLayout.NORTH); getContentPane().add(panelBotones, BorderLayout.CENTER);
        btnEquipos.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { new VistaEquipos().setVisible(true); } });
        btnCategorias.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { new VistaCategorias().setVisible(true); } });
        btnMantenimientos.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { new VistaMantenimientos().setVisible(true); } });
        btnReparaciones.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { new VistaReparaciones().setVisible(true); } });
        btnTiposCombustible.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { new VistaTiposCombustible().setVisible(true); } });
        btnMovimientosCombustible.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { Componentes.ejecutar(VistaPrincipal.this, () -> new VistaCombustible().setVisible(true)); } });
        btnAlertas.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { new VistaAlertas().setVisible(true); } });
    }
}
