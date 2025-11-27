package com.levels.backend.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.levels.backend.model.Orden;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findByUsuarioId(Long usuarioId); // Para el historial del perfil
}