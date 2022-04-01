package com.vaadin.tutorial.crm.ui.view.dashboard;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.*;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.legend.Position;
import com.github.appreciated.apexcharts.config.plotoptions.builder.BarBuilder;
import com.github.appreciated.apexcharts.config.responsive.builder.OptionsBuilder;
import com.github.appreciated.apexcharts.config.tooltip.builder.YBuilder;
import com.github.appreciated.apexcharts.config.yaxis.builder.TitleBuilder;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.backend.entity.Cliente;
import com.vaadin.tutorial.crm.backend.entity.Source;
import com.vaadin.tutorial.crm.backend.entity.Sucursal;
import com.vaadin.tutorial.crm.backend.service.ClienteService;
import com.vaadin.tutorial.crm.backend.service.SourceService;
import com.vaadin.tutorial.crm.backend.service.SucursalService;
import com.vaadin.tutorial.crm.ui.MainLayout;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Dashboard | Certified Autos CRM")
public class DashboardView extends VerticalLayout {

    private SucursalService sucursalService;
    private ClienteService clienteService;
    private SourceService sourceService;

    private ComboBox<String> comboListaEstadisticas= new ComboBox<String>();
    private Grid<Estadistica> grillaClientesPorSucursal = new Grid<Estadistica>();
    private Grid<Estadistica> grillaClientePorSucursalSource = new Grid<Estadistica>();
    private Grid<Estadistica> grillaClientePorAccion = new Grid<Estadistica>();
    private VerticalLayout clientesPorBranch = new VerticalLayout();
    private VerticalLayout sourcePorBranch = new VerticalLayout();
    private VerticalLayout graficoYgrillaSourceByBranch = new VerticalLayout();
    private VerticalLayout graficoPorAccion = new VerticalLayout();

    private String CLIENTE_POR_SUCURSAL="Number of customers per branch";
    private String SOURCE_POR_SUCURSAL = "Number of clients per branch by source";
    private String CLIENTES_POR_ACCION = "Number of clients per action";

    ApexCharts pieChartClienteSucursalSource=new ApexCharts();
    private DatePicker fechaDesde = new DatePicker("From");
    private DatePicker fechaHasta = new DatePicker("To");
    private ComboBox <Sucursal> comboSucursales = new ComboBox<Sucursal>("Branch");

    private DatePicker fechaDesdeAccion = new DatePicker("From");
    private DatePicker fechaHastaAccion = new DatePicker("To");
    private ApexCharts barChart= new ApexCharts();
    private VerticalLayout scroll = new VerticalLayout();
    HorizontalLayout fechasAccion= new HorizontalLayout();

    public DashboardView(SucursalService sucursalService, ClienteService clienteService, SourceService sourceService) {
        this.sucursalService = sucursalService;
        this.clienteService = clienteService;
        this.sourceService = sourceService;

        comboListaEstadisticas.setItems(CLIENTE_POR_SUCURSAL,SOURCE_POR_SUCURSAL,CLIENTES_POR_ACCION);
        comboListaEstadisticas.setClearButtonVisible(true);
        comboListaEstadisticas.setLabel("Statistics");
        comboListaEstadisticas.setWidth("30%");
        comboListaEstadisticas.setValue(CLIENTE_POR_SUCURSAL);


        crearClientesPorSucursal();
        crearSourcePorSucursal();
        crearGraficoAccionCliente();

        comboListaEstadisticas.addValueChangeListener(event -> {
            cambioEstadistica(event.getValue()); });

        addClassName("dashboard-view");

        setSizeFull();
        //setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        LocalDate hoyMenosMes =LocalDate.now();
        fechaDesdeAccion.setLocale(Locale.UK);
        LocalDate fechaMenosUnMes =hoyMenosMes.minusMonths(1);
        fechaDesdeAccion.setValue(fechaMenosUnMes);

        LocalDate hoy = LocalDate.now();
        fechaHastaAccion.setLocale(Locale.UK);
        fechaHastaAccion.setValue(hoy);

        add(comboListaEstadisticas, clientesPorBranch, sourcePorBranch, graficoPorAccion);

    }

