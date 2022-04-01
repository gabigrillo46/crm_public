package com.vaadin.tutorial.crm.ui.view.users;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.*;
import com.vaadin.tutorial.crm.backend.entity.Sucursal;
import com.vaadin.tutorial.crm.backend.entity.Users;
import com.vaadin.tutorial.crm.backend.service.SucursalService;
import com.vaadin.tutorial.crm.backend.service.UsersService;
import com.vaadin.tutorial.crm.ui.Constante;
import com.vaadin.tutorial.crm.ui.MainLayout;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Route(value = "UserDatosView", layout = MainLayout.class)
@PageTitle("User data | Certified Autos CRM")
public class UserDatosView extends VerticalLayout implements HasUrlParameter<String> {

    private UsersService usersService;
    private SucursalService sucursalService;
    FormLayout formularioUsuario = new FormLayout();
    private int idUsuarioActual = -1;
    private int accion = -1;
    private Users usuarioActual;

    TextField txtNombre = new TextField("Name");
    TextField txtApellido = new TextField("Last name");
    ComboBox<Sucursal> comboSucursal = new ComboBox<Sucursal>("Branch");
    TextField txtUsuario = new TextField("Username");
    TextField txtMovil = new TextField("Phone");
    PasswordField contrasena = new PasswordField("Password");
    TextField txtPreguntaContrasena = new TextField("Question to recover password");
    TextField txtRespuestaContrasena = new TextField("Answer to recover password");
    TextField txtEmail = new TextField("Email");

    Span mensaje = new Span("There is other user with same name");

    Notification notification = new Notification(mensaje);

    Dialog dialogoCambiarContrasena = new Dialog();
    PasswordField txtContrasenaActual = new PasswordField("Current password");
    PasswordField txtNuevaContrasena = new PasswordField("New Password");
    PasswordField txtNuevaContrasena2 = new PasswordField("Repeat the new password");
    Button botonCancelarContrasena = new Button("Cancel", event -> {
        dialogoCambiarContrasena.close();
    });

    Button botonAceptarCambiarContrasena = new Button("Summit", event -> {
        aceptoCambioContrasena();
    });

    private void aceptoCambioContrasena() {
        if (this.usuarioActual != null) {
            Span mensaje = new Span();
            Notification notification = new Notification(mensaje);
            notification.setDuration(2000);

            notification.setPosition(Notification.Position.MIDDLE);
            BCryptPasswordEncoder j = new BCryptPasswordEncoder();
            if (txtContrasenaActual.getValue().trim().length() == 0) {
                mensaje.setText("You must enter the current password");
                notification.open();
                return;
            }
            if (txtNuevaContrasena.getValue().trim().length() == 0) {
                mensaje.setText("You must enter a new password");
                notification.open();
                return;
            }
            if (txtNuevaContrasena2.getValue().trim().length() == 0) {
                mensaje.setText("You must repeat the new password");
                notification.open();
                return;
            }
            if (!j.matches(txtContrasenaActual.getValue(), this.usuarioActual.getPassword())) {
                mensaje.setText("your current password is not valid");
                notification.open();
                return;
            }
            if (!txtNuevaContrasena.getValue().trim().equals(txtNuevaContrasena2.getValue().trim())) {
                mensaje.setText("the new passwords do not match");
                notification.open();
                return;
            }
            String passencrypt = j.encode(txtNuevaContrasena.getValue().trim());
            this.usuarioActual.setPassword(passencrypt);
            this.usersService.saveUsuario(this.usuarioActual);
            mensaje.setText("Password changed successfully");
            notification.open();
            dialogoCambiarContrasena.close();
        }
    }

    Binder<Users> binder = new Binder<>(Users.class);

    Button botonAceptar = new Button("Submit", event -> {
        Acepto();
    });

    Button botonCancelar = new Button("Cancel", event -> {
        cancelar();
    });

    Button botonCambiarPassword = new Button("Change password", event -> {

        cambiarContrasena();
    });

