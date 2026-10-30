package proyecto.vista;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;

/** Tema compartido Swing sin dependencias externas. */
public final class Estilo {
    public static final Color FONDO = new Color(242, 246, 251);
    public static final Color TEXTO = new Color(27, 44, 67);
    public static final Color TENUE = new Color(99, 116, 138);
    public static final Color AZUL = new Color(20, 39, 65);
    public static final Color VERDE = new Color(0, 119, 105);
    private static final Color LINEA = new Color(218, 227, 237);
    private static boolean instalado;
    private Estilo() { }

    public static void iniciar() {
        if (instalado) return;
        instalado = true;
        Font normal = new Font("Segoe UI", Font.PLAIN, 13);
        for (String nombre : new String[]{"Label", "Button", "TextField", "TextArea", "ComboBox", "Table", "TableHeader", "TabbedPane", "OptionPane", "List", "MenuItem"})
            UIManager.put(nombre + ".font", normal);
        UIManager.put("Panel.background", FONDO);
        UIManager.put("Label.foreground", TEXTO);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", TEXTO);
        UIManager.put("TextField.selectionBackground", new Color(192, 230, 222));
        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("ComboBox.foreground", TEXTO);
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.foreground", TEXTO);
        UIManager.put("Table.selectionBackground", new Color(212, 238, 233));
        UIManager.put("Table.selectionForeground", TEXTO);
        UIManager.put("ScrollPane.background", FONDO);
        UIManager.put("Button.defaultButtonFollowsFocus", Boolean.FALSE);
    }

    public static void aplicar(JFrame ventana) {
        iniciar();
        ventana.setResizable(true);
        Container raiz = ventana.getContentPane();
        decorar(raiz);
        if (raiz instanceof JComponent) ((JComponent)raiz).setBorder(new Cabecera(ventana.getTitle()));
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        int ancho = Math.max(ventana.getWidth(), ventana instanceof VistaProveedores ? 1280 : 1160);
        int alto = Math.max(ventana.getHeight(), 790);
        ventana.setSize(Math.min(ancho, Math.max(800, pantalla.width-60)), Math.min(alto, Math.max(600, pantalla.height-80)));
        ventana.setLocationRelativeTo(null);
    }

