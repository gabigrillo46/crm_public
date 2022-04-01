package com.vaadin.tutorial.crm.ui.view.Sucursal;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.backend.entity.Cliente;
import com.vaadin.tutorial.crm.backend.entity.Sucursal;
import com.vaadin.tutorial.crm.backend.service.ClienteService;
import com.vaadin.tutorial.crm.backend.service.SucursalService;
import com.vaadin.tutorial.crm.ui.Constante;
import com.vaadin.tutorial.crm.ui.MainLayout;

import java.util.List;

@Route(value = "SucursalView", layout = MainLayout.class)
@PageTitle("Branch | Certified Autos CRM")
public class SucursalView extends VerticalLayout {

    private SucursalService sucursalService;
    private ClienteService clienteService;
    Notification notification;
    private Grid<Sucursal> grid = new Grid<>(Sucursal.class);
    private TextField filterText = new TextField();
    private Button buscar = new Button();
    private Sucursal sucursalActual;
    private Dialog dialogoEstaSeguro=new Dialog();
    Span content = new Span();
    private Span mensajeEstaSeguro=new Span("Are you sure?");
    private Button botonSeguro = new Button("Yes",event -> {
        eliminarSucursal();
    });
    private Button botonNoSeguro = new Button("No", event -> {
        dialogoEstaSeguro.close();
    }) ;


    Dialog dialogo = new Dialog();

    TextField textNombre = new TextField();
    private int accion=-1;


    public SucursalView(SucursalService sucursalService, ClienteService clienteService) {
        GridContextMenu<Sucursal> contextMenu = grid.addContextMenu();
        contextMenu.addItem("Edit", e -> {
            e.getItem().ifPresent(Sucursal -> {
                editarSucursal(Sucursal);
            });
        });

        contextMenu.addItem("Delete", event -> {
            event.getItem().ifPresent(Sucursal->{
                abrirDialogoEstaSeguro(Sucursal);
            });
        });

        notification = new Notification(content);
        notification.setDuration(2000);
        notification.setPosition(Notification.Position.MIDDLE);

        HorizontalLayout botonesEstaSeguro = new HorizontalLayout();
        botonesEstaSeguro.add(botonSeguro,botonNoSeguro);
        this.dialogoEstaSeguro.add(mensajeEstaSeguro,botonesEstaSeguro);


        this.sucursalService = sucursalService;
        this.clienteService = clienteService;

        addClassName("sucursal-view");
        setSizeFull();
        configureGrid();


        grid.setClassName("contact-grid");



        add(this.getToolbar(), grid);
        updateList();


        Button botonAgregar = new Button("Add", event -> {
            aceptarDialogo();
        });
        botonAgregar.addClickShortcut(Key.ENTER);
        Button cancelar = new Button("Cancel", event -> {
            dialogo.close();
        });

        HorizontalLayout botones = new HorizontalLayout(botonAgregar, cancelar);

        dialogo.add(textNombre, botones);
    }

    private void abrirDialogoEstaSeguro(Sucursal sucursal) {
        if(sucursal!=null)
        {
            this.sucursalActual=sucursal;
            dialogoEstaSeguro.open();
        }
    }

    private void eliminarSucursal() {
        if(this.sucursalActual!=null)
        {
            List<Cliente> clientesBD=clienteService.getClientePorSucursal(this.sucursalActual.getId());
            if(clientesBD.size()>0)
            {
                   content.setText("There is a cliente ["+clientesBD.get(0).getNombre()+" "+clientesBD.get(0).getApellido()+"] asociated with the branch");
                   notification.open();
            }
            else {
                sucursalService.eliminarSucursal(this.sucursalActual);
            }
        }
        this.sucursalActual=null;
        this.dialogoEstaSeguro.close();
        this.updateList();
    }

    private void editarSucursal(Sucursal s)
    {
        accion=Constante.OPERACIONES_ABN.MODIFICACION;
        this.sucursalActual=s;
    textNombre.setValue(s.getName());
    textNombre.focus();
    dialogo.open();
    }

    private void aceptarDialogo() {
        String nombre = textNombre.getValue();
        if (nombre != null && nombre.trim().length() > 0) {
            if (accion==Constante.OPERACIONES_ABN.ALTA)
            {
                sucursalActual=new Sucursal();
            }
            if(validarCampos()) {
                sucursalActual.setEstado(Constante.ESTADOS_SUCURSALES.ACTIVO);
                sucursalActual.setName(nombre);
                sucursalService.insertarSucursal(sucursalActual);
                this.updateList();
            }
        }
        this.textNombre.setValue("");
        this.sucursalActual=null;
        this.accion=-1;
        dialogo.close();
    }

    private boolean validarCampos()
    {
        String nombre = textNombre.getValue().trim();
        if(nombre !=null && nombre.length()>0)
        {
            Sucursal sucursalBD = this.sucursalService.buscarPorNombre(nombre);
            if(sucursalBD!=null && accion == Constante.OPERACIONES_ABN.ALTA)
            {

                content.setText("There is other branch with same name");

                notification.open();
                        return false;
            } else if(sucursalBD!=null && accion==Constante.OPERACIONES_ABN.MODIFICACION && sucursalBD.getId()!=this.sucursalActual.getId())
            {

                content.setText("There is other branch with same name");




                notification.open();
                return false;
            }
            return true;
        }
        return false;
    }

    private void agregarSucursal() {
        accion = Constante.OPERACIONES_ABN.ALTA;
        textNombre.setValue("");
        textNombre.focus();
        dialogo.open();


    }

    private void updateList() {
        grid.setItems(sucursalService.buscarPorFiltro(filterText.getValue()));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addContactButton = new Button("Add branch");

        addContactButton.addClickListener(e -> agregarSucursal());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addContactButton);
        toolbar.addClassName("toolbar");

        return toolbar;
    }

    private void configureGrid() {
        grid.addClassName("sucursal-grid");
        grid.setSizeFull();
        grid.setColumns("name");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }
}
