package com.vaadin.tutorial.crm.ui.view.Clientes;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.backend.entity.Auto;
import com.vaadin.tutorial.crm.backend.entity.Sucursal;
import com.vaadin.tutorial.crm.backend.service.AutoService;
import com.vaadin.tutorial.crm.backend.service.SucursalService;
import com.vaadin.tutorial.crm.ui.Constante;
import com.vaadin.tutorial.crm.ui.MainLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "SaleView", layout = MainLayout.class)
@PageTitle("Sales | Certified Autos CRM")
public class SaleView extends VerticalLayout{

    private TextField txtBuscarTelefono = new TextField("Mobile: ");
    private ComboBox<Sucursal> cmbSucursales=new ComboBox<Sucursal>("Branch:");
    private SucursalService sucursalService;
    private Button botonBuscar = new Button("Search", e ->buscarPorFiltro());
    private Button botonNuevoContrato = new Button("New Sale", e -> nuevaVenta());
    private FormLayout formularioBusqueda= new FormLayout();
    private Grid<Auto> grid = new Grid<>(Auto.class);
    private AutoService autoService;
    private DatePicker dtpFechaDesde = new DatePicker("From");
    private DatePicker dtpFechaHasta = new DatePicker("Until");
    private H3 resultado = new H3();
    public SaleView(SucursalService sucursalService, AutoService autoService)
    {
        this.autoService = autoService;
        this.sucursalService= sucursalService;
        cmbSucursales.setMaxWidth("300px");
        txtBuscarTelefono.setMaxWidth("300px");
        formularioBusqueda.setMaxWidth("900px");
        dtpFechaDesde.setMaxWidth("300px");
        dtpFechaHasta.setMaxWidth("300px");
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth("300px");
        layout.setBoxSizing(BoxSizing.CONTENT_BOX);
        botonBuscar.addClickShortcut(Key.ENTER);
        botonBuscar.addClickListener(event -> buscarPorFiltro());
        layout.add(botonBuscar, botonNuevoContrato, resultado);
        layout.setDefaultVerticalComponentAlignment(Alignment.END);
        formularioBusqueda.add(cmbSucursales, txtBuscarTelefono, dtpFechaDesde,dtpFechaHasta, layout);
        formularioBusqueda.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 2));
        this.cargarCombos();
        this.configureGrid();
        setSizeFull();

        grid.addItemDoubleClickListener(event -> {
            mostrarAuto(event.getItem());
        });

        add(formularioBusqueda, grid);


    }

    private void cargarCombos()
    {
        cmbSucursales.setClearButtonVisible(true);
        cmbSucursales.setItems(sucursalService.buscarTodasActivas());
        cmbSucursales.setItemLabelGenerator(Sucursal::getName);
    }

    private void buscarPorFiltro()
    {
        String numero =null;
        Long idSucursal=null;
        LocalDate fechaDesdeDate = null;
        LocalDate fechaHastaDate = null;
        if(txtBuscarTelefono.getValue()!=null && txtBuscarTelefono.getValue().trim().length()>0)
        {
            numero=txtBuscarTelefono.getValue();
        }
        if(cmbSucursales.getValue()!=null)
        {
            idSucursal = cmbSucursales.getValue().getId();
        }
        if(dtpFechaDesde.getValue()!=null)
        {
            fechaDesdeDate = dtpFechaDesde.getValue();
        }
        if(dtpFechaHasta.getValue()!=null)
        {
            fechaHastaDate = dtpFechaHasta.getValue();
        }

        List<Auto> listaResultado =this.autoService.buscarAutosPorFiltro(numero,idSucursal, fechaDesdeDate, fechaHastaDate);
        this.grid.setItems(listaResultado);
        resultado.setText("Quantity: "+listaResultado.size());
    }

    private void configureGrid()
    {

        grid.setClassName("contact-form");
        grid.setSizeFull();
        grid.removeAllColumns();
        grid.addColumn(
                Auto -> Auto.getClienteAuto().getApellido()+" "+Auto.getClienteAuto().getNombre())
                .setHeader("Full Name").setResizable(true);
        grid.addColumn(
                Auto-> Auto.getMarca())
                .setHeader("Brand").setResizable(true);
        grid.addColumn(
                Auto->Auto.getModelo())
                .setHeader("Model").setResizable(true);
        grid.addColumn(
                Auto->Auto.getRego())
                .setHeader("Rego").setResizable(true);
        grid.addColumn(
                Auto->this.getFechaPickupAuto(Auto))
                .setHeader("Pick up").setResizable(true);


        grid.getColumns().forEach(col -> col.setAutoWidth(true));


    }

    private String getFechaPickupAuto(Auto auto)
    {
        String resultado ="";
        if(auto!=null && auto.getPick_up_date()!=null)
        {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            resultado = auto.getPick_up_date().format(dtf);
        }
        if(auto!=null && auto.getPick_up_time()!=null && auto.getPick_up_time().trim().length()>0)
        {
            resultado = resultado+" "+auto.getPick_up_time();
        }
        return resultado;
    }

    private void nuevaVenta()
    {
        UI.getCurrent().navigate("SaleForm");
    }

    private void mostrarAuto(Auto auto)
    {
        if(auto !=null) {
            UI.getCurrent().navigate("SaleForm/"+String.valueOf(auto.getId())+"/"+ String.valueOf(Constante.OPERACIONES_ABN.MODIFICACION)+"/"+"SaleView");
        }
    }

}
