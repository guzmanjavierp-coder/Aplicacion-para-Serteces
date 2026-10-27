package proyecto.vista;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import proyecto.gestion.*;

public class VistaPrincipal extends JFrame {
    private final JPanel accesos = new JPanel(new GridLayout(0, 3, 14, 14));
    private final JLabel seccion = new JLabel("Todos los módulos");
    private final JLabel equipos = new JLabel(), combustibles = new JLabel(), alertas = new JLabel();
    private final JLabel[] cifras = {equipos, combustibles, alertas};
    private final java.util.List<JButton> navegacion = new java.util.ArrayList<>();
    private final String[][] modulos = {
        {"Equipos", "Inventario, ubicación y responsables", "Equipos"},
        {"Categorías", "Organiza y clasifica tus equipos", "Equipos"},
        {"Mantenimientos", "Servicios e historial de cada equipo", "Equipos"},
        {"Reparaciones", "Reparaciones, observaciones y costos", "Equipos"},
        {"Tipos de combustible", "Catálogo y niveles mínimos", "Combustible"},
        {"Movimientos", "Registra entradas y salidas", "Combustible"},
        {"Compras", "Compras y costo por galón", "Combustible"},
        {"Alertas de inventario", "Consulta los combustibles en nivel bajo", "Combustible"},
        {"Proveedores", "Empresas y sus registros asociados", "Administración"},
        {"Responsables", "Consulta y agrega responsables", "Administración"},
        {"Reportes y consumo", "Consulta y exporta tus reportes", "Administración"}
    };
    public VistaPrincipal() {
        Estilo.iniciar();
        setTitle("Serteces | Panel de control"); setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(Math.min(1230, pantalla.width-60), Math.min(900, pantalla.height-80));
        setMinimumSize(new Dimension(960, 680)); setLocationRelativeTo(null);
        getContentPane().setBackground(Estilo.FONDO);
        add(barraLateral(), BorderLayout.WEST);
        JPanel contenido = new JPanel(new BorderLayout(0,20)); contenido.setBackground(Estilo.FONDO);
        contenido.setBorder(new EmptyBorder(28,28,24,28));
        JPanel cabecera = new JPanel(new BorderLayout(0,22)); cabecera.setOpaque(false);
        JPanel bienvenida = new JPanel(new GridLayout(2,1,0,8)); bienvenida.setOpaque(false);
        JLabel titulo = new JLabel("Panel de control"); titulo.setFont(new Font("Segoe UI",Font.BOLD,30)); titulo.setForeground(Estilo.TEXTO);
        JLabel descripcion = new JLabel("Todo lo que necesitas para organizar equipos y combustible."); descripcion.setFont(new Font("Segoe UI",Font.PLAIN,14)); descripcion.setForeground(Estilo.TENUE);
        bienvenida.add(titulo); bienvenida.add(descripcion); cabecera.add(bienvenida,BorderLayout.NORTH);
        JPanel resumen = new JPanel(new GridLayout(1,3,14,0)); resumen.setOpaque(false);
        String[] nombres = {"Equipos registrados", "Tipos de combustible", "Alertas de bajo nivel"};
        for(int i=0;i<cifras.length;i++) {
            JPanel tarjeta = new JPanel(new BorderLayout(0,6)); tarjeta.setBackground(Color.WHITE);
            tarjeta.setBorder(BorderFactory.createCompoundBorder(new Estilo.BordeRedondo(new Color(218,227,237)), new EmptyBorder(16,18,16,18)));
            cifras[i].setFont(new Font("Segoe UI",Font.BOLD,30)); cifras[i].setForeground(i==2?new Color(168,92,13):Estilo.VERDE);
            JLabel etiqueta=new JLabel(nombres[i]); etiqueta.setFont(new Font("Segoe UI",Font.PLAIN,12)); etiqueta.setForeground(Estilo.TENUE);
            tarjeta.add(cifras[i],BorderLayout.CENTER); tarjeta.add(etiqueta,BorderLayout.SOUTH); resumen.add(tarjeta);
        }
        cabecera.add(resumen,BorderLayout.CENTER); contenido.add(cabecera,BorderLayout.NORTH);
        JPanel centro=new JPanel(new BorderLayout(0,14)); centro.setOpaque(false);
        seccion.setFont(new Font("Segoe UI",Font.BOLD,18)); seccion.setForeground(Estilo.TEXTO); centro.add(seccion,BorderLayout.NORTH);
        accesos.setOpaque(false);
        JPanel contenedor = new JPanel(new BorderLayout()); contenedor.setOpaque(false); contenedor.add(accesos,BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(contenedor); scroll.setBorder(null); scroll.setOpaque(false); scroll.getViewport().setOpaque(false); scroll.getVerticalScrollBar().setUnitIncrement(24);
        centro.add(scroll); contenido.add(centro);
        JLabel pie=new JLabel("SERTECES  ·  Sistema de gestión de equipos y combustible"); pie.setForeground(Estilo.TENUE); pie.setFont(new Font("Segoe UI",Font.PLAIN,12)); contenido.add(pie,BorderLayout.SOUTH);
        add(contenido); mostrar("Inicio"); actualizarResumen();
        addWindowFocusListener(new WindowAdapter() { @Override public void windowGainedFocus(WindowEvent e) { actualizarResumen(); } });
    }
    private JPanel barraLateral() {
        JPanel barra=new JPanel(new BorderLayout()); barra.setBackground(Estilo.AZUL); barra.setPreferredSize(new Dimension(210,100)); barra.setBorder(new EmptyBorder(32,20,24,20));
        JPanel marca=new JPanel(new GridLayout(2,1,0,8)); marca.setOpaque(false);
        JLabel nombre=new JLabel("SERTECES"); nombre.setFont(new Font("Segoe UI",Font.BOLD,23)); nombre.setForeground(Color.WHITE);
        JLabel leyenda=new JLabel("EQUIPOS Y COMBUSTIBLE"); leyenda.setFont(new Font("Segoe UI",Font.PLAIN,10)); leyenda.setForeground(new Color(167,188,208)); marca.add(nombre); marca.add(leyenda);
        barra.add(marca,BorderLayout.NORTH);
        JPanel menu=new JPanel(new GridLayout(4,1,0,10)); menu.setOpaque(false); menu.setBorder(new EmptyBorder(40,0,0,0));
        for(String grupo:new String[]{"Inicio","Equipos","Combustible","Administración"}) {
            JButton boton=Componentes.boton(grupo,this,()->mostrar(grupo)); Estilo.boton(boton);
            boton.setHorizontalAlignment(SwingConstants.LEFT); boton.setBackground(new Color(35,57,83)); boton.setForeground(Color.WHITE);
            navegacion.add(boton);
            menu.add(boton);
        }
        JPanel posicion=new JPanel(new BorderLayout()); posicion.setOpaque(false); posicion.add(menu,BorderLayout.NORTH); barra.add(posicion);
        JLabel abajo=new JLabel("Control y organización"); abajo.setFont(new Font("Segoe UI",Font.PLAIN,11)); abajo.setForeground(new Color(167,188,208)); barra.add(abajo,BorderLayout.SOUTH);
        return barra;
    }
    private void mostrar(String grupo) {
        for (JButton boton : navegacion) boton.setBackground(boton.getText().equals(grupo) ? Estilo.VERDE : new Color(35,57,83));
        seccion.setText(grupo.equals("Inicio")?"Todos los módulos":grupo); accesos.removeAll();
        for(int i=0;i<modulos.length;i++) if(grupo.equals("Inicio")||modulos[i][2].equals(grupo)) {
            final int indice=i; String[] modulo=modulos[i];
            JButton boton=Componentes.boton("<html><div style='width:195px'><span style='font-size:14px;color:#007769'>"+modulo[2]+"</span><br><b style='font-size:17px'>"+modulo[0]+"</b><br><span style='font-size:11px;color:#63748a'>"+modulo[1]+"</span></div></html>",this,()->abrir(indice));
            Estilo.boton(boton); boton.setHorizontalAlignment(SwingConstants.LEFT); boton.setBackground(Color.WHITE); boton.setForeground(Estilo.TEXTO); boton.setPreferredSize(new Dimension(240,115));
            accesos.add(boton);
        }
        accesos.revalidate(); accesos.repaint();
    }
    private void abrir(int modulo) {
        JFrame ventana;
        switch(modulo) {
            case 0: ventana=new VistaEquipos(); break;
            case 1: ventana=new VistaCategorias(); break;
            case 2: ventana=new VistaMantenimientos(); break;
            case 3: ventana=new VistaReparaciones(); break;
            case 4: ventana=new VistaTiposCombustible(); break;
            case 5: ventana=new VistaCombustible(); break;
            case 6: ventana=new VistaCompras(); break;
            case 7: ventana=new VistaAlertas(); break;
            case 8: ventana=new VistaProveedores(); break;
            case 9: ventana=new VistaResponsables(); break;
            default: ventana=new VistaReportes(); break;
        }
        ventana.setVisible(true);
    }
    private void actualizarResumen() {
        try {
            equipos.setText(""+new GestionEquipos().listar().size());
            combustibles.setText(""+new GestionTiposCombustible().listar().size());
            alertas.setText(""+new GestionAlertas().obtenerTodasLasAlertas().size());
        } catch(IllegalStateException ex) { for(JLabel cifra:cifras) { cifra.setText("—"); cifra.setToolTipText(ex.getMessage()); } }
    }
}
