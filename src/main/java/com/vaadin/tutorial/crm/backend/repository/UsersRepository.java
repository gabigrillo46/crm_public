package com.vaadin.tutorial.crm.backend.repository;

import com.vaadin.tutorial.crm.backend.entity.Users;
import com.vaadin.tutorial.crm.ui.Constante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsersRepository extends JpaRepository<Users, Long> {




    @Query("FROM Users u where u.user_id = ?1")
    Users buscarPorId(int id);

    @Query("FROM Users u where u.username = ?1 "+
            "and u.estado !="+ Constante.ESTADOS_USUARIOS.BAJA_LOGICA)
    Users buscarPorNomberUusario(String nombreUsuario);

    @Query("FROM Users u where (:nombre is null or u.nombre LIKE %:nombre% or u.apellido like  %:nombre% ) " +
            "and (:sucursal is null or u.sucursal.id = :sucursal)" +
            "and u.estado !="+ Constante.ESTADOS_USUARIOS.BAJA_LOGICA)
    List<Users> buscarPorFiltro(@Param("nombre") String nombre,@Param("sucursal")Long idSucursal);

    @Query("FROM Users u where u.sucursal.id = :sucursal and u.estado != "+Constante.ESTADOS_USUARIOS.BAJA_LOGICA)
    List<Users> buscarPorSucursal(@Param("sucursal") Long idSucursal);

    @Query("FROM Users u where (:nombre is null or u.nombre = :nombre)" +
            "and u.estado !="+Constante.ESTADOS_USUARIOS.BAJA_LOGICA)
    Users buscarPorNombre(@Param("nombre") String nombre);







}
