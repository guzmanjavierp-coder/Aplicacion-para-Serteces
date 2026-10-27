# Tercera entrega: 19 de 20 requisitos

Documento historico de la tercera entrega. RF16 se implemento posteriormente en
la cuarta entrega: el estado actual es **20 de 20**. Ver [RF16.md](RF16.md).

Se implementaron seis requisitos nuevos: **11, 12, 13, 17, 19 y 20**.
El requisito **10 (alertas de bajo inventario)** ya estaba implementado en
`GestionAlertas` y `VistaAlertas`, aunque no figuraba en la lista inicial de hechos.
Por tanto habia 13 requisitos, y los seis nuevos llevan el total a 19.
La expresion `815` de la lista se interpreta como RF8 y RF15.

## Reparto propuesto

Cada integrante tiene dos requisitos nuevos en esta entrega. Para atender tambien
la peticion de tres por persona, se propone agregar un requisito existente para
explicar en la demostracion. Este reparto es una propuesta de responsabilidad;
no acredita autoria pasada ni sustituye los registros reales de trabajo del equipo.

| Integrante | Nuevo requisito 1 | Nuevo requisito 2 | Existente para explicar (tercero) |
|---|---|---|---|
| Agustin | RF12 Gestionar proveedores | RF17 Consultar informacion y registros de proveedores | RF1 Gestionar equipos |
| Javier | RF13 Registrar compras de combustible | RF11 Generar reportes de inventario | RF6 Registrar entradas de combustible |
| Esteban | RF19 Consultar consumo por periodo | RF20 Generar reportes de consumo | RF7 Registrar salidas de combustible |

## Requisito reservado: RF16

**Generar reportes detallados de mantenimiento y reparaciones** es el de mayor
trabajo restante en este proyecto: el modelo de mantenimiento no almacena costos.
Su implementacion completa exige capturar ese dato, definir que hacer con los costos
desconocidos de registros antiguos, actualizar la edicion y persistencia, combinar
ambos servicios y proporcionar filtros, detalle y totales consistentes.

RF11 entrega listados generales separados de equipos, existencias, mantenimientos
y reparaciones, con los datos que ya existen. Esto no constituye el RF16 completo:
no se han inventado costos de mantenimiento ni se ha agregado su reporte detallado.

## Uso de las presentaciones proporcionadas

Se consultaron las diez presentaciones indicadas, como material de referencia.
Los ejercicios o instrucciones dentro de las diapositivas no se trataron como
pedidos adicionales del usuario. Sus conceptos se aplican asi:

| Presentacion | Aplicacion concreta |
|---|---|
| Clase 1: Introduccion a POO | Modelos encapsulados con atributos privados y operaciones de gestion separadas de la vista |
| Clase 3: Constructores | Inicializacion de Proveedor, CompraCombustible, AsociacionProveedor y ConsumoCombustible |
| Clase 5: Funciones miembro | Metodos de consulta, registro, modificacion y validacion; getters de modelos |
| Clases 6 y 7: Herencia | CompraCombustible extends MovimientoCombustible, constructor super; ArchivoCombustible extends ArchivoLista |
| Clase 8: Polimorfismo | Reporte abstracto; ReporteInventario y ReporteConsumo redefinen titulo, columnas y filas; la vista usa una referencia Reporte |
| Clase 9: Paquetes | Se conservan modelo, gestion, persistencia, vista y util; se agrega reportes |
| Clase 10: Interfaces | Repositorio<T> y Exportable como contratos implementados por ArchivoLista y Reporte |
| Clase 11: Excepciones | Validacion de numeros y periodos, manejo de fallos de archivos y exportacion con mensajes Swing |
| Clase 12: Plantillas | ArchivoLista<T extends Serializable>, Repositorio<T> y colecciones con parametros de tipo |

## Reglas importantes

- Una compra persiste como una sola entrada con proveedor y costo total, usando
  el archivo existente de movimientos. No hay dos escrituras de datos que puedan
  dejar compra y entrada desincronizadas.
- Se rechazan identificadores duplicados, proveedores o combustibles inexistentes,
  cantidades cero o negativas, NaN e infinitos, costos negativos y fechas invalidas.
- Asociar servicios o equipos requiere que ambos registros existan. Se impiden
  vinculos duplicados y se permite retirar un vinculo. Las compras conservan su proveedor.
- Los proveedores con compras o asociaciones no pueden eliminarse. Si un modulo
  anterior elimina un registro vinculado, la consulta muestra "Registro eliminado"
  y permite quitar el vinculo; no inventa datos del registro.
- Los reportes son vistas y archivos CSV, no modifican inventario.
- La aplicacion conserva el esquema de archivos locales de una sola instancia;
  no incluye coordinacion entre varios procesos abiertos simultaneamente.

## Demostracion sugerida

1. Agustin registra y modifica un proveedor, vincula un equipo, mantenimiento y
   reparacion existentes, y consulta los registros del proveedor.
2. Javier registra una compra, verifica su aparicion en movimientos y el aumento
   exacto de existencias, y genera los cuatro reportes generales.
3. Esteban registra salidas de dos tipos, consulta un periodo incluyendo sus limites
   y exporta el CSV de consumo. Muestra que las compras no se cuentan como consumo.
4. Intentar duplicar un codigo, ingresar una fecha imposible, eliminar un proveedor
   con compras y registrar una salida mayor que el stock: deben rechazarse.

## Validacion

El script `tests/EjecutarPruebas.ps1` compila con `--release 17` y ejecuta las pruebas
en datos aislados. La prueba verifica reglas de negocio, archivos, CSV y construccion
de las ventanas. No equivale a una revision visual manual de todos los formularios
ni permite prometer ausencia absoluta de errores.

Resultado obtenido: **84 comprobaciones correctas**, incluyendo los botones de los
cuatro reportes generales y la consulta Swing de consumo. Tambien se leyeron los
archivos reales anteriores (2 equipos, 2 tipos de combustible y 2 movimientos) con
las clases nuevas, sin modificar esos datos.
