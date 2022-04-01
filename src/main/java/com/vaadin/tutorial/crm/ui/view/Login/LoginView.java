package com.vaadin.tutorial.crm.ui.view.Login;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.backend.entity.Users;
import com.vaadin.tutorial.crm.backend.service.EnviarEmail;
import com.vaadin.tutorial.crm.backend.service.UsersService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Route("login")
@PageTitle("Login | Certified Autos CRM")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private LoginForm login = new LoginForm();
    Dialog dialogoReset = new Dialog();
    UsersService usersService;
    EnviarEmail enviarEmail;
    Users usuarioCambioContrasena;


    TextField txtPregunta = new TextField("Question");
    TextField txtRespuesta = new TextField("Answer");
    Span mensaje = new Span();
    Notification notification;

    TextField txtNombreUsuario = new TextField("Username");
    Button botonResetPass = new Button("Reset", event -> {
        buscarPreguntaUsuario();
    });

    Button botonAceptarReset = new Button("Summit",event -> {
        aceptarResetPass();
    });

    private void aceptarResetPass() {
        if(usuarioCambioContrasena!=null && txtRespuesta.getValue()!=null)
        {
            if(usuarioCambioContrasena.getRespuesta().equalsIgnoreCase(txtRespuesta.getValue()))
            {
                BCryptPasswordEncoder j= new BCryptPasswordEncoder();
                String passCrypt =j.encode("321");
                this.usuarioCambioContrasena.setPassword(passCrypt);
                this.usersService.saveUsuario(this.usuarioCambioContrasena);
                mensaje.setText("The new password is:  321");
                notification.setDuration(2000);
                notification.open();
                dialogoReset.close();
            }
            else
            {
                mensaje.setText("the answer is wrong");
                notification.open();
            }
        }
    }

    Button botonCancelarReset = new Button("Cancel", event -> {
        this.usuarioCambioContrasena=null;
        txtPregunta.setValue("");
        txtRespuesta.setValue("");
        txtNombreUsuario.setValue("");
        dialogoReset.close();
    });


    private void buscarPreguntaUsuario() {
        if(txtNombreUsuario.getValue()!=null && txtNombreUsuario.getValue().trim().length()>0)
        {
            Users usuario = usersService.buscarPorNombreUsuario(txtNombreUsuario.getValue().trim());
            if(usuario!=null)
            {
                this.usuarioCambioContrasena= usuario;
                txtPregunta.setValue(usuario.getPregunta());
                txtPregunta.setVisible(true);
                txtRespuesta.setVisible(true);
            }
            else
            {
                mensaje.setText("User entered does not exist");
                notification.open();
            }
        }
    }


    public LoginView(UsersService usersService, EnviarEmail enviarEmail){
        txtPregunta.setVisible(false);
        txtPregunta.setEnabled(false);
        txtRespuesta.setVisible(false);


        notification = new Notification(mensaje);

        notification.setDuration(2000);

        notification.setPosition(com.vaadin.flow.component.notification.Notification.Position.MIDDLE);

        this.usersService = usersService;
        this.enviarEmail=enviarEmail;
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.setAction("login");

        login.addForgotPasswordListener(event -> {
            comprobarUsuario();
        });

        HorizontalLayout userBoton = new HorizontalLayout();
        userBoton.add(txtNombreUsuario,botonResetPass);
        userBoton.setDefaultVerticalComponentAlignment(Alignment.END);

        HorizontalLayout botonesAceptarCan = new HorizontalLayout();
        botonesAceptarCan.add(botonAceptarReset, botonCancelarReset);

        VerticalLayout preguntaRespuesta = new VerticalLayout();
        preguntaRespuesta.add(txtPregunta);
        preguntaRespuesta.add(txtRespuesta);

        dialogoReset.setCloseOnOutsideClick(false);
        dialogoReset.setModal(true);
        dialogoReset.setCloseOnEsc(false);

        dialogoReset.add(userBoton,
                preguntaRespuesta,
                botonesAceptarCan);

        add(new H1("Certified Autos CRM"), login);


    //    this.enviarEmail.sendEmailTool("prueba", "gabigrillo46@gmail.com","llego?");
    }





    private  void comprobarUsuario()
    {
        this.usuarioCambioContrasena = null;
        txtPregunta.setVisible(false);
        txtRespuesta.setVisible(false);
        dialogoReset.open();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // inform the user about an authentication error
        if(beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }
}