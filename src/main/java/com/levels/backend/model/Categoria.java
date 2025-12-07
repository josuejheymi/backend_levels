package com.levels.backend.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre de la categoría (Ej: "Consolas", "Accesorios")
    private String nombre;

    // Nuevo campo para guardar la URL de la imagen de la categoría
    private String imagenUrl; 
    
    // Relación Inversa: Una Categoría puede tener muchos Productos
    // Usamos JsonIgnore para evitar recursión infinita al serializar a JSON
    @JsonIgnore
    @OneToMany(mappedBy = "categoria")
    private List<Producto> productos;

    // Constructor vacío (necesario para JPA)
    public Categoria() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public List<Producto> getProductos() { return productos; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }
}