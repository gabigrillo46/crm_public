package com.vaadin.tutorial.crm.ui.view.Sucursal;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import com.vaadin.tutorial.crm.backend.entity.Sucursal;
import com.vaadin.tutorial.crm.ui.MainLayout;

@Route(value = "DatosSucursalView", layout = MainLayout.class)
@PageTitle("Branch data | Certified Autos CRM")
public class DatosSucursalView extends HorizontalLayout implements HasUrlParameter<String> {

    Sucursal sucursalActual= null;
    String idSucursalStr;
    FormLayout formularioSucursal=new FormLayout();
    Binder<Sucursal> binder= new Binder<>(Sucursal.class);

    TextField textNombre = new TextField();
    Button botonAgregar=new Button();
    Button cancelar=new Button();

    public DatosSucursalView()
    {
        setClassName("datos-sucursal");

        botonAgregar.setText("Add");
        cancelar.setText("Cancel");

        textNombre.setPlaceholder("Branch name");

        HorizontalLayout panelBotones = new HorizontalLayout();
        panelBotones.add(botonAgregar,cancelar);

        formularioSucursal.add(
                textNombre,
        panelBotones
        );

        this.setMargin(true);
        this.setSpacing(true);
        add(formularioSucursal);

    }


    @Override
    public void setParameter(BeforeEvent event,
        @OptionalParameter String parameter) {
        if (parameter != null) {
            idSucursalStr = parameter;
        }
    }
}
