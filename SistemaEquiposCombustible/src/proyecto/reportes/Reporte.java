package proyecto.reportes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

/** Exportacion compartida; las subclases definen columnas y filas. */
public abstract class Reporte implements Exportable {
    public abstract String getTitulo();
    public abstract String[] getColumnas();
    public abstract List<String[]> getFilas();
    private String linea(String[] valores) {
        StringBuilder linea = new StringBuilder();
        for (String valor : valores) {
            if (linea.length() > 0) linea.append(';');
            String texto = valor == null ? "" : valor;
            if (texto.matches("(?s)^\\s*[=+@-].*")) texto = "'" + texto;
            linea.append('"').append(texto.replace("\"", "\"\"")).append('"');
        }
        return linea.append("\r\n").toString();
    }
    @Override public void exportar(Path destino) throws IOException {
        Path ruta = destino.toAbsolutePath();
        Path temporal = Files.createTempFile(ruta.getParent(), "reporte-", ".tmp");
        try {
            StringBuilder csv = new StringBuilder("\uFEFF");
            csv.append(linea(getColumnas()));
            for (String[] fila : getFilas()) csv.append(linea(fila));
            Files.write(temporal, csv.toString().getBytes(StandardCharsets.UTF_8));
            try { Files.move(temporal, ruta, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
            catch (AtomicMoveNotSupportedException ex) { Files.move(temporal, ruta, StandardCopyOption.REPLACE_EXISTING); }
        } finally { Files.deleteIfExists(temporal); }
    }
    @Override public String toString() { return getTitulo(); }
}
