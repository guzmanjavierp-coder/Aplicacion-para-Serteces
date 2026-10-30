import proyecto.gestion.*;
public class ConsultarCombustibles { public static void main(String[] args) { for(var t:new GestionTiposCombustible().listar()) System.out.println(t.getCodigo()+" | "+t.getNombre()); } }
