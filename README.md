# spring.boot.orace.db.container.setup.local.environment
Local environment to develop spring boot microservice connecting to oracle database container running in docker

En este ejercicio vamos a levantar un ambiente local y desarrollaremos un microservicio en spring boot para conectarnos a una base de datos oracle, la cual estara montada en nuestro mismo host pero en un entorno de Docker.

# Prerequisitos
Este ejercicio fue realizado sobre el sistema operativo Windows 11 y la instalacion de las siguientes herramientas de software:

1. Visual Studio Code [https://code.visualstudio.com/]
2. Docker Desktop[https://docs.docker.com/desktop/install/windows-install/]
3. OpenJDK 11 [https://jdk.java.net/archive/]
4. Maven [https://maven.apache.org/download.cgi]
5. DBeaver [https://dbeaver.io/download/]

Una vez instaladas estas herramientas, antes de empezar con el desarrollo, vamos a instalar algunos plugins de Visual Studio Code muy utiles que serán de mucha ayuda a lo largo del ejercicio (el ejercicio no cubre la instalación de estas herramientas):

1. Spring Initializr Java Support
2. Spring Boot Dashboard
3. Spring Boot Tools
4. Spring Boot Extension Pack

Estas herramientas serán de mucha utilidad a lo largo del desarrollo del microservicio.

# Ejercicio
A este punto se espera que usted ya tenga todas las herramientas ya mencionadas ya instaladas y configuradas.

## 1. Despliegue de la base de datos Oracle con Docker
El primer punto será montar una base de datos Oracle en un entorno contenerizado con Docker, para lo cual, hemos decidido tomar la imagen base https://hub.docker.com/r/oracleinanutshell/oracle-xe-11g. Como nos indica en la pagina, lo primero que debemos hacer es abrir nuestra terminal de comandos y ejecutar el siguiente comando:

```
docker pull oracleinanutshell/oracle-xe-11g
```
Como podemos ver en el nombre, es una imagen que viene con una instalación de una base de datos oracle 11g. Una vez descargada la imagen, toca ejecutar el contenedor de esta imagen con el siguiente comando:

```
docker run -d -p 1521:1521 oracleinanutshell/oracle-xe-11g
```

Debemos esperar unos segundos antes de comenzar a ocupar la base de datos, puesto que necesita iniciarce el servicio dentro del contenedor.

## 2. Conexión a la base de datos con DBeaver
Una vez ya tenemos nuestro Oracle en ejecución, haremos una prueba de conexión por medio de DBeaver, una herramienta cliente para conectarnos con distintas bases de datos.

Iniciamos DBeaver y daremos click en el boton Nueva **Conexión**, el cual esta representado por el simbolo de un **'enchufe'** con un simbolo **+**. Posterior a esto, se desplegará una ventana en donde debemos seleccionar la base de datos a la cual queremos conectarnos, en este caso, Oracle. Si es la primera vez que está usando DBeaver, es probable que se instalen algunos drivers que el software necesita para poder comunicarse con la base de datos. Una vez instalados los drivers, DBeaver pedirá que ingrese los datos de conexión hacia el motor de base de datos, para lo cual, según la documentacion de la imagen de docker, los parámetros serán los siguientes:

1. Host: localhost
2. Port: 1521
3. Database: XE (por SID)
4. Authentication: Oracle Database Native
5. Nombre de usuario: system
6. Role: Normal
7. Contraseña: oracle
8. Cliente: **Vacío**

Antes de Aceptar, podemos probar nuestra conexión, dando click en el botón **'Probar Conexión'**. Una vez que la prueba sea exitósa, podemos dar click en aceptar y DBeaver se conectará a oracle y podremos navegar entre sus tablas que vienen configuradas en la instalación de la base de datos.

## 3. Instalación del driver ojdbc en Maven
Para el correcto funcionamiento del microservicio que queremos desarrollar, es necesario instalar el driver ojdbc.

>#### Nota
>Debido a las restricciones de la licencia de Oracle, el controlador JDBC de Oracle no está disponible en el repositorio público de Maven. Para usar el controlador Oracle JDBC con Maven, debe descargarlo e instalarlo en su repositorio local de Maven manualmente.

Nos dirigiremos a maven y encontraremos que existe una definición para importar la librería en nuestro proyecto de java [https://mvnrepository.com/artifact/com.oracle/ojdbc7/12.1.0.2], pero la librería está alojada en otro repositorio, asi que hay que descargarla dando click en el botón **jar (3.5 MB)**.

Una vez descargada la librería, debemos abrir la terminal y ejecutar el siguiente comando:

``` 
mvn install:install-file -Dfile="<ubicación>\ojdbc7-12.1.0.2.jar" -DgroupId="com.oracle" -DartifactId="ojdbc7" -Dversion="12.1.0.2" -Dpackaging="jar"
```

Con esto tendremos instalado la librería en nuestro repositorio local de Maven y podremos importarlo a nuestro proyecto.

>#### Nota
>Para un ambiente que no es local, por ejemplo NPE (Not Production Environment), UAT (User Acceptance Testing) o PRD (Production), es necesario que esta librería sea instalada en donde se ejecutará la aplicación para que esta pueda obtener la libreria y usar sus componentes, como por ejemplo instalarla en la máquina virtual o si esta corriendo su aplicacion en un entorno de contenedores, añadir la librería a la imagen creada junto con su aplicación.

## 4. Creación del proyecto
Como mencionamos en la sección de prerequisitos, instalamos algunos plugins de Spring Boot en Visual Studio Code, los cuales nos permiten agilizar el desarrollo de nuestro microservicio.

Ahora, en alguna carpeta que tengas definida para tus proyectos, abriremos esta con Visual Studio Code, posterior a esto usaremos la combinacion `Ctrl+Shift+p` para abrir la paleta de comandos de nuestro editor, y buscaremos el siguiente plugin: `Spring Initializr: Create a Maven project...`, el cual nos guiará por una serie de opciones para crear un nuevo proyecto de Spring Boot. Las opciones que utilizaremos para nuestro proyecto son las siguientes:

1. Spring Boot version: 2.7.2
2. Project Language: Java
3. Group Id: com.example
4. Artifact Id: oracle
5. Packaging type: jar
6. Java version: 11
7. Dependencies: De las indicaremos mas adelante editando el archivo pom.xml

Una vez terminado el proceso de seleccion de las configuraciones de nuestro proyecto, Visual Studio Code comenzara a crear nuestro proyecto de Spring Boot.

>#### Nota
>No es extraño que Visual Studio Code nos ofresca instalar plugins asociados a archivos de extension .java, de ser asi, es recomendable que las instalemos para que nuestra experiencia de desarrollo en dicho lenguaje sea mejor.

## 5. Configuracion del proyecto
Ahora comencemos con las configuraciones de nuestro proyecto, por lo que primera no dirigiremos al archivo pom.xml, el cual corresponde al archivo de definición de dependencias del proyecto.

A continuacion se indica el archivo pom.xml con las dependencias que necesitaremos para nuestro proyecto:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.7.2</version>
		<relativePath/>
		<!-- lookup parent from repository -->
	</parent>
	<groupId>com.example</groupId>
	<artifactId>oracle</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>oracle</name>
	<description>Demo project for Spring Boot</description>
	<properties>
		<java.version>11</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>com.oracle</groupId>
			<artifactId>ojdbc7</artifactId>
			<version>12.1.0.2</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>
	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>
</project>
```

Como podemos observar en el archivo anterior, las dependencias usadas para nuestra aplicación son:

1. spring-boot-starter-web
2. spring-boot-starter-data-jpa
3. ojdbc7 (el cual instalamos en el punto 3 del ejercicio)
4. spring-boot-starter-test

Una vez modificado el archivo pom.xml, abriremos la terminal del sistema (puede ser la terminal integrada de Visual Studio Code), y ejecutaremos el comando ``mvn clean install`.