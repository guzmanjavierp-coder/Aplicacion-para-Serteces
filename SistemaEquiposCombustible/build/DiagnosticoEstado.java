import proyecto.gestion.*;
public class DiagnosticoEstado {
 public static void main(String[] args) {
  var equipos = new GestionEquipos();
  for (var e : equipos.listar()) System.out.println(e.getCodigo()+" | "+e.getNombre()+" | "+e.getEstado());
  for (var r : new GestionReparaciones().listar()) System.out.println("Reparacion: "+r.getId()+" | equipo "+r.getCodigoEquipo());
 }
}