    private void crearGraficoAccionCliente()
    {



        barChart.setColors("#2bd991","#2031c7","#e01814", "#f5f518","#14f5f5");
        grillaClientePorAccion.setClassName("contact-form");
        grillaClientePorAccion.setSizeFull();
        grillaClientePorAccion.removeAllColumns();
        grillaClientePorAccion.addColumn(
                Estadistica->Estadistica.getNombre())
                .setHeader("Name").setResizable(true);
        grillaClientePorAccion.addColumn(
                Estadistica->Estadistica.getCantidad())
                .setHeader("Quantity").setResizable(true)
                .setSortable(true);
        grillaClientePorAccion.addColumn(
                Estadistica->Estadistica.getPorcentaje()+"%")
                .setHeader("Percentage").setResizable(true);
        grillaClientePorAccion.getColumns().forEach(col->col.setAutoWidth(true));

      //  grillaClientePorAccion.setItems(listaDeEstadisticas);
        grillaClientePorAccion.setSizeFull();
        barChart.setHeight("400");
        barChart.setWidth("800");

        LocalDate hoyMenosMes =LocalDate.now();
        fechaDesdeAccion.setLocale(Locale.UK);
        LocalDate fechaMenosUnMes =hoyMenosMes.minusMonths(1);
        fechaDesdeAccion.setValue(fechaMenosUnMes);

        LocalDate hoy = LocalDate.now();
        fechaHastaAccion.setLocale(Locale.UK);
        fechaHastaAccion.setValue(hoy);

        fechaDesdeAccion.addValueChangeListener(event -> refrescarGraficoAccionCliente());
        fechaHastaAccion.addValueChangeListener(event -> refrescarGraficoAccionCliente());



        fechasAccion.add(fechaDesdeAccion, fechaHastaAccion);
        graficoPorAccion.add(fechasAccion);
        graficoPorAccion.add(barChart);
        graficoPorAccion.add(scroll);
        graficoPorAccion.setSizeFull();
        graficoPorAccion.setVisible(false);
        refrescarGraficoAccionCliente();
    }

