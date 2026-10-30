package proyecto.util;

import proyecto.gestion.GestionEquipos;
import proyecto.modelo.Equipo;

public class NombreEquipo {
    private NombreEquipo() {}
    public static String mostrar(String codigo) {
        Equipo equipo = new GestionEquipos().buscarPorCodigo(codigo);
        return equipo == null ? "Equipo no disponible (" + codigo + ")" : equipo.getNombre();
    }
}
