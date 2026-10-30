package proyecto.util;

import java.util.*;
import java.util.regex.*;
import proyecto.gestion.*;
import proyecto.persistencia.ArchivoLista;

/** Reserva consecutivos persistentes por prefijo, sin reutilizarlos al eliminar registros. */
public class GeneradorId {
    private GeneradorId() {}
    public static synchronized String generarId(String prefijo) {
        if (prefijo == null || !prefijo.matches("[A-Z]+")) throw new IllegalArgumentException("Prefijo invalido.");
        ArchivoLista<String> archivo = new ArchivoLista<>("consecutivos.dat", String.class);
        List<String> reservas = archivo.leer();
        List<String> usados = new ArrayList<>(reservas);
        switch (prefijo) {
            case "EQ": new GestionEquipos().listar().forEach(e -> usados.add(e.getCodigo())); break;
            case "CAT": new GestionCategorias().listar().forEach(e -> usados.add(e.getCodigo())); break;
            case "COMB": new GestionTiposCombustible().listar().forEach(e -> usados.add(e.getCodigo())); break;
            case "PROV": new GestionProveedores().listar().forEach(e -> usados.add(e.getCodigo())); break;
            case "MANT": new GestionMantenimientos().listar().forEach(e -> usados.add(e.getId())); break;
            case "REP": new GestionReparaciones().listar().forEach(e -> usados.add(e.getId())); break;
            case "MOV": case "COMPRA": new GestionCombustible().listar().forEach(e -> usados.add(e.getId())); break;
            default: break;
        }
        Pattern patron = Pattern.compile(Pattern.quote(prefijo) + "([0-9]+)");
        long maximo = 0;
        for (String id : usados) {
            Matcher coincidencia = patron.matcher(id);
            if (coincidencia.matches()) maximo = Math.max(maximo, Long.parseLong(coincidencia.group(1)));
        }
        String nuevo = prefijo + String.format(Locale.ROOT, "%04d", Math.addExact(maximo, 1));
        reservas.removeIf(id -> patron.matcher(id).matches());
        reservas.add(nuevo);
        archivo.guardar(reservas);
        return nuevo;
    }
}
