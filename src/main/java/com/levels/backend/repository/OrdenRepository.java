package com.levels.backend.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.levels.backend.model.Orden;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findByUsuarioId(Long usuarioId);

    // NUEVO: Sumar todas las ventas de la historia
    @Query("SELECT SUM(o.total) FROM Orden o")
    Double sumarVentasTotales();
}