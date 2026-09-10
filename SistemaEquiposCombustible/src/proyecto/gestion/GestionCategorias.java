package proyecto.gestion;

import java.util.List;
import proyecto.modelo.Categoria;
import proyecto.modelo.Equipo;
import proyecto.persistencia.ArchivoCategorias;
import proyecto.persistencia.ArchivoEquipos;
import proyecto.util.Validador;

public class GestionCategorias {
    private final ArchivoCategorias archivoCategorias = new ArchivoCategorias();
    private final ArchivoEquipos archivoEquipos = new ArchivoEquipos();

    public String registrar(Categoria categoria) {
        if (categoria == null) return "La categoria es obligatoria.";
        String error = validar(categoria);
        if (error != null) return error;
        List<Categoria> lista = archivoCategorias.leer();
        if (buscarEnLista(lista, categoria.getCodigo()) != null) return "Ya existe una categoria con ese codigo.";
        lista.add(categoria);
        archivoCategorias.guardar(lista);
        return null;
    }

    public String modificar(Categoria categoria) {
        if (categoria == null) return "La categoria es obligatoria.";
        String error = validar(categoria);
        if (error != null) return error;
        List<Categoria> lista = archivoCategorias.leer();
        Categoria existente = buscarEnLista(lista, categoria.getCodigo());
        if (existente == null) return "No existe una categoria con ese codigo.";
        existente.setNombre(categoria.getNombre());
        existente.setDescripcion(categoria.getDescripcion());
        archivoCategorias.guardar(lista);
        return null;
    }

    public String eliminar(String codigo) {
        List<Categoria> lista = archivoCategorias.leer();
        Categoria existente = buscarEnLista(lista, codigo);
        if (existente == null) return "No existe una categoria con ese codigo.";
        for (Equipo equipo : archivoEquipos.leer()) {
            if (equipo.getCodigoCategoria().equals(codigo)) return "No se puede eliminar: existen equipos asociados a esta categoria.";
        }
        lista.remove(existente);
        archivoCategorias.guardar(lista);
        return null;
    }

    public Categoria buscarPorCodigo(String codigo) { return buscarEnLista(archivoCategorias.leer(), codigo); }
    public List<Categoria> listar() { return archivoCategorias.leer(); }

    private String validar(Categoria categoria) {
        if (!Validador.esTextoValido(categoria.getCodigo())) return "El codigo de la categoria es obligatorio.";
        if (!Validador.esTextoValido(categoria.getNombre())) return "El nombre de la categoria es obligatorio.";
        if (!Validador.esTextoValido(categoria.getDescripcion())) return "La descripcion de la categoria es obligatoria.";
        return null;
    }
    private Categoria buscarEnLista(List<Categoria> lista, String codigo) {
        if (codigo == null) return null;
        for (Categoria c : lista) if (codigo.equals(c.getCodigo())) return c;
        return null;
    }
}