    @Override
    public void setParameter(BeforeEvent event,
                             @WildcardParameter String parameter) {
        if (parameter != null && parameter.trim().length() > 0) {
            String[] parametros = parameter.split("/");
            idUsuarioActual = Integer.parseInt(parametros[0]);
            accion = Integer.parseInt(parametros[1]);
            contrasena.setEnabled(false);
            botonCambiarPassword.setVisible(true);
            usuarioActual = usersService.buscarPorId(idUsuarioActual);
            if (usuarioActual == null) {
                //no deberia pasar
                usuarioActual = new Users();
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String usuarioLogueado = authentication.getName();
            if (usuarioLogueado.equals(this.usuarioActual.getUsername())) {
                txtPreguntaContrasena.setVisible(true);
                txtRespuestaContrasena.setVisible(true);
            } else {
                txtPreguntaContrasena.setVisible(false);
                txtRespuestaContrasena.setVisible(false);
            }
        } else {
            accion = Constante.OPERACIONES_ABN.ALTA;
            contrasena.setEnabled(true);
            botonCambiarPassword.setVisible(false);
            usuarioActual = new Users();
            txtPreguntaContrasena.setVisible(true);
            txtRespuestaContrasena.setVisible(true);
        }
        binder.readBean(usuarioActual);
    }

    private void cambiarContrasena() {
        if (this.usuarioActual != null) {
            dialogoCambiarContrasena.open();
            txtContrasenaActual.focus();
        }
    }

    private void cancelar() {
        UI.getCurrent().navigate("UsersView");
    }

    private void Acepto() {
        try {
            binder.writeBean(this.usuarioActual);
            if(this.usuarioActual.getUsername().trim().equalsIgnoreCase("gaby"))
            {
                mensaje.setText("There is another user with the same name");
                notification.open();
                return;
            }
            Users usuarioBD = usersService.buscarPorNombreUsuario(this.usuarioActual.getUsername().trim());
            if ((usuarioBD != null && accion == Constante.OPERACIONES_ABN.ALTA) || (usuarioBD != null && accion == Constante.OPERACIONES_ABN.MODIFICACION && usuarioBD.getUser_id() != this.usuarioActual.getUser_id())) {
                mensaje.setText("There is another user with the same name");
                notification.open();
                return;
            }

            BCryptPasswordEncoder j = new BCryptPasswordEncoder();
            String passCrypt = j.encode(this.usuarioActual.getPassword());
            this.usuarioActual.setPassword(passCrypt);

            this.usuarioActual.setEnabled(true);
            this.usuarioActual.setRole(Constante.ROLE_USERS.admin);

            usersService.saveUsuario(this.usuarioActual);

            Span content = new Span("User saved");

            Notification notification = new Notification(content);
            notification.setDuration(2000);

            notification.setPosition(Notification.Position.MIDDLE);
            notification.open();

            this.usuarioActual = new Users();
            this.accion = Constante.OPERACIONES_ABN.ALTA;
            binder.setBean(this.usuarioActual);
            UI.getCurrent().navigate("UsersView");

        } catch (ValidationException e) {
            e.printStackTrace();
        }


    }

    public UserDatosView(UsersService usersService, SucursalService sucursalService) {
        this.usersService = usersService;
        this.sucursalService = sucursalService;

        notification.setDuration(2000);

        notification.setPosition(Notification.Position.MIDDLE);


        txtPreguntaContrasena.setPlaceholder("Question to recover password");
        txtRespuestaContrasena.setPlaceholder("Answer");
        txtPreguntaContrasena.setVisible(false);
        txtRespuestaContrasena.setVisible(false);

        HorizontalLayout panelBotones = new HorizontalLayout();
        panelBotones.add(botonAceptar, botonCancelar);


        HorizontalLayout contrasenaConBoton = new HorizontalLayout();
        contrasenaConBoton.add(contrasena, botonCambiarPassword);
        contrasenaConBoton.setDefaultVerticalComponentAlignment(Alignment.END);

        HorizontalLayout prueba = new HorizontalLayout();
        prueba.add(txtUsuario, contrasenaConBoton);

        comboSucursal.setItems(this.sucursalService.buscarTodasActivas());
        comboSucursal.setItemLabelGenerator(Sucursal::getName);


        binder.forField(txtNombre)
                .asRequired("You must enter a name")
                .bind(Users::getNombre, Users::setNombre);

        binder.forField(txtApellido)
                .asRequired("You mus enter a last name")
                .bind(Users::getApellido, Users::setApellido);

        binder.forField(txtMovil)
                .bind(Users::getMovil, Users::setMovil);

        binder.forField(txtUsuario)
                .asRequired("You must enter a username")
                .bind(Users::getUsername, Users::setUsername);

        binder.forField(contrasena)
                .asRequired("You mus enter a password")
                .bind(Users::getPassword, Users::setPassword);

        binder.forField(txtPreguntaContrasena)
                .bind(Users::getPregunta, Users::setPregunta);

        binder.forField(txtRespuestaContrasena)
                .bind(Users::getRespuesta, Users::setRespuesta);

        binder.bind(comboSucursal, Users::getSucursal, Users::setSucursal);

        binder.forField(txtEmail)
                .bind(Users::getEmail, Users::setEmail);

        formularioUsuario.setColspan(txtPreguntaContrasena, 2);
        formularioUsuario.setColspan(txtRespuestaContrasena, 2);
        formularioUsuario.setColspan(txtEmail, 2);


        formularioUsuario.add(txtNombre,
                txtApellido,
                comboSucursal,
                txtMovil,
                prueba,
                txtEmail,
                txtPreguntaContrasena,
                txtRespuestaContrasena);
        formularioUsuario.setMaxWidth("800px");
        this.setMargin(true);
        this.setSpacing(true);
        add(formularioUsuario, panelBotones);

        VerticalLayout vertical = new VerticalLayout();
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(botonAceptarCambiarContrasena, botonCancelarContrasena);
        vertical.add(
                txtContrasenaActual,
                txtNuevaContrasena,
                txtNuevaContrasena2,
                horizontalLayout
        );

        dialogoCambiarContrasena.add(
                vertical
        );


    }


}
