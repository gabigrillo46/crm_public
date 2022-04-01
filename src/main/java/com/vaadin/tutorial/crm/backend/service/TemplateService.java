package com.vaadin.tutorial.crm.backend.service;

import com.vaadin.tutorial.crm.backend.entity.Template;
import com.vaadin.tutorial.crm.backend.repository.TemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateService {

    private TemplateRepository templateRepository;

    public TemplateService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public List<Template> getListaTemplatePorFiltro(String nombre)
    {
        return templateRepository.getListaTemplatePorNombreSimilar(nombre);
    }

    public void saveTemplate(Template template)
    {
        this.templateRepository.save(template);
    }

    public Template getTemplatePorId(Long idTemplate)
    {
        return this.templateRepository.getTemplatePorID(idTemplate);
    }

    public Template getTemplatePorNombre(String nombre)
    {
        return this.templateRepository.getTemplatePorNombreExacto(nombre);
    }

    public List<Template> getListaTemplateActivos()
    {
        return this.templateRepository.getListaTemplateActivos();
    }

}
