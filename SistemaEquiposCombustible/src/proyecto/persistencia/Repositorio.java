package proyecto.persistencia;

import java.util.List;

/** Contrato generico de persistencia (clases 10 y 12). */
public interface Repositorio<T> {
    List<T> leer();
    void guardar(List<T> registros);
}
