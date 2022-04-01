package com.vaadin.tutorial.crm.ui.view.Clientes;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.tutorial.crm.backend.entity.*;
import com.vaadin.tutorial.crm.backend.service.*;
import com.vaadin.tutorial.crm.ui.Constante;
import com.vaadin.tutorial.crm.ui.MainLayout;
import net.sf.jasperreports.engine.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Route(value = "SaleForm", layout = MainLayout.class)
@PageTitle("Client Form | Certified Autos CRM")
public class SaleForm extends VerticalLayout implements HasUrlParameter<String> {
    //TextField txtMobile = new TextField("Mobile: ");
    TextField txtMovil = new TextField("Mobile:");
    TextField txtPhoneHome = new TextField("Home phone: ");
    TextField txtNombre = new TextField("Name: ");
    TextField txtApellido = new TextField("Last name: ");
    DatePicker dtpFechaNacimiento = new DatePicker("DOB:");
    TextField txtEmail = new TextField("E-MAIL:");
    TextField txtDriverLicense = new TextField("Driver license number:");
    TextField txtDireccion = new TextField("Address:");
    TextField txtSuburbio = new TextField("Suburb:");
    TextField txtState = new TextField("State: ");
    TextField txtPostCode = new TextField("PostCode: ");
    H2 titulo = new H2("Customer information");
    H2 tituloAuto = new H2("Car Details");
    ComboBox<Sucursal> cmbSucursales = new ComboBox<Sucursal>("Branch");
    ComboBox<Source> cmbSources = new ComboBox<>("Source");
    TextField txtUser = new TextField("Seller");
    ComboBox<String> cmbTipoTiempo = new ComboBox<String>("Type");
    Anchor link= new Anchor();

    TextField txtMarca = new TextField("Car make");
    TextField txtModelo = new TextField("Car model");
    TextField txtRego = new TextField("Rego");
    TextField txtStockNumber = new TextField("Stock number");
    DatePicker dtpFechaPickup = new DatePicker("Date pickup");
    TimePicker tpHoraPickup = new TimePicker("Time pickup");



    FormLayout formularioCliente = new FormLayout();

    FormLayout formularioAuto = new FormLayout();

    ClienteService clienteService;
    SucursalService sucursalService;
    SourceService sourceService;
    private UsersService usuarioService;
    private AutoService autoService;
    private ComentarioService comentarioService;
    Binder<Auto> binderAuto = new Binder<>(Auto.class);
    Binder<Cliente> binderCliente = new Binder<>(Cliente.class);

    Button botonSummit = new Button("Submmit", event -> aceptarFormulario());

    Button botonCancelar = new Button("Cancel", event -> cancelarFormulario());

    Button botonEliminar = new Button("Delete", event -> eliminarAuto());



    private long idAuto = -1;
    private int accion = -1;
    private Auto autoActual = null;
    private String pantallaAnterior = "";

    private long idCliente = -1;
    private Long idClienteAdicional = null;
    private Cliente clienteActual = null;

    Span mensaje = new Span();
    Notification notification = new Notification(mensaje);
    Button botonSeguro = new Button("Yes", event -> eliminarAutoSeguro());
    Button botonNoSeguro = new Button("No", event -> cerraDialogoConfirmacion());
    HorizontalLayout botonesEstaSeguro= new HorizontalLayout();
    Dialog dialogoEstaSeguro = new Dialog();
    Span mensajeEstaSeguro=new Span("Are you sure?");
    IntegerField numberField = new IntegerField();
    HorizontalLayout layout = new HorizontalLayout();


    Users ususario=null;

