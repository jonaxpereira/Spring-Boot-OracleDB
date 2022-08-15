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

Una vez instaladas estas herramientas, antes de empezar con el desarrollo, vamos a instalar algunos plugins de Visual Studio Code muy utiles que serán de mucha ayuda a lo largo del ejercicio:

1. Spring Initializr Java Support
2. Spring Boot Dashboard
3. Spring Boot Tools
4. Spring Boot Extension Pack

Estas herramientas serán de mucha utilidad a lo largo del desarrollo del microservicio.

# Ejercicio
A este punto se espera que usted ya tenga todas las herramientas ya mencionadas ya instaladas y configuradas.

## 1. Despliegue de la base de datos Oracle con Docker
El primer punto será montar una base de datos Oracle en un entorno contenerizado con Docker, para lo cual, hemos decidido tomar la imagen base https://hub.docker.com/r/oracleinanutshell/oracle-xe-11g. Como nos indica en la pagina, lo primero que debemos hacer es abrir nuestra terminal de comandos y ejecutar el siguiente comando:

```docker
docker pull oracleinanutshell/oracle-xe-11g
```