package proyecto.persistencia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import proyecto.modelo.Categoria;

/**
 * Encargada de leer y escribir la lista de categorias en el archivo categorias.dat.
 */
public class ArchivoCategorias {

    private static final String RUTA_ARCHIVO = "categorias.dat";

    @SuppressWarnings("unchecked")
    public List<Categoria> leer() {
        File archivo = new File(RUTA_ARCHIVO);
        if (!archivo.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (List<Categoria>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al leer categorias.dat: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void guardar(List<Categoria> categorias) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RUTA_ARCHIVO))) {
            oos.writeObject(categorias);
        } catch (IOException e) {
            System.out.println("Error al guardar categorias.dat: " + e.getMessage());
        }
    }
}
