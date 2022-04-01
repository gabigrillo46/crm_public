package com.vaadin.tutorial.crm.backend.entity;

import javax.persistence.Entity;

@Entity
public class Template extends AbstractEntity{

    private String nombre="";

    private String mensaje ="";

    private int estado = -1;

    public static final int ACTIVO=0;
    public static final int BAJA=1;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
