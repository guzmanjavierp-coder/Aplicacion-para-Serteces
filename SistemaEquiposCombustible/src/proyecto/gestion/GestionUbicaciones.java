package proyecto.gestion;

import java.util.*;
import proyecto.modelo.Equipo;
import proyecto.persistencia.ArchivoEquipos;
import proyecto.persistencia.ArchivoLista;

/** Catalogo persistente que conserva las ubicaciones de equipos anteriores. */
public class GestionUbicaciones {
    private final ArchivoLista<String> archivo = new ArchivoLista<>("ubicaciones.dat", String.class);

    public List<String> listar() {
        Set<String> nombres = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        nombres.addAll(archivo.leer());
        for (Equipo equipo : new GestionEquipos().listar()) {
            String nombre = equipo.getUbicacion();
            if (nombre != null && !nombre.trim().isEmpty()) nombres.add(nombre.trim());
        }
        return new ArrayList<>(nombres);
    }

    public String registrar(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return "Ingrese el nombre de la ubicacion.";
        List<String> nombres = listar();
        if (buscar(nombres, nombre) != null) return "La ubicacion ya esta registrada.";
        nombres.add(nombre.trim());
        archivo.guardar(nombres);
        return null;
    }

    public String modificar(String anterior, String nombre) {
        List<String> nombres = listar();
        String actual = buscar(nombres, anterior);
        if (actual == null) return "Seleccione una ubicacion existente.";
        if (nombre == null || nombre.trim().isEmpty()) return "Ingrese el nombre de la ubicacion.";
        String duplicado = buscar(nombres, nombre);
        if (duplicado != null && !duplicado.equalsIgnoreCase(actual)) return "La ubicacion ya esta registrada.";
        nombres.remove(actual);
        nombres.add(nombre.trim());
        ArchivoEquipos equipos = new ArchivoEquipos();
        List<Equipo> lista = equipos.leer();
        boolean cambio = false;
        for (Equipo equipo : lista) {
            if (equipo.getUbicacion() != null && equipo.getUbicacion().trim().equalsIgnoreCase(actual)) {
                equipo.setUbicacion(nombre.trim());
                cambio = true;
            }
        }
        if (cambio) equipos.guardar(lista);
        archivo.guardar(nombres);
        return null;
    }

    public String eliminar(String nombre) {
        List<String> nombres = listar();
        String actual = buscar(nombres, nombre);
        if (actual == null) return "Seleccione una ubicacion existente.";
        for (Equipo equipo : new GestionEquipos().listar()) {
            if (equipo.getUbicacion() != null && equipo.getUbicacion().trim().equalsIgnoreCase(actual))
                return "La ubicacion tiene equipos asignados. Reasignelos antes de eliminarla.";
        }
        nombres.remove(actual);
        archivo.guardar(nombres);
        return null;
    }

    private String buscar(List<String> nombres, String nombre) {
        if (nombre != null) for (String actual : nombres)
            if (actual.equalsIgnoreCase(nombre.trim())) return actual;
        return null;
    }
}
