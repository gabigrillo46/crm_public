package com.vaadin.tutorial.crm.backend.service;

import com.vaadin.tutorial.crm.backend.entity.Sucursal;
import com.vaadin.tutorial.crm.backend.repository.SucursalRepository;
import com.vaadin.tutorial.crm.ui.Constante;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SucursalService {

    private SucursalRepository sucursalRepository;

    public SucursalService(SucursalRepository sucursalRepository) {
        this.sucursalRepository = sucursalRepository;
    }

    public List<Sucursal> findAll() {
        return sucursalRepository.findAll();
    }

    public List<Sucursal> buscarPorFiltro(String nombre)
    {
        if(nombre !=null && nombre.trim().length()>0)
        {
            return this.sucursalRepository.buscarPorFiltro(nombre);
        }
        else
        {
            return this.sucursalRepository.buscarTodasActivas();
        }
    }

    public void insertarSucursal(Sucursal sucursal)
    {
        this.sucursalRepository.save(sucursal);
    }

    public void eliminarSucursal(Sucursal sucursal)
    {
        sucursal.setEstado(Constante.ESTADOS_SUCURSALES.BAJA_LOGICA);
        this.sucursalRepository.save(sucursal);
    }

    public Sucursal buscarPorNombre(String nombre)
    {
       return this.sucursalRepository.buscarPorNombre(nombre);
    }

    public Map<String, Integer> getStats() {
        HashMap<String, Integer> stats = new HashMap<>();
        findAll().forEach(sucursal -> stats.put(sucursal.getName(),5));
        return stats;
    }

    public List<Sucursal>buscarTodasActivas()
    {
        return sucursalRepository.buscarTodasActivas();
    }
}
