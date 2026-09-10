package proyecto.gestion;
import java.util.ArrayList;
import java.util.List;
import proyecto.modelo.TipoCombustible;
public class GestionAlertas {
    private final GestionTiposCombustible gestionTiposCombustible = new GestionTiposCombustible();
    private final GestionCombustible gestionCombustible = new GestionCombustible();
    public boolean tieneInventarioBajo(String codigo) {
        TipoCombustible tipo = gestionTiposCombustible.buscarPorCodigo(codigo); if (tipo == null) return false;
        return gestionCombustible.calcularExistencia(codigo) <= tipo.getNivelMinimo();
    }
    public String obtenerMensajeAlerta(String codigo) {
        TipoCombustible tipo = gestionTiposCombustible.buscarPorCodigo(codigo); if (tipo == null) return null;
        double existencia = gestionCombustible.calcularExistencia(codigo); if (existencia > tipo.getNivelMinimo()) return null;
        return "ALERTA: " + tipo.getNombre() + " tiene existencia baja. Existencia actual: " + existencia + ". Nivel minimo: " + tipo.getNivelMinimo() + ".";
    }
    public List<String> obtenerTodasLasAlertas() {
        List<String> alertas = new ArrayList<>();
        for (TipoCombustible tipo : gestionTiposCombustible.listar()) { String m = obtenerMensajeAlerta(tipo.getCodigo()); if (m != null) alertas.add(m); }
        return alertas;
    }
}
