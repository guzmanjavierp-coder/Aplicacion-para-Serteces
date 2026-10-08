package proyecto.persistencia;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/** No interpreta un archivo danado como vacio y reemplaza el archivo al terminar. */
public class ArchivoLista<T extends Serializable> implements Repositorio<T> {
    private final Path ruta;
    private final Class<T> tipo;

    public ArchivoLista(String ruta, Class<T> tipo) {
        this.ruta = Paths.get(ruta).toAbsolutePath();
        this.tipo = tipo;
    }

    @Override
    public List<T> leer() {
        if (!Files.exists(ruta)) return new ArrayList<>();
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(ruta))) {
            Object contenido = entrada.readObject();
            if (!(contenido instanceof List<?>)) throw new IOException("Formato de lista incorrecto.");
            List<T> resultado = new ArrayList<>();
            for (Object registro : (List<?>) contenido) resultado.add(tipo.cast(registro));
            return resultado;
        } catch (IOException | ClassNotFoundException | ClassCastException ex) {
            throw new IllegalStateException("No se pudo leer " + ruta.getFileName() + ". Revise el archivo antes de continuar.", ex);
        }
    }

    @Override
    public void guardar(List<T> registros) {
        Path temporal = null;
        try {
            temporal = Files.createTempFile(ruta.getParent(), "inventario-", ".tmp");
            try (ObjectOutputStream salida = new ObjectOutputStream(Files.newOutputStream(temporal))) {
                salida.writeObject(new ArrayList<>(registros));
            }
            try {
                Files.move(temporal, ruta, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temporal, ruta, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo guardar " + ruta.getFileName() + ".", ex);
        } finally {
            if (temporal != null) try { Files.deleteIfExists(temporal); } catch (IOException ignored) { }
        }
    }
}