    private void refrescarGraficoAccionCliente()
    {
        if(fechaDesdeAccion==null || fechaHastaAccion==null)
        {
            return;
        }
        Date fechaDesdeDate =Date.valueOf(fechaDesdeAccion.getValue());
        Date fechaHastaDate = Date.valueOf(fechaHastaAccion.getValue().plusDays(1));
        Series serieRegistrado = new Series("Registered");
        Series serieAppoiment = new Series("Appoiment");
        Series serieShowUp = new Series("Show up");
        Series serieSale = new Series("Sale");


        List<Sucursal>listaSucursales = sucursalService.buscarTodasActivas();
        Object[] objetosRegistrados = new Object[listaSucursales.size()];
        Object[] objetosAppoiment = new Object[listaSucursales.size()];
        Object[] objetosShowUp = new Object[listaSucursales.size()];
        Object[] objetosSale = new Object[listaSucursales.size()];
        List<String> listaNombreSucursales = new ArrayList<String>(listaSucursales.size());

        List<Div> listaGrillas = new ArrayList<Div>();

        for(int a=0;a<listaSucursales.size();a++)
        {
            Sucursal sucursalAhora = listaSucursales.get(a);
            Div prueba = new Div();
            prueba.setTitle(sucursalAhora.getName());
            Grid<Estadistica> grillaNueva= new Grid<Estadistica>();

            grillaNueva.setClassName("contact-form");
            grillaNueva.setSizeFull();
            grillaNueva.removeAllColumns();
            grillaNueva.addColumn(
                    Estadistica->Estadistica.getNombre())
                    .setHeader(sucursalAhora.getName()).setResizable(true);
            grillaNueva.addColumn(
                    Estadistica->Estadistica.getCantidad())
                    .setHeader("Quantity").setResizable(true)
                    .setSortable(true);
            grillaNueva.addColumn(
                    Estadistica->Estadistica.getPorcentaje()+"%")
                    .setHeader("Percentage").setResizable(true);
            grillaNueva.getColumns().forEach(col->col.setAutoWidth(true));


            grillaNueva.setSizeFull();

            List<Estadistica> listaDeEstadisticas = new ArrayList<Estadistica>();

            Integer cantidadClientesRegistrados =Integer.valueOf(clienteService.getTodosClientesPorSucursalFecha(sucursalAhora.getId(), fechaDesdeDate, fechaHastaDate).size());
            objetosRegistrados[a]=cantidadClientesRegistrados;
            Estadistica estadisticaRegistrado= new Estadistica();
            estadisticaRegistrado.setNombre("Registered");
            estadisticaRegistrado.setCantidad(cantidadClientesRegistrados);
            estadisticaRegistrado.setPorcentaje(100);
            listaDeEstadisticas.add(estadisticaRegistrado);

            Integer cantidadClienteApp = Integer.valueOf(clienteService.getTodosClientesAppoimentSucursalFecha(sucursalAhora.getId(), fechaDesdeDate, fechaHastaDate).size());
            objetosAppoiment[a]=cantidadClienteApp;
            Estadistica estadisticaAppoiment = new Estadistica();
            estadisticaAppoiment.setNombre("Appoiment");
            estadisticaAppoiment.setCantidad(cantidadClienteApp);
            float porcentajeApp=0;
            if(cantidadClientesRegistrados>0) {
                porcentajeApp = (cantidadClienteApp * 100) / cantidadClientesRegistrados;
            }
            int porcentajeAppInt = (int)(porcentajeApp*100);
            porcentajeApp = ((float) porcentajeAppInt)/100;
            estadisticaAppoiment.setPorcentaje(porcentajeApp);
            listaDeEstadisticas.add(estadisticaAppoiment);

            Integer cantidadClienteShowUp = Integer.valueOf(clienteService.getTodosClientesShowUpSucursalFecha(sucursalAhora.getId(), fechaDesdeDate, fechaHastaDate).size());
            objetosShowUp[a]=cantidadClienteShowUp;
            Estadistica estadisticaShowUp = new Estadistica();
            estadisticaShowUp.setNombre("Show Up");
            estadisticaShowUp.setCantidad(cantidadClienteShowUp);
            float porcentajeShowUp=0;
            if(cantidadClientesRegistrados>0) {
                porcentajeShowUp = (cantidadClienteShowUp * 100) / cantidadClientesRegistrados;
            }
            int porcentajeShowUpInt = (int)(porcentajeShowUp*100);
            porcentajeShowUp= ((float)porcentajeShowUpInt)/100;
            estadisticaShowUp.setPorcentaje(porcentajeShowUp);
            listaDeEstadisticas.add(estadisticaShowUp);

            Integer cantidadClientesSale = Integer.valueOf(clienteService.getTodosClientesSaleSucursalFecha(sucursalAhora.getId(), fechaDesdeDate, fechaHastaDate).size());
            objetosSale[a]=cantidadClientesSale;
            Estadistica estadisticaSale = new Estadistica();
            estadisticaSale.setNombre("Sale");
            estadisticaSale.setCantidad(cantidadClientesSale);
            float porcentajeSale=0;
            if(cantidadClientesRegistrados>0) {
                porcentajeSale = (cantidadClientesSale * 100) / cantidadClientesRegistrados;
            }
            int porcentajeSaleInt = (int)(porcentajeSale*100);
            porcentajeSale = ((float) porcentajeSaleInt)/100;
            estadisticaSale.setPorcentaje(porcentajeSale);
            listaDeEstadisticas.add(estadisticaSale);

            grillaNueva.setItems(listaDeEstadisticas);
            prueba.add(grillaNueva);
            prueba.setSizeFull();
            listaGrillas.add(prueba);

            listaNombreSucursales.add(sucursalAhora.getName());
        }

        serieRegistrado.setData(objetosRegistrados);
        serieAppoiment.setData(objetosAppoiment);
        serieShowUp.setData(objetosShowUp);
        serieSale.setData(objetosSale);


        barChart = ApexChartsBuilder.get()
                .withChart(ChartBuilder.get()
                        .withType(Type.bar)
                        .build())
                .withPlotOptions(PlotOptionsBuilder.get()
                        .withBar(BarBuilder.get()
                                .withHorizontal(false)
                                .withColumnWidth("55%")
                                .build())
                        .build())
                .withDataLabels(DataLabelsBuilder.get()
                        .withEnabled(false).build())
                .withStroke(StrokeBuilder.get()
                        .withShow(true)
                        .withWidth(2.0)
                        .withColors("transparent")
                        .build())
                .withSeries(serieRegistrado,
                        serieAppoiment,
                        serieShowUp,
                        serieSale)
                .withYaxis(YAxisBuilder.get()
                        .withTitle(TitleBuilder.get()
                                .withText("Clients")
                                .build())
                        .build())
                .withXaxis(XAxisBuilder.get().withCategories(listaNombreSucursales).build())
                .withFill(FillBuilder.get()
                        .withOpacity(1.0).build())
                .withTooltip(TooltipBuilder.get()
                        .withY(YBuilder.get()
                                .withFormatter("function (val) {\n" + // Formatter currently not yet working
                                        "return \" \" + val + \" \"\n" +
                                        "}").build())
                        .build())
                .build();
        barChart.setHeight("400");
        barChart.setWidth("800");
        barChart.setColors("#2bd991","#2031c7","#e01814", "#f5f518","#14f5f5");
        scroll.removeAll();
        //scroll.getStyle().set("overflow-y", "auto");
        int largo = listaSucursales.size()*250;
        scroll.setHeight(largo+"px");

        for(int a=0;a<listaGrillas.size();a++)
        {
            Div divPrueba = listaGrillas.get(a);
            scroll.add(divPrueba);
        }
        graficoPorAccion.removeAll();
        graficoPorAccion.add(fechasAccion);
        graficoPorAccion.add(barChart);
        graficoPorAccion.add(scroll);
        graficoPorAccion.setSizeFull();
    }

