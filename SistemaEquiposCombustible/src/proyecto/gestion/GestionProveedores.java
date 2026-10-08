package proyecto.gestion;

import java.util.List;
import java.util.stream.Collectors;
import proyecto.modelo.*;
import proyecto.persistencia.ArchivoLista;

public class GestionProveedores {
    private final ArchivoLista<Proveedor> archivo = new ArchivoLista<>("proveedores.dat", Proveedor.class);
    private final ArchivoLista<AsociacionProveedor> relaciones = new ArchivoLista<>("asociacionesProveedores.dat", AsociacionProveedor.class);

    public List<Proveedor> listar() { return archivo.leer(); }
    public Proveedor buscarPorCodigo(String codigo) {
        for (Proveedor p : listar()) if (p.getCodigo().equals(codigo)) return p;
        return null;
    }
    private String validar(Proveedor p) {
        if (p == null) return "El proveedor es obligatorio.";
        String[] obligatorios = {p.getCodigo(), p.getNombre(), p.getContacto(), p.getTelefono(), p.getDireccion(), p.getServicios()};
        for (String s : obligatorios) if (s == null || s.trim().isEmpty())
            return "Complete codigo, nombre, contacto, telefono, direccion y servicios.";
        if (p.getCorreo() != null && !p.getCorreo().isEmpty() && !p.getCorreo().matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))
            return "El correo no tiene un formato valido.";
        return null;
    }
    public String registrar(Proveedor p) {
        String error = validar(p); if (error != null) return error;
        List<Proveedor> lista = listar();
        if (buscarPorCodigo(p.getCodigo()) != null) return "Ya existe un proveedor con ese codigo.";
        lista.add(p); archivo.guardar(lista); return null;
    }
    public String modificar(Proveedor p) {
        String error = validar(p); if (error != null) return error;
        List<Proveedor> lista = listar();
        for (int i = 0; i < lista.size(); i++) if (lista.get(i).getCodigo().equals(p.getCodigo())) {
            lista.set(i, p); archivo.guardar(lista); return null;
        }
        return "El proveedor no existe.";
    }
    public String eliminar(String codigo) {
        if (buscarPorCodigo(codigo) == null) return "El proveedor no existe.";
        if (!consultarAsociaciones(codigo).isEmpty() || !new GestionCompras().buscarPorProveedor(codigo).isEmpty())
            return "No se puede eliminar un proveedor con registros asociados.";
        List<Proveedor> lista = listar(); lista.removeIf(p -> p.getCodigo().equals(codigo));
        archivo.guardar(lista); return null;
    }
    public List<AsociacionProveedor> consultarAsociaciones(String codigo) {
        return relaciones.leer().stream().filter(a -> a.getCodigoProveedor().equals(codigo)).collect(Collectors.toList());
    }
    public String asociar(String proveedor, TipoRegistroProveedor tipo, String id) {
        if (buscarPorCodigo(proveedor) == null) return "El proveedor no existe.";
        if (tipo == null || id == null || id.trim().isEmpty()) return "Seleccione tipo y registro.";
        boolean existe = false;
        switch (tipo) {
            case EQUIPO: existe = new GestionEquipos().buscarPorCodigo(id) != null; break;
            case MANTENIMIENTO: existe = new GestionMantenimientos().listar().stream().anyMatch(m -> m.getId().equals(id)); break;
            case REPARACION: existe = new GestionReparaciones().listar().stream().anyMatch(r -> r.getId().equals(id)); break;
        }
        if (!existe) return "El registro indicado no existe.";
        List<AsociacionProveedor> lista = relaciones.leer();
        for (AsociacionProveedor a : lista)
            if (a.getCodigoProveedor().equals(proveedor) && a.getTipo() == tipo && a.getIdRegistro().equals(id))
                return "La asociacion ya existe.";
        lista.add(new AsociacionProveedor(proveedor, tipo, id)); relaciones.guardar(lista); return null;
    }
    public String desasociar(String proveedor, TipoRegistroProveedor tipo, String id) {
        List<AsociacionProveedor> lista = relaciones.leer();
        if (!lista.removeIf(a -> a.getCodigoProveedor().equals(proveedor) && a.getTipo() == tipo && a.getIdRegistro().equals(id)))
            return "La asociacion no existe.";
        relaciones.guardar(lista); return null;
    }
}
