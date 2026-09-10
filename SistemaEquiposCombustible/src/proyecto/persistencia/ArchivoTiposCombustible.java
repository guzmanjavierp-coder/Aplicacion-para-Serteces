package proyecto.persistencia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import proyecto.modelo.TipoCombustible;

/**
 * Encargada de leer y escribir la lista de tipos de combustible en el archivo tiposCombustible.dat.
 */
public class ArchivoTiposCombustible {

    private static final String RUTA_ARCHIVO = "tiposCombustible.dat";

    @SuppressWarnings("unchecked")
    public List<TipoCombustible> leer() {
        File archivo = new File(RUTA_ARCHIVO);
        if (!archivo.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (List<TipoCombustible>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al leer tiposCombustible.dat: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void guardar(List<TipoCombustible> tipos) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RUTA_ARCHIVO))) {
            oos.writeObject(tipos);
        } catch (IOException e) {
            System.out.println("Error al guardar tiposCombustible.dat: " + e.getMessage());
        }
    }
}
