package com.levels.backend.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * ENTIDAD: CATEGORÍA
 * ----------------------------------------------------
 * Representa la agrupación lógica de productos (ej: "Consolas", "PC Gamer").
 * Es una entidad "Padre" en la relación con Productos.
 */
@Entity
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre visible en el menú del Frontend (Navbar y Filtros)
    private String nombre;

    // URL de la imagen que se muestra en las tarjetas de la página Home
    private String imagenUrl; 
    
    /**
     * RELACIÓN 1 a N (Bidireccional)
     * Una Categoría tiene MUCHOS Productos.
     * * 1. mappedBy = "categoria": Indica que la dueña de la relación (quien tiene la Foreign Key)
     * es la clase 'Producto'. Aquí solo estamos definiendo la vista inversa.
     * * 2. @JsonIgnore: ¡CRÍTICO!
     * Al convertir esto a JSON, Jackson intenta leer la categoría, luego sus productos,
     * luego la categoría de esos productos, y así infinitamente.
     * @JsonIgnore corta el ciclo aquí: "Cuando pidas una categoría, NO me traigas la lista gigante de productos".
     */
    @JsonIgnore
    @OneToMany(mappedBy = "categoria")
    private List<Producto> productos;

    // --- CONSTRUCTORES ---
    
    // Constructor vacío (Requerido obligatoriamente por JPA/Hibernate)
    public Categoria() {}

    // --- GETTERS Y SETTERS ---
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public List<Producto> getProductos() { return productos; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }
}