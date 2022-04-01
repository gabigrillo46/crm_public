package com.vaadin.tutorial.crm.backend.service;

import com.vaadin.tutorial.crm.backend.entity.Comentario;
import com.vaadin.tutorial.crm.backend.repository.ComentarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComentarioService {

    private ComentarioRepository comentarioRepository;

    public ComentarioService(ComentarioRepository comentarioRepository)
    {
        this.comentarioRepository=comentarioRepository;
    }

    public List<Comentario> getListaComentariosCliente(Long idCliente)
    {
        return this.comentarioRepository.getComentariosCliente(idCliente);
    }

    public void save(Comentario comentario)
    {
        this.comentarioRepository.save(comentario);
    }

    public void eliminarComentario(Comentario comentario)
    {
        this.comentarioRepository.delete(comentario);
    }
}
