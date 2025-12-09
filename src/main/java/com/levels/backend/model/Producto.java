package com.levels.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * ENTIDAD: PRODUCTO
 * ----------------------------------------------------
 * Representa un artículo vendible en la tienda.
 * Esta clase se convierte automáticamente en la tabla 'producto' en MySQL.
 */
@Entity
public class Producto {

    // --- IDENTIFICADOR ÚNICO ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- DATOS BÁSICOS ---
    private String nombre;
    private String descripcion;
    
    /**
     * PRECIO Y STOCK (Wrappers)
     * * Usamos 'Double' e 'Integer' (Clases) en lugar de 'double' e 'int' (Primitivos).
     * * ¿Por qué? 
     * Porque los primitivos no pueden ser NULL (valen 0 por defecto).
     * Las clases Wrapper permiten guardar 'null', lo cual es útil para diferenciar
     * entre "precio cero (gratis)" y "precio no asignado".
     */
    private Double precio;
    private Integer stock;

    // --- MULTIMEDIA ---
    // Guardamos solo la URL (String), no la imagen binaria (BLOB).
    // Es una mejor práctica: la imagen vive en la nube (o carpeta pública) y la BD solo guarda el link.
    private String imagenUrl; 
    
    // URL para embeber videos de YouTube en la vista de detalles
    private String videoUrl;

    /**
     * RELACIÓN: CATEGORÍA (ManyToOne)
     * * Lógica: MUCHOS productos pertenecen a UNA categoría.
     * * @JoinColumn(name = "categoria_id"):
     * Esta anotación crea la columna física de Llave Foránea (FK) en la tabla 'producto'.
     * Esto convierte a la entidad Producto en la "Dueña de la Relación".
     */
    @ManyToOne 
    @JoinColumn(name = "categoria_id") 
    private Categoria categoria;

    // --- CONSTRUCTORES ---
    
    // Constructor vacío obligatorio para que Hibernate pueda instanciar la clase
    public Producto() {}
    
    // --- GETTERS Y SETTERS ---
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
}