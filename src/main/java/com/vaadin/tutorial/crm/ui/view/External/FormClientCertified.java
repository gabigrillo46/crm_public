package com.vaadin.tutorial.crm.ui.view.External;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.backend.entity.Cliente;
import com.vaadin.tutorial.crm.backend.entity.Comentario;
import com.vaadin.tutorial.crm.backend.entity.Sucursal;
import com.vaadin.tutorial.crm.backend.entity.Users;
import com.vaadin.tutorial.crm.backend.service.*;
import com.vaadin.tutorial.crm.ui.Constante;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

@Route("FormClientCertified")
public class FormClientCertified extends VerticalLayout {


    Span mensaje = new Span();
    Notification notification = new Notification(mensaje);

    TextField txtMovil = new TextField("Mobile:");

    TextField txtNombre = new TextField("Name: ");
    TextArea txtNote = new TextArea("Enquiry");
    TextField txtVerificationCode = new TextField("Verification code");
    FormLayout formularioCliente = new FormLayout();
    Button botonSubmit = new Button("Submit", event -> apretoSubmit());
    H2 mensajeCabecera = new H2("Please fill out the following form and we will contact you shortly.");
    H2 mensajeExito = new H2("The data has been saved, we will call you shortly.");
    private Button botonRegresar = new Button("Back to website");

    private HorizontalLayout horizontalLayout = new HorizontalLayout();
    String codigoVerificacion = "";
    private boolean codigoEnviado = false;
    ClienteService clienteService;
    SucursalService sucursalService;
    UsersService usersService;
    ComentarioService comentarioService;
    Anchor anchor = new Anchor("http://certifiedautos.com.au/", "Go back to website...");
    Image logo = new Image("http://certifiedsystem.com.au/resources/imagenes/logo.png","Certified Autos");



