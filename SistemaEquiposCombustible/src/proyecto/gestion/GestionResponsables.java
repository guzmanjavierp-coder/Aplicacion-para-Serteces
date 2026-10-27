package proyecto.gestion;

import java.util.*;
import proyecto.modelo.*;
import proyecto.persistencia.ArchivoLista;

/** Catalogo de nombres registrados, incluyendo los existentes en equipos y movimientos. */
public class GestionResponsables {
    private final ArchivoLista<String> archivo = new ArchivoLista<>("responsables.dat", String.class);
    public List<String> listar() {
        Set<String> nombres = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (String nombre : archivo.leer()) agregar(nombres, nombre);
        for (Equipo e : new GestionEquipos().listar()) agregar(nombres, e.getResponsable());
        for (MovimientoCombustible m : new GestionCombustible().listar()) agregar(nombres, m.getResponsable());
        return new ArrayList<>(nombres);
    }
    private void agregar(Set<String> lista, String nombre) {
        if (nombre != null && !nombre.trim().isEmpty()) lista.add(nombre.trim());
    }
    public String registrar(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return "Ingrese el nombre del responsable.";
        for (String actual : listar()) if (actual.equalsIgnoreCase(nombre.trim())) return "El responsable ya esta registrado.";
        List<String> nombres = archivo.leer(); nombres.add(nombre.trim()); archivo.guardar(nombres); return null;
    }
}
