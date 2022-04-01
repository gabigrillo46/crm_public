package com.vaadin.tutorial.crm.backend.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class Auto extends AbstractEntity{

    private String marca="";

    private String modelo="";

    private String rego="";

    private String stock_number="";

    private LocalDate pick_up_date =null;

    private String pick_up_time =null;


    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente clienteAuto=null;

    @ManyToOne
    @JoinColumn(name = "id_cliente_adicional")
    private Cliente clienteAdicional=null;

    private int cantidad_tiempo=0;

    private String concepto_tiempo="";

    @ManyToOne
    @JoinColumn(name = "id_user")
    private Users usuario_seller;

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getRego() {
        return rego;
    }

    public void setRego(String rego) {
        this.rego = rego;
    }

    public String getStock_number() {
        return stock_number;
    }

    public void setStock_number(String stock_number) {
        this.stock_number = stock_number;
    }

    public LocalDate getPick_up_date() {
        return pick_up_date;
    }

    public void setPick_up_date(LocalDate pick_up_date) {
        this.pick_up_date = pick_up_date;
    }

    public String getPick_up_time() {
        return pick_up_time;
    }

    public void setPick_up_time(String pick_up_time) {
        this.pick_up_time = pick_up_time;
    }

    public Cliente getClienteAuto() {
        return clienteAuto;
    }

    public void setClienteAuto(Cliente clienteAuto) {
        this.clienteAuto = clienteAuto;
    }

    public int getCantidad_tiempo() {
        return cantidad_tiempo;
    }

    public void setCantidad_tiempo(int cantidad_tiempo) {
        this.cantidad_tiempo = cantidad_tiempo;
    }

    public String getConcepto_tiempo() {
        return concepto_tiempo;
    }

    public void setConcepto_tiempo(String concepto_tiempo) {
        this.concepto_tiempo = concepto_tiempo;
    }

    public Cliente getClienteAdicional() {
        return clienteAdicional;
    }

    public void setClienteAdicional(Cliente clienteAdicional) {
        this.clienteAdicional = clienteAdicional;
    }

    public Users getUsuario_seller() {
        return usuario_seller;
    }

    public void setUsuario_seller(Users usuario_seller) {
        this.usuario_seller = usuario_seller;
    }
}
