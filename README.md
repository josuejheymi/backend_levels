#  LevelUp Store API (Backend)

Este es el backend de la tienda de videojuegos "LevelUp Store", desarrollado con **Spring Boot** y **MySQL**. Proporciona la lógica de negocio, el acceso a datos y la API REST para la aplicación cliente (Frontend).

##  Conceptos Clave del Proyecto

Este proyecto es una API robusta que implementa seguridad y reglas de negocio complejas:

1.  **Seguridad JWT Stateless:** Usa Tokens JWT para la autenticación, eliminando la necesidad de sesiones en el servidor.
2.  **Transacciones Atómicas:** El proceso de Checkout (`OrdenService`) es seguro, garantizando la integridad de stock mediante **@Transactional**.
3.  **Gamificación y Beneficios:** Implementa la lógica de **Puntos LevelUp** y el **Descuento Duoc** (20% para usuarios @duoc.cl).
4.  **Limpieza Segura:** La eliminación de productos (`ProductoService`) limpia manualmente las referencias en carritos y reseñas para evitar errores de clave foránea.

---

##  Tecnologías

* **Framework:** Spring Boot 3
* **Lenguaje:** Java 17
* **Base de Datos:** MySQL
* **Persistencia:** Spring Data JPA / Hibernate
* **Seguridad:** Spring Security + JWT
* **Documentación:** OpenAPI 3 (Swagger)

---

##  Configuración y Ejecución Local

### Requisitos Previos

* **JDK 17** o superior.
* **Maven** (para construir el proyecto).
* **MySQL Server** instalado y corriendo.

### 1. Configuración de la Base de Datos

Debe crear una base de datos local llamada `levels_db` y ejecutar el script SQL provisto en el repositorio del Frontend Level_Up_1:
[text](../Level_Up_1/script_datos.sql)
```sql
CREATE DATABASE levels_db;
