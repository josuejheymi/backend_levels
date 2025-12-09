package com.levels.backend.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * ENTIDAD: USUARIO
 * ----------------------------------------------------
 * Representa a un cliente o administrador registrado en el sistema.
 * Es la base para la autenticación (Login) y la personalización.
 */
@Entity
public class Usuario {

    // --- IDENTIFICADOR Y SEGURIDAD ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    
    // Email: Debe ser único en la BD (Se define en el Repository o Service)
    private String email;
    
    // Contraseña: Se guarda encriptada (aunque en el proyecto se usa NoOp para pruebas)
    private String password;
    
    // Fecha de nacimiento: Usado para validación de edad (+18) en el servicio de registro
    private LocalDate fechaNacimiento; 

    // --- LÓGICA DE NEGOCIO Y GAMIFICACIÓN ---
    
    // Bandera para aplicar un descuento permanente (Ej: @duoc.cl)
    private boolean esEstudianteDuoc; 
    
    // Código único generado por el sistema para que el usuario invite a otros
    private String codigoReferidoPropio; 
    
    // Puntos acumulados por compras y acciones
    private Integer puntosLevelUp = 0; 
    
    // Nivel actual del jugador (Ej: Novato, Pro, Leyenda). Inicialmente "Novato".
    private String nivel = "Novato"; 
    
    // --- AUTORIZACIÓN (Roles) ---
    // Define los permisos (ADMIN puede acceder al Panel, CLIENTE solo puede comprar)
    private String rol = "CLIENTE"; // Valores posibles: "CLIENTE", "ADMIN", "VENDEDOR"

    // Constructor vacío (necesario para JPA)
    public Usuario() {}

    // --- GETTERS Y SETTERS ---
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    
    public boolean isEsEstudianteDuoc() { return esEstudianteDuoc; }
    public void setEsEstudianteDuoc(boolean esEstudianteDuoc) { this.esEstudianteDuoc = esEstudianteDuoc; }
    
    public String getCodigoReferidoPropio() { return codigoReferidoPropio; }
    public void setCodigoReferidoPropio(String codigoReferidoPropio) { this.codigoReferidoPropio = codigoReferidoPropio; }
    
    public Integer getPuntosLevelUp() { return puntosLevelUp; }
    public void setPuntosLevelUp(Integer puntosLevelUp) { this.puntosLevelUp = puntosLevelUp; }
    
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}