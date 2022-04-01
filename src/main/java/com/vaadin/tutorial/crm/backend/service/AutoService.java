package com.vaadin.tutorial.crm.backend.service;

import com.vaadin.tutorial.crm.backend.entity.Auto;
import com.vaadin.tutorial.crm.backend.repository.AutoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AutoService {

    private AutoRepository autoRepository;

    public AutoService(AutoRepository autoRepository)
    {
        this.autoRepository=autoRepository;
    }

    public Auto getAutoPorId(long id)
    {
        return this.autoRepository.getAutoPorId(id);
    }

    public void saveAuto(Auto auto)
    {
        this.autoRepository.save(auto);
    }

    public void eliminarAuto(Auto auto)
    {
        this.autoRepository.delete(auto);
    }

    public List<Auto>buscarAutosPorFiltro(String movil, Long idSucursal, LocalDate fechaDesde, LocalDate fechaHasta)
    {
        return this.autoRepository.listaAutosPorFiltro(movil, idSucursal, fechaDesde, fechaHasta);
    }
}
