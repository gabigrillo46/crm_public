package com.vaadin.tutorial.crm.backend.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class Mensaje extends AbstractEntity{

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Users usuario =null;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente =null;

    private String mensaje = null;

    private LocalDateTime fecha_hora=null;

    private String numero_destino = "";

    private String numero_origen = "";

    private int sentido= -1;

    public static final int ENTRADA = 0;
    public static final int SALIDA = 1;

    public Users getUsuario() {
        return usuario;
    }

    public void setUsuario(Users usuario) {
        this.usuario = usuario;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(LocalDateTime fecha_hora) {
        this.fecha_hora = fecha_hora;
    }

    public String getNumero_destino() {
        return numero_destino;
    }

    public void setNumero_destino(String numero_destino) {
        this.numero_destino = numero_destino;
    }

    public String getNumero_origen() {
        return numero_origen;
    }

    public void setNumero_origen(String numero_origen) {
        this.numero_origen = numero_origen;
    }

    public int getSentido() {
        return sentido;
    }

    public void setSentido(int sentido) {
        this.sentido = sentido;
    }
}
