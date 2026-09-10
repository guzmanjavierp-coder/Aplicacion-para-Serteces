package proyecto.persistencia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import proyecto.modelo.Reparacion;

/**
 * Encargada de leer y escribir la lista de reparaciones en el archivo reparaciones.dat.
 */
public class ArchivoReparaciones {

    private static final String RUTA_ARCHIVO = "reparaciones.dat";

    @SuppressWarnings("unchecked")
    public List<Reparacion> leer() {
        File archivo = new File(RUTA_ARCHIVO);
        if (!archivo.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (List<Reparacion>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al leer reparaciones.dat: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void guardar(List<Reparacion> reparaciones) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RUTA_ARCHIVO))) {
            oos.writeObject(reparaciones);
        } catch (IOException e) {
            System.out.println("Error al guardar reparaciones.dat: " + e.getMessage());
        }
    }
}
