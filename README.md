# spring.boot.orace.db.container.setup.local.environment
Local environment to develop spring boot microservice connecting to oracle database container running in docker

En este ejercicio vamos a configurar un ambiente local y desarrollaremos un microservicio en spring boot para conectarnos a una base de datos oracle, la cual estara montada en nuestro mismo host pero en un entorno de Docker.

# Prerequisitos
Este ejercicio fue realizado sobre el sistema operativo Windows 11 y la instalación de las siguientes herramientas de software (el ejercicio no cubre la instalación de estas herramientas):

1. Visual Studio Code [https://code.visualstudio.com/]
2. Docker Desktop[https://docs.docker.com/desktop/install/windows-install/]
3. OpenJDK 11 [https://jdk.java.net/archive/]
4. Maven [https://maven.apache.org/download.cgi]
5. DBeaver [https://dbeaver.io/download/]

Una vez instaladas estas herramientas, antes de empezar con el desarrollo, vamos a instalar algunos plugins de Visual Studio Code muy útiles que serán de mucha ayuda para la creación y desarrollo de la aplicación:

1. Spring Initializr Java Support
2. Spring Boot Dashboard
3. Spring Boot Tools
4. Spring Boot Extension Pack

# Ejercicio
A este punto se espera que usted tenga todas las herramientas mencionadas instaladas y configuradas.

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
Ahora comencemos con las configuraciones de nuestro proyecto, por lo que primero no dirigiremos al archivo pom.xml, el cual corresponde al archivo de definición de dependencias del proyecto.

A continuacion se indica el archivo pom.xml con las dependencias necesarias para nuestra aplicación:

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

Una vez modificado el archivo pom.xml, abriremos la terminal del sistema (puede ser la terminal integrada de Visual Studio Code), y ejecutaremos el comando ``mvn install`, el cual descargará las dependencias definidas para la aplicación.

Ahora, configuraremos el archivo de propiedades del proyecto, el cual configura algunas propiedades del mismo framework de spring y las dependencias que incluimos en la aplicación. Este archivo suele llamarse `application.properties` o `bootstrap.yaml`, y suele ubicarse en la ruta `/src/main/resources`.

Los parámetros de configuración de nuestro servicio serán los siguientes:

spring.main.banner-mode=off

```properties
# create tables and sequences, loads import.sql file
spring.jpa.hibernate.ddl-auto=create

# Oracle settings
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
spring.datasource.username=system
spring.datasource.password=oracle
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.Oracle10gDialect
```
Una de las características que nos brinda JPA es que podemos ejecutar un script inicial hacia la base de datos, por medio de un archivo llamado `import.sql` ubicado en la ruta `/src/main/resources`, y por medio del parámetro `spring.jpa.hibernate.ddl-auto=create` en el archivo `application.properties`, ejecutará el código escrito dentro de este archivo. Hay que tener mucho cuidado con el valor de esta propiedad, ya que si se manipula de forma inadecuada, podriamos tener un evento no deseado en nuestra base de datos:

* spring.jpa.hibernate.ddl-auto=none (no ejecuta nada hacia la base de datos)
* spring.jpa.hibernate.ddl-auto=create (crea las tablas en base a nuestras entities)
* spring.jpa.hibernate.ddl-auto=create-drop (crea las tablas en base a nuestras entities y las elimina cuando finaliza el proceso de nuestro servicio)
* spring.jpa.hibernate.ddl-auto=update ()
* spring.jpa.hibernate.ddl-auto=validate ()

## 6. A desarrollar
Este es el punto que nos gusta a los desarrolladores, escribir coódigo y que todo funcione, pues manos a la obra. Dividiremos nuestra aplicación en tres packages que definirán capas de lógica diferentes:

1. entities: package con clases que representan las tablas en la base de datos
2. repositories: package con clases que ejecutan consulas y acciones en la base de datos
3. controllers: package con clases que representan los endpoints de nuestro servicio

A partir de esta definición, enfocaremos nuestra aplicación a la exposición de datos de clientes.A continuación se muestran las tres clases de java (una por cada capa) que conforman nuestro microservicio:

### Customer.java (Entity)
```java
package com.example.oracle.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
    @SequenceGenerator(sequenceName = "customer_seq", allocationSize = 1, name = "CUST_SEQ")
    private Long id;

    private String name;

    private String email;

    @Column(name = "CREATED_DATE")
    private Date date;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
```

### CustomerRepository.java (Repository)

```java
package com.example.oracle.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.oracle.entities.Customer;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

}
```

### CustomerController.java (Controller)

```java
package com.example.oracle.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.oracle.entities.Customer;
import com.example.oracle.repositories.CustomerRepository;

@RestController
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/customers")
    public Iterable<Customer> getAll() {
        return customerRepository.findAll();
    }

    @GetMapping(value = "/customers/{id}")
    public Customer findCustomerById(@PathVariable(name = "id") Long id) {
        return customerRepository.findById(id).orElse(null);
    }
    
}
```

Como podemos observar, creamos una **entidad** que representa nuestra tabla en base de datos, un **repositorio** que ejecuta las consultas de los datos y el **controlador** que expondrá nuestro servicio de Clientes por medio de una API Rest.

## 7. El despliegue
Ahora que ya tenemos nuestra aplicación lista, solo nos queda ejecutarla en nuestro equipo, por lo cual, por medio de la interfaz de linea de comandos, nos dirigiremos a la ruta raíz del proyecto, al mismo nivel en que se encuentra el archivo `pom.xml`, y ejecutaremos el comando `mvn spring-boot:run`, el cual iniciará la ejecución de nuestro microservicio en nuestro host.

## 8. La prueba final
Ahora que tenemos nuestro servicio en ejecución, abriremos el navegador web que tengamos instalado y nos dirigiremos a la ruta `http://localhost:8080/customers`, la cual nos entregará como respuesta un archivo json desplegado por el navegador como respuesta de nuestra API Rest:

```json
[
    {
        "id": 1,
        "name": "mkyong",
        "email": "111@yahoo.com",
        "date": "2017-02-11T03:00:00.000+00:00"
    },
    {
        "id": 2,
        "name": "yflow",
        "email": "222@yahoo.com",
        "date": "2017-02-12T03:00:00.000+00:00"
    },
    {
        "id": 3,
        "name": "zilap",
        "email": "333@yahoo.com",
        "date": "2017-02-13T03:00:00.000+00:00"
    }
]
```

Felicidades si llegaste hasta aquí, ahora tienes los conocimientos necesarios para ejecutar un entorno de desarrollo con java y base de datos Oracle, y que mejor que no instalar la base de datos en nuestro propio equipo, sino montarlo desde un contenedor efímero que puede eliminarse y recrearse las veces que nosotros queramos.

Buena suerte para lo que siga en tu camino :)