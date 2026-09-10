package proyecto.gestion;

import java.util.ArrayList;
import java.util.List;
import proyecto.modelo.Categoria;
import proyecto.modelo.Equipo;
import proyecto.persistencia.ArchivoCategorias;
import proyecto.persistencia.ArchivoEquipos;
import proyecto.util.Validador;

public class GestionEquipos {
    private final ArchivoEquipos archivoEquipos = new ArchivoEquipos();
    private final ArchivoCategorias archivoCategorias = new ArchivoCategorias();

    public String registrar(Equipo equipo) {
        if (equipo == null) return "El equipo es obligatorio.";
        String error = validar(equipo);
        if (error != null) return error;
        List<Equipo> lista = archivoEquipos.leer();
        if (buscarEnLista(lista, equipo.getCodigo()) != null) return "Ya existe un equipo con ese codigo.";
        lista.add(equipo);
        archivoEquipos.guardar(lista);
        return null;
    }

    public String modificar(Equipo equipo) {
        if (equipo == null) return "El equipo es obligatorio.";
        String error = validar(equipo);
        if (error != null) return error;
        List<Equipo> lista = archivoEquipos.leer();
        Equipo existente = buscarEnLista(lista, equipo.getCodigo());
        if (existente == null) return "No existe un equipo con ese codigo.";
        existente.setNombre(equipo.getNombre());
        existente.setDescripcion(equipo.getDescripcion());
        existente.setCodigoCategoria(equipo.getCodigoCategoria());
        existente.setCantidad(equipo.getCantidad());
        existente.setUbicacion(equipo.getUbicacion());
        existente.setEstado(equipo.getEstado());
        existente.setResponsable(equipo.getResponsable());
        archivoEquipos.guardar(lista);
        return null;
    }

    public String eliminar(String codigo) {
        List<Equipo> lista = archivoEquipos.leer();
        Equipo existente = buscarEnLista(lista, codigo);
        if (existente == null) return "No existe un equipo con ese codigo.";
        lista.remove(existente);
        archivoEquipos.guardar(lista);
        return null;
    }
    public Equipo buscarPorCodigo(String codigo) { return buscarEnLista(archivoEquipos.leer(), codigo); }
    public List<Equipo> buscarPorCategoria(String codigoCategoria) {
        List<Equipo> resultado = new ArrayList<>();
        for (Equipo e : archivoEquipos.leer()) if (e.getCodigoCategoria().equals(codigoCategoria)) resultado.add(e);
        return resultado;
    }
    public List<Equipo> listar() { return archivoEquipos.leer(); }

    private String validar(Equipo equipo) {
        if (!Validador.esTextoValido(equipo.getCodigo())) return "El codigo del equipo es obligatorio.";
        if (!Validador.esTextoValido(equipo.getNombre())) return "El nombre del equipo es obligatorio.";
        if (!Validador.esTextoValido(equipo.getDescripcion())) return "La descripcion del equipo es obligatoria.";
        if (!Validador.esTextoValido(equipo.getCodigoCategoria())) return "La categoria del equipo es obligatoria.";
        Categoria categoria = null;
        for (Categoria c : archivoCategorias.leer()) if (c.getCodigo().equals(equipo.getCodigoCategoria())) { categoria = c; break; }
        if (categoria == null) return "La categoria indicada no existe.";
        if (!Validador.esEnteroValidoNoNegativo(equipo.getCantidad())) return "La cantidad no puede ser negativa.";
        if (!Validador.esTextoValido(equipo.getUbicacion())) return "La ubicacion es obligatoria.";
        if (equipo.getEstado() == null) return "El estado es obligatorio.";
        if (!Validador.esTextoValido(equipo.getResponsable())) return "El responsable es obligatorio.";
        return null;
    }
    private Equipo buscarEnLista(List<Equipo> lista, String codigo) {
        if (codigo == null) return null;
        for (Equipo e : lista) if (codigo.equals(e.getCodigo())) return e;
        return null;
    }
}