    private void crearSourcePorSucursal()
    {
        List<Sucursal> listaSucursales =new ArrayList<>();
        Sucursal sucursalTodas = new Sucursal();
        sucursalTodas.setName("All");
        listaSucursales.add(sucursalTodas);
        listaSucursales.addAll(this.sucursalService.buscarTodasActivas());



        LocalDate hoyMenosMes =LocalDate.now();
        fechaDesde.setLocale(Locale.UK);
        LocalDate fechaMenosUnMes =hoyMenosMes.minusMonths(1);
        fechaDesde.setValue(fechaMenosUnMes);

        LocalDate hoy = LocalDate.now();
        fechaHasta.setLocale(Locale.UK);
        fechaHasta.setValue(hoy);

        fechaDesde.addValueChangeListener(event -> eligioSucursalSource());
        fechaHasta.addValueChangeListener(event -> eligioSucursalSource());

        comboSucursales.setItems(listaSucursales);
        comboSucursales.setItemLabelGenerator(Sucursal::getName);
        comboSucursales.addValueChangeListener(
                event -> {
                    eligioSucursalSource();
                }
        );
        graficoYgrillaSourceByBranch.setSizeFull();
        HorizontalLayout horizontal = new HorizontalLayout();
        horizontal.add(comboSucursales, fechaDesde,fechaHasta);
        horizontal.setDefaultVerticalComponentAlignment(Alignment.END);
        sourcePorBranch.add(horizontal, graficoYgrillaSourceByBranch);
        pieChartClienteSucursalSource.setColors("#2bd991","#2031c7","#e01814", "#f5f518","#14f5f5");
        sourcePorBranch.setHorizontalComponentAlignment(Alignment.CENTER,pieChartClienteSucursalSource);
        sourcePorBranch.setSizeFull();
        sourcePorBranch.setVisible(false);
    }

