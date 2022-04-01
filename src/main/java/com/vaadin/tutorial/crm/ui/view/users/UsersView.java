package com.vaadin.tutorial.crm.ui.view.users;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.backend.entity.Sucursal;
import com.vaadin.tutorial.crm.backend.entity.Users;
import com.vaadin.tutorial.crm.backend.service.SenderSms;
import com.vaadin.tutorial.crm.backend.service.SucursalService;
import com.vaadin.tutorial.crm.backend.service.UsersService;
import com.vaadin.tutorial.crm.ui.Constante;
import com.vaadin.tutorial.crm.ui.MainLayout;

import java.util.ArrayList;
import java.util.List;

@Route(value = "UsersView", layout = MainLayout.class)
@PageTitle("Users | Certified Autos CRM")
public class UsersView extends VerticalLayout  {

    private UsersService usersService;
    private SucursalService sucursalService;
    private String idUsuarioActual="";
    private Users usuarioActual=null;
    private int accion=-1;
    private TextField textFiltroNombre = new TextField();
    private Button BotonEnviarSms = new Button("Prueba", event -> {
        enviarsms();
    });

    private Dialog dialogoEstaSeguro = new Dialog();
    private Button botonSiEliminar = new Button("Yes", event -> {
        estaSeguroEliminar();
    });

    private Button botonNoEliminar = new Button("No", event -> {
        dialogoEstaSeguro.close();
    });

    Span mensajeEstaSeguro = new Span("Are you sure?");


    private Grid<Users> grid = new Grid(Users.class);
    private Button botonBuscar = new Button("Search", event -> {
        updateGrid();
    });
    private ComboBox<Sucursal> comboSucursales= new ComboBox();

    private Button agregarNuevoUsuario = new Button("Add user", event -> {
        nuevoUsuario();
    });

    private void nuevoUsuario() {
        UI.getCurrent().navigate("UserDatosView");
    }




    public UsersView(UsersService usersService, SucursalService sucursalService)
    {
        this.usersService= usersService;
        this.sucursalService=sucursalService;

        this.textFiltroNombre.setPlaceholder("Search by name or last name");

        List<Sucursal> listaSucursales = this.sucursalService.buscarTodasActivas();
        comboSucursales.setItems(listaSucursales);

        comboSucursales.setItemLabelGenerator(Sucursal::getName);

        comboSucursales.setLabel("Branch:");
        this.textFiltroNombre.setLabel("Name or Last name");

        HorizontalLayout camposBuscar = new HorizontalLayout();
        camposBuscar.setDefaultVerticalComponentAlignment(Alignment.END);
        camposBuscar.add(textFiltroNombre,comboSucursales,botonBuscar, agregarNuevoUsuario, BotonEnviarSms);

        HorizontalLayout botonesEstaSeguro = new HorizontalLayout();
        botonesEstaSeguro.add(botonSiEliminar, botonNoEliminar);
        dialogoEstaSeguro.add(mensajeEstaSeguro,botonesEstaSeguro);
        dialogoEstaSeguro.setCloseOnEsc(false);
        dialogoEstaSeguro.setCloseOnOutsideClick(false);
        dialogoEstaSeguro.setModal(true);

        addClassName("users-view");
        setSizeFull();
        configureGrid();


        add(camposBuscar,grid);



        GridContextMenu<Users> contextMenu = grid.addContextMenu();
        contextMenu.addItem("Edit", e -> {
            e.getItem().ifPresent(Users -> {
                editarUsuario(Users);
            });
        });

        contextMenu.addItem("Delete", event -> {
            event.getItem().ifPresent(Users->{
                abrirDialogoEstaSeguro(Users);
            });
        });

    }

    private void abrirDialogoEstaSeguro(Users users) {
        this.usuarioActual=users;
        dialogoEstaSeguro.open();
    }

    private void editarUsuario(Users users) {
        UI.getCurrent().navigate("UserDatosView/"+String.valueOf(users.getUser_id())+"/"+ String.valueOf(Constante.OPERACIONES_ABN.MODIFICACION));
    }


    private void updateGrid()
    {
        String nombre =null;
        Long idSucursal=null;
        List<Users> resultados = new ArrayList<>();
        if(textFiltroNombre.getValue()!=null && textFiltroNombre.getValue().trim().length()>0)
        {
            nombre= textFiltroNombre.getValue();
        }
        if(comboSucursales.getValue()!=null)
        {
            idSucursal=comboSucursales.getValue().getId();
        }
        resultados = usersService.buscarPorFiltro(nombre,idSucursal);
        grid.setItems(resultados);
    }

    private void configureGrid() {
        grid.setClassName("user-grid");
        grid.setSizeFull();
        grid.removeAllColumns();
        grid.addColumn(
                Users -> Users.getNombre() + " " + Users.getApellido())
                .setHeader("Full Name").setResizable(true);
        grid.addColumn(
                Users -> Users.getUsername())
                .setHeader("Username").setResizable(true);
        grid.addColumn(
                Users->Users.getMovil())
                .setHeader("Phone").setResizable(true);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.addItemDoubleClickListener(event -> mostrarUsuario(event.getItem()));
    }

    private void mostrarUsuario(Users usuario)
    {
        this.editarUsuario(usuario);
    }


    public void  estaSeguroEliminar()
    {
        if(this.usuarioActual!=null)
        {
            this.usuarioActual.setEstado(Constante.ESTADOS_USUARIOS.BAJA_LOGICA);
            this.usuarioActual.setEnabled(false);
            usersService.saveUsuario(this.usuarioActual);
            this.usuarioActual=null;
            dialogoEstaSeguro.close();
            updateGrid();
        }
    }

    private void  enviarsms()
    {
        SenderSms veamos = new SenderSms();
        veamos.prueba();
    }

}
