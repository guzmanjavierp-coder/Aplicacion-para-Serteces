package proyecto.persistencia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import proyecto.modelo.MovimientoCombustible;

/**
 * Encargada de leer y escribir la lista de movimientos de combustible
 * en el archivo movimientosCombustible.dat. La existencia actual NO se
 * guarda aqui; se calcula posteriormente en GestionCombustible a partir
 * de estos movimientos.
 */
public class ArchivoCombustible {

    private static final String RUTA_ARCHIVO = "movimientosCombustible.dat";

    @SuppressWarnings("unchecked")
    public List<MovimientoCombustible> leer() {
        File archivo = new File(RUTA_ARCHIVO);
        if (!archivo.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (List<MovimientoCombustible>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al leer movimientosCombustible.dat: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void guardar(List<MovimientoCombustible> movimientos) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RUTA_ARCHIVO))) {
            oos.writeObject(movimientos);
        } catch (IOException e) {
            System.out.println("Error al guardar movimientosCombustible.dat: " + e.getMessage());
        }
    }
}
