package proyecto.persistencia;

import proyecto.modelo.Reparacion;

/** Lista serializada compatible con las entregas anteriores; guardado atomico. */
public class ArchivoReparaciones extends ArchivoLista<Reparacion> {
    public ArchivoReparaciones() { super("reparaciones.dat", Reparacion.class); }
}
