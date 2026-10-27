# Sistema de Gestion de Equipos y Combustible

Proyecto Java Swing compatible con NetBeans (ANT).

La interfaz incluye un panel principal por areas, resumen de inventario y un tema
visual compartido en todos los formularios. Consultar
[docs/INTERFAZ_GRAFICA.md](docs/INTERFAZ_GRAFICA.md). No requiere librerias externas.

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
- Proveedores y consulta de registros asociados (RF12 y RF17)
- Compras de combustible con entrada automatica al inventario (RF13)
- Reportes generales de equipos, combustible, mantenimientos y reparaciones (RF11)
- Consulta de consumo por periodo y tipo, y reporte CSV (RF19 y RF20)
- Reporte detallado de mantenimientos y reparaciones con costos, filtros y totales (RF16)

## Persistencia
Se utilizan archivos serializados `.dat` creados en el directorio de ejecucion:
`equipos.dat`, `categorias.dat`, `mantenimientos.dat`, `reparaciones.dat`, `tiposCombustible.dat`, `movimientosCombustible.dat`.

Los proveedores y sus vinculos se guardan en `proveedores.dat` y
`asociacionesProveedores.dat`. Las compras se guardan en `movimientosCombustible.dat`
como entradas especializadas: una compra es un unico movimiento, no debe registrarse
otra entrada manual para la misma compra. Los archivos anteriores siguen siendo compatibles.

## Tercera entrega
En la tercera entrega se agregaron RF11, RF12, RF13, RF17, RF19 y RF20. El RF10 ya
existia, por lo que en esa etapa quedaron 19 de 20 requisitos. En la cuarta entrega
se implemento RF16, completando los **20 requisitos funcionales**.
La numeracion oficial y el reparto propuesto estan en [docs/TERCERA_ENTREGA.md](docs/TERCERA_ENTREGA.md).

En **Proveedores y registros asociados**, registre una empresa (codigo automatico) o
seleccione una fila para consultar o modificar sus datos. Vincule equipos,
mantenimientos o reparaciones seleccionando su tipo e identificador. Las compras
aparecen automaticamente. Un proveedor con registros asociados no puede eliminarse.

En **Compras de combustible**, seleccione empresa y combustible, cantidad en galones,
costo por galon y responsable. El total y la fecha son automaticos. Para editar,
seleccione una fila y pulse **Guardar cambios**; se conservan codigo y fecha. Para
otra compra use **Nueva / limpiar**. **Actualizar** recarga los catalogos.

**Responsables disponibles** permite consultar y agregar nombres. Los formularios de
equipos, movimientos y compras ofrecen un selector editable de responsables.
**Alertas de Bajo Inventario** muestra una tabla filtrable por tipo de combustible.
Los formularios de registro no requieren ingresar codigos ni fechas de operacion.
Consultar [docs/AJUSTES_ANTES_RF16.md](docs/AJUSTES_ANTES_RF16.md) para el detalle.

En **Reportes y consulta de consumo**, seleccione uno de los cuatro reportes generales
o **Consumo de combustible**. Este ultimo permite fechas `dd/MM/yyyy` y un tipo o todos.
El consumo se calcula con las salidas, incluyendo el dia inicial y final; las entradas
y compras no son consumo. Los tipos sin salidas muestran cero. **Exportar CSV** actualiza
la consulta y guarda un archivo UTF-8 separado por punto y coma, con resumen y detalle
de salidas en el reporte de consumo. Las filas TOTAL son resumen de las salidas;
no deben sumarse de nuevo junto con el detalle.

## Cuarta entrega: RF16
En **Mantenimientos**, capture el costo total de los nuevos servicios (cero si fue
gratuito). Los mantenimientos antiguos conservan costo **No registrado** hasta que
se complete; no se les asigna cero automaticamente. Se puede seleccionar una fila
y modificar el costo conservando su codigo y fecha original.

En el panel principal, abra **Reportes y consumo** y seleccione **Mantenimientos y
reparaciones (detallado)**. Puede filtrar por equipo, por servicio y por periodo, o
seleccionar **Todo el historial**. Pulse **Generar / consultar** para ver el detalle,
subtotales, total de costos registrados y cantidad de registros sin costo.
**Exportar CSV** incluye detalle, resumen y los filtros utilizados.
Mas informacion en [docs/RF16.md](docs/RF16.md).

## Verificar
Con un JDK 17 o superior, desde PowerShell:

```powershell
powershell -ExecutionPolicy Bypass -File .\tests\EjecutarPruebas.ps1
```

Las pruebas compilan todo el codigo y trabajan en una carpeta nueva dentro de `build`.
No modifican los archivos `.dat` reales. Comprueban proveedores, asociaciones, compras,
stock, consumo, limites de fechas, persistencia, CSV y construccion de ventanas Swing.