    Button botonDriverAdicional = new Button("Add additional driver",event -> agregarDriverAdicional());
    private Dialog dialogoDriverAdiconal=new Dialog();
    private TextField txtMovilAdicional = new TextField("Mobile:");
    private TextField txtPhoneHomeAdicional = new TextField("Home phone: ");
    private TextField txtNombreAdicional = new TextField("Name: ");
    private TextField txtApellidoAdicional = new TextField("Last name: ");
    private DatePicker dtpFechaNacimientoAdicional = new DatePicker("DOB:");
    private TextField txtEmailAdicional = new TextField("E-MAIL:");
    private TextField txtDriverLicenseAdicional = new TextField("Driver license number:");
    private TextField txtDireccionAdicional = new TextField("Address:");
    private TextField txtSuburbioAdicional = new TextField("Suburb:");
    private TextField txtStateAdicional = new TextField("State: ");
    private TextField txtPostCodeAdicional = new TextField("PostCode: ");
    FormLayout formularioClienteAdicional = new FormLayout();
    private Binder<Cliente> binderClienteAdicional = new Binder<>();
    Button botonAceptarAdicional = new Button("Submit",event -> aceptarClienteAdicional());
    Button botonCancelarAdicional = new Button("Cancel",event -> cancelarAdicional());
    private Cliente clienteAdicional = new Cliente();
    private Grid<Cliente> grillaClienteAdicional = new Grid<>();
    private List<Cliente> listaDriverAdicional = new ArrayList<>();
    private Button botonEliminarAdicional = new Button("Delete", event -> eliminarAdicional());





    public SaleForm(ClienteService clienteService, SucursalService sucursalService, SourceService sourceService,
                    AutoService autoService, UsersService usuarioService, ComentarioService comentarioService) {
        this.clienteService = clienteService;
        this.sucursalService = sucursalService;
        this.sourceService = sourceService;
        this.autoService = autoService;
        this.usuarioService = usuarioService;
        this.comentarioService = comentarioService;
        this.cargarCombos();
        this.setSizeFull();



        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            ususario = this.usuarioService.buscarPorNombreUsuario(currentUserName);
        }

        botonesEstaSeguro.add(botonSeguro,botonNoSeguro);
        this.dialogoEstaSeguro.add(mensajeEstaSeguro,botonesEstaSeguro);

        notification.setDuration(2000);
        notification.setPosition(Notification.Position.MIDDLE);

        txtMovil.addBlurListener(event -> ingresoTextoMovil(event.getSource().getValue()));
        txtMovilAdicional.addBlurListener(event -> ingresoTextoMovilAdicional(event.getSource().getValue()));

        numberField.setHasControls(true);
        numberField.setLabel("Rental time");

