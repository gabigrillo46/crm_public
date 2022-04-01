package com.vaadin.tutorial.crm.backend.repository;

import com.vaadin.tutorial.crm.backend.entity.Auto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AutoRepository extends JpaRepository<Auto, Long> {

    @Query("FROM Auto a where a.id = ?1")
    Auto getAutoPorId(long id);

    @Query("FROM Auto a where a.clienteAuto.id in (select c.id from Cliente c where (:movil is null or c.movil = :movil) " +
            "and (:sucursal is null or c.sucursal.id=:sucursal)) " +
            "and (:fechaDesde is null or  a.pick_up_date >=:fechaDesde ) " +
            "and (:fechaHasta is null or a.pick_up_date <= :fechaHasta ) order by a.pick_up_date desc")
    List<Auto> listaAutosPorFiltro(@Param("movil") String movil, @Param("sucursal")Long idSucursal,
                                   @Param("fechaDesde") LocalDate fechaDesde, @Param("fechaHasta") LocalDate fechaHasta);
}
