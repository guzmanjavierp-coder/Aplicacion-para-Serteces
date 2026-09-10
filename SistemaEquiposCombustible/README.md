# Sistema de Gestion de Equipos y Combustible

Proyecto Java Swing compatible con NetBeans (ANT).

## Ejecutar
1. Abrir la carpeta `SistemaEquiposCombustible` en NetBeans.
2. Ejecutar el proyecto. La clase principal es `proyecto.app.Main`.
3. Al primer inicio se cargan datos de prueba si los archivos `.dat` estan vacios.

## Modulos
- Equipos
- Categorias
- Mantenimientos
- Reparaciones
- Tipos de combustible
- Movimientos de combustible
- Alertas de bajo inventario

## Persistencia
Se utilizan archivos serializados `.dat` creados en el directorio de ejecucion:
`equipos.dat`, `categorias.dat`, `mantenimientos.dat`, `reparaciones.dat`, `tiposCombustible.dat`, `movimientosCombustible.dat`.
