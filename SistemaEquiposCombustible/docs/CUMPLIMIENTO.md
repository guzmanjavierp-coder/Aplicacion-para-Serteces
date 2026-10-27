# Revision de cumplimiento tecnico

## Requisitos funcionales segun la numeracion oficial

| RF | Funcion | Estado |
|---|---|---|
| 1 | Gestionar equipos | Existente |
| 2 | Gestionar categorias | Existente |
| 3 | Gestionar mantenimientos | Existente |
| 4 | Consultar historial de mantenimiento | Existente |
| 5 | Gestionar reparaciones | Existente |
| 6 | Registrar entradas de combustible | Existente |
| 7 | Registrar salidas de combustible | Existente |
| 8 | Consultar existencias de combustible | Existente |
| 9 | Gestionar tipos de combustible | Existente |
| 10 | Generar alertas de bajo inventario | Existente |
| 11 | Generar reportes de inventario | Tercera entrega |
| 12 | Gestionar proveedores | Tercera entrega |
| 13 | Registrar compras de combustible | Tercera entrega |
| 14 | Consultar historial de movimientos de combustible | Existente |
| 15 | Establecer niveles minimos de combustible | Existente |
| 16 | Generar reportes detallados de mantenimiento y reparaciones | Implementado: cuarta entrega |
| 17 | Consultar informacion y registros de proveedores | Tercera entrega |
| 18 | Registrar responsables de movimientos de combustible | Existente |
| 19 | Consultar consumo de combustible por periodo | Tercera entrega |
| 20 | Generar reportes de consumo de combustible | Tercera entrega |

Total: **20 requisitos funcionales implementados**. RF11 ofrece reportes generales
separados; RF16 agrega costos de mantenimiento y reportes detallados con filtros,
subtotales, total registrado y exportacion CSV. Los costos historicos desconocidos
se indican como "No registrado", sin asumir que son cero.
Consultar [TERCERA_ENTREGA.md](TERCERA_ENTREGA.md) para reparto y fundamentos de POO.
Consultar [RF16.md](RF16.md) para la cuarta entrega y la verificacion del ultimo requisito.

## Requisitos tecnicos de la segunda entrega cubiertos por el proyecto
- Implementacion completa lista para demostracion mediante Swing.
- Uso de ficheros `.dat` para manipular la informacion.
- Carga automatica de datos de prueba si los ficheros estan vacios.
- Clases del modelo, gestion, persistencia, utilidades, vistas y aplicacion.
- Proyecto NetBeans basado en ANT.

## Verificacion de tercera entrega
- Compilacion completa con `javac --release 17`.
- Pruebas reproducibles: `tests/EjecutarPruebas.ps1`, con archivos aislados.
- Proveedores, asociaciones, compras, duplicados, stock, consumo, fechas,
  CSV, compatibilidad de movimientos, fallos de archivos y ventanas Swing.
- ANT no se ejecuto en esta entrega; no esta disponible en el PATH.

## Elementos de la consigna que NO pueden certificarse desde el codigo
- Repositorio GitHub compartido y mas de 3 commits semanales por integrante.
- Formularios reales de gestion de tiempo de cada integrante.
- PDF final de Blackboard con requisitos, planificacion, formularios y diagrama preliminar.

Esos elementos deben completarse con informacion real del equipo antes de entregar en Blackboard.
