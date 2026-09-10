package proyecto.persistencia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import proyecto.modelo.Mantenimiento;

/**
 * Encargada de leer y escribir la lista de mantenimientos en el archivo mantenimientos.dat.
 */
public class ArchivoMantenimientos {

    private static final String RUTA_ARCHIVO = "mantenimientos.dat";

    @SuppressWarnings("unchecked")
    public List<Mantenimiento> leer() {
        File archivo = new File(RUTA_ARCHIVO);
        if (!archivo.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (List<Mantenimiento>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al leer mantenimientos.dat: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void guardar(List<Mantenimiento> mantenimientos) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RUTA_ARCHIVO))) {
            oos.writeObject(mantenimientos);
        } catch (IOException e) {
            System.out.println("Error al guardar mantenimientos.dat: " + e.getMessage());
        }
    }
}
