package proyecto.reportes;
import java.io.IOException;
import java.nio.file.Path;
public interface Exportable { void exportar(Path destino) throws IOException; }
