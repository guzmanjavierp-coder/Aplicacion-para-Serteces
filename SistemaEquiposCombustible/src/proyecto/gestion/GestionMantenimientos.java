package proyecto.gestion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import proyecto.modelo.Equipo;
import proyecto.modelo.EstadoEquipo;
import proyecto.modelo.Mantenimiento;
import proyecto.persistencia.ArchivoEquipos;
import proyecto.persistencia.ArchivoMantenimientos;
import proyecto.util.Validador;
import proyecto.util.FechaUtil;

/**
 * Administra las operaciones de negocio sobre los mantenimientos:
 * registro, modificacion, eliminacion y consultas por equipo o por fecha.
 */
public class GestionMantenimientos {

    private final ArchivoMantenimientos archivoMantenimientos;
    private final ArchivoEquipos archivoEquipos;

    public GestionMantenimientos() {
        this.archivoMantenimientos = new ArchivoMantenimientos();
        this.archivoEquipos = new ArchivoEquipos();
    }

    public String registrar(Mantenimiento mantenimiento) {
        String error = validarDatosBasicos(mantenimiento);
        if (error != null) {
            return error;
        }
        if (!mantenimiento.tieneCostoRegistrado()) return "Debe indicar el costo del nuevo mantenimiento (puede ser cero).";
        List<Mantenimiento> mantenimientos = archivoMantenimientos.leer();
        if (buscarEnLista(mantenimientos, mantenimiento.getId()) != null) {
            return "Ya existe un mantenimiento con ese ID.";
        }
        List<Equipo> equipos = archivoEquipos.leer();
        for (Equipo equipo : equipos) {
            if (equipo.getCodigo().equals(mantenimiento.getCodigoEquipo())) {
                equipo.setEstado(EstadoEquipo.EN_MANTENIMIENTO);
                break;
            }
        }
        mantenimientos.add(mantenimiento);
        archivoMantenimientos.guardar(mantenimientos);
        try {
            archivoEquipos.guardar(equipos);
        } catch (IllegalStateException ex) {
            // Si no se pudo actualizar el equipo, deshacer el registro nuevo.
            mantenimientos.remove(mantenimiento);
            try { archivoMantenimientos.guardar(mantenimientos); }
            catch (IllegalStateException falloReversion) { ex.addSuppressed(falloReversion); }
            throw ex;
        }
        return null;
    }

    public String modificar(Mantenimiento mantenimiento) {
        String error = validarDatosBasicos(mantenimiento);
        if (error != null) {
            return error;
        }
        List<Mantenimiento> mantenimientos = archivoMantenimientos.leer();
        Mantenimiento existente = buscarEnLista(mantenimientos, mantenimiento.getId());
        if (existente == null) {
            return "No existe un mantenimiento con ese ID.";
        }
        if (existente.tieneCostoRegistrado() && !mantenimiento.tieneCostoRegistrado())
            return "No se puede quitar un costo ya registrado.";
        existente.setCodigoEquipo(mantenimiento.getCodigoEquipo());
        existente.setFecha(mantenimiento.getFecha());
        existente.setTipo(mantenimiento.getTipo());
        existente.setObservaciones(mantenimiento.getObservaciones());
        existente.setCosto(mantenimiento.getCosto());
        archivoMantenimientos.guardar(mantenimientos);
        return null;
    }

    public String eliminar(String id) {
        List<Mantenimiento> mantenimientos = archivoMantenimientos.leer();
        Mantenimiento existente = buscarEnLista(mantenimientos, id);
        if (existente == null) {
            return "No existe un mantenimiento con ese ID.";
        }
        mantenimientos.remove(existente);
        archivoMantenimientos.guardar(mantenimientos);
        return null;
    }

    public List<Mantenimiento> listar() {
        return ordenarPorFecha(archivoMantenimientos.leer());
    }

    public List<Mantenimiento> buscarPorEquipo(String codigoEquipo) {
        List<Mantenimiento> resultado = new ArrayList<>();
        for (Mantenimiento mantenimiento : archivoMantenimientos.leer()) {
            if (mantenimiento.getCodigoEquipo().equals(codigoEquipo)) {
                resultado.add(mantenimiento);
            }
        }
        return ordenarPorFecha(resultado);
    }

    public List<Mantenimiento> consultarPorPeriodo(LocalDate inicio, LocalDate fin) {
        List<Mantenimiento> resultado = new ArrayList<>();
        if (inicio == null || fin == null || inicio.isAfter(fin)) {
            return resultado;
        }
        for (Mantenimiento mantenimiento : archivoMantenimientos.leer()) {
            if (FechaUtil.estaEnRango(mantenimiento.getFecha(), inicio, fin)) {
                resultado.add(mantenimiento);
            }
        }
        return ordenarPorFecha(resultado);
    }

    private List<Mantenimiento> ordenarPorFecha(List<Mantenimiento> mantenimientos) {
        Collections.sort(mantenimientos, new Comparator<Mantenimiento>() {
            @Override
            public int compare(Mantenimiento m1, Mantenimiento m2) {
                return m1.getFecha().compareTo(m2.getFecha());
            }
        });
        return mantenimientos;
    }

    private String validarDatosBasicos(Mantenimiento mantenimiento) {
        if (mantenimiento == null) return "El mantenimiento es obligatorio.";
        if (mantenimiento.tieneCostoRegistrado() && (!Double.isFinite(mantenimiento.getCosto()) || mantenimiento.getCosto() < 0))
            return "El costo debe ser finito y no negativo.";
        if (!Validador.esTextoValido(mantenimiento.getId())) {
            return "El ID del mantenimiento es obligatorio.";
        }
        if (!Validador.esTextoValido(mantenimiento.getCodigoEquipo())) {
            return "El codigo de equipo es obligatorio.";
        }
        if (buscarEquipo(mantenimiento.getCodigoEquipo()) == null) {
            return "El equipo indicado no existe.";
        }
        if (mantenimiento.getFecha() == null) {
            return "La fecha del mantenimiento es obligatoria.";
        }
        if (mantenimiento.getTipo() == null) {
            return "El tipo de mantenimiento es obligatorio.";
        }
        if (!Validador.esTextoValido(mantenimiento.getObservaciones())) {
            return "Las observaciones del mantenimiento son obligatorias.";
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

    private Mantenimiento buscarEnLista(List<Mantenimiento> mantenimientos, String id) {
        for (Mantenimiento mantenimiento : mantenimientos) {
            if (mantenimiento.getId().equals(id)) {
                return mantenimiento;
            }
        }
        return null;
    }
}
