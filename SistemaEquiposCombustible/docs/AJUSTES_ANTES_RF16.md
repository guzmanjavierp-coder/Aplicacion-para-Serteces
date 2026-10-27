# Ajustes solicitados antes del RF16

## Cambios de uso

- Proveedores muestra **Empresa** en el formulario y la tabla. Se conserva el nombre
  interno del atributo serializado para leer los archivos anteriores.
- Equipos, categorias, tipos de combustible y proveedores generan su codigo al
  registrar. Los mantenimientos, reparaciones, movimientos y compras conservan sus
  identificadores automaticos. Los codigos existentes no se renumeran.
- Los formularios ya no muestran campos de codigo/ID automatico. Los identificadores
  se consultan en las tablas. Seleccione una fila para editar; los botones de busqueda
  por codigo solicitan el codigo en un dialogo de consulta.
- Mantenimientos, reparaciones, movimientos y compras registran automaticamente la
  fecha del dia, usando America/Mexico_City. Al editar un servicio o una compra se
  conserva su fecha original. Las fechas de los filtros de reportes e historial
  siguen siendo seleccionadas por el usuario.
- Compras permite seleccionar una fila y pulsar **Guardar cambios**. La entrada
  original se reemplaza: no se agrega otra entrada. Cambiar cantidad o combustible
  recalcula las existencias y se rechaza si deja un saldo negativo para alguno de
  los tipos afectados. Para otra compra use **Nueva / limpiar**.
- Compras solicita **Cantidad (galones)** y **Costo por galon**. El total se calcula
  automaticamente, con dos decimales y redondeo decimal HALF_UP. El total es una
  etiqueta, no un campo editable.
- Las compras anteriores conservan su costo total y cantidad; su precio por galon
  se obtiene dividiendo ambos. No se convierte ni se altera la cantidad historica.
  Desde esta interfaz se trabaja en galones; si los datos anteriores representan
  otra unidad, el equipo debe definir una conversion antes de mezclarlos.
- **Responsables disponibles** muestra nombres encontrados en equipos y movimientos
  y permite agregar otros, persistidos en `responsables.dat`. Equipos, movimientos
  y compras permiten escoger esos nombres o escribir uno nuevo. No se infiere
  disponibilidad por turnos ni estado laboral: es un catalogo de nombres registrados.
- Alertas abre una tabla con todos los combustibles en existencia menor o igual al
  minimo, con existencias, nivel minimo y faltante. El filtro permite todos los tipos
  o uno especifico; muestra un mensaje cuando no hay alertas. Actualmente los equipos
  no tienen un nivel minimo configurado, por lo que no se inventan alertas para ellos.

## Archivos

Nuevos: `GestionResponsables`, `SelectorResponsable`, `VistaResponsables` y
`tests/AjustesTest.java`. Se actualizaron los modelos de compra y proveedor, la gestion
de compras y proveedores, FechaUtil y las vistas afectadas.

Los `.dat` reales se conservan. Antes de los cambios se guardo una copia del codigo
en `build/respaldo-ajustes`. Los ZIP anteriores de la tercera entrega son historicos;
no contienen estos ajustes.

## Verificacion

Ejecutar `powershell -ExecutionPolicy Bypass -File .\tests\EjecutarPruebas.ps1`.
Compila para Java 17 y ejecuta la bateria anterior y la de ajustes en carpetas
aisladas, sin modificar los archivos de trabajo del proyecto. Se prueban precios,
redondeo, edicion, saldos, responsables, generacion de codigo de proveedor, fecha
de compra y los controles Swing. Las capturas se guardan en la carpeta de pruebas.

En la etapa descrita en este documento RF16 permanecia pendiente. Se implemento
posteriormente con costos de mantenimiento y reporte detallado: ver [RF16.md](RF16.md).
