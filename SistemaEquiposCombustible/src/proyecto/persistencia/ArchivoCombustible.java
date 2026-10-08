package proyecto.persistencia;
import proyecto.modelo.MovimientoCombustible;
/** Conserva el formato anterior y admite compras mediante herencia. */
public class ArchivoCombustible extends ArchivoLista<MovimientoCombustible> {
    public ArchivoCombustible() { super("movimientosCombustible.dat", MovimientoCombustible.class); }
}
