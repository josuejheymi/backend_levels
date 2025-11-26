package com.levels.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String email;
    private String password;
    private LocalDate fechaNacimiento; // Validación +18

    // --- Lógica de Negocio ---
    private boolean esEstudianteDuoc; // Descuento 20%
    private String codigoReferidoPropio; // Para invitar amigos
    private Integer puntosLevelUp = 0; // Gamificación
    private String nivel = "Novato"; // Novato, Pro, Leyenda
    
    // --- Seguridad ---
    private String rol = "CLIENTE"; // "CLIENTE" o "ADMIN"

    public Usuario() {}

    // Getters y Setters
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
