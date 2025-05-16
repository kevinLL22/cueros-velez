# Proyecto Spring Boot - VTEX Data Processor

Este proyecto es un servicio backend en Spring Boot que automatiza la recopilación de datos desde VTEX y los expone via REST y tareas programadas. A continuación se describe cómo configurar y ejecutar el proyecto.

---

## Requisitos previos

* Java 17 o superior
* Maven 3.6+
* Base de datos MySQL o MariaDB corriendo (versión 5.7+ / 10.x+)
* Git (para clonar el repositorio)

---

## Clonar el repositorio

```bash
git clone https://tu-repositorio.git
git checkout main
cd nombre-del-proyecto
```

---

## Configuración de la base de datos

1. Crea la base de datos si no existe:

   ```sql
   CREATE DATABASE cuerose_velez;
   ```
2. Aplica el esquema (ejemplo DDL para `product_origin_destination`):

   ```sql
   CREATE TABLE product_origin_destination (
     id BIGINT NOT NULL AUTO_INCREMENT,
     product_id BIGINT,
     creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     warehouse VARCHAR(50),
     destination VARCHAR(50),
     PRIMARY KEY (id)
   );
   ```

---

## Variables de entorno / configuración

El proyecto carga propiedades desde `src/main/resources/application.properties` o `application.yml`. Edita las siguientes claves:

```properties
# URL de conexión (ajusta host, puerto y nombre de BD)
spring.datasource.url=jdbc:mysql://localhost:3306/cueros_velez
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Pool HikariCP
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Scheduler (opcional)
vtex.scheduler.cron=0 0 * * * *
# O también fixedRate en milisegundos
dashboard.scheduler.rate=3600000
```
---

## Compilar y ejecutar

Desde la raíz del proyecto:

```bash
mvn clean install
mvn spring-boot:run
```

O bien genera el JAR y ejecútalo:

```bash
mvn clean package
java -jar target/nombre-del-proyecto-0.0.1-SNAPSHOT.jar
```

---

## Endpoints disponibles

* **POST** `/api/vtex/process`:

  * Ejecuta manualmente la tarea de recopilación de datos.
  * Respuesta: HTTP 202 Accepted.

* **GET** `/api/product-origins/export?start={fecha}&end={fecha}`:

  * Descarga un Excel con los registros `ProductOriginDestination` entre las fechas.
  * Parámetros `start` y `end` en formato ISO-8601, p.ej. `2025-05-01T00:00:00`.

---

## Tarea programada

El servicio incluye un scheduler que ejecuta automáticamente `VTEXService.processProductRouteData()` según la expresión cron configurada en `vtex.scheduler.cron`. Por defecto está deshabilitado si no configuras la propiedad.

---

## FAQ

* **¿Cómo cambio la frecuencia del scheduler?**
  Modifica `vtex.scheduler.cron` en `application.properties` o usa `fixedRateString`.

* **¿Dónde configuro el pool de conexiones?**
  En las propiedades `spring.datasource.hikari.*`.

---

