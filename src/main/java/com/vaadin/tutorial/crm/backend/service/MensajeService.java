package com.vaadin.tutorial.crm.backend.service;

import com.vaadin.tutorial.crm.backend.entity.Mensaje;
import com.vaadin.tutorial.crm.backend.repository.MensajeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MensajeService {

    private MensajeRepository mensajeRepository;

    public MensajeService(MensajeRepository mensajeRepository)
    {
        this.mensajeRepository=mensajeRepository;
    }

    public List<Mensaje> getListaMensajeCliente(Long idCliente)
    {
        return this.mensajeRepository.listaMensajeDesdeCliente(idCliente);
    }

    public void saveMensaje(Mensaje mensaje)
    {
        this.mensajeRepository.save(mensaje);
    }

    public Mensaje getUltimoMensajeCliente(Long idCliente)
    {
        return this.mensajeRepository.getUltimoMensajeDeCliente(idCliente);
    }
}
