package com.vaadin.tutorial.crm.ui.view.Clientes;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.*;
import com.vaadin.tutorial.crm.backend.entity.*;
import com.vaadin.tutorial.crm.backend.service.*;
import com.vaadin.tutorial.crm.ui.Constante;
import com.vaadin.tutorial.crm.ui.MainLayout;
import com.vaadin.tutorial.crm.ui.view.converterBooleanToInteger;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

@Route(value = "ClientForm", layout = MainLayout.class)
@PageTitle("Client | Certified Autos CRM")
@CssImport(value = "./styles/my.css", themeFor = "vaadin-grid")
public class ClientForm extends HorizontalLayout implements HasUrlParameter<String> {

    TextField nombre = new TextField("Name");
    TextField apellido = new TextField("Last name");
    TextField telefono = new TextField("Phone");
    int accion = -1;
    String pantallaAnterior = "";
    Button botonAddNote = new Button("Add note", event -> {
        agregarNota();
    });
    Span mensaje = new Span();
    Notification notification = new Notification(mensaje);

    Button botonAceptarDialogo = new Button("Submit", event -> {
        aceptarDialogo();
    });
    Button botonCancelarDialogo = new Button("Cancel", event -> {
        cancelarDialogo();
    });
    Button botonLost = new Button("Lost", event -> {
        clientePerdidoDialogo();
    });
    Button botonActivarCliente = new Button("Activate", event -> {
        activarCliente();
    });
    ComentarioService comentarioService;
    ClienteService clienteService;
    SucursalService sucursalService;
    SourceService sourceService;
    MensajeService mensajeService;
    Grid<Comentario> grillaComentarios = new Grid<Comentario>();
    TextArea observacion = new TextArea("Note");
    ComboBox<Sucursal> sucursales = new ComboBox<Sucursal>("Branch");
    ComboBox<Source> sources = new ComboBox<>("Source");
    Cliente clienteActual;
    Long idClienteActual = null;
    Binder<Cliente> binder = new Binder<>(Cliente.class);
    DateTimePicker fechaAppoiment = new DateTimePicker("Date Appoiment");
    DateTimePicker fechaLlamada = new DateTimePicker("Date call");
    TextField txtCreadoPor = new TextField("Created by");
    TextField txtPerdidoPor = new TextField("Lost by");
    Checkbox chkSale = new Checkbox("Sale");
    List<Comentario> comentarios = null;
    UsersService usersService;
    RadioButtonGroup<String> radiohorizontal = new RadioButtonGroup<>();
    Checkbox chkAppoiment = new Checkbox("Appoiment");
    Checkbox chkShowUp = new Checkbox("Show up");
    Checkbox chkTruck = new Checkbox("Truck");
    Checkbox chkRetail = new Checkbox("Retail client");
    FormLayout formularioCliente = new FormLayout();
    TextField txtMotivoLost = new TextField("Reason");
    Dialog dialogoRazon = new Dialog();

    Button botonMensaje = new Button("SMS", event -> abrirMensajesCliente());
    Dialog dialogoSMSs = new Dialog();
    ComboBox<Template> cmbTemplate = new ComboBox<>("Template");
    TextArea txtSMSCustom = new TextArea("Custom text");
    Button botonEnviarSMS = new Button("Send", event -> enviarSMS());
    Button botonCancelarSMS = new Button("Cancel", event -> cancelarSMS());
    Button botonLeido = new Button("Read", event ->leido());
    Grid<Mensaje> grillaMensajes = new Grid<>();
    TemplateService templateService;
    List<Mensaje> listaMensajesCliente = new ArrayList<>();
    Users usuarioConectado=null;
    Calendar c;
    TextArea textAreaComentario = new TextArea("Detail");
    Dialog dialogo = new Dialog();

    private Comentario comentarioActual=null;
    TextField txtUsuario = new TextField("User");
    TextField txtFecha = new TextField("Date");


    Button botonOk = new Button("Ok", event -> aceptoComentarioLargo());
    Button botonCancelar = new Button("Cancel", event -> {
        this.comentarioActual=null;
        dialogo.close();
    });

    Dialog dialogoSMSGrande = new Dialog();
    TextField txtFechaMensajeGrande = new TextField("Date");
    TextArea txtMensajeGrande = new TextArea("Message");
    Button botonOKMensajeGrande = new Button("Ok", event -> cerrarDialogoMensajeGrande());

    EnviarEmail enviarEmail;


