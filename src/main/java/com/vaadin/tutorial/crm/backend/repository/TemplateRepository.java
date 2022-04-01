package com.vaadin.tutorial.crm.backend.repository;

import com.vaadin.tutorial.crm.backend.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TemplateRepository extends JpaRepository<Template, Long> {

    @Query("FROM Template t where (:nombre is null or t.nombre like %:nombre%) and t.estado != "+Template.BAJA)
    List<Template> getListaTemplatePorNombreSimilar(@Param("nombre")String nombre);

    @Query("FROM Template t where t.id =:id and t.estado != "+Template.BAJA)
    Template getTemplatePorID(@Param("id")Long id);

    @Query("FROM Template t where t.nombre =:nombre and t.estado != "+Template.BAJA)
    Template getTemplatePorNombreExacto(@Param("nombre")String nombre);

    @Query("FROM Template t where  t.estado != "+Template.BAJA)
    List<Template> getListaTemplateActivos();


}
