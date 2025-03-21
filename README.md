# CANHUB
Somos Min Wang, Anahí Hinojosa y Marina Sierra

https://github.com/miin807
https://github.com/AnahiHinojosa
https://github.com/MarinaSierra

## FIGMA
[Prototipo](https://www.figma.com/proto/OunNjvK0FjgY8fAPnY2CSC/CanHub?node-id=3-2&t=4wVmLrhoBist5nUQ-0&scaling=scale-down&content-scaling=fixed&page-id=0%3A1&starting-point-node-id=3%3A2&show-proto-sidebar=1)


## TRELLO
[Trello](https://trello.com/b/h65ZF2fz/canhub)


## Significado de la aplicación-->

*CanHub* hace referencia al intercambio de información que se da en el concurso Cansat, a nivel estatal.

Es la unión de las palabras: _Cansat_ y _Hub_.

El logo de la aplicación es una lata sujeta de un paracaídas, pues es el propósito del concurso:

![logotipo](img/logotipo.png)

## Descripción -->

El fin de esta aplicación es poder almacenar los proyectos que el usuario suba y
que sirva de fuente de recursos para el futuro.


## Activities -->

### Splash:

Simulamos el evento, la lata volando por el cielo:

![Splash](img/Splash.png)

### Login:
Este sería el login de la aplicación, se pedirá el nombre de usuario
y contraseña, en caso de ser nuevo usuario, existe la opción de registrarse o continuar sin cuenta.

![Login](img/Login.png)

### SignUp:
En SignUp, será obligatorio un nombre de usuario, un correo y una contraseña
para crear la cuenta.
En caso de tener cuenta ya, se podrá iniciar sesión o en caso de
no querer registrarse ni iniciar sesión, se puede continuar sin cuenta.
![SignUp](img/SignUp.png)

### Inicio:
Dentro del inicio de la aplicacion, observamos el nombre, logo y descripción de los diferentes
institutos, están ordenados por más reciente.

Esta activity es un scroll activity y tiene un bottom navigation view:

`<com.google.android.material.bottomnavigation.BottomNavigationView
    android:id="@+id/bottom_navigation"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom"
    android:background="@color/gris"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:menu="@menu/bottom_navigation_menu" />`

![Inicio](img/Inicio.png)

### Búsqueda:
En la sección de busqueda, se encontrará ordenado por años los institutos 
que han participado en los diferentes año.
![Busqueda](img/Busqueda.png) 
![seleccion](img/Seleccion.png)


La seleccion de años esta en progreso...

### Ajustes:
En ajustes, encontramos la opción del perfil, el proyecto y sobre CanHub

![Ajustes](img/Ajustes.png)

Próximamente, será un menú desplegable. 

### Perfil
En el perfil, se observa la foto de perfil y al lado el nombre de usuario, que en este caso
es el del instituto. Saldrá un gráfico de como se ha ido midiendo la temperatura y por último
un mapa de donde se encuentra el instituto.

![Perfil](img/Perfil.png)

