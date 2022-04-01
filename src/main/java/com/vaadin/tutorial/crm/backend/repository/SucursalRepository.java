package com.vaadin.tutorial.crm.backend.repository;

import com.vaadin.tutorial.crm.backend.entity.Sucursal;
import com.vaadin.tutorial.crm.ui.Constante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SucursalRepository extends JpaRepository<Sucursal, Long> {

    @Query("FROM Sucursal s where s.name = ?1 and s.estado = "+Constante.ESTADOS_SUCURSALES.ACTIVO)
    Sucursal buscarPorNombre(String nombre);

    @Query("FROM Sucursal s where s.name like %:nombre% and s.estado = "+Constante.ESTADOS_SUCURSALES.ACTIVO)
    List<Sucursal>buscarPorFiltro(@Param("nombre") String nombre);

    @Query("FROM Sucursal s where s.estado = "+ Constante.ESTADOS_SUCURSALES.ACTIVO)
    List<Sucursal>buscarTodasActivas();


}
