Resumen del Ejercicio: Arquitectura y Correcciones
El objetivo fue transformar una app multiactividad en una Single Activity App con navegación adaptativa mediante fragmentos.

Problemas Resueltos:

Cierre al rotar (Crash): Provocado por un conflicto de IDs. Se solucionó asignando el ID estándar @android:id/list al ListView en el ListFragment y unificando los IDs de los contenedores en activity_main.

Pérdida de Estado: Al girar la pantalla, los fragmentos perdían la película seleccionada. Se resolvió usando arguments y recuperando el índice en el onViewCreated.

Duplicidad de Vistas: En modo Tablet (Land), se eliminó la imagen del detalle para evitar redundancia con la lista, optimizando el espacio para los datos técnicos.

Navegación Incorrecta: Se eliminaron FilmDataActivity y FilmEditActivity, centralizando los saltos de pantalla en MainActivity mediante transiciones de fragmentos con addToBackStack.

Resultado Final:

Modo Móvil: Navegación secuencial y fluida con botón "Volver".

Modo Tablet: Diseño profesional de doble panel donde el editor y el detalle se cargan dinámicamente en el lateral derecho sin recargar la lista.
