package com.levels.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.levels.backend.model.DetalleOrden;

public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {}