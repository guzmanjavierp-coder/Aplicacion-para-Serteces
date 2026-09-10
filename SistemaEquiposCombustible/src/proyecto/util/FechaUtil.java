package proyecto.util;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FechaUtil {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private FechaUtil() {}
    public static LocalDate parsear(String texto) {
        try { return LocalDate.parse(texto, FORMATO); }
        catch (DateTimeParseException e) { return null; }
    }
    public static String formatear(LocalDate fecha) {
        if (fecha == null) return "";
        return fecha.format(FORMATO);
    }
    public static boolean estaEnRango(LocalDate fecha, LocalDate inicio, LocalDate fin) {
        if (fecha == null || inicio == null || fin == null) return false;
        return !fecha.isBefore(inicio) && !fecha.isAfter(fin);
    }
}