    public FormClientCertified(ClienteService clienteService, SucursalService sucursalService, UsersService usersService, ComentarioService comentarioService) {
        txtMovil.setHelperText("We will send you a code to verify your mobile, please enter a valid number format (04952...)");
        txtVerificationCode.setHelperText("Please enter the code that has been sent via SMS");

        txtMovil.setMinWidth("200px");
        formularioCliente.add(txtMovil);
        txtNombre.setMinWidth("200px");
        formularioCliente.add(txtNombre);
        txtVerificationCode.setMinWidth("200px");
        formularioCliente.add(txtVerificationCode);
        formularioCliente.setColspan(txtNote, 3);
        formularioCliente.add(txtNote);
        botonSubmit.setWidth("Auto");
        formularioCliente.add(botonSubmit);
        formularioCliente.setMaxWidth("900px");
        formularioCliente.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));
        horizontalLayout.add(formularioCliente);

        this.add(logo, mensajeCabecera, horizontalLayout, botonSubmit);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setHorizontalComponentAlignment(Alignment.CENTER);
        notification.setDuration(2000);
        notification.setPosition(Notification.Position.MIDDLE);

        mensajeExito.setVisible(false);
        botonRegresar.setVisible(false);
        anchor.setVisible(false);
        this.add(mensajeExito, anchor);

        txtMovil.addBlurListener(event -> ingresoMovil());

        this.clienteService = clienteService;
        this.sucursalService = sucursalService;
        this.usersService = usersService;
        this.comentarioService = comentarioService;

    }

    public void ingresoMovil() {
        if(codigoEnviado)
        {
            return;
        }
        if (txtMovil.getValue() == null) {
            System.out.println("No ingreso nada en movil, es null");
            return;
        }
        if (txtMovil.getValue() != null && txtMovil.getValue().trim().length() < 1) {
            System.out.println("No es null pero no ingreso nada");
            return;
        }
        if (txtMovil.getValue() != null && !txtMovil.getValue().startsWith("0")) {
            System.out.println("numero de movil no empieza con 0");
            return;
        }
        if (txtMovil.getValue() != null && txtMovil.getValue().trim().length() != 10) {
            System.out.println("Los digitos del numero de movil es distinto de 10");
            return;
        }
        String numeroStr = txtMovil.getValue();
        for (int a = 0; a < numeroStr.length(); a++) {
            String caracter = numeroStr.substring(a, a + 1);
            try {
                Integer.valueOf(caracter);
            } catch (Exception e) {
                System.out.println("El numero de movil no es un numero");
                return;
            }
        }
        if (txtMovil.getValue() != null && txtMovil.getValue().trim().length() > 0) {
            String telefono = txtMovil.getValue().trim();
            telefono = telefono.substring(1, telefono.length());
            telefono = "+61" + telefono;
            String codigo = "";

            Random r = new Random();
            while (codigo.trim().length() < 5) {
                codigo += "" + r.nextInt(100);
            }
            codigoVerificacion = codigo;
            SenderSMSTelstra senderSMSTelstra = new SenderSMSTelstra();
            String repuesta = senderSMSTelstra.enviarSMSTelstra(telefono, codigoVerificacion);
            if (repuesta.trim().length() > 0) {
                System.out.println("No se pudo enviar el mensaje porque: " + repuesta);
                mensaje.setText("Please verify the mobile number entered");
                notification.open();
            } else {
                codigoEnviado = true;
            }
        }
    }

    private void apretoSubmit() {
        if (validarDatosIngresados()) {
            Cliente clienteNuevo = new Cliente();
            Cliente clienteBD = this.clienteService.getClientePorTelefono(this.txtMovil.getValue().trim());
            if (clienteBD != null) {
                clienteNuevo = clienteBD;
            }
            clienteNuevo.setCalltobemade("Yes");
            clienteNuevo.setNombre(this.txtNombre.getValue().trim());
            clienteNuevo.setMovil(this.txtMovil.getValue().trim());
            clienteNuevo.setEstado(Constante.ESTADOS_CLIENTES.ACTIVO);

            TimeZone timeZone = TimeZone.getTimeZone("Australia/Sydney");
            Calendar c = Calendar.getInstance(timeZone);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String creadoPor = "Automatically by the client on the web" + " " + sdf.format(c.getTime());
            clienteNuevo.setCreado_por(creadoPor);

            Sucursal sucursal = this.sucursalService.buscarPorNombre("LIVERPOOL");
            if (sucursal != null) {
                clienteNuevo.setSucursal(sucursal);
            }




            this.clienteService.registrarCliente(clienteNuevo);

            Comentario comentario = new Comentario();
            LocalDateTime v = new java.sql.Timestamp(
                    c.getTime().getTime()).toLocalDateTime();
            comentario.setFecha(v);
            String comentarioNote = "";
            if (txtNote.getValue() != null && txtNote.getValue().trim().length() > 0) {
                comentarioNote += " - Note: " + txtNote.getValue().trim();
            }

            comentario.setComentario(comentarioNote);
            comentario.setCliente(clienteNuevo);
            comentario.setEstado(Constante.ESTADOS_COMENTARIOS.ACTIVO);
            Users user = this.usersService.buscarPorNombre("gabriel");

            comentario.setUsuario(user);
            this.comentarioService.save(comentario);

            this.mensajeExito.setVisible(true);
            this.mensajeCabecera.setVisible(false);
            this.formularioCliente.setVisible(false);
            this.botonSubmit.setVisible(false);
            this.anchor.setVisible(true);


        }
    }

    private boolean validarDatosIngresados() {
        if (codigoEnviado == false) {
            if (txtMovil.getValue() == null || txtMovil.getValue().trim().length() == 0) {
                mensaje.setText("You have to enter the phone number");
                notification.open();
                return false;
            }
            if (txtMovil.getValue().trim().length() != 10 || !txtMovil.getValue().trim().startsWith("0")) {
                mensaje.setText("The format of the mobile number is incorrect, it must be 10 digits and start with 0");
                notification.open();
                return false;
            }
            String numeroStr = txtMovil.getValue();
            for (int a = 0; a < numeroStr.length(); a++) {
                String caracter = numeroStr.substring(a, a + 1);
                try {
                    Integer.valueOf(caracter);
                } catch (Exception e) {
                    mensaje.setText("The mobile number have to be a number");
                    notification.open();
                    return false;
                }
            }
        } else {
            if (txtVerificationCode.getValue() == null || txtVerificationCode.getValue().trim().length() == 0) {
                mensaje.setText("You have to enter the verification code");
                notification.open();
                return false;
            }
            if (!txtVerificationCode.getValue().equalsIgnoreCase(codigoVerificacion)) {
                mensaje.setText("The verification code does not match with the code sent");
                notification.open();
                return false;
            }
        }
        if (txtNombre.getValue() == null || txtNombre.getValue().trim().length() == 0) {
            mensaje.setText("Please enter your name");
            notification.open();
            return false;
        }
        return true;
    }
}
