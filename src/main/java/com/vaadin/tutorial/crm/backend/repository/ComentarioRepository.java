package com.vaadin.tutorial.crm.backend.repository;

import com.vaadin.tutorial.crm.backend.entity.Comentario;
import com.vaadin.tutorial.crm.ui.Constante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    @Query("FROM Comentario c where c.cliente.id = :cliente  and c.estado != "+ Constante.ESTADOS_COMENTARIOS.BAJA_LOGICA)
    List<Comentario> getComentariosCliente(@Param("cliente") Long idCliente);
}
