package proyecto.util;

public class Validador {
    private Validador() {}
    public static boolean esTextoValido(String texto) { return texto != null && !texto.trim().isEmpty(); }
    public static boolean esEnteroValidoNoNegativo(int valor) { return valor >= 0; }
    public static boolean esDecimalValidoNoNegativo(double valor) { return valor >= 0; }
    public static boolean esCantidadMayorQueCero(double valor) { return valor > 0; }
}
