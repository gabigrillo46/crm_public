package com.vaadin.tutorial.crm.backend.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class Comentario extends AbstractEntity{

    private String comentario="";

    private int estado =-1;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente =null;

    private LocalDateTime fecha = null;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Users usuario =null;


    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Users getUsuario() {
        return usuario;
    }

    public void setUsuario(Users usuario) {
        this.usuario = usuario;
    }
}