    private void crearClientesPorSucursal()
    {
        List<Sucursal> listaSucursales= this.sucursalService.buscarTodasActivas();
        String[] nombre = new String[listaSucursales.size()*2];
        Double [] modulos = new Double[listaSucursales.size()*2];
        List<Estadistica> listaDeEstadisticas = new ArrayList<>();
        int totalClientes=0;
        for(int j=0;j<listaSucursales.size();j++)
        {
            nombre[j*2]= listaSucursales.get(j).getName();
            Estadistica esta = new Estadistica();
            esta.setNombre(listaSucursales.get(j).getName());
            List<Cliente> clienteDeSucursal = clienteService.getClienteActivosPorSucursal(listaSucursales.get(j).getId());
            Double cantidad = Double.valueOf(clienteDeSucursal.size()+"");
            modulos[j*2]=cantidad;
            esta.setCantidad(clienteDeSucursal.size());
            totalClientes = totalClientes+clienteDeSucursal.size();
            listaDeEstadisticas.add(esta);

            nombre[(j*2)+1]= listaSucursales.get(j).getName()+" Tentative";
            esta = new Estadistica();
            esta.setNombre(listaSucursales.get(j).getName()+" Tentative");
            clienteDeSucursal = clienteService.getClienteTentativePorSucursal(listaSucursales.get(j).getId());
            cantidad = Double.valueOf(clienteDeSucursal.size()+"");
            modulos[(j*2)+1]=cantidad;
            esta.setCantidad(clienteDeSucursal.size());
            totalClientes = totalClientes+clienteDeSucursal.size();
            listaDeEstadisticas.add(esta);

        }

        for(int a=0;a<listaDeEstadisticas.size();a++)
        {
            Estadistica esta = listaDeEstadisticas.get(a);
            float porcentaje = (esta.getCantidad()*100)/totalClientes;

            //int porcentajeInt = (int)(porcentaje*100);
            //porcentaje = ((float)porcentajeInt)/100;
            esta.setPorcentaje(porcentaje);
            listaDeEstadisticas.remove(a);
            listaDeEstadisticas.add(a,esta);
        }
        Estadistica estadisticaTotal = new Estadistica();
        estadisticaTotal.setNombre("Total");
        estadisticaTotal.setCantidad(totalClientes);
        estadisticaTotal.setPorcentaje(100);
        listaDeEstadisticas.add(estadisticaTotal);

        grillaClientesPorSucursal.setClassName("contact-form");
        grillaClientesPorSucursal.setSizeFull();
        grillaClientesPorSucursal.removeAllColumns();
        grillaClientesPorSucursal.addColumn(
                Estadistica->Estadistica.getNombre())
                .setHeader("Name").setResizable(true);
        grillaClientesPorSucursal.addColumn(
                Estadistica->Estadistica.getCantidad())
                .setHeader("Quantity").setResizable(true)
                .setSortable(true);
        grillaClientesPorSucursal.addColumn(
                Estadistica->Estadistica.getPorcentaje()+"%")
                .setHeader("Percentage").setResizable(true);
        grillaClientesPorSucursal.getColumns().forEach(col->col.setAutoWidth(true));

        grillaClientesPorSucursal.setItems(listaDeEstadisticas);
        grillaClientesPorSucursal.setSizeFull();



        ApexCharts pieChart = ApexChartsBuilder.get()
                .withChart(ChartBuilder.get().withType(Type.pie).build())
                .withLabels(nombre)
                .withLegend(LegendBuilder.get()
                        .withPosition(Position.right)
                        .build())
                .withSeries(modulos)
                .withResponsive(ResponsiveBuilder.get()
                        .withBreakpoint(480.0)
                        .withOptions(OptionsBuilder.get()
                                .withLegend(LegendBuilder.get()
                                        .withPosition(Position.bottom)
                                        .build())
                                .build())
                        .build())
                .build();

        pieChart.setWidth("60%");


        pieChart.setColors("#2bd991","#2031c7","#e01814", "#f5f518","#14f5f5","#a021db","#fa05c1","#05acfa","#fab005");


        clientesPorBranch.setSizeFull();
        clientesPorBranch.add(pieChart, grillaClientesPorSucursal);
        clientesPorBranch.setHorizontalComponentAlignment(Alignment.CENTER,pieChart);

    }

    private Component getContactStats() {
        Span stats = new Span(5 + " contacts");
        stats.addClassName("contact-stats");
        return stats;
    }

    private void cambioEstadistica(String valor)
    {
            clientesPorBranch.setVisible(false);
            sourcePorBranch.setVisible(false);
            graficoPorAccion.setVisible(false);
            if(valor==null)
            {
                return;
            }
        if(valor.equals(this.CLIENTE_POR_SUCURSAL)) {
            clientesPorBranch.setVisible(true);
        }else if(valor.equals(this.SOURCE_POR_SUCURSAL))
        {
            sourcePorBranch.setVisible(true);
        }else if(valor.equals(this.CLIENTES_POR_ACCION))
        {
            graficoPorAccion.setVisible(true);
        }

    }

