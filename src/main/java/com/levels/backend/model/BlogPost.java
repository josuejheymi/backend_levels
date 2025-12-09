package com.levels.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * ENTIDAD: BLOG POST (Noticia)
 * ----------------------------------------------------
 * Representa una noticia o artículo en la base de datos.
 * La anotación @Entity le dice a Spring: "Crea una tabla llamada 'blog_post' 
 * con estas columnas".
 */
@Entity
@Table(name = "blog_posts") // Opcional: Definimos explícitamente el nombre de la tabla
public class BlogPost {

    // --- CLAVE PRIMARIA (Primary Key) ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementable (1, 2, 3...)
    private Long id;

    private String titulo;

    /**
     * CONTENIDO LARGO
     * Por defecto, un String en Java se convierte en VARCHAR(255) en MySQL.
     * Eso es muy poco para una noticia completa.
     * 'columnDefinition = "TEXT"' fuerza a MySQL a usar el tipo TEXT (hasta 65KB)
     * o LONGTEXT para que quepan párrafos enteros.
     */
    @Column(columnDefinition = "TEXT") 
    private String contenido;

    private String autor;      // Nombre del redactor (o Admin)
    private String imagenUrl;  // URL de la imagen de portada
    
    private LocalDateTime fechaPublicacion;

    /**
     * CICLO DE VIDA: PRE-PERSISTENCIA
     * Este método se ejecuta AUTOMÁTICAMENTE justo antes de que
     * Hibernate guarde el objeto en la base de datos (INSERT).
     * * Beneficio: No tenemos que setear la fecha manualmente en el Controller.
     * El sistema pone el sello de tiempo actual por sí solo.
     */
    @PrePersist
    public void prePersist() {
        if (this.fechaPublicacion == null) {
            this.fechaPublicacion = LocalDateTime.now();
        }
    }

    // --- GETTERS Y SETTERS (Boilerplate) ---
    // Necesarios para que Jackson (Librería JSON) pueda leer y escribir el objeto.

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public LocalDateTime getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(LocalDateTime fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
}