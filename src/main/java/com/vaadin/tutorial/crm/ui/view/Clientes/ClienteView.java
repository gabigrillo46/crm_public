package com.vaadin.tutorial.crm.ui.view.Clientes;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.backend.entity.Cliente;
import com.vaadin.tutorial.crm.backend.entity.Source;
import com.vaadin.tutorial.crm.backend.entity.Sucursal;
import com.vaadin.tutorial.crm.backend.service.*;
import com.vaadin.tutorial.crm.ui.Constante;
import com.vaadin.tutorial.crm.ui.MainLayout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Route(value = "ClienteView", layout = MainLayout.class)
@PageTitle("Clients | Certified Autos CRM")
public class ClienteView extends VerticalLayout {

    ClienteService clienteService;

    SucursalService sucursalService;
    SourceService sourceService;
    UsersService usersService;

    TextField txtBuscarNombre = new TextField("Name or Last name");
    ComboBox<Sucursal> comboSucursales = new ComboBox<Sucursal>("Branch");
    TextField txtBuscarPalabraObservacion = new TextField("Word in observation field");
    TextField txtBuscarTelefono = new TextField("Phone number");
    Notification notification = new Notification();
    ComboBox<Source> comboSource = new ComboBox<Source>("Source");
    DatePicker fechaDesde = new DatePicker("Created from");
    DatePicker fechaHasta = new DatePicker("Created up to");




    Span mensaje=new Span();
    private Span mensajeEstaSeguro=new Span("Are you sure?");
    private Dialog dialogoEstaSeguro=new Dialog();
    Checkbox chkCalltobemade = new Checkbox("Call to be made");
    Checkbox appoiment = new Checkbox("Appoiment");
    Checkbox chkLost = new Checkbox("Lost");
    Checkbox chkSale = new Checkbox("Sale");
    Checkbox chkTentative = new Checkbox("Tentative");
    HorizontalLayout botonesEstaSeguro = new HorizontalLayout();


    private Button botonSeguro = new Button("Yes",event -> {
        eliminarCliente();
    });
    private Button botonNoSeguro = new Button("No", event -> {
        dialogoEstaSeguro.close();
    }) ;


    Cliente clienteActual;



    MultiSelectListBox<String> listBox = new MultiSelectListBox<>();

    public ClienteView(ClienteService clienteService,
                       SucursalService sucursalService,
                       SourceService sourceService,
                       UsersService usersService,
                       ComentarioService comentarioService)
    {
        this.clienteService=clienteService;
        this.sucursalService= sucursalService;
        this.sourceService = sourceService;
        this.usersService = usersService;

        dialogo.setMinWidth("100%");











        grid.addItemDoubleClickListener(event -> {
            mostrarCliente(event.getItem());
        });
        botonesEstaSeguro.add(botonSeguro,botonNoSeguro);
        this.dialogoEstaSeguro.add(mensajeEstaSeguro,botonesEstaSeguro);

        comboSucursales.setItems(this.sucursalService.buscarTodasActivas());
        comboSucursales.setItemLabelGenerator(Sucursal::getName);

        comboSource.setItems(this.sourceService.buscarTodosActivos());
        comboSource.setItemLabelGenerator(Source::getName);
        comboSource.setClearButtonVisible(true);
        comboSource.setWidth("250px");



        GridContextMenu<Cliente> contextMenu = grid.addContextMenu();
        contextMenu.addItem("Edit", e -> {
            e.getItem().ifPresent(Cliente -> {
                editarCliente(Cliente);
            });
        });

        contextMenu.addItem("Delete", event -> {
            event.getItem().ifPresent(Cliente->{
                abrirDialogoEstaSeguro(Cliente);
            });
        });
        contextMenu.addItem("Lost", event -> {
            event.getItem().ifPresent(Cliente ->{
                this.clientePerdidoContextual(Cliente);
            });
        });

        addClassName("list-view");
        setSizeFull();
        configureGrid();
        dialogo.setModal(true);

        // dialogo.add(clienteForm, createButtonsLayout());


        add(this.getToolbar(), grid);

        notification.add(mensaje);

        notification.setDuration(2000);

        notification.setPosition(com.vaadin.flow.component.notification.Notification.Position.MIDDLE);




    }


