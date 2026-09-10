package proyecto.persistencia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import proyecto.modelo.Equipo;

/**
 * Encargada de leer y escribir la lista de equipos en el archivo equipos.dat.
 */
public class ArchivoEquipos {

    private static final String RUTA_ARCHIVO = "equipos.dat";

    @SuppressWarnings("unchecked")
    public List<Equipo> leer() {
        File archivo = new File(RUTA_ARCHIVO);
        if (!archivo.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (List<Equipo>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al leer equipos.dat: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void guardar(List<Equipo> equipos) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RUTA_ARCHIVO))) {
            oos.writeObject(equipos);
        } catch (IOException e) {
            System.out.println("Error al guardar equipos.dat: " + e.getMessage());
        }
    }
}
