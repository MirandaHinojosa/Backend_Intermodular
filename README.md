# Backend_Intermodular
API REST desarrollada con Spring Boot que constituye el núcleo del ecosistema CineVerse, una plataforma integral de gestión y venta de entradas de cine. Este backend centraliza toda la lógica de negocio, la persistencia de datos y la seguridad, sirviendo tanto a la aplicación móvil Android como a la interfaz web de administración.

Arquitectura

El proyecto sigue una arquitectura por capas:

    Capa de presentación: Controladores REST que exponen los endpoints
    Capa de negocio: Servicios con la lógica de la aplicación
    Capa de persistencia: Repositorios JPA para acceso a datos
    Capa de seguridad: Autenticación JWT y control de acceso basado en roles

Tecnologías principales

    Java 17
    Spring Boot 3.x
    Spring Security con autenticación JWT
    Spring Data JPA / Hibernate
    Spring WebSocket para chat en tiempo real
    MySQL como base de datos relacional
    Maven para gestión de dependencias
    BCrypt para encriptación de contraseñas

Funcionalidades clave

Gestión de usuarios y autenticación

    Registro y login con generación de tokens JWT
    Control de acceso basado en roles (ADMIN, USER, GUEST)
    Protección de endpoints según permisos

Catálogo de cine

    CRUD completo de películas, salas y sesiones (solo ADMIN)
    Consulta de cartelera y detalles de películas
    Gestión de asientos por sala

Sistema de compra de entradas

    Visualización de asientos disponibles por sesión
    Selección y reserva de butacas
    Compra de boletos con validación de disponibilidad
    Historial de compras por usuario

Chat en tiempo real

    Comunicación bidireccional mediante WebSockets
    Canales privados para usuarios (suscripción por email)
    Canal general para administradores
    Gestión de mensajes no leídos y estado de usuarios


Requisitos Previos para Instalación

Antes de empezar, asegúrate de tener instalado lo siguiente:

    Java Development Kit (JDK): Versión 17. Puedes descargarlo de Oracle o usar OpenJDK.
    Maven: Para gestionar las dependencias y construir el proyecto. Descárgalo desde maven.apache.org.
    MySQL: Necesitarás un servidor MySQL. Puedes instalar MySQL Community Server.
    IDE: Un entorno de desarrollo como IntelliJ IDEA (Community o Ultimate), Eclipse o VS Code con extensiones para Java y Spring Boot.
    Cliente MySQL: Herramientas como MySQL Workbench, DBeaver o la línea de comandos para gestionar la base de datos.

Pasos para la Ejecución

Sigue estos pasos en orden:
1. Clonar el Repositorio

Abre tu terminal y ejecuta el siguiente comando para clonar el proyecto:

    git clone https://github.com/tu-usuario/tu-repositorio-backend.git
    cd nombre-del-proyecto-backend

Configurar la Base de Datos
El proyecto necesita una base de datos para funcionar.

    Inicia tu servidor MySQL.
    Conéctate a MySQL usando tu cliente favorito (En mi caso, Heidi o SQLWorkenbench).
    Crea la base de datos. El nombre por defecto que busca la aplicación es cine_db. Ejecuta este comando SQL:

3. Configurar el Archivo application.properties

El proyecto necesita saber cómo conectarse a tu base de datos.

    Navega a la carpeta src/main/resources/.

    Abre el archivo application.properties.

Busca y modifica las siguientes líneas con los datos de tu instalación de MySQL (usuario, contraseña y URL de la base de datos): 

    properties

    # Configuración de la Base de Datos
    spring.datasource.url=jdbc:mysql://localhost:3306/cine_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    spring.datasource.username=TU_USUARIO_DE_MYSQL   # Ejemplo: root
    spring.datasource.password=TU_CONTRASEÑA_DE_MYSQL

    # Configuración de JPA (Hibernate)
    spring.jpa.hibernate.ddl-auto=update
    # Con 'update', Hibernate creará/actualizará las tablas automáticamente.
    # Para entornos de producción se recomienda 'validate' o 'none'.


Construir y Ejecutar el Proyecto

Ahora ejecutaremos la aplicación. Asegúrate de estar en la carpeta raíz del proyecto (donde está el archivo pom.xml).
Abre el proyecto en IntelliJ IDEA.

    Espera a que el IDE indexe los archivos y descargue las dependencias de Maven (esto puede tardar un poco la primera vez).
    Localiza la clase principal del proyecto: com.javadevs.springapirest.SpringApiRestApplication.
    Haz clic derecho sobre ella y selecciona Run 'SpringApiRestApplication' (o haz clic en el botón verde de ejecución junto al nombre de la clase).

Probar la API

Ya puedes empezar a hacer peticiones a la API. Aquí tienes algunos endpoints básicos para probar:

    Registro de un usuario:
        Método: POST
        URL: http://localhost:8080/api/auth/register

        Body (JSON):
        json
        {
          "username": "usuario_prueba",
          "password": "123456",
          "email": "test@email.com",
          "nombre": "Nombre"
        }

    Login:
        Método: POST
        URL: http://localhost:8080/api/auth/login
        Body (JSON):
        json
        {
          "username": "usuario_prueba",
          "password": "123456"
        }

        Este endpoint te devolverá un token que deberás usar en el resto de peticiones.

    Listar Películas (público):

        Método: GET
        URL: http://localhost:8080/api/pelicula/listar

7. Insertar Datos Iniciales

La base de datos se crea vacía. Para empezar a usar la aplicación con datos, necesitarás insertar algunos registros básicos.

    Crear Roles: La aplicación necesita los roles ADMIN, USER y GUEST en la base de datos. Ejecuta estas sentencias SQL en tu cliente de MySQL:
    sql

    USE cine_db;
    INSERT INTO role (name) VALUES ('ADMIN');
    INSERT INTO role (name) VALUES ('USER');
    INSERT INTO role (name) VALUES ('GUEST');

    Crear un Usuario Administrador: Puedes usar el endpoint /api/auth/registerAdm para crear un usuario con rol de administrador.

        Método: POST
        URL: http://localhost:8080/api/auth/registerAdm
        Body (JSON):
        json
        {
          "username": "admin",
          "password": "admin123",
          "email": "admin@cine.com",
          "nombre": "Administrador"
        }

        Al crear este usuario, el campo password se encriptará automáticamente.
