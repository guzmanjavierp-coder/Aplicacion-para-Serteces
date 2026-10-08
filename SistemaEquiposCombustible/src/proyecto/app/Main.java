package proyecto.app;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import proyecto.vista.VistaPrincipal;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    DatosPrueba.cargarDatosSiEsNecesario();
                    VistaPrincipal ventana = new VistaPrincipal();
                    ventana.setVisible(true);
                } catch (IllegalStateException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "No se pudo iniciar", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
