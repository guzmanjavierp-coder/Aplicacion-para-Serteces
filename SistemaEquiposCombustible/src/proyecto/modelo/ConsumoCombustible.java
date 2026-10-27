package proyecto.modelo;

public class ConsumoCombustible {
    private final String codigo, nombre;
    private final double cantidad;
    public ConsumoCombustible(String codigo, String nombre, double cantidad) {
        this.codigo = codigo; this.nombre = nombre; this.cantidad = cantidad;
    }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public double getCantidad() { return cantidad; }
}
