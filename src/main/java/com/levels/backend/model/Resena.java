package com.levels.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * ENTIDAD: RESEÑA (Review / Feedback)
 * ----------------------------------------------------
 * Representa la opinión de un usuario sobre un producto específico.
 * Es vital para el "Social Proof" (Prueba Social) del e-commerce.
 * Técnicamente, es una Entidad Asociativa que vincula Usuario <-> Producto.
 */
@Entity
@Table(name = "resenas") // Definimos el nombre explícito en la BD
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * RELACIÓN: AUTOR DE LA RESEÑA
     * @ManyToOne: Muchas reseñas pueden ser escritas por un mismo usuario.
     * @JoinColumn: Crea la columna 'usuario_id' (Foreign Key) en la tabla 'resenas'.
     */
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    /**
     * RELACIÓN: PRODUCTO EVALUADO
     * @ManyToOne: Muchas reseñas pueden pertenecer al mismo producto.
     * @JoinColumn: Crea la columna 'producto_id' (Foreign Key).
     */
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    // Puntuación numérica (Generalmente de 1 a 5 estrellas)
    private Integer calificacion;
    
    // Texto de la opinión
    private String comentario;
    
    // Fecha de publicación
    private LocalDateTime fecha;

    /**
     * CICLO DE VIDA: AUTOMATIZACIÓN DE FECHA
     * Al igual que en la Orden o el Blog, usamos @PrePersist para que
     * Hibernate asigne la fecha/hora actual justo antes de guardar (INSERT).
     * Así el Controller no tiene que preocuparse de hacer 'new LocalDateTime()'.
     */
    @PrePersist
    public void prePersist() {
        this.fecha = LocalDateTime.now();
    }

    // --- GETTERS Y SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getCalificacion() { return calificacion; }
    public void setCalificacion(Integer calificacion) { this.calificacion = calificacion; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}