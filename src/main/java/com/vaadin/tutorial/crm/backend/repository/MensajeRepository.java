package com.vaadin.tutorial.crm.backend.repository;

import com.vaadin.tutorial.crm.backend.entity.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    @Query("FROM Mensaje m where m.cliente.id = :idCliente order by id desc")
    List<Mensaje> listaMensajeDesdeCliente(@Param("idCliente") Long idCliente);

    @Query("FROM Mensaje m where m.id in (select max(me.id) from Mensaje me where me.cliente.id = :idCliente)")
    Mensaje getUltimoMensajeDeCliente(@Param("idCliente") Long idCliente);

}
