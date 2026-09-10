package proyecto.app;

import java.time.LocalDate;
import proyecto.gestion.GestionCategorias;
import proyecto.gestion.GestionCombustible;
import proyecto.gestion.GestionEquipos;
import proyecto.gestion.GestionTiposCombustible;
import proyecto.modelo.Categoria;
import proyecto.modelo.EstadoEquipo;
import proyecto.modelo.Equipo;
import proyecto.modelo.MovimientoCombustible;
import proyecto.modelo.TipoCombustible;
import proyecto.util.GeneradorId;

public class DatosPrueba {
    public static void cargarDatosSiEsNecesario() {
        GestionCategorias categorias = new GestionCategorias();
        GestionEquipos equipos = new GestionEquipos();
        GestionTiposCombustible tipos = new GestionTiposCombustible();
        GestionCombustible combustible = new GestionCombustible();

        if (categorias.listar().isEmpty()) {
            categorias.registrar(new Categoria("CAT01", "Herramientas", "Herramientas manuales y electricas"));
            categorias.registrar(new Categoria("CAT02", "Maquinaria", "Maquinaria pesada y equipos grandes"));
        }
        if (equipos.listar().isEmpty()) {
            equipos.registrar(new Equipo("EQ001", "Taladro", "Taladro electrico", "CAT01", 5, "Bodega A", EstadoEquipo.DISPONIBLE, "Carlos"));
            equipos.registrar(new Equipo("EQ002", "Generador", "Generador electrico portatil", "CAT02", 2, "Bodega B", EstadoEquipo.DISPONIBLE, "Ana"));
        }
        if (tipos.listar().isEmpty()) {
            tipos.registrar(new TipoCombustible("COMB01", "Diesel", 50));
            tipos.registrar(new TipoCombustible("COMB02", "Gasolina", 30));
        }
        if (combustible.listar().isEmpty()) {
            combustible.registrarEntrada(new MovimientoCombustible(GeneradorId.generarId("MOV"), "COMB01", null, 120, LocalDate.now(), "Javier"));
            combustible.registrarEntrada(new MovimientoCombustible(GeneradorId.generarId("MOV"), "COMB02", null, 80, LocalDate.now(), "Javier"));
        }
    }
}
