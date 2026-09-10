package proyecto.util;
import java.util.UUID;
public class GeneradorId {
    private GeneradorId() {}
    public static String generarId(String prefijo) { return prefijo + "-" + UUID.randomUUID().toString(); }
}
