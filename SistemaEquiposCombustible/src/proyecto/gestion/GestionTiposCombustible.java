package proyecto.gestion;

import java.util.List;
import proyecto.modelo.MovimientoCombustible;
import proyecto.modelo.TipoCombustible;
import proyecto.persistencia.ArchivoCombustible;
import proyecto.persistencia.ArchivoTiposCombustible;
import proyecto.util.Validador;

public class GestionTiposCombustible {
    private final ArchivoTiposCombustible archivoTiposCombustible = new ArchivoTiposCombustible();
    private final ArchivoCombustible archivoCombustible = new ArchivoCombustible();

    public String registrar(TipoCombustible tipo) {
        if (tipo == null) return "El tipo de combustible es obligatorio.";
        String error = validarDatosBasicos(tipo); if (error != null) return error;
        List<TipoCombustible> tipos = archivoTiposCombustible.leer();
        if (buscarEnLista(tipos, tipo.getCodigo()) != null) return "Ya existe un tipo de combustible con ese codigo.";
        tipos.add(tipo); archivoTiposCombustible.guardar(tipos); return null;
    }
    public String modificar(TipoCombustible tipo) {
        if (tipo == null) return "El tipo de combustible es obligatorio.";
        String error = validarDatosBasicos(tipo); if (error != null) return error;
        List<TipoCombustible> tipos = archivoTiposCombustible.leer();
        TipoCombustible existente = buscarEnLista(tipos, tipo.getCodigo());
        if (existente == null) return "No existe un tipo de combustible con ese codigo.";
        existente.setNombre(tipo.getNombre()); existente.setNivelMinimo(tipo.getNivelMinimo());
        archivoTiposCombustible.guardar(tipos); return null;
    }
    public String eliminar(String codigo) {
        List<TipoCombustible> tipos = archivoTiposCombustible.leer();
        TipoCombustible existente = buscarEnLista(tipos, codigo);
        if (existente == null) return "No existe un tipo de combustible con ese codigo.";
        for (MovimientoCombustible m : archivoCombustible.leer()) if (m.getCodigoTipoCombustible().equals(codigo)) return "No se puede eliminar: existen movimientos de combustible asociados a este tipo.";
        tipos.remove(existente); archivoTiposCombustible.guardar(tipos); return null;
    }
    public TipoCombustible buscarPorCodigo(String codigo) { return buscarEnLista(archivoTiposCombustible.leer(), codigo); }
    public List<TipoCombustible> listar() { return archivoTiposCombustible.leer(); }
    private String validarDatosBasicos(TipoCombustible tipo) {
        if (!Validador.esTextoValido(tipo.getCodigo())) return "El codigo del tipo de combustible es obligatorio.";
        if (!Validador.esTextoValido(tipo.getNombre())) return "El nombre del tipo de combustible es obligatorio.";
        if (!Validador.esDecimalValidoNoNegativo(tipo.getNivelMinimo())) return "El nivel minimo no puede ser negativo.";
        return null;
    }
    private TipoCombustible buscarEnLista(List<TipoCombustible> tipos, String codigo) {
        if (codigo == null) return null;
        for (TipoCombustible tipo : tipos) if (tipo.getCodigo().equals(codigo)) return tipo;
        return null;
    }
}