        txtMovil.setMaxWidth("300px");
        formularioCliente.add(txtMovil);
        txtPhoneHome.setMaxWidth("300px");
        formularioCliente.add(txtPhoneHome);
        txtNombre.setMaxWidth("300px");
        formularioCliente.add(txtNombre);
        txtApellido.setMaxWidth("300px");
        formularioCliente.add(txtApellido);
        dtpFechaNacimiento.setMaxWidth("300px");
        formularioCliente.add(dtpFechaNacimiento);
        txtEmail.setMaxWidth("300px");
        formularioCliente.add(txtEmail);
        txtDriverLicense.setMaxWidth("300px");
        formularioCliente.add(txtDriverLicense);
        txtDireccion.setMaxWidth("300px");
        formularioCliente.add(txtDireccion);
        txtSuburbio.setMaxWidth("300px");
        formularioCliente.add(txtSuburbio);
        txtState.setMaxWidth("300px");
        formularioCliente.add(txtState);
        txtPostCode.setMaxWidth("300px");
        formularioCliente.add(txtPostCode);
        cmbSucursales.setMinWidth("200px");
        formularioCliente.add(cmbSucursales);
        cmbSources.setMinWidth("200px");
        formularioCliente.add(cmbSources);
        txtUser.setMinWidth("200px");
        txtUser.setEnabled(false);
        formularioCliente.add(txtUser);
        formularioCliente.setMaxWidth("900px");
        formularioCliente.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));

        binderCliente.forField(txtMovil)
                .asRequired("You have to enter the mobile number")
                .bind(Cliente::getMovil,Cliente::setMovil);
        binderCliente.forField(txtPhoneHome)
                .bind(Cliente::getHome_phone, Cliente::setHome_phone);
        binderCliente.forField(dtpFechaNacimiento)
                .asRequired("You have to enter the DOB of the client")
                .bind(Cliente::getDOB, Cliente::setDOB);
        binderCliente.forField(txtDriverLicense)
                .bind(Cliente::getLicense, Cliente::setLicense);
        binderCliente.forField(txtEmail)
                .bind(Cliente::getEmail, Cliente::setEmail);
        binderCliente.forField(txtDireccion)
                .bind(Cliente::getDireccion, Cliente::setDireccion);
        binderCliente.forField(txtSuburbio)
                .bind(Cliente::getSuburb, Cliente::setSuburb);
        binderCliente.forField(txtState)
                .bind(Cliente::getState, Cliente::setState);
        binderCliente.forField(txtPostCode)
                .bind(Cliente::getPostcode, Cliente::setPostcode);
        binderCliente.forField(txtNombre)
                .asRequired("You have to enter the name of the client")
                .bind(Cliente::getNombre, Cliente::setNombre);
        binderCliente.forField(txtApellido)
                .asRequired("You have to enter the last name of the client")
                .bind(Cliente::getApellido, Cliente::setApellido);
        binderCliente.forField(cmbSources)
                .bind(Cliente::getSource,Cliente::setSource);
        binderCliente.forField(cmbSucursales)
                .bind(Cliente::getSucursal,Cliente::setSucursal);





        txtMovilAdicional.setMaxWidth("300px");
        formularioClienteAdicional.add(txtMovilAdicional);
        txtPhoneHomeAdicional.setMaxWidth("300px");
        formularioClienteAdicional.add(txtPhoneHomeAdicional);
        txtNombreAdicional.setMaxWidth("300px");
        formularioClienteAdicional.add(txtNombreAdicional);
        txtApellidoAdicional.setMaxWidth("300px");
        formularioClienteAdicional.add(txtApellidoAdicional);
        dtpFechaNacimientoAdicional.setMaxWidth("300px");
        formularioClienteAdicional.add(dtpFechaNacimientoAdicional);
        txtEmailAdicional.setMaxWidth("300px");
        formularioClienteAdicional.add(txtEmailAdicional);
        txtDriverLicenseAdicional.setMaxWidth("300px");
        formularioClienteAdicional.add(txtDriverLicenseAdicional);
        txtDireccionAdicional.setMaxWidth("300px");
        formularioClienteAdicional.add(txtDireccionAdicional);
        txtSuburbioAdicional.setMaxWidth("300px");
        formularioClienteAdicional.add(txtSuburbioAdicional);
        txtStateAdicional.setMaxWidth("300px");
        formularioClienteAdicional.add(txtStateAdicional);
        txtPostCodeAdicional.setMaxWidth("300px");
        formularioClienteAdicional.add(txtPostCodeAdicional);
        formularioClienteAdicional.setMaxWidth("900px");
        formularioClienteAdicional.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));

        binderClienteAdicional.forField(txtMovilAdicional)
                .asRequired("You have to enter the mobile number")
                .bind(Cliente::getMovil,Cliente::setMovil);
        binderClienteAdicional.forField(txtPhoneHomeAdicional)
                .bind(Cliente::getHome_phone, Cliente::setHome_phone);
        binderClienteAdicional.forField(dtpFechaNacimientoAdicional)
                .asRequired("You have to enter the DOB of the client")
                .bind(Cliente::getDOB, Cliente::setDOB);
        binderClienteAdicional.forField(txtDriverLicenseAdicional)
                .bind(Cliente::getLicense, Cliente::setLicense);
        binderClienteAdicional.forField(txtEmailAdicional)
                .bind(Cliente::getEmail, Cliente::setEmail);
        binderClienteAdicional.forField(txtDireccionAdicional)
                .bind(Cliente::getDireccion, Cliente::setDireccion);
        binderClienteAdicional.forField(txtSuburbioAdicional)
                .bind(Cliente::getSuburb, Cliente::setSuburb);
        binderClienteAdicional.forField(txtStateAdicional)
                .bind(Cliente::getState, Cliente::setState);
        binderClienteAdicional.forField(txtPostCodeAdicional)
                .bind(Cliente::getPostcode, Cliente::setPostcode);
        binderClienteAdicional.forField(txtNombreAdicional)
                .asRequired("You have to enter the name of the client")
                .bind(Cliente::getNombre, Cliente::setNombre);
        binderClienteAdicional.forField(txtApellidoAdicional)
                .asRequired("You have to enter the last name of the client")
                .bind(Cliente::getApellido, Cliente::setApellido);


        txtMarca.setMaxWidth("300px");
        formularioAuto.add(txtMarca);
        txtModelo.setMaxWidth("300px");
        formularioAuto.add(txtModelo);
        txtRego.setMaxWidth("300px");
        formularioAuto.add(txtRego);
        txtStockNumber.setMaxWidth("300px");
        formularioAuto.add(txtStockNumber);
        dtpFechaPickup.setMaxWidth("300px");
        formularioAuto.add(dtpFechaPickup);
        tpHoraPickup.setMaxWidth("300px");
        formularioAuto.add(tpHoraPickup);

        formularioAuto.add(numberField);
        cmbTipoTiempo.setMaxWidth("300px");
        formularioAuto.add(cmbTipoTiempo);
        formularioAuto.setMaxWidth("900px");
        formularioAuto.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));

        binderAuto.forField(txtMarca)
                .asRequired("you have to enter the brand of the car")
                .bind(Auto::getMarca, Auto::setMarca);
        binderAuto.forField(txtModelo)
                .asRequired("You have to enter the model")
                .bind(Auto::getModelo, Auto::setModelo);
        binderAuto.forField(txtRego)
                .bind(Auto::getRego, Auto::setRego);
        binderAuto.forField(txtStockNumber)
                .asRequired("You have to enter the stock number")
                .bind(Auto::getStock_number, Auto::setStock_number);
        binderAuto.forField(dtpFechaPickup)
                .bind(Auto::getPick_up_date, Auto::setPick_up_date);
        binderAuto.forField(numberField)
                .asRequired("You have to enter a number mora than 0")
                .bind(Auto::getCantidad_tiempo,Auto::setCantidad_tiempo);
        binderAuto.forField(cmbTipoTiempo)
                .asRequired("You have to select a type of time")
                .bind(Auto::getConcepto_tiempo,Auto::setConcepto_tiempo);


        botonSummit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        botonCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        botonEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        botonAceptarAdicional.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        botonCancelarAdicional.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        botonEliminarAdicional.addThemeVariants(ButtonVariant.LUMO_ERROR);

        this.configureGrid();


        layout.setMaxWidth("900px");
        layout.setBoxSizing(BoxSizing.CONTENT_BOX);
        link.setVisible(false);
        link.add(new Button("Download"));
        layout.add(botonSummit, botonCancelar, botonEliminar, link);

        VerticalLayout verticalLayoutGrilla = new VerticalLayout();
        verticalLayoutGrilla.add(grillaClienteAdicional);
        verticalLayoutGrilla.setHeight("150px");

        add(titulo, formularioCliente,botonDriverAdicional, verticalLayoutGrilla, tituloAuto, formularioAuto, layout);

        HorizontalLayout hBotonesAdicional = new HorizontalLayout();
        hBotonesAdicional.add(botonAceptarAdicional,botonCancelarAdicional, botonEliminarAdicional);

        dialogoDriverAdiconal.add(formularioClienteAdicional,hBotonesAdicional);

        if(this.autoActual!=null && this.autoActual.getUsuario_seller()!=null)
        {
            txtUser.setValue(this.autoActual.getUsuario_seller().getNombre()+" "+this.autoActual.getUsuario_seller().getApellido());
        }
        else {
           txtUser.setValue(ususario.getNombre() + " " + ususario.getApellido());
        }




    }

    @Override
    public void setParameter(BeforeEvent event,
                             @WildcardParameter String parameter) {
        if (parameter != null && parameter.trim().length() > 0) {
            String[] parametros = parameter.split("/");
            idAuto = Long.valueOf(parametros[0]);
            accion = Integer.parseInt(parametros[1]);
            pantallaAnterior = parametros[2];
            this.autoActual = this.autoService.getAutoPorId(idAuto);

            if (this.autoActual == null) {
                this.autoActual = new Auto();
                this.clienteActual = new Cliente();

            } else {
                this.idCliente = this.autoActual.getClienteAuto().getId();
                this.clienteActual = this.clienteService.getClientePorId(this.idCliente);
                if(this.autoActual.getPick_up_time()!=null && this.autoActual.getPick_up_time().trim().length()>0) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                    LocalTime tiempo = LocalTime.parse(this.autoActual.getPick_up_time(),formatter);
                    tpHoraPickup.setValue(tiempo);
                }

                if(this.autoActual!=null && this.autoActual.getUsuario_seller()!=null)
                {
                    txtUser.setValue(this.autoActual.getUsuario_seller().getNombre()+" "+this.autoActual.getUsuario_seller().getApellido());
                }


                if(this.autoActual.getClienteAdicional()!=null) {
                    this.idClienteAdicional = this.autoActual.getClienteAdicional().getId();
                    Cliente clienteAdicionalCrear = this.clienteService.getClientePorId(this.idClienteAdicional);
                    if(clienteAdicionalCrear!=null)
                    {
                        this.listaDriverAdicional.add(clienteAdicionalCrear);
                        this.grillaClienteAdicional.setItems(this.listaDriverAdicional);
                    }
                }

            }
        } else {
            accion = Constante.OPERACIONES_ABN.ALTA;
            this.autoActual = new Auto();
            this.clienteActual = new Cliente();
        }
        binderAuto.readBean(this.autoActual);
        binderCliente.readBean(this.clienteActual);

    }

    public void aceptarFormulario() {
        try {
            int cantidadTiempo = numberField.getValue();
            if(cantidadTiempo==0)
            {
                mensaje.setText("The quatity of time have to be mora than 0");
                notification.open();
                return;
            }


            binderCliente.writeBean(this.clienteActual);
            binderAuto.writeBean(this.autoActual);
            if(tpHoraPickup.getValue()!=null)
            {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                String hora =tpHoraPickup.getValue().format(dtf);
                this.autoActual.setPick_up_time(hora);
            }

            this.clienteActual.setCalltobemade("No");
            this.clienteActual.setSale(1);
            this.clienteActual.setEstado(Constante.ESTADOS_CLIENTES.ACTIVO);

            if(this.listaDriverAdicional.size()>0)
            {
                this.clienteAdicional = this.listaDriverAdicional.get(0);
                this.clienteAdicional.setCalltobemade("No");
                this.clienteAdicional.setSale(1);
                this.clienteAdicional.setEstado(Constante.ESTADOS_CLIENTES.ACTIVO);
            }
            else {
                this.clienteAdicional = null;
            }

            if(this.clienteAdicional!=null)
            {
                this.clienteService.registrarCliente(this.clienteAdicional);
                this.autoActual.setClienteAdicional(this.clienteAdicional);
            }

            if(this.clienteAdicional!=null)
            {
                Comentario comentario = new Comentario();
                comentario.setCliente(this.clienteAdicional);
                comentario.setComentario("Additional driver in the sale of the car: "+this.autoActual.getMarca()+" "+this.autoActual.getModelo()+" Stock: "+this.autoActual.getStock_number());
                comentario.setEstado(Constante.ESTADOS_COMENTARIOS.ACTIVO);
                TimeZone timeZone = TimeZone.getTimeZone("Australia/Sydney");
                Calendar c = Calendar.getInstance(timeZone);
                LocalDateTime v=  new java.sql.Timestamp(
                        c.getTime().getTime()).toLocalDateTime();
                comentario.setFecha(v);
                if(ususario!=null) {
                    comentario.setUsuario(ususario);
                }
                this.comentarioService.save(comentario);
            }




            if (accion == Constante.OPERACIONES_ABN.ALTA) {

                if (ususario != null) {
                    this.autoActual.setUsuario_seller(ususario);
                    TimeZone timeZone = TimeZone.getTimeZone("Australia/Sydney");
                    Calendar c = Calendar.getInstance(timeZone);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    String creadoPor = ususario.getNombre() + " " + ususario.getApellido() + " " + sdf.format(c.getTime());
                    this.clienteActual.setCreado_por(creadoPor);
                    this.clienteActual.setFecha_creado(c.getTime());
                    if(this.clienteAdicional!=null) {
                        this.clienteAdicional.setCreado_por(creadoPor);
                        this.clienteAdicional.setFecha_creado(c.getTime());
                    }
                }
                this.clienteService.registrarCliente(this.clienteActual);

                this.autoActual.setClienteAuto(this.clienteActual);
                Comentario comentario = new Comentario();
                comentario.setCliente(this.clienteActual);
                comentario.setComentario("The car was sold: "+this.autoActual.getMarca()+" "+this.autoActual.getModelo()+" Stock: "+this.autoActual.getStock_number());
                comentario.setEstado(Constante.ESTADOS_COMENTARIOS.ACTIVO);
                TimeZone timeZone = TimeZone.getTimeZone("Australia/Sydney");
                Calendar c = Calendar.getInstance(timeZone);
                LocalDateTime v=  new java.sql.Timestamp(
                        c.getTime().getTime()).toLocalDateTime();
                comentario.setFecha(v);
                if(ususario!=null) {
                    comentario.setUsuario(ususario);
                }
                this.comentarioService.save(comentario);


                this.autoService.saveAuto(this.autoActual);
            } else {
                this.clienteService.registrarCliente(this.clienteActual);
                if(this.clienteAdicional!=null)
                {
                    this.clienteService.registrarCliente(this.clienteAdicional);
                }
                this.autoService.saveAuto(this.autoActual);
            }
            printFormulario();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelarFormulario() {
            if(pantallaAnterior!=null && pantallaAnterior.trim().length()>0)
            {
                UI.getCurrent().navigate(pantallaAnterior);
            }
            else {
                UI.getCurrent().navigate("SaleView");
            }
    }

    private void cargarCombos() {
        cmbSucursales.setItems(this.sucursalService.buscarTodasActivas());
        cmbSucursales.setItemLabelGenerator(Sucursal::getName);
        cmbSources.setItems(sourceService.buscarTodosActivos());
        cmbSources.setItemLabelGenerator(Source::getName);
        List<String> tipoTiempo = new ArrayList<String>();
        tipoTiempo.add("Year");
        tipoTiempo.add("Month");
        tipoTiempo.add("Day");
        cmbTipoTiempo.setItems(tipoTiempo);
    }


    private void ingresoTextoMovil(String texto) {
        if(texto!=null && texto.trim().length()>0)
        {
            Cliente clienteEnBD = this.clienteService.getClientePorTelefono(texto);
            if(clienteEnBD!=null)
            {
                this.clienteActual= clienteEnBD;
            }
            else
            {
                this.clienteActual=new Cliente();
                this.clienteActual.setMovil(texto);
            }
            binderCliente.readBean(this.clienteActual);
        }
    }

    private void ingresoTextoMovilAdicional(String texto) {
        if(texto!=null && texto.trim().length()>0)
        {
            Cliente clienteEnBD = this.clienteService.getClientePorTelefono(texto);
            if(clienteEnBD!=null)
            {
                this.clienteAdicional = clienteEnBD;
            }
            else
            {
                this.clienteAdicional = new Cliente();
                this.clienteAdicional.setMovil(texto);
            }
            binderClienteAdicional.readBean(this.clienteAdicional);
        }
    }

    private void eliminarAuto()
    {
        dialogoEstaSeguro.open();
    }

    private void eliminarAutoSeguro()
    {
        if(this.autoActual!=null && this.autoActual.getId()>-1)
        {
            this.autoService.eliminarAuto(this.autoActual);
            if(this.clienteActual!=null && this.clienteActual.getId()>-1) {
                Comentario comentario = new Comentario();
                comentario.setCliente(this.clienteActual);
                comentario.setComentario("The car : " + this.autoActual.getMarca() + " " + this.autoActual.getModelo() + " Stock: " + this.autoActual.getStock_number()+" agreement has been canceled");
                comentario.setEstado(Constante.ESTADOS_COMENTARIOS.ACTIVO);
                TimeZone timeZone = TimeZone.getTimeZone("Australia/Sydney");
                Calendar c = Calendar.getInstance(timeZone);
                LocalDateTime v = new java.sql.Timestamp(
                        c.getTime().getTime()).toLocalDateTime();
                comentario.setFecha(v);
                if (ususario != null) {
                    comentario.setUsuario(ususario);
                }
                this.comentarioService.save(comentario);
                this.clienteActual.setSale(0);
                clienteService.registrarCliente(this.clienteActual);
            }
            dialogoEstaSeguro.close();
            mensaje.setText("Agreement deleted successfully");
            notification.open();
            this.cancelarFormulario();
        }
    }

    private void cerraDialogoConfirmacion()
    {
        dialogoEstaSeguro.close();
    }

    private void printFormulario()
    {
        Map<String, Object> params = new HashMap<>();
            params.put("customerName", this.clienteActual.getNombre()+" "+this.clienteActual.getApellido());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String fecha = this.clienteActual.getDOB().format(formatter);
            params.put("dob", fecha);
            params.put("licenseNumber", this.clienteActual.getLicense());
            params.put("email", this.clienteActual.getEmail());
            params.put("direccion", this.clienteActual.getDireccion());
            params.put("suburb", this.clienteActual.getSuburb());
            params.put("state", this.clienteActual.getState());
            params.put("postcode", this.clienteActual.getPostcode());
            params.put("mobile", this.clienteActual.getMovil());
            params.put("homeNumber", this.clienteActual.getHome_phone());

            if(this.clienteAdicional !=null && this.clienteAdicional.getNombre().trim().length()>0)
            {
                params.put("customerNameAdicional", this.clienteAdicional.getNombre()+" "+this.clienteAdicional.getApellido());
                formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String fechaAdicional = this.clienteAdicional.getDOB().format(formatter);
                params.put("dobAdicional", fechaAdicional);
                params.put("licenseNumberAdicional", this.clienteAdicional.getLicense());
                params.put("emailAdicional", this.clienteAdicional.getEmail());
                params.put("direccionAdicional", this.clienteAdicional.getDireccion());
                params.put("suburbAdicional", this.clienteAdicional.getSuburb());
                params.put("stateAdicional", this.clienteAdicional.getState());
                params.put("postcodeAdicional", this.clienteAdicional.getPostcode());
                params.put("mobileAdicional", this.clienteAdicional.getMovil());
                params.put("homeNumberAdicional", this.clienteAdicional.getHome_phone());
            }
            else {
                params.put("customerNameAdicional", "");
                params.put("dobAdicional","");
                params.put("licenseNumberAdicional", "");
                params.put("emailAdicional", "");
                params.put("direccionAdicional", "");
                params.put("suburbAdicional", "");
                params.put("stateAdicional", "");
                params.put("postcodeAdicional", "");
                params.put("mobileAdicional", "");
                params.put("homeNumberAdicional", "");
            }

            params.put("marca", this.autoActual.getMarca());
            params.put("modelo", this.autoActual.getModelo());
            params.put("rego", this.autoActual.getRego());
            params.put("stock_number", this.autoActual.getStock_number());
            params.put("tiempo", this.autoActual.getCantidad_tiempo());
            String concepto = this.autoActual.getConcepto_tiempo();
            if(this.autoActual.getCantidad_tiempo()>1)
            {
                concepto=concepto+"s";
            }
            params.put("concepto_tiempo", concepto);
            String pickup= "";
            if(this.autoActual.getPick_up_date()!=null)
            {
                pickup = this.autoActual.getPick_up_date().format(formatter);
            }
            if(this.autoActual.getPick_up_time()!=null && this.autoActual.getPick_up_time().trim().length()>0)
            {
                pickup=pickup+" "+this.autoActual.getPick_up_time();
            }
            params.put("pickup", pickup);

            if(this.autoActual.getUsuario_seller()!=null)
            {
                params.put("seller", this.autoActual.getUsuario_seller().getNombre()+" "+this.autoActual.getUsuario_seller().getApellido());
            }
            else
            {
                params.put("seller","");
            }

            try
            {
                File f = new File("reportesMios\\form_client.jrxml");
                System.out.println(f.getAbsolutePath());
                String reportPath_base = f.getAbsolutePath().replace("form_client.jrxml", "");
                JasperReport jasperReport = JasperCompileManager.compileReport(f.getAbsolutePath());
                params.put("SUBREPORT_DIR", reportPath_base);

                List<String> arraysDetails = new ArrayList<String>();

                // Get data source

                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params,
                        new JREmptyDataSource());

                // Export the report to a PDF file
                //JasperExportManager.exportReportToPdfFile(jasperPrint, reportPathBase + "conr.pdf");
                // Export the report to a PDF file
                //         JasperExportManager.exportReportToPdfFile(jasperPrint, reportPath.replace("sumary.jrxml", "") + "sumary.pdf");

                JasperExportManager.exportReportToPdfFile(jasperPrint,reportPath_base+"prueba.pdf");
                StreamResource creado = new StreamResource("test.pdf", new InputStreamFactory() {
                    @Override
                    public InputStream createInputStream() {
                        File file = new File(reportPath_base+"prueba.pdf");
                        if(file.exists()==false)
                        {
                            System.out.println("archivo no existe");
                        }
                        try {
                            return new FileInputStream(file);
                        } catch (FileNotFoundException e) {
                            // TODO: handle FileNotFoundException somehow
                            throw new RuntimeException(e);
                        }
                    }
                });
                if(link.isVisible())
                {
                    link.setVisible(false);
                }
                link.setHref(creado);
                link.getElement().setAttribute("download", true);

                link.setVisible(true);
                /*Anchor anchor = new Anchor(new StreamResource("test.pdf", new InputStreamFactory() {
                    @Override
                    public InputStream createInputStream() {
                        File file = new File(reportPath_base+"prueba.pdf");
                        if(file.exists()==false)
                        {
                            System.out.println("archivo no existe");
                        }
                        try {
                            return new FileInputStream(file);
                        } catch (FileNotFoundException e) {
                            // TODO: handle FileNotFoundException somehow
                            throw new RuntimeException(e);
                        }
                    }
                }), "");
            //    anchor.getElement().setAttribute("download", true);
            //    anchor.add(new Button("Download"));
                layout.add(anchor);*/


                //FacesContext.getCurrentInstance().responseComplete();

                System.out.println("Done");
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

    }

    private void descargo()
    {
        this.cancelarFormulario();
    }

    private void agregarDriverAdicional()
    {
        this.clienteAdicional = new Cliente();
        binderClienteAdicional.readBean(clienteAdicional);
        this.dialogoDriverAdiconal.open();
    }

    private void configureGrid()
    {
        grillaClienteAdicional.setSizeFull();
        grillaClienteAdicional.setSizeFull();
        grillaClienteAdicional.removeAllColumns();
        grillaClienteAdicional.addColumn(
                Cliente -> Cliente.getApellido())
                .setHeader("Last name").setResizable(true);
        grillaClienteAdicional.addColumn(
                Cliente-> Cliente.getNombre())
                .setHeader("Name").setResizable(true);
        grillaClienteAdicional.getColumns().forEach(col -> col.setAutoWidth(true));
        grillaClienteAdicional.addItemDoubleClickListener(event ->mostrarClienteAdicional(event.getItem()));
        grillaClienteAdicional.setHeight("300px");
    }

    private void mostrarClienteAdicional(Cliente cli)
    {
        if(cli !=null)
        {
            this.clienteAdicional = cli;
            this.binderClienteAdicional.readBean(this.clienteAdicional);
            this.dialogoDriverAdiconal.open();
        }
    }

    private void aceptarClienteAdicional()
    {
        try
        {
            binderClienteAdicional.writeBean(this.clienteAdicional);
            this.listaDriverAdicional.clear();
            this.listaDriverAdicional.add(this.clienteAdicional);
            this.grillaClienteAdicional.setItems(this.listaDriverAdicional);
            this.clienteAdicional=new Cliente();
            this.dialogoDriverAdiconal.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void cancelarAdicional()
    {
        this.clienteAdicional=new Cliente();
        this.dialogoDriverAdiconal.close();
    }

    private void eliminarAdicional()
    {
        this.dialogoDriverAdiconal.close();
        this.listaDriverAdicional.clear();
        this.grillaClienteAdicional.setItems(this.listaDriverAdicional);
        this.clienteAdicional=new Cliente();
    }



}

