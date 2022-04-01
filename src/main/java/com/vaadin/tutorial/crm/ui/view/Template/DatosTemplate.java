package com.vaadin.tutorial.crm.ui.view.Template;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import com.vaadin.tutorial.crm.backend.entity.Template;
import com.vaadin.tutorial.crm.backend.service.TemplateService;
import com.vaadin.tutorial.crm.ui.Constante;
import com.vaadin.tutorial.crm.ui.MainLayout;

@Route(value = "DatosTemplate", layout = MainLayout.class)
@PageTitle("Template | Certified Autos CRM")
public class DatosTemplate extends VerticalLayout implements HasUrlParameter<String> {

    private Template templateActual = null;
    private int accion = -1;
    private String pantallaAnterior = "";
    private TemplateService templateService;
    private Binder<Template> binder = new Binder<>();

    private TextField txtNombre = new TextField("Name");
    private TextArea txtTexto = new TextArea("Text");
    private Button botonAceptar = new Button("Submit", event -> aceptoTemplate());
    private Button botonCancelar = new Button("Cancel", event -> canceloTemplate());
    private Button botonEliminar = new Button("Delete", event -> eliminarTemplate());
    private FormLayout formulario = new FormLayout();

    Span mensaje = new Span();
    Notification notification = new Notification(mensaje);


    public DatosTemplate(TemplateService templateService) {
        this.templateService = templateService;
        formulario.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1));
        formulario.add(txtNombre, txtTexto);
        formulario.setMaxWidth("700px");

        binder.forField(txtNombre)
                .asRequired("You have to enter the name")
                .bind(Template::getNombre, Template::setNombre);

        binder.forField(txtTexto)
                .asRequired("You have to enter the text")
                .bind(Template::getMensaje, Template::setMensaje);

        notification.setDuration(2000);
        notification.setPosition(Notification.Position.MIDDLE);


        this.setSizeFull();

        HorizontalLayout hBotones = new HorizontalLayout();
        botonAceptar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        botonCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        botonEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);
        hBotones.add(botonAceptar, botonCancelar, botonEliminar);

        this.add(formulario, hBotones);
    }


    @Override
    public void setParameter(BeforeEvent event,
                             @WildcardParameter String parameter) {
        if (parameter != null && parameter.trim().length() > 0) {
            String[] parametros = parameter.split("/");
            Long idCTemplateActual = Long.valueOf(parametros[0]);
            accion = Integer.parseInt(parametros[1]);
            pantallaAnterior = parametros[2];
            this.templateActual = this.templateService.getTemplatePorId(idCTemplateActual);
            if (this.templateActual == null) {
                this.templateActual = new Template();
            }

        } else {
            accion = Constante.OPERACIONES_ABN.ALTA;
            this.templateActual = new Template();
        }
        binder.readBean(this.templateActual);
    }

    private void aceptoTemplate() {
        if (this.verificarCampos()) {
            this.registrar();
        }
    }

    private boolean verificarCampos() {
        try {
            binder.writeBean(this.templateActual);
            Template templateBD = this.templateService.getTemplatePorNombre(this.templateActual.getNombre());
            if (templateBD != null && templateBD.getId() != this.templateActual.getId()) {
                mensaje.setText("There is another template with the name entered");
                notification.open();
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void registrar() {
        this.templateService.saveTemplate(this.templateActual);
        mensaje.setText("Successful registration");
        notification.open();
        this.canceloTemplate();
    }

    private void canceloTemplate() {
        if (pantallaAnterior != null && pantallaAnterior.trim().length() > 0) {
            UI.getCurrent().navigate(pantallaAnterior);
        } else {
            UI.getCurrent().navigate("GrillaTemplate");
        }
    }

    private void eliminarTemplate() {

    }

}