    static void decorar(Component componente) {
        if (componente instanceof JPanel) componente.setBackground(FONDO);
        if (componente instanceof JLabel) {
            componente.setFont(new Font("Segoe UI", Font.PLAIN, 13)); componente.setForeground(TEXTO);
            JLabel etiqueta = (JLabel)componente;
            etiqueta.setBorder(BorderFactory.createCompoundBorder(etiqueta.getBorder(), new EmptyBorder(2,0,2,12)));
        }
        if (componente instanceof JTextField) {
            JTextField campo = (JTextField)componente;
            campo.setFont(new Font("Segoe UI", Font.PLAIN, 14)); campo.setBackground(Color.WHITE); campo.setForeground(TEXTO);
            campo.setBorder(BorderFactory.createCompoundBorder(new BordeRedondo(LINEA), new EmptyBorder(7, 10, 7, 10)));
            campo.setMinimumSize(new Dimension(60, 34));
        }
        if (componente instanceof JComboBox<?>) {
            JComboBox<?> combo = (JComboBox<?>)componente;
            combo.setFont(new Font("Segoe UI", Font.PLAIN, 13)); combo.setBackground(Color.WHITE);
            combo.setBorder(new BordeRedondo(LINEA)); combo.setPreferredSize(new Dimension(Math.min(360, Math.max(160, combo.getPreferredSize().width)), 36));
            combo.setRenderer(new DefaultListCellRenderer() {
                @Override public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice, boolean seleccionado, boolean foco) {
                    if (valor instanceof proyecto.modelo.Equipo) {
                        proyecto.modelo.Equipo e=(proyecto.modelo.Equipo)valor; valor=e.getNombre();
                    } else if (valor instanceof proyecto.modelo.Categoria) {
                        proyecto.modelo.Categoria c=(proyecto.modelo.Categoria)valor; valor=c.getCodigo()+" - "+c.getNombre();
                    } else if (valor instanceof proyecto.modelo.TipoCombustible) {
                        proyecto.modelo.TipoCombustible t=(proyecto.modelo.TipoCombustible)valor; valor=t.getCodigo()+" - "+t.getNombre();
                    } else if (valor instanceof proyecto.modelo.Proveedor) {
                        proyecto.modelo.Proveedor p=(proyecto.modelo.Proveedor)valor; valor=p.getCodigo()+" - "+p.getEmpresa();
                    } else if (valor instanceof Enum<?>) valor=valor.toString().replace('_',' ');
                    Component celda=super.getListCellRendererComponent(lista,valor,indice,seleccionado,foco);
                    setBorder(new EmptyBorder(5,8,5,8)); return celda;
                }
            });
        }
        if (componente instanceof JButton) boton((JButton)componente);
        if (componente instanceof JTable) {
            JTable tabla = (JTable)componente;
            tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13)); tabla.setRowHeight(36);
            tabla.setShowGrid(false); tabla.setIntercellSpacing(new Dimension(0, 0));
            tabla.setBackground(Color.WHITE); tabla.setSelectionBackground(new Color(212, 238, 233)); tabla.setSelectionForeground(TEXTO);
            tabla.setFillsViewportHeight(true);
            tabla.setDefaultRenderer(Object.class, new Celda());
            tabla.setDefaultRenderer(Number.class, new Celda());
            tabla.getTableHeader().setPreferredSize(new Dimension(100, 40));
            tabla.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(JTable t, Object valor, boolean s, boolean f, int fila, int columna) {
                    super.getTableCellRendererComponent(t, valor, s, f, fila, columna);
                    setBackground(AZUL); setForeground(Color.WHITE); setFont(new Font("Segoe UI", Font.BOLD, 12));
                    setBorder(new EmptyBorder(10, 10, 10, 10)); return this;
                }
            });
        }
        if (componente instanceof JScrollPane) {
            JScrollPane scroll = (JScrollPane)componente;
            scroll.setBorder(new BordeRedondo(LINEA)); scroll.getViewport().setBackground(Color.WHITE);
            scroll.getVerticalScrollBar().setUnitIncrement(24);
        }
        if (componente instanceof JSplitPane) {
            JSplitPane division = (JSplitPane)componente; division.setBorder(new EmptyBorder(0,0,0,0)); division.setDividerSize(10);
        }
        if (componente instanceof Container && !(componente instanceof JTable) && !(componente instanceof JComboBox<?>) && !(componente instanceof JButton))
            for (Component hijo : ((Container)componente).getComponents()) decorar(hijo);
    }

    static void boton(JButton boton) {
        boton.setUI(new BotonUI()); boton.setOpaque(false); boton.setContentAreaFilled(false); boton.setBorderPainted(false);
        boton.setFocusPainted(false); boton.setRolloverEnabled(true); boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setBorder(new EmptyBorder(11, 15, 11, 15));
        String texto = boton.getText() == null ? "" : boton.getText().toLowerCase(java.util.Locale.ROOT);
        boolean principal = texto.startsWith("registrar") || texto.startsWith("guardar") || texto.equals("agregar") || texto.startsWith("generar") || texto.startsWith("exportar");
        boolean peligro = texto.startsWith("eliminar") || texto.startsWith("quitar");
        boton.setBackground(principal ? VERDE : peligro ? new Color(252, 234, 236) : Color.WHITE);
        boton.setForeground(principal ? Color.WHITE : peligro ? new Color(169, 38, 57) : TEXTO);
    }

    private static class Celda extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccion, boolean foco, int fila, int columna) {
            super.getTableCellRendererComponent(tabla, valor, seleccion, foco, fila, columna);
            setBackground(seleccion ? new Color(212,238,233) : fila % 2 == 0 ? Color.WHITE : new Color(246,249,252));
            setForeground(TEXTO); setBorder(new EmptyBorder(7,10,7,10)); setHorizontalAlignment(valor instanceof Number ? RIGHT : LEFT);
            setToolTipText(valor == null ? null : valor.toString()); return this;
        }
    }
    static class BordeRedondo extends AbstractBorder {
        private final Color color;
        BordeRedondo(Color color) { this.color = color; }
        @Override public Insets getBorderInsets(Component c) { return new Insets(1,1,1,1); }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D p = (Graphics2D)g.create(); p.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            p.setColor(color); p.drawRoundRect(x,y,w-1,h-1,10,10); p.dispose();
        }
    }
    private static class BotonUI extends BasicButtonUI {
        @Override public void paint(Graphics g, JComponent c) {
            JButton b = (JButton)c;
            Graphics2D p = (Graphics2D)g.create(); p.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color color = b.getBackground();
            if (b.getModel().isPressed()) color = color.darker();
            else if (b.getModel().isRollover()) color = color.equals(Color.WHITE) ? new Color(231,241,247) : color.darker();
            p.setColor(color); p.fill(new RoundRectangle2D.Double(0,0,c.getWidth()-1,c.getHeight()-1,12,12));
            p.setColor(b.hasFocus() ? VERDE : LINEA); p.drawRoundRect(0,0,c.getWidth()-1,c.getHeight()-1,12,12);
            p.dispose(); super.paint(g,c);
        }
    }
    private static class Cabecera extends AbstractBorder {
        private final String titulo;
        Cabecera(String titulo) { this.titulo = titulo; }
        @Override public Insets getBorderInsets(Component c) { return new Insets(90,24,22,24); }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D p=(Graphics2D)g.create(); p.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            p.setColor(TEXTO); p.setFont(new Font("Segoe UI",Font.BOLD,25)); p.drawString(titulo,24,37);
            p.setColor(TENUE); p.setFont(new Font("Segoe UI",Font.PLAIN,13)); p.drawString("SERTECES   /   Gestión de equipos y combustible",25,61);
            p.setColor(LINEA); p.drawLine(24,76,w-24,76); p.dispose();
        }
    }
}
