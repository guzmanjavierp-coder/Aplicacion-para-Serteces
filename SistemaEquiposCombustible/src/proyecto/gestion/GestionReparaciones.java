package proyecto.gestion;

import java.util.ArrayList;
import java.util.List;
import proyecto.modelo.Equipo;
import proyecto.modelo.Reparacion;
import proyecto.persistencia.ArchivoEquipos;
import proyecto.persistencia.ArchivoReparaciones;
import proyecto.util.Validador;

/**
 * Administra las operaciones de negocio sobre las reparaciones:
 * registro, modificacion, eliminacion y consulta por equipo.
 */
public class GestionReparaciones {

    private final ArchivoReparaciones archivoReparaciones;
    private final ArchivoEquipos archivoEquipos;

    public GestionReparaciones() {
        this.archivoReparaciones = new ArchivoReparaciones();
        this.archivoEquipos = new ArchivoEquipos();
    }

    public String registrar(Reparacion reparacion) {
        String error = validarDatosBasicos(reparacion);
        if (error != null) {
            return error;
        }
        List<Reparacion> reparaciones = archivoReparaciones.leer();
        if (buscarEnLista(reparaciones, reparacion.getId()) != null) {
            return "Ya existe una reparacion con ese ID.";
        }
        reparaciones.add(reparacion);
        archivoReparaciones.guardar(reparaciones);
        return null;
    }

    public String modificar(Reparacion reparacion) {
        String error = validarDatosBasicos(reparacion);
        if (error != null) {
            return error;
        }
        List<Reparacion> reparaciones = archivoReparaciones.leer();
        Reparacion existente = buscarEnLista(reparaciones, reparacion.getId());
        if (existente == null) {
            return "No existe una reparacion con ese ID.";
        }
        existente.setCodigoEquipo(reparacion.getCodigoEquipo());
        existente.setFecha(reparacion.getFecha());
        existente.setDescripcion(reparacion.getDescripcion());
        existente.setCosto(reparacion.getCosto());
        existente.setObservaciones(reparacion.getObservaciones());
        archivoReparaciones.guardar(reparaciones);
        return null;
    }

    public String eliminar(String id) {
        List<Reparacion> reparaciones = archivoReparaciones.leer();
        Reparacion existente = buscarEnLista(reparaciones, id);
        if (existente == null) {
            return "No existe una reparacion con ese ID.";
        }
        reparaciones.remove(existente);
        archivoReparaciones.guardar(reparaciones);
        return null;
    }

    public List<Reparacion> listar() {
        return archivoReparaciones.leer();
    }

    public List<Reparacion> buscarPorEquipo(String codigoEquipo) {
        List<Reparacion> resultado = new ArrayList<>();
        for (Reparacion reparacion : archivoReparaciones.leer()) {
            if (reparacion.getCodigoEquipo().equals(codigoEquipo)) {
                resultado.add(reparacion);
            }
        }
        return resultado;
    }

    private String validarDatosBasicos(Reparacion reparacion) {
        if (!Validador.esTextoValido(reparacion.getId())) {
            return "El ID de la reparacion es obligatorio.";
        }
        if (!Validador.esTextoValido(reparacion.getCodigoEquipo())) {
            return "El codigo de equipo es obligatorio.";
        }
        if (buscarEquipo(reparacion.getCodigoEquipo()) == null) {
            return "El equipo indicado no existe.";
        }
        if (reparacion.getFecha() == null) {
            return "La fecha de la reparacion es obligatoria.";
        }
        if (!Validador.esTextoValido(reparacion.getDescripcion())) {
            return "La descripcion de la reparacion es obligatoria.";
        }
        if (!Validador.esDecimalValidoNoNegativo(reparacion.getCosto())) {
            return "El costo de la reparacion no puede ser negativo.";
        }
        if (!Validador.esTextoValido(reparacion.getObservaciones())) {
            return "Las observaciones de la reparacion son obligatorias.";
        }
        return null;
    }

    private Equipo buscarEquipo(String codigoEquipo) {
        for (Equipo equipo : archivoEquipos.leer()) {
            if (equipo.getCodigo().equals(codigoEquipo)) {
                return equipo;
            }
        }
        return null;
    }

    private Reparacion buscarEnLista(List<Reparacion> reparaciones, String id) {
        for (Reparacion reparacion : reparaciones) {
            if (reparacion.getId().equals(id)) {
                return reparacion;
            }
        }
        return null;
    }
}