    @Override
    public void setParameter(BeforeEvent event,
                             @WildcardParameter String parameter) {
        if (parameter != null && parameter.trim().length() > 0) {
            String[] parametros = parameter.split("/");
            idClienteActual = Long.valueOf(parametros[0]);
            accion = Integer.parseInt(parametros[1]);
            pantallaAnterior = parametros[2];
            botonLost.setVisible(true);
            this.clienteActual = this.clienteService.getClientePorId(idClienteActual);

            if (this.clienteActual == null) {
                //no deberia pasar
                this.clienteActual = new Cliente();
            }
            updateGrilla();
            if (this.clienteActual.getAppoiment() == 1) {
                fechaAppoiment.setEnabled(true);
            } else {
                fechaAppoiment.setEnabled(false);
                fechaAppoiment.setValue(null);
            }
            if (this.clienteActual.getCalltobemade().equalsIgnoreCase("Yes")) {
                fechaLlamada.setEnabled(true);
            } else {
                fechaLlamada.setValue(null);
                fechaLlamada.setEnabled(false);
            }
            if (clienteActual.getEstado() == Constante.ESTADOS_CLIENTES.LOST) {
                botonLost.setVisible(false);
                botonActivarCliente.setVisible(true);
            } else {
                botonLost.setVisible(true);
                botonActivarCliente.setVisible(false);
            }
            if(this.clienteActual!=null && this.clienteActual.getId()!=null)
            {
                this.listaMensajesCliente = this.mensajeService.getListaMensajeCliente(this.clienteActual.getId());
                this.grillaMensajes.setItems(this.listaMensajesCliente);
            }

        } else {
            accion = Constante.OPERACIONES_ABN.ALTA;
            botonLost.setVisible(false);
            botonActivarCliente.setVisible(false);
            fechaAppoiment.setEnabled(false);
            fechaLlamada.setEnabled(false);
            this.clienteActual = new Cliente();
        }
        binder.readBean(this.clienteActual);
    }