    private void  eliminarCliente()
    {
        if(this.clienteActual!=null)
        {
            this.clienteActual.setEstado(Constante.ESTADOS_CLIENTES.BAJA_LOGICA);
            clienteService.registrarCliente(this.clienteActual);
        }
        this.clienteActual=null;
        this.dialogoEstaSeguro.close();
        this.buscarPorFiltro();
    }





    Button botonBuscar=new Button("Search",event -> {
        buscarPorFiltro();
    });
    Button botonNuevoCliente = new Button("New Client",event -> {
        nuevoCLiente();
    });
    ClientForm clienteForm;
    Dialog dialogo = new Dialog();

    private void nuevoCLiente() {
        UI.getCurrent().navigate("ClientForm");
    }

    private Grid<Cliente> grid = new Grid<>(Cliente.class);


    private void buscarPorFiltro() {

        String nombre=null;
        String palabraObservacion = null;
        String telefono = null;
        Long idSucursal=null;
        String calltobemade = null;
        Integer appoimentCheck=null;
        Integer lostState = null;
        Integer sale =null;
        Long idSource=null;
        Date fechaDesdeDate = null;
        Date fechaHastaDate = null;


        if(txtBuscarNombre.getValue()!=null && txtBuscarNombre.getValue().trim().length()>0)
        {
            nombre = txtBuscarNombre.getValue().trim();
        }

        if(txtBuscarPalabraObservacion.getValue()!=null && txtBuscarPalabraObservacion.getValue().trim().length()>0)
        {
            palabraObservacion=txtBuscarPalabraObservacion.getValue().trim();
        }

        if(txtBuscarTelefono.getValue()!=null && txtBuscarTelefono.getValue().trim().length()>0)
        {
            telefono=txtBuscarTelefono.getValue().trim();
        }

        if(comboSucursales.getValue()!=null)
        {
            idSucursal = comboSucursales.getValue().getId();
        }
        if(comboSource.getValue()!=null)
        {
            idSource = comboSource.getValue().getId();
        }
        if(chkCalltobemade.getValue())
        {
            calltobemade = "Yes";
        }
        if(appoiment.getValue())
        {
            appoimentCheck=Integer.valueOf(1);
        }
        if(chkLost.getValue())
        {
            lostState = Integer.valueOf(Constante.ESTADOS_CLIENTES.LOST);
        }
        else
        {
            lostState= Integer.valueOf(Constante.ESTADOS_CLIENTES.ACTIVO);
        }

        if(chkSale.getValue())
        {
            sale =Integer.valueOf(1);
        }
        else
        {
            sale =Integer.valueOf(0);
        }



        if(fechaDesde.getValue()!=null)
        {
            fechaDesdeDate = java.sql.Date.valueOf(fechaDesde.getValue());
        }

        if(fechaHasta.getValue()!=null)
        {
            fechaHastaDate = java.sql.Date.valueOf(fechaHasta.getValue().plusDays(1));
        }
        //este es el que manda, si es tentative, vamos a desactivar los otros filtros
        if(chkTentative.getValue())
        {
            appoimentCheck=Integer.valueOf(0);
            calltobemade = "No";
        }

        if(telefono!=null && nombre==null && palabraObservacion==null && idSucursal==null)
        {
            grid.setItems(clienteService.getClientesPorTelefono(telefono));
        }
        else {
            System.out.println("Resultado: "+clienteService.getClientesPorFiltro(nombre, palabraObservacion, telefono, idSucursal, calltobemade, appoimentCheck, lostState, sale,idSource, fechaDesdeDate, fechaHastaDate).size());
            grid.setItems(clienteService.getClientesPorFiltro(nombre, palabraObservacion, telefono, idSucursal, calltobemade, appoimentCheck, lostState, sale,idSource, fechaDesdeDate, fechaHastaDate));
        }
    }

