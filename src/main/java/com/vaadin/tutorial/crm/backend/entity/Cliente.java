package com.vaadin.tutorial.crm.backend.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class Cliente extends AbstractEntity{

    private String nombre="";
    private String apellido="";
    private String movil="";

    private String email="";

    private int estado=0;

    private String observacion="";

    @ManyToOne
    @JoinColumn(name = "id_sucursal")
    private Sucursal sucursal =null;

    private String calltobemade ="Yes";

    private int appoiment =0;

    private LocalDateTime fecha_appoiment;

    private LocalDateTime fecha_llamada;

    private String hora_appoiment = "";

    private int show_up=0;

    private int sale = 0;

    private String creado_por ="";

    private Date fecha_creado = null;

    @ManyToOne
    @JoinColumn(name = "id_source")
    private Source source =null;

    private LocalDate DOB=null;

    private String license=null;

    private String direccion=null;

    private String suburb=null;

    private String state=null;

    private String postcode=null;

    private String home_phone =null;

    private String perdido_por="";

    private boolean is_truck = false;

    private boolean is_retail = false;


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getMovil() {
        return movil;
    }

    public void setMovil(String movil) {
        this.movil = movil;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }



    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    public String getCalltobemade() {
        return calltobemade;
    }

    public void setCalltobemade(String calltobemade) {
        this.calltobemade = calltobemade;
    }

    public int getAppoiment() {
        return appoiment;
    }

    public void setAppoiment(int appoiment) {
        this.appoiment = appoiment;
    }

    public LocalDateTime getFecha_appoiment() {
        return fecha_appoiment;
    }

    public void setFecha_appoiment(LocalDateTime fecha_appoiment) {
        this.fecha_appoiment = fecha_appoiment;
    }

    public String getHora_appoiment() {
        return hora_appoiment;
    }

    public void setHora_appoiment(String hora_appoiment) {
        this.hora_appoiment = hora_appoiment;
    }

    public int getShow_up() {
        return show_up;
    }

    public void setShow_up(int show_up) {
        this.show_up = show_up;
    }

    public int getSale() {
        return sale;
    }

    public void setSale(int sale) {
        this.sale = sale;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getCreado_por() {
        return creado_por;
    }

    public void setCreado_por(String creado_por) {
        this.creado_por = creado_por;
    }

    public LocalDateTime getFecha_llamada() {
        return fecha_llamada;
    }

    public void setFecha_llamada(LocalDateTime fecha_llamada) {
        this.fecha_llamada = fecha_llamada;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getHome_phone() {
        return home_phone;
    }

    public void setHome_phone(String home_phone) {
        this.home_phone = home_phone;
    }

    public LocalDate getDOB() {
        return DOB;
    }

    public void setDOB(LocalDate DOB) {
        this.DOB = DOB;
    }

    public Date getFecha_creado() {
        return fecha_creado;
    }

    public void setFecha_creado(Date fecha_creado) {
        this.fecha_creado = fecha_creado;
    }

    public String getPerdido_por() {
        return perdido_por;
    }

    public void setPerdido_por(String perdido_por) {
        this.perdido_por = perdido_por;
    }

    public boolean isIs_truck() {
        return is_truck;
    }

    public void setIs_truck(boolean is_truck) {
        this.is_truck = is_truck;
    }

    public boolean isIs_retail() {
        return is_retail;
    }

    public void setIs_retail(boolean is_retail) {
        this.is_retail = is_retail;
    }
}