    public ClientForm(UsersService usersService,
                      ComentarioService comentarioService,
                      ClienteService clienteService,
                      SucursalService sucursalService,
                      SourceService sourceService,
                      TemplateService templateService,
                      MensajeService mensajeService,
                      EnviarEmail enviarEmail) {
        this.usersService = usersService;
        this.comentarioService = comentarioService;
        this.clienteService = clienteService;
        this.sucursalService = sucursalService;
        this.sourceService = sourceService;
        this.templateService = templateService;
        this.mensajeService = mensajeService;
        this.enviarEmail = enviarEmail;
        this.cargarCombos();
        notification.setDuration(3000);
        notification.setPosition(Notification.Position.MIDDLE);








        fechaAppoiment.setLabel("Date Appoiment");
        fechaAppoiment.setStep(Duration.ofMinutes(15));
        fechaLlamada.setStep(Duration.ofMinutes(30));
        this.configurarGrilla();
        addClassName("contact-form");
        binder.forField(nombre)
                .asRequired("You must enter the name of the client")
                .bind(Cliente::getNombre, Cliente::setNombre);

        binder.forField(apellido)
                .bind(Cliente::getApellido, Cliente::setApellido);

        binder.forField(telefono)
                .asRequired("You must enter a phone number")
                .bind(Cliente::getMovil, Cliente::setMovil);



        binder.forField(chkTruck)
                .bind(Cliente::isIs_truck, Cliente::setIs_truck);

        binder.forField(chkRetail)
                .bind(Cliente::isIs_retail, Cliente::setIs_retail);


        binder.bind(sucursales, Cliente::getSucursal, Cliente::setSucursal);
        binder.bind(sources, Cliente::getSource, Cliente::setSource);

        binder.forField(fechaAppoiment)
                .bind(Cliente::getFecha_appoiment, Cliente::setFecha_appoiment);

        binder.forField(fechaLlamada)
                .bind(Cliente::getFecha_llamada, Cliente::setFecha_llamada);

        txtCreadoPor.setEnabled(false);
        txtPerdidoPor.setEnabled(false);

        observacion.setMaxHeight("50%");


        radiohorizontal.setItems("Yes", "No");
        radiohorizontal.setLabel("Call to be made");
        radiohorizontal.setValue("No");
        radiohorizontal.addValueChangeListener(event -> {
            seleccionaronUnaOpcion(event.getValue());
        });


        binder.forField(observacion)
                .bind(Cliente::getObservacion, Cliente::setObservacion);

        binder.forField(radiohorizontal)
                .asRequired("Please select one option")
                .bind(Cliente::getCalltobemade, Cliente::setCalltobemade);

        binder.forField(chkAppoiment)
                .withConverter(new converterBooleanToInteger())
                .bind(Cliente::getAppoiment, Cliente::setAppoiment);

        binder.forField(chkShowUp)
                .withConverter(new converterBooleanToInteger())
                .bind(Cliente::getShow_up, Cliente::setShow_up);

        binder.forField(chkSale)
                .withConverter(new converterBooleanToInteger())
                .bind(Cliente::getSale, Cliente::setSale);

        binder.forField(fechaAppoiment)
                .bind(Cliente::getFecha_appoiment, Cliente::setFecha_appoiment);

        binder.forField(txtCreadoPor)
                .bind(Cliente::getCreado_por, Cliente::setCreado_por);

        binder.forField(txtPerdidoPor)
                .bind(Cliente::getPerdido_por, Cliente::setPerdido_por);



        formularioCliente.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 3));


        formularioCliente.setColspan(observacion, 2);
        formularioCliente.setColspan(txtCreadoPor, 2);
        formularioCliente.setColspan(txtPerdidoPor, 2);


        chkAppoiment.addValueChangeListener(event -> {
            fechaAppoiment.setEnabled(chkAppoiment.getValue());
            if (!chkAppoiment.getValue()) {
                fechaAppoiment.setValue(null);
            }
        });


        HorizontalLayout botonaddnote = new HorizontalLayout();

        botonaddnote.add(botonAddNote);
        formularioCliente.setColspan(botonaddnote, 2);



        formularioCliente.add(nombre, chkRetail,
                telefono, chkTruck,
                sucursales, sources, radiohorizontal,fechaLlamada, chkAppoiment,fechaAppoiment, chkShowUp, chkSale,
                observacion,  botonaddnote,
                txtCreadoPor, txtPerdidoPor);


        /*formularioCliente.add(nombre, apellido, radiohorizontal,
                telefono, emailField, fechaLlamada,
                sucursales, sources, chkAppoiment,
                observacion, fechaAppoiment, botonaddnote, chkShowUp,
                txtCreadoPor, chkSale, txtPerdidoPor);*/


        formularioCliente.addClassName("contact-grid");
        VerticalLayout nose = new VerticalLayout();


        nose.add(formularioCliente, createButtonsLayout());
        nose.setSizeFull();
        nose.addClassName("contact-form");
        //nose.setMinWidth("600px");
        /*telefono.setMaxWidth("150px");
        emailField.setMaxWidth("220px");
        fechaLlamada.setMinWidth("200px");
        sucursales.setMaxWidth("180px");
        nombre.setMaxWidth("180px");
        apellido.setMaxWidth("180px");
        observacion.setMaxWidth("390px");
        fechaAppoiment.setMinWidth("200px");
        txtCreadoPor.setMaxWidth("400px");
        txtPerdidoPor.setMaxWidth("400px");*/
        formularioCliente.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 2));



        VerticalLayout botonGrilla = new VerticalLayout();
        botonGrilla.add(botonMensaje, grillaComentarios);
        botonGrilla.setClassName("contact-grid");
        add(nose, botonGrilla);
        setSizeFull();
        addClassName("content");


        Button botonConfirmar = new Button("Submmit", event -> clientePerdido());
        Button botonCancel = new Button("Cancel", event -> {
            dialogoRazon.close();
        });
        HorizontalLayout botones = new HorizontalLayout();
        botones.add(botonConfirmar, botonCancel);
        dialogoRazon.add(txtMotivoLost, botones);
        dialogoRazon.setWidth("300px");
        txtMotivoLost.setWidth("250px");


        GridContextMenu<Comentario> contextMenu = grillaComentarios.addContextMenu();
        contextMenu.addItem("Delete", e -> {
            e.getItem().ifPresent(Comentario -> {
                eliminarComentario(Comentario);
            });
        });


        HorizontalLayout hBotonesMensaje = new HorizontalLayout();
        hBotonesMensaje.add(botonEnviarSMS,botonCancelarSMS, botonLeido);
        this.txtSMSCustom.setVisible(false);



        FormLayout formulario =new FormLayout();
        formulario.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1));
        formulario.add(cmbTemplate,txtSMSCustom);
        cmbTemplate.setMaxWidth("500px");
        txtSMSCustom.setMaxWidth("600px");
        formulario.setMaxWidth("800px");

        VerticalLayout vFormulario = new VerticalLayout();
        vFormulario.add(formulario,hBotonesMensaje,grillaMensajes);
        vFormulario.setSizeFull();


        this.dialogoSMSs.add(vFormulario);
        this.dialogoSMSs.setSizeFull();
        this.cmbTemplate.addValueChangeListener(event -> this.eligioTemplate());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            usuarioConectado =this.usersService.buscarPorNombreUsuario(currentUserName);
        }

        TimeZone timeZone = TimeZone.getTimeZone("Australia/Sydney");
        c = Calendar.getInstance(timeZone);

        HorizontalLayout horizontal = new HorizontalLayout();
        horizontal.add(txtUsuario, txtFecha);

        VerticalLayout vertical = new VerticalLayout();
        HorizontalLayout hBotonesAceptar = new HorizontalLayout();
        hBotonesAceptar.add(botonOk,botonCancelar);
        vertical.add(textAreaComentario, hBotonesAceptar);
        vertical.setHeight("80%");
        vertical.setWidth("100%");
        dialogo.add(horizontal, vertical);
        dialogo.setWidth("60%");
        dialogo.setHeight("70%");


        HorizontalLayout hBotonOKMensajeGrande = new HorizontalLayout();
        hBotonOKMensajeGrande.add(botonOKMensajeGrande);
        hBotonOKMensajeGrande.setAlignItems(Alignment.CENTER);

        VerticalLayout verticalSMSGrande = new VerticalLayout();
        txtFechaMensajeGrande.setReadOnly(true);
        txtMensajeGrande.setHeight("300px");
        txtMensajeGrande.setWidth("500px");
        txtMensajeGrande.setReadOnly(true);
        verticalSMSGrande.add(txtFechaMensajeGrande, txtMensajeGrande, hBotonOKMensajeGrande);

        dialogoSMSGrande.add(verticalSMSGrande);
        dialogoSMSGrande.setModal(true);
        dialogoSMSGrande.setCloseOnEsc(true);
    }

    public Cliente getClienteActual() {

        try {
            binder.writeBean(this.clienteActual);
        } catch (ValidationException e) {
            e.printStackTrace();
            return null;
        }
        return this.clienteActual;
    }

    public void setClienteActual(Cliente clienteActual, List<Sucursal> listaSucursales, List<Source> listaSources) {
        if (clienteActual == null) {
            this.clienteActual = new Cliente();
        } else {
            this.clienteActual = clienteActual;
        }
        sucursales.setItems(listaSucursales);
        sucursales.setItemLabelGenerator(Sucursal::getName);
        if (this.clienteActual.getAppoiment() == 1) {
            fechaAppoiment.setVisible(true);
        } else {
            fechaAppoiment.setVisible(false);
        }
        sources.setItems(listaSources);
        sources.setItemLabelGenerator(Source::getName);
        binder.readBean(clienteActual);

    }

    private void seleccionaronUnaOpcion(String valor) {
        if (valor != null && valor.trim().length() > 0) {
            this.clienteActual.setCalltobemade(valor);
            if (valor.equalsIgnoreCase("Yes")) {
                fechaLlamada.setEnabled(true);
            } else {
                fechaLlamada.setEnabled(false);
                fechaLlamada.setValue(null);
            }
        }

    }

    private void agregarNota() {
        if (observacion.getValue() != null && observacion.getValue().trim().length() > 0) {
            Comentario comentario = new Comentario();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication instanceof AnonymousAuthenticationToken)) {
                String currentUserName = authentication.getName();
                Users ususario = this.usersService.buscarPorNombreUsuario(currentUserName);
                if (ususario != null) {
                    comentario.setUsuario(ususario);
                }
            }
            comentario.setComentario(observacion.getValue());
            TimeZone timeZone = TimeZone.getTimeZone("Australia/Sydney");
            Calendar c = Calendar.getInstance(timeZone);
            LocalDateTime v = new java.sql.Timestamp(
                    c.getTime().getTime()).toLocalDateTime();
            comentario.setFecha(v);
            comentario.setEstado(Constante.ESTADOS_COMENTARIOS.ACTIVO);
            if (comentarios == null) {
                comentarios = new ArrayList<Comentario>();
            }
            comentarios.add(comentario);

            grillaComentarios.setItems(comentarios);
            observacion.setValue("");
        }
    }

    private void configurarGrilla() {
        grillaComentarios.addClassName("contact-form");

        grillaComentarios.removeAllColumns();
        grillaComentarios.addColumn(
                Comentario -> (this.getFeche(Comentario.getFecha())+" "+this.getNombreUsuarioComentario(Comentario)))
                .setHeader("Date").setResizable(true);
        grillaComentarios.addColumn(
                Comentario -> Comentario.getComentario())
                .setHeader("Message").setResizable(true);

        grillaComentarios.setHeightByRows(true);
        grillaComentarios.setWidth("100%");
        grillaComentarios.setHeight("100%");
        grillaComentarios.addItemDoubleClickListener(event -> {
            mostrarComentariosEnAlgoMasGrande(event.getItem());
        });



        grillaMensajes.removeAllColumns();
        grillaMensajes.addColumn(
                Mensaje -> this.getFeche(Mensaje.getFecha_hora()))
                .setHeader("Date").setResizable(true);
        grillaMensajes.addColumn(
                Mensaje -> Mensaje.getMensaje())
                .setHeader("Text").setResizable(true);
        grillaMensajes.addColumn(
                Mensaje -> this.getTextoSentido(Mensaje))
                .setHeader("Status").setResizable(true);

        grillaMensajes.setClassNameGenerator(Mensaje -> {
            if(Mensaje.getSentido()== com.vaadin.tutorial.crm.backend.entity.Mensaje.ENTRADA)
            {
                return "red";
            }
            else if(Mensaje.getSentido()==com.vaadin.tutorial.crm.backend.entity.Mensaje.SALIDA)
            {
                return "green";
            }
            else
            {
                return "";
            }
        });

        grillaMensajes.addItemDoubleClickListener(event -> abrirMensajeGrande(event.getItem()));

    }

    private void abrirMensajeGrande(Mensaje mensaje)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        txtFechaMensajeGrande.setValue(mensaje.getFecha_hora().format(dtf));
        txtMensajeGrande.setValue(mensaje.getMensaje());
        dialogoSMSGrande.open();
    }

    private String getTextoSentido(Mensaje mensaje)
    {
        if(mensaje.getSentido()==Mensaje.ENTRADA)
        {
            return "Received";
        }
        else if(mensaje.getSentido()==Mensaje.SALIDA)
        {
            return "Sent";
        }
        else
        {
            return "";
        }
    }

    private String getNombreUsuarioComentario(Comentario comentario) {
        String resultado = "";
        if (comentario != null && comentario.getUsuario() != null) {
            resultado = comentario.getUsuario().getNombre() + " " + comentario.getUsuario().getApellido();
        }
        return resultado;
    }


    private void updateGrilla() {
        this.comentarios = this.comentarioService.getListaComentariosCliente(this.clienteActual.getId());
        if (this.comentarios == null) {
            this.comentarios = new ArrayList<Comentario>();
        }
        grillaComentarios.setItems(this.comentarios);
    }

    private HorizontalLayout createButtonsLayout() {
        botonAceptarDialogo.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        botonCancelarDialogo.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        botonLost.addThemeVariants(ButtonVariant.LUMO_ERROR);
        botonActivarCliente.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        return new HorizontalLayout(botonAceptarDialogo, botonCancelarDialogo, botonLost, botonActivarCliente);
    }

    private void aceptarDialogo() {
        try {
            binder.writeBean(this.clienteActual);

/*
            ZoneId zona = ZoneId.of("Australia/Sydney");
            ZonedDateTime zonedDateTime = ZonedDateTime.of(this.clienteActual.getFecha_llamada(),zona);
            this.clienteActual.setFecha_llamada(zonedDateTime.toLocalDateTime());*/


            if (this.clienteActual != null) {

                Cliente clienteBD = clienteService.getClientePorTelefono(this.clienteActual.getMovil());
                if (clienteBD != null) {
                    if (accion == Constante.OPERACIONES_ABN.ALTA) {
                        mensaje.setText("There is another cliente [" + clienteBD.getId() + "] with phone number entered ");
                        System.out.println("There is another cliente bd: " + clienteBD.getId() + " alta with phone number entered " + this.clienteActual.getMovil());
                        notification.open();
                        return;
                    }
                    if (accion == Constante.OPERACIONES_ABN.MODIFICACION && !clienteBD.getId().equals(clienteActual.getId())) {
                        mensaje.setText("There is another cliente [" + clienteBD.getId() + "] with phone number entered");
                        System.out.println("There is another cliente bd " + clienteBD.getId() + " actual: " + this.clienteActual.getId() + " with phone number entered" + this.clienteActual.getMovil());
                        notification.open();
                        return;
                    }
                }
                if (this.clienteActual.getAppoiment() == 1 && this.clienteActual.getFecha_appoiment() == null) {
                    mensaje.setText("You must enter a appoiment date");
                    notification.open();
                    return;
                }

                if (accion == Constante.OPERACIONES_ABN.ALTA) {
                    this.clienteActual.setEstado(Constante.ESTADOS_CLIENTES.ACTIVO);
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (!(authentication instanceof AnonymousAuthenticationToken)) {
                        String currentUserName = authentication.getName();
                        Users ususario = this.usersService.buscarPorNombreUsuario(currentUserName);
                        if (ususario != null) {
                            TimeZone timeZone = TimeZone.getTimeZone("Australia/Sydney");
                            Calendar c = Calendar.getInstance(timeZone);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            String creadoPor = ususario.getNombre() + " " + ususario.getApellido() + " " + sdf.format(c.getTime());
                            this.clienteActual.setCreado_por(creadoPor);
                            this.clienteActual.setFecha_creado(c.getTime());
                        }
                    }
                }
                String mensajeSms = "";
                if (this.clienteActual.getSucursal() != null) {
                    if (this.accion == Constante.OPERACIONES_ABN.ALTA && this.clienteActual.getCalltobemade().equalsIgnoreCase("yes") && this.clienteActual.getAppoiment() == 1) {
                        mensajeSms = "New client (Mobile:"+this.clienteActual.getMovil()+") with call to be made and appoiment";
                    } else if (this.accion == Constante.OPERACIONES_ABN.ALTA && this.clienteActual.getCalltobemade().equalsIgnoreCase("yes") && this.clienteActual.getFecha_llamada()==null) {
                        mensajeSms = "New client (Mobile:"+this.clienteActual.getMovil()+") with call to be made";
                    } else if (this.accion == Constante.OPERACIONES_ABN.ALTA && this.clienteActual.getAppoiment() == 1) {
                        mensajeSms = "New client (Mobile:"+this.clienteActual.getMovil()+") with appoiment";
                    } else if (clienteBD != null && clienteBD.getCalltobemade().equalsIgnoreCase("no") && this.clienteActual.getCalltobemade().equalsIgnoreCase("yes")  && this.clienteActual.getFecha_llamada()==null) {
                        mensajeSms = "New client (Mobile:"+this.clienteActual.getMovil()+") with call to be made";
                    } else if (clienteBD != null && clienteBD.getAppoiment() == 0 && this.clienteActual.getAppoiment() == 1) {
                        mensajeSms = "New client (Mobile:"+this.clienteActual.getMovil()+") with appoiment";
                    }
                    if (mensajeSms.trim().length() > 0) {
                        List<Users> usuariosANotificar = usersService.buscarUsuariosPorSucursal(this.clienteActual.getSucursal().getId());
                        //SenderSms sender = new SenderSms();
                        SenderSMSTelstra sender = new SenderSMSTelstra();

                        for (int j = 0; j < usuariosANotificar.size(); j++) {
                            Users usuario = usuariosANotificar.get(j);
                            if (usuario != null && usuario.getMovil() != null && usuario.getMovil().trim().length()>0) {
                                //enviarEmail.sendEmailTool(mensajeSms, usuario.getEmail(), mensajeSms);
                                sender.enviarSMSTelstra(usuario.getMovil(),mensajeSms);
                                //sender.enviarMensaje(mensajeSms, usuario.getMovil());
                            }
                        }
                    }
                }
                if (chkSale.getValue()) {
                    this.clienteActual.setShow_up(1);
                }

                if (this.clienteActual.getObservacion() != null && this.observacion.getValue() != null && this.observacion.getValue().trim().length() > 0) {
                    agregarNota();
                    this.clienteActual.setObservacion("");
                }


                clienteService.registrarCliente(this.clienteActual);

                if (this.comentarios != null) {
                    if (this.clienteActual != null && this.clienteActual.getId() > -1) {
                        for (int y = 0; y < this.comentarios.size(); y++) {
                            Comentario comentarioAlta = this.comentarios.get(y);
                            comentarioAlta.setCliente(this.clienteActual);
                            this.comentarioService.save(comentarioAlta);
                        }
                    }
                }

                for(int k=0;k<this.listaMensajesCliente.size();k++)
                {
                    Mensaje mensajeGrabar = this.listaMensajesCliente.get(k);
                    if(mensajeGrabar.getId()==null)
                    {
                        mensajeGrabar.setCliente(this.clienteActual);
                        this.mensajeService.saveMensaje(mensajeGrabar);
                    }
                }


                mensaje.setText("Successfully registered customer");
                notification.open();
                if (pantallaAnterior != null && pantallaAnterior.trim().length() > 0) {
                    UI.getCurrent().navigate(pantallaAnterior);
                } else {
                    UI.getCurrent().navigate("ClienteView");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void cancelarDialogo() {
        if (pantallaAnterior != null && pantallaAnterior.trim().length() > 0) {
            UI.getCurrent().navigate(pantallaAnterior);
        } else {
            UI.getCurrent().navigate("ClienteView");
        }
    }

    public void clientePerdidoDialogo() {
        dialogoRazon.open();
    }

    public void clientePerdido() {
        if (txtMotivoLost.getValue() == null || txtMotivoLost.getValue().trim().length() == 0) {
            mensaje.setText("You have to enter the reason for which to put the client as lost.");
            notification.open();
            return;
        }
        if (this.clienteActual != null) {
            this.observacion.setValue(txtMotivoLost.getValue());
            if (this.observacion.getValue() != null && this.observacion.getValue().trim().length() > 0) {
                agregarNota();
                this.clienteActual.setObservacion("");
            }
            Users ususarioLogin = null;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication instanceof AnonymousAuthenticationToken)) {
                String currentUserName = authentication.getName();
                ususarioLogin = this.usersService.buscarPorNombreUsuario(currentUserName);
            }

            TimeZone timeZone = TimeZone.getTimeZone("Australia/Sydney");
            Calendar c = Calendar.getInstance(timeZone);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String perdidoPor = ususarioLogin.getNombre() + " " + ususarioLogin.getApellido() + " " + sdf.format(c.getTime());
            this.clienteActual.setPerdido_por(perdidoPor);
            this.clienteActual.setEstado(Constante.ESTADOS_CLIENTES.LOST);
            clienteService.registrarCliente(this.clienteActual);

            if (this.comentarios != null) {
                if (this.clienteActual != null && this.clienteActual.getId() > -1) {
                    for (int y = 0; y < this.comentarios.size(); y++) {
                        Comentario comentarioAlta = this.comentarios.get(y);
                        comentarioAlta.setCliente(this.clienteActual);
                        this.comentarioService.save(comentarioAlta);
                    }
                }
            }

            dialogoRazon.close();
            mensaje.setText("Successfully updated customer");
            notification.open();
            if (pantallaAnterior != null && pantallaAnterior.trim().length() > 0) {
                UI.getCurrent().navigate(pantallaAnterior);
            } else {
                UI.getCurrent().navigate("ClienteView");
            }
        }
    }

    private String getFeche(LocalDateTime local) {
        String resultado = "";
        if (local != null) {
            DateTimeFormatter drf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            resultado = drf.format(local);
        }
        return resultado;
    }

    private void cargarCombos() {
        sucursales.setItems(this.sucursalService.buscarTodasActivas());
        sucursales.setItemLabelGenerator(Sucursal::getName);
        sources.setItems(sourceService.buscarTodosActivos());
        sources.setItemLabelGenerator(Source::getName);
        Template temp = new Template();
        temp.setNombre("Custom");
        List<Template> listaTemplate = new ArrayList<>();
        listaTemplate.add(temp);
        listaTemplate.addAll(this.templateService.getListaTemplateActivos());
        cmbTemplate.setItems(listaTemplate);
        cmbTemplate.setItemLabelGenerator(Template::getNombre);
    }

    private void eliminarComentario(Comentario comentario) {
        if (comentario != null) {
            comentarioService.eliminarComentario(comentario);
            this.updateGrilla();
        }
    }

    private void mostrarComentariosEnAlgoMasGrande(Comentario coment) {
        if (coment != null) {
            this.comentarioActual = coment;

            txtUsuario.setValue(coment.getUsuario().getNombre() + " " + coment.getUsuario().getApellido());

            txtFecha.setReadOnly(true);
            txtUsuario.setReadOnly(true);
            DateTimeFormatter drf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            txtFecha.setValue(coment.getFecha().format(drf));
            dialogo.setModal(true);
            dialogo.setCloseOnEsc(true);
            textAreaComentario.addKeyDownListener(Key.ESCAPE,event ->{dialogo.close();});




            textAreaComentario.setWidth("80%");
            textAreaComentario.setHeight("80%");
            textAreaComentario.setValue(coment.getComentario());

            dialogo.open();

        }

    }

    private void aceptoComentarioLargo()
    {
        if(this.comentarioActual!=null)
        {
            if(!this.comentarioActual.getComentario().trim().equalsIgnoreCase(textAreaComentario.getValue().trim())) {
                this.comentarioActual.setComentario(textAreaComentario.getValue());
                this.comentarioActual.setUsuario(usuarioConectado);
                TimeZone timeZone = TimeZone.getTimeZone("Australia/Sydney");
                Calendar c = Calendar.getInstance(timeZone);
                LocalDateTime v = new java.sql.Timestamp(
                        c.getTime().getTime()).toLocalDateTime();
                this.comentarioActual.setFecha(v);
                this.comentarios.remove(this.comentarioActual);
                this.comentarios.add(0, this.comentarioActual);
                this.grillaComentarios.setItems(this.comentarios);
            }
            this.comentarioActual = null;
            this.dialogo.close();
        }
    }

    private void activarCliente() {
        try {
            binder.writeBean(this.clienteActual);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        if (this.clienteActual != null) {
            Cliente clienteBD = clienteService.getClientePorTelefono(this.clienteActual.getMovil());
            this.clienteActual.setEstado(Constante.ESTADOS_CLIENTES.ACTIVO);
            this.clienteActual.setPerdido_por("");
            if (this.observacion.getValue() != null && this.observacion.getValue().trim().length() > 0) {
                agregarNota();
                this.clienteActual.setObservacion("");
            }
            clienteService.registrarCliente(this.clienteActual);
            if (this.comentarios != null) {
                if (this.clienteActual != null && this.clienteActual.getId() > -1) {
                    for (int y = 0; y < this.comentarios.size(); y++) {
                        Comentario comentarioAlta = this.comentarios.get(y);
                        comentarioAlta.setCliente(this.clienteActual);
                        this.comentarioService.save(comentarioAlta);
                    }
                }
            }
            String mensajeSms = "";
            if (this.clienteActual.getSucursal() != null) {
                if (this.clienteActual.getCalltobemade().equalsIgnoreCase("yes") && this.clienteActual.getAppoiment() == 1) {
                    mensajeSms = "New client (Mobile:"+this.clienteActual.getMovil()+") with call to be made and appoiment";
                } else if (this.clienteActual.getCalltobemade().equalsIgnoreCase("yes")) {
                    mensajeSms = "New client (Mobile:"+this.clienteActual.getMovil()+") with call to be made";

                } else if (this.clienteActual.getAppoiment() == 1) {
                    mensajeSms = "New client (Mobile:"+this.clienteActual.getMovil()+") with appoiment";
                } else if (clienteBD != null && !clienteBD.getCalltobemade().equalsIgnoreCase("yes") && this.clienteActual.getCalltobemade().equalsIgnoreCase("yes")) {
                    mensajeSms = "New client (Mobile:"+this.clienteActual.getMovil()+") with call to be made";
                } else if (clienteBD != null && clienteBD.getAppoiment() == 0 && this.clienteActual.getAppoiment() == 1) {
                    mensajeSms = "New client (Mobile:"+this.clienteActual.getMovil()+") with appoiment";
                }
                if (mensajeSms.trim().length() > 0) {
                    List<Users> usuariosANotificar = usersService.buscarUsuariosPorSucursal(this.clienteActual.getSucursal().getId());
                    //SenderSms sender = new SenderSms();
                    SenderSMSTelstra sender = new SenderSMSTelstra();
                    for (int j = 0; j < usuariosANotificar.size(); j++) {
                        Users usuario = usuariosANotificar.get(j);
                        if (usuario != null && usuario.getMovil() != null) {
                            //enviarEmail.sendEmailTool(mensajeSms, usuario.getEmail(), mensajeSms);
                            sender.enviarSMSTelstra(usuario.getMovil(),mensajeSms);
                            //sender.enviarMensaje(mensajeSms, usuario.getMovil());
                        }
                    }
                }
            }
            mensaje.setText("Successfully updated customer");
            notification.open();
            if (pantallaAnterior != null && pantallaAnterior.trim().length() > 0) {
                UI.getCurrent().navigate(pantallaAnterior);
            } else {
                UI.getCurrent().navigate("ClienteView");
            }
        }
    }

    private void abrirMensajesCliente() {
        try {
            binder.writeBean(this.clienteActual);
            this.dialogoSMSs.setModal(true);
            this.dialogoSMSs.setCloseOnEsc(true);
            this.dialogoSMSs.open();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void enviarSMS() {
        try {
            binder.writeBean(this.clienteActual);
            String mensaje = null;
            String numero = null;
            if (this.clienteActual == null || this.clienteActual.getMovil() == null || this.clienteActual.getMovil().trim().length() == 0) {
                this.mensaje.setText("You have to enter the phone number to send the message");
                this.notification.open();
                return;
            } else {
                numero = this.clienteActual.getMovil();
                if(this.clienteActual.getMovil().startsWith("0"))
                {
                    numero= numero.substring(1,numero.length());
                }
                numero = "+61"+numero;
            }
            if (cmbTemplate.getValue() != null && cmbTemplate.getValue().getId() == null) {
                //es custom
                if (txtSMSCustom.getValue() == null || txtSMSCustom.getValue().trim().length() == 0) {
                    this.mensaje.setText("You have to enter a text to send");
                    this.notification.open();
                    return;
                }
                mensaje = txtSMSCustom.getValue().trim();
            } else {
                mensaje = cmbTemplate.getValue().getMensaje();
            }
            SenderSMSTelstra senderSMSTelstra = new SenderSMSTelstra();
            String repuesta =senderSMSTelstra.enviarSMSTelstra(numero, mensaje);
            if(repuesta.trim().length()==0) {

                // ver de manejar los numeros que estan mal formato
                Mensaje mensajeNuevo = new Mensaje();
                mensajeNuevo.setMensaje(mensaje);
                mensajeNuevo.setNumero_destino(numero);
                mensajeNuevo.setUsuario(this.usuarioConectado);
                mensajeNuevo.setSentido(Mensaje.SALIDA);
                LocalDateTime ahora = c.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                mensajeNuevo.setFecha_hora(ahora);
                if (this.clienteActual != null && this.clienteActual.getId() != null) {
                    mensajeNuevo.setCliente(this.clienteActual);
                    this.mensajeService.saveMensaje(mensajeNuevo);
                }
                this.listaMensajesCliente.add(0, mensajeNuevo);
                this.grillaMensajes.setItems(this.listaMensajesCliente);
                this.txtSMSCustom.setValue("");
                this.cmbTemplate.setValue(null);
                this.txtSMSCustom.setVisible(false);
                this.mensaje.setText("Message sent successfully");
            }
            else
            {
                this.mensaje.setText(repuesta);
            }
            this.notification.open();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


    }

    private void cancelarSMS() {
        this.dialogoSMSs.close();
    }

    private void eligioTemplate()
    {
        if(this.cmbTemplate.getValue()!=null)
        {
            if(this.cmbTemplate.getValue().getId()==null)
            {
                txtSMSCustom.setVisible(true);
                return;
            }
            else
            {
                txtSMSCustom.setVisible(false);
            }
        }
        txtSMSCustom.setVisible(false);
    }

    private void leido()
    {
        Mensaje mensajeNuevo = new Mensaje();
        mensajeNuevo.setMensaje("Read");
        mensajeNuevo.setUsuario(this.usuarioConectado);
        mensajeNuevo.setSentido(-1);
        LocalDateTime ahora = c.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        mensajeNuevo.setFecha_hora(ahora);
        if(this.clienteActual!=null && this.clienteActual.getId()!=null)
        {
            mensajeNuevo.setCliente(this.clienteActual);
            this.mensajeService.saveMensaje(mensajeNuevo);
        }
        this.listaMensajesCliente.add(0,mensajeNuevo);
        this.grillaMensajes.setItems(this.listaMensajesCliente);
    }

    private void cerrarDialogoMensajeGrande()
    {
        dialogoSMSGrande.close();
    }

}