    private void abrirDialogoEstaSeguro(Cliente cliente) {
        if(cliente!=null)
        {
            this.clienteActual=cliente;
            dialogoEstaSeguro.open();
        }
    }



    private void editarCliente(Cliente cliente) {
        if(cliente !=null) {
            UI.getCurrent().navigate("ClientForm/"+String.valueOf(cliente.getId())+"/"+ String.valueOf(Constante.OPERACIONES_ABN.MODIFICACION)+"/"+"ClienteView");
        }
        buscarPorFiltro();
    }

    private VerticalLayout getToolbar() {

        botonBuscar.addClickShortcut(Key.ENTER);

        HorizontalLayout horizontal1= new HorizontalLayout();
        horizontal1.add(
                txtBuscarNombre,
                comboSucursales,
                comboSource
        );
        //horizontal1.setDefaultVerticalComponentAlignment(Alignment.END);

        HorizontalLayout horizontal2=new HorizontalLayout();
        horizontal2.add(
                txtBuscarPalabraObservacion,
                txtBuscarTelefono
        );
        horizontal2.setDefaultVerticalComponentAlignment(Alignment.END);

        HorizontalLayout horizontal3 = new HorizontalLayout();
        horizontal3.add(fechaDesde, fechaHasta);

        HorizontalLayout horizontal4 = new HorizontalLayout();
        horizontal4.add(chkCalltobemade,appoiment,chkLost, chkSale, chkTentative);

        HorizontalLayout horizontal5=new HorizontalLayout();
        horizontal5.add(
                botonBuscar,
                botonNuevoCliente
        );

        VerticalLayout vertical = new VerticalLayout();
        vertical.add(
                horizontal1,
                horizontal2,
                horizontal3,
                horizontal4,
                horizontal5
        );



        return vertical;
    }

    private void configureGrid()
    {

        grid.setClassName("contact-form");
        grid.setSizeFull();
        grid.removeAllColumns();
        grid.addColumn(
                Cliente -> Cliente.getNombre() + " " + Cliente.getApellido())
                .setHeader("Full Name").setResizable(true);
        grid.addColumn(
                Cliente -> Cliente.getMovil())
                .setHeader("Phone").setResizable(true);
        grid.addColumn(
                Cliente->Cliente.getEmail())
                .setHeader("Email").setResizable(true);
        grid.addColumn(
                Cliente->Cliente.getCalltobemade())
                .setHeader("Call to be made").setResizable(true);
        grid.addColumn(
                Cliente->this.getFecheAppoiment(Cliente.getFecha_appoiment()))
                .setHeader("Approiment").setResizable(true)
        .setSortable(true);


        grid.getColumns().forEach(col -> col.setAutoWidth(true));


    }

    private String getFechaStr(LocalDate fecha)
    {
        if(fecha!=null)
        {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
            return formatter.format(fecha);
        }
        else
        {
            return "";
        }
    }

    private void mostrarCliente(Cliente cliente)
    {
        this.editarCliente(cliente);
    }

    private String getFecheAppoiment(LocalDateTime local)
    {
        String resultado="";
        if(local!=null) {
            DateTimeFormatter drf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            resultado = drf.format(local);
        }
        return resultado;
    }

    public void clientePerdido()
    {
        this.clienteActual=clienteForm.getClienteActual();
        if(this.clienteActual!=null)
        {
            this.clienteActual.setEstado(Constante.ESTADOS_CLIENTES.LOST);
            clienteService.registrarCliente(this.clienteActual);
            dialogo.close();
            buscarPorFiltro();
        }
    }

    private void clientePerdidoContextual(Cliente cliente)
    {
        if(cliente != null)
        {
            cliente.setEstado(Constante.ESTADOS_CLIENTES.LOST);
            this.clienteService.registrarCliente(cliente);
            buscarPorFiltro();
        }
    }




}