    private void eligioSucursalSource()
    {
        if(fechaDesde.getValue()==null || fechaHasta.getValue()==null)
        {
            return;
        }
        Sucursal sucursal=comboSucursales.getValue();
        if(sucursal==null)
        {
            return;
        }
        if(sucursal!=null && sucursal.getId()!=null) {
            List<Source> listaSource = sourceService.buscarTodosActivos();
            String[] nombre = new String[listaSource.size()];
            Double[] modulos = new Double[listaSource.size()];
            List<Estadistica> listaDeEstadisticas = new ArrayList<>();
            int totalClientes = 0;
            for (int j = 0; j < listaSource.size(); j++) {
                nombre[j] = listaSource.get(j).getName();
                Estadistica esta = new Estadistica();
                esta.setNombre(listaSource.get(j).getName());
                Date fechaDesdeDate =Date.valueOf(fechaDesde.getValue());
                Date fechaHastaDate = Date.valueOf(fechaHasta.getValue().plusDays(1));
                List<Cliente> clienteDeSucursalSource =clienteService.getClientePorSucursalYSource(sucursal.getId(),listaSource.get(j).getId(), fechaDesdeDate, fechaHastaDate);
                Double cantidad = Double.valueOf(clienteDeSucursalSource.size() + "");
                modulos[j] = cantidad;
                esta.setCantidad(clienteDeSucursalSource.size());
                totalClientes = totalClientes + clienteDeSucursalSource.size();
                listaDeEstadisticas.add(esta);
            }

            if(totalClientes>0) {
                for (int a = 0; a < listaDeEstadisticas.size(); a++) {
                    Estadistica esta = listaDeEstadisticas.get(a);
                    float porcentaje = (esta.getCantidad() * 100) / totalClientes;

                    int porcentajeInt = (int) (porcentaje * 100);
                    porcentaje = ((float) porcentajeInt) / 100;
                    esta.setPorcentaje(porcentaje);
                    listaDeEstadisticas.remove(a);
                    listaDeEstadisticas.add(a, esta);
                }
            }
            Estadistica estadisticaTotal = new Estadistica();
            estadisticaTotal.setNombre("Total");
            estadisticaTotal.setCantidad(totalClientes);
            estadisticaTotal.setPorcentaje(100);
            listaDeEstadisticas.add(estadisticaTotal);

            grillaClientePorSucursalSource.setClassName("contact-form");
            grillaClientePorSucursalSource.setSizeFull();
            grillaClientePorSucursalSource.removeAllColumns();
            grillaClientePorSucursalSource.addColumn(
                    Estadistica -> Estadistica.getNombre())
                    .setHeader("Name").setResizable(true);
            grillaClientePorSucursalSource.addColumn(
                    Estadistica -> Estadistica.getCantidad())
                    .setHeader("Quantity").setResizable(true)
                    .setSortable(true);
            grillaClientePorSucursalSource.addColumn(
                    Estadistica -> Estadistica.getPorcentaje() + "%")
                    .setHeader("Percentage").setResizable(true);
            grillaClientePorSucursalSource.getColumns().forEach(col -> col.setAutoWidth(true));

            grillaClientePorSucursalSource.setItems(listaDeEstadisticas);
            grillaClientePorSucursalSource.setSizeFull();


            pieChartClienteSucursalSource = ApexChartsBuilder.get()
                    .withChart(ChartBuilder.get().withType(Type.pie).build())
                    .withLabels(nombre)
                    .withLegend(LegendBuilder.get()
                            .withPosition(Position.right)
                            .build())
                    .withSeries(modulos)
                    .withResponsive(ResponsiveBuilder.get()
                            .withBreakpoint(480.0)
                            .withOptions(OptionsBuilder.get()
                                    .withLegend(LegendBuilder.get()
                                            .withPosition(Position.bottom)
                                            .build())
                                    .build())
                            .build())
                    .build();


            pieChartClienteSucursalSource.setColors("#2bd991","#2031c7","#e01814", "#f5f518","#14f5f5","#a021db","#fa05c1","#05acfa","#fab005");
            graficoYgrillaSourceByBranch.removeAll();
            graficoYgrillaSourceByBranch.add(pieChartClienteSucursalSource);
            graficoYgrillaSourceByBranch.add(grillaClientePorSucursalSource);
            graficoYgrillaSourceByBranch.setHorizontalComponentAlignment(Alignment.CENTER,pieChartClienteSucursalSource);

            pieChartClienteSucursalSource.setWidth("60%");
            pieChartClienteSucursalSource.setWidth("60%");

            pieChartClienteSucursalSource.setVisible(true);
            grillaClientePorSucursalSource.setVisible(true);




        }else if(sucursal!=null && sucursal.getId()==null)
        {
            List<Source> listaSource = sourceService.buscarTodosActivos();
            String[] nombre = new String[listaSource.size()];
            Double[] modulos = new Double[listaSource.size()];
            List<Estadistica> listaDeEstadisticas = new ArrayList<>();
            int totalClientes = 0;
            for (int j = 0; j < listaSource.size(); j++) {
                nombre[j] = listaSource.get(j).getName();
                Estadistica esta = new Estadistica();
                esta.setNombre(listaSource.get(j).getName());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date fechaDesdeDate =Date.valueOf(fechaDesde.getValue());
                Date fechaHastaDate = Date.valueOf(fechaHasta.getValue().plusDays(1));
                List<Cliente> clienteDeSucursalSource =clienteService.getClientePorSource(listaSource.get(j).getId(), fechaDesdeDate, fechaHastaDate);
                Double cantidad = Double.valueOf(clienteDeSucursalSource.size() + "");
                modulos[j] = cantidad;
                esta.setCantidad(clienteDeSucursalSource.size());
                totalClientes = totalClientes + clienteDeSucursalSource.size();
                listaDeEstadisticas.add(esta);
            }

            if(totalClientes>0) {
                for (int a = 0; a < listaDeEstadisticas.size(); a++) {
                    Estadistica esta = listaDeEstadisticas.get(a);
                    float porcentaje = (esta.getCantidad() * 100) / totalClientes;

                    int porcentajeInt = (int) (porcentaje * 100);
                    porcentaje = ((float) porcentajeInt) / 100;
                    esta.setPorcentaje(porcentaje);
                    listaDeEstadisticas.remove(a);
                    listaDeEstadisticas.add(a, esta);
                }
            }
            Estadistica estadisticaTotal = new Estadistica();
            estadisticaTotal.setNombre("Total");
            estadisticaTotal.setCantidad(totalClientes);
            estadisticaTotal.setPorcentaje(100);
            listaDeEstadisticas.add(estadisticaTotal);

            grillaClientePorSucursalSource.setClassName("contact-form");
            grillaClientePorSucursalSource.setSizeFull();
            grillaClientePorSucursalSource.removeAllColumns();
            grillaClientePorSucursalSource.addColumn(
                    Estadistica -> Estadistica.getNombre())
                    .setHeader("Name").setResizable(true);
            grillaClientePorSucursalSource.addColumn(
                    Estadistica -> Estadistica.getCantidad())
                    .setHeader("Quantity").setResizable(true)
                    .setSortable(true);
            grillaClientePorSucursalSource.addColumn(
                    Estadistica -> Estadistica.getPorcentaje() + "%")
                    .setHeader("Percentage").setResizable(true);
            grillaClientePorSucursalSource.getColumns().forEach(col -> col.setAutoWidth(true));

            grillaClientePorSucursalSource.setItems(listaDeEstadisticas);
            grillaClientePorSucursalSource.setSizeFull();


            pieChartClienteSucursalSource = ApexChartsBuilder.get()
                    .withChart(ChartBuilder.get().withType(Type.pie).build())
                    .withLabels(nombre)
                    .withLegend(LegendBuilder.get()
                            .withPosition(Position.right)
                            .build())
                    .withSeries(modulos)
                    .withResponsive(ResponsiveBuilder.get()
                            .withBreakpoint(480.0)
                            .withOptions(OptionsBuilder.get()
                                    .withLegend(LegendBuilder.get()
                                            .withPosition(Position.bottom)
                                            .build())
                                    .build())
                            .build())
                    .build();
            pieChartClienteSucursalSource.setColors("#2bd991","#2031c7","#e01814", "#f5f518","#14f5f5","#a021db","#fa05c1","#05acfa","#fab005");
            graficoYgrillaSourceByBranch.removeAll();
            graficoYgrillaSourceByBranch.add(pieChartClienteSucursalSource);
            graficoYgrillaSourceByBranch.add(grillaClientePorSucursalSource);
            graficoYgrillaSourceByBranch.setHorizontalComponentAlignment(Alignment.CENTER,pieChartClienteSucursalSource);

            pieChartClienteSucursalSource.setWidth("60%");
            pieChartClienteSucursalSource.setWidth("60%");

            pieChartClienteSucursalSource.setVisible(true);
            grillaClientePorSucursalSource.setVisible(true);

        }
        else
        {
            pieChartClienteSucursalSource.setVisible(false);
            grillaClientePorSucursalSource.setVisible(false);
        }
    }

}