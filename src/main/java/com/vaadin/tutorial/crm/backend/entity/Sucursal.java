package com.vaadin.tutorial.crm.backend.entity;

import javax.persistence.Entity;

@Entity
public class Sucursal extends AbstractEntity{

    private String name="";

    private int estado=-1;

    public Sucursal()
    {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
