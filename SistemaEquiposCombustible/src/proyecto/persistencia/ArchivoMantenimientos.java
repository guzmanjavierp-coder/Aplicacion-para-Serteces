package proyecto.persistencia;

import proyecto.modelo.Mantenimiento;

/** Lista serializada compatible con las entregas anteriores; guardado atomico. */
public class ArchivoMantenimientos extends ArchivoLista<Mantenimiento> {
    public ArchivoMantenimientos() { super("mantenimientos.dat", Mantenimiento.class); }
}
