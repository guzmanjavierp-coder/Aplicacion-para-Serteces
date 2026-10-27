# Renovacion de la interfaz grafica

La aplicacion mantiene Java Swing y no requiere instalar bibliotecas adicionales.

## Panel principal

- Barra lateral azul oscuro con Inicio, Equipos, Combustible y Administracion.
- Accesos agrupados por area, con titulo y descripcion de cada modulo.
- Resumen con equipos registrados, tipos de combustible y alertas activas. Los
  valores se leen de los archivos de la aplicacion y se actualizan al volver al panel.
- Desplazamiento vertical cuando los accesos no caben en la altura disponible.

## Formularios y tablas

- Tema compartido en `proyecto.vista.Estilo`: fondo claro, azul oscuro y verde.
- Tipografia Segoe UI, titulos de ventana y espacios mas amplios.
- Botones principales verdes y acciones de eliminacion diferenciadas en rojo.
- Campos con margenes y bordes suaves; ventanas ajustadas al espacio de pantalla.
- Tablas con cabecera oscura, filas alternadas, seleccion verde y valores numericos
  alineados a la derecha. Las celdas muestran el contenido completo como ayuda al
  mantener el cursor encima.
- Desplegables de equipos, categorias, combustibles y proveedores muestran codigo y
  nombre, conservando los mismos objetos y las mismas operaciones de seleccion.

## Integracion

Se agrega `Estilo.java`, se renueva `VistaPrincipal`, se inicializa el tema en Main
y se aplica al terminar de construir cada formulario. No hay cambios en el formato
de los archivos `.dat` ni en las reglas de gestion por esta renovacion visual.

Para ver los cambios, cerrar la version que este ejecutandose, usar **Limpiar y
construir** en NetBeans y ejecutar `proyecto.app.Main` con JDK 17 o superior.
El ZIP actualizado contiene `src` completo y conserva los ajustes anteriores al RF16.
No sustituir los archivos `.dat` al actualizar otra copia del proyecto.

## Verificacion

Se ejecutan las pruebas anteriores con `tests/EjecutarPruebas.ps1` y se revisan
capturas de los formularios generadas con datos aislados. Las imagenes de muestra
usan datos de prueba y no representan los registros reales de la empresa.

Al renovar la interfaz RF16 estaba pendiente. Su implementacion posterior se
describe en [RF16.md](RF16.md); el proyecto actual completa 20 de 20 requisitos.
