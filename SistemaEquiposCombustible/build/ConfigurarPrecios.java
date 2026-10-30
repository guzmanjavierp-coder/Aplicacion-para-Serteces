import java.nio.file.*;
import java.util.*;
import proyecto.persistencia.*;
public class ConfigurarPrecios {
 public static void main(String[] args) throws Exception {
  var archivo = new ArchivoTiposCombustible();
  var lista = archivo.leer();
  var precios = Map.of("COMB01",33.0,"COMB02",34.0,"COMB-8fc5e58f-a7aa-440b-8ff0-55edd9578857",31.0,"COMB0003",32.0,"COMB0004",33.2);
  if(lista.size()!=5 || lista.stream().anyMatch(t -> !precios.containsKey(t.getCodigo()))) throw new IllegalStateException("El catalogo cambio. Revise los precios antes de continuar.");
  var respaldo=Paths.get("respaldos", "precios-"+System.currentTimeMillis()); Files.createDirectories(respaldo);
  Files.copy(Paths.get("tiposCombustible.dat"),respaldo.resolve("tiposCombustible.dat"));
  for(var tipo:lista) tipo.setCostoPorGalon(precios.get(tipo.getCodigo()));
  archivo.guardar(lista);
  for(var tipo:archivo.leer()) {
   if(!precios.get(tipo.getCodigo()).equals(tipo.getCostoPorGalon())) throw new IllegalStateException("Precio no guardado.");
   System.out.println(tipo.getNombre()+": "+tipo.getCostoPorGalon());
  }
 }
}
