package com.vaadin.tutorial.crm.backend.service;

import com.vaadin.tutorial.crm.backend.entity.Source;
import com.vaadin.tutorial.crm.backend.repository.SourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SourceService {

    private SourceRepository sourceRepository;

    public SourceService(SourceRepository sourceRepository)
    {
        this.sourceRepository=sourceRepository;
    }

    public List<Source> getListoPorFiltro(String nombre)
    {
        return sourceRepository.buscarPorFiltro(nombre);
    }

    public List<Source> buscarTodosActivos()
    {
        return sourceRepository.buscarTodosActivos();
    }

    public Source getSourcePorNombre(String nombre)
    {
        return sourceRepository.buscarPorNombre(nombre);
    }

    public void saveSource(Source source)
    {
        sourceRepository.save(source);
    }
}
