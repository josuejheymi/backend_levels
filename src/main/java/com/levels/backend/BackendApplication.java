package com.levels.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * CLASE PRINCIPAL DE ARRANQUE (BOOTSTRAP CLASS)
 * ----------------------------------------------------
 * Esta clase contiene el método 'main' que lanza toda la aplicación Spring Boot.
 * Es el punto de inicio de la inyección de dependencias, controladores, y servicios.
 */
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "LevelUp Store API", version = "1.0", description = "Documentación de la tienda de videojuegos"))
public class BackendApplication {

    /**
     * MÉTODO PRINCIPAL
     * @param args Argumentos de la línea de comandos (no se usan aquí).
     */
    public static void main(String[] args) {
        // Lanza la aplicación. Spring Boot se encarga de:
        // 1. Escanear el proyecto (Controllers, Services, Repositories).
        // 2. Iniciar el servidor web integrado (Tomcat por defecto).
        // 3. Conectar la Base de Datos (a través de application.properties).
        SpringApplication.run(BackendApplication.class, args);
    }

}