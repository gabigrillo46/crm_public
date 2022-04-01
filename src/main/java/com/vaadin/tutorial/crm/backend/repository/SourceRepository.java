package com.vaadin.tutorial.crm.backend.repository;

import com.vaadin.tutorial.crm.backend.entity.Source;
import com.vaadin.tutorial.crm.ui.Constante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SourceRepository extends JpaRepository<Source, Long> {

    @Query("FROM Source s where s.name like %:nombre% and s.estado != "+ Constante.ESTADOS_SOURCE.BAJA_LOGICA)
    List<Source> buscarPorFiltro(@Param("nombre") String nombre);

    @Query("FROM Source s where  s.estado != "+ Constante.ESTADOS_SOURCE.BAJA_LOGICA)
    List<Source> buscarTodosActivos();

    @Query("FROM Source s where s.name = ?1  and s.estado != "+Constante.ESTADOS_SOURCE.BAJA_LOGICA)
    Source buscarPorNombre(String nombre);

}
