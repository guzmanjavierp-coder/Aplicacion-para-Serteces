import java.nio.file.*;
import java.lang.reflect.Field;
import java.util.*;
import proyecto.modelo.*;
import proyecto.persistencia.*;

/** Migracion puntual: ejecutar con la aplicacion cerrada y desde su carpeta de datos. */
public class MigrarProveedores {
    private static void cambiar(Object objeto, String campo, String valor) throws Exception {
        Field atributo = objeto.getClass().getDeclaredField(campo);
        atributo.setAccessible(true);
        atributo.set(objeto, valor);
    }
    public static void main(String[] args) throws Exception {
        var archivo = new ArchivoLista<Proveedor>("proveedores.dat", Proveedor.class);
        var relaciones = new ArchivoLista<AsociacionProveedor>("asociacionesProveedores.dat", AsociacionProveedor.class);
        var movimientos = new ArchivoCombustible();
        var proveedores = archivo.leer();
        var asociaciones = relaciones.leer();
        var compras = movimientos.leer();
        Map<String,String> mapa = new LinkedHashMap<>();
        long siguiente = 1;
        Set<String> reservados = new HashSet<>();
        for (Proveedor p : proveedores) if (p.getCodigo().matches("PROV[0-9]{4,}")) {
            reservados.add(p.getCodigo());
            siguiente = Math.max(siguiente, Long.parseLong(p.getCodigo().substring(4)) + 1);
        }
        for (Proveedor p : proveedores) {
            if (p.getCodigo().matches("PROV[0-9]{4,}")) continue;
            String nuevo;
            do { nuevo = "PROV" + String.format(Locale.ROOT, "%04d", siguiente++); } while (reservados.contains(nuevo));
            if (mapa.put(p.getCodigo(), nuevo) != null) throw new IllegalStateException("Codigo duplicado de proveedor.");
            reservados.add(nuevo);
        }
        if (mapa.isEmpty()) { System.out.println("Los proveedores ya usan PROVXXXX."); return; }
        String[] archivos = {"proveedores.dat", "asociacionesProveedores.dat", "movimientosCombustible.dat"};
        Path respaldo = Paths.get("respaldos", "proveedores-" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS")));
        Files.createDirectories(respaldo);
        for (String nombre : archivos) if (Files.exists(Paths.get(nombre))) Files.copy(Paths.get(nombre), respaldo.resolve(nombre));
        for (Proveedor p : proveedores) if (mapa.containsKey(p.getCodigo())) cambiar(p, "codigo", mapa.get(p.getCodigo()));
        int vinculos = 0, cantidadCompras = 0;
        for (AsociacionProveedor a : asociaciones) if (mapa.containsKey(a.getCodigoProveedor())) {
            cambiar(a, "codigoProveedor", mapa.get(a.getCodigoProveedor())); vinculos++;
        }
        for (MovimientoCombustible m : compras) if (m instanceof CompraCombustible) {
            CompraCombustible c = (CompraCombustible)m;
            if (mapa.containsKey(c.getCodigoProveedor())) { cambiar(c, "codigoProveedor", mapa.get(c.getCodigoProveedor())); cantidadCompras++; }
        }
        try {
            archivo.guardar(proveedores);
            relaciones.guardar(asociaciones);
            movimientos.guardar(compras);
            if (archivo.leer().stream().anyMatch(p -> !p.getCodigo().matches("PROV[0-9]{4,}"))) throw new IllegalStateException("Verificacion de codigos fallida.");
            if (relaciones.leer().stream().anyMatch(a -> mapa.containsKey(a.getCodigoProveedor()))) throw new IllegalStateException("Verificacion de vinculos fallida.");
            if (movimientos.leer().stream().filter(m -> m instanceof CompraCombustible).anyMatch(m -> mapa.containsKey(((CompraCombustible)m).getCodigoProveedor()))) throw new IllegalStateException("Verificacion de compras fallida.");
        } catch (Exception ex) {
            for (String nombre : archivos) {
                if (Files.exists(respaldo.resolve(nombre))) Files.copy(respaldo.resolve(nombre), Paths.get(nombre), StandardCopyOption.REPLACE_EXISTING);
                else Files.deleteIfExists(Paths.get(nombre));
            }
            throw ex;
        }
        Files.write(respaldo.resolve("equivalencias.txt"), mapa.entrySet().stream().map(e -> e.getKey()+" -> "+e.getValue()).toList());
        System.out.println("Proveedores actualizados: " + mapa.size() + "; vinculos: " + vinculos + "; compras: " + cantidadCompras);
        System.out.println("Respaldo: " + respaldo.toAbsolutePath());
        for (var e : mapa.entrySet()) System.out.println(e.getKey()+" -> "+e.getValue());
    }
}
