package proyecto.modelo;

import java.io.Serializable;

public class Proveedor implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String codigo;
    private final String nombre;
    private final String contacto;
    private final String telefono;
    private final String correo;
    private final String direccion;
    private final String servicios;

    public Proveedor(String codigo, String nombre, String contacto, String telefono,
                     String correo, String direccion, String servicios) {
        this.codigo = codigo; this.nombre = nombre; this.contacto = contacto;
        this.telefono = telefono; this.correo = correo; this.direccion = direccion;
        this.servicios = servicios;
    }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getContacto() { return contacto; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public String getDireccion() { return direccion; }
    public String getServicios() { return servicios; }
    @Override public String toString() { return codigo + " - " + nombre; }
}
