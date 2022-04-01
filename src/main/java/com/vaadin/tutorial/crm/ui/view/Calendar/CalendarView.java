package com.vaadin.tutorial.crm.ui.view.Calendar;

import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.backend.entity.Cliente;
import com.vaadin.tutorial.crm.backend.entity.Mensaje;
import com.vaadin.tutorial.crm.backend.entity.Sucursal;
import com.vaadin.tutorial.crm.backend.service.ClienteService;
import com.vaadin.tutorial.crm.backend.service.MensajeService;
import com.vaadin.tutorial.crm.backend.service.SucursalService;
import com.vaadin.tutorial.crm.ui.Constante;
import com.vaadin.tutorial.crm.ui.MainLayout;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.stefan.fullcalendar.Timezone;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Route(value = "CalendarView", layout = MainLayout.class)
@PageTitle("Calendar | Certified Autos CRM")
public class CalendarView extends VerticalLayout {
    FullCalendar calendar = FullCalendarBuilder.create().build();

    // Create a initial sample entry
    Entry entry = new Entry();
    Button buttonPrevious = new Button("Previous", VaadinIcon.ANGLE_LEFT.create(), e -> calendar.previous());
    Button buttonNext = new Button("Next", VaadinIcon.ANGLE_RIGHT.create(), e -> calendar.next());
    Button buttonToday = new Button("Today", VaadinIcon.HOME.create(), e -> calendar.today());
    private Button buttonDatePicker;
    ComboBox <Sucursal>comboSucursales = new ComboBox<Sucursal>();
    SucursalService sucursalService;
    ClienteService clienteService;
    MensajeService mensajeService;
    ZoneId zonaSydney;
    Timezone t;

    Span callRetail = new Span();
    Span callLong = new Span();
    Span callTruck = new Span();
    Span appRetail = new Span();
    Span appLong = new Span();
    Span appTruck = new Span();


    public  CalendarView(SucursalService sucursalService,ClienteService clienteService, MensajeService mensajeService)
    {
        this.mensajeService = mensajeService;
        this.sucursalService = sucursalService;
        this.clienteService = clienteService;
        setSizeFull();
        callRetail.setText("Call Retail");
        callRetail.getStyle().set("color","white");
        callRetail.setWidth("auto");
        callLong.setText("Call Long Term Rental");
        callLong.getStyle().set("color", "white");
        callTruck.setText("Call Truck");
        callTruck.getStyle().set("color", "white");
        appRetail.setText("App Retail");
        appRetail.getStyle().set("color","white");
        appLong.setText("App Long Term Rental");
        appLong.getStyle().set("color","white");
        appTruck.setText("App Truck");
        appTruck.getStyle().set("color","white");

        appRetail.getStyle().set("background-color","#ffa214");
        appTruck.getStyle().set("background-color","#c01cfc");
        appLong.getStyle().set("background-color","#ff3333");

        callRetail.getStyle().set("background-color","#0BE0E8");
        callTruck.getStyle().set("background-color","#000000");
        callLong.getStyle().set("background-color","#0000FF");






        setFlexGrow(1, calendar);
        this.cargarCombos();

        calendar.addEntryClickedListener(event -> {
            mostrarCliente(event.getEntry());
        });

        zonaSydney =  ZoneId.of( "Australia/Sydney" );
        t = new Timezone(zonaSydney);
        calendar.setTimezone(t);



        entry.setTitle("Some event");

        entry.setStart(LocalDate.now().withDayOfMonth(3).atTime(10, 0), calendar.getTimezone());
        entry.setEnd(entry.getStart().plusHours(2), calendar.getTimezone());
        entry.setColor("#ff3333");

        //calendar.addEntry(entry);
        HorizontalLayout temporalSelectorLayout = new HorizontalLayout();
        Button buttonPrevious = new Button("", VaadinIcon.ANGLE_LEFT.create(), e -> calendar.previous());
        Button buttonNext = new Button("", VaadinIcon.ANGLE_RIGHT.create(), e -> calendar.next());
        buttonNext.setIconAfterText(true);
        DatePicker gotoDate = new DatePicker();
        gotoDate.addValueChangeListener(event1 -> calendar.gotoDate(event1.getValue()));
        gotoDate.getElement().getStyle().set("visibility", "hidden");
        gotoDate.getElement().getStyle().set("position", "fixed");
        gotoDate.setWidth("0px");
        gotoDate.setHeight("0px");
        gotoDate.setWeekNumbersVisible(true);
        buttonDatePicker = new Button(VaadinIcon.CALENDAR.create());
        buttonDatePicker.getElement().appendChild(gotoDate.getElement());
        buttonDatePicker.addClickListener(event -> gotoDate.open());
        buttonDatePicker.setWidthFull();
        buttonToday.setMinWidth("100px");
        comboSucursales.setLabel("Branch");
        temporalSelectorLayout.setDefaultVerticalComponentAlignment(Alignment.END);

        temporalSelectorLayout.add(buttonToday, buttonPrevious, buttonDatePicker, buttonNext, gotoDate, comboSucursales);
        HorizontalLayout hColors = new HorizontalLayout();
        hColors.add(callRetail, callLong, callTruck, appRetail,appLong,appTruck);
        //temporalSelectorLayout.setSpacing(false);
//        HorizontalLayout botones = new HorizontalLayout();
//        botones.add(buttonToday, buttonPrevious, buttonNext, comboSucursales);
        add(temporalSelectorLayout,hColors,calendar);
   //     add(botones,calendar);
        comboSucursales.addValueChangeListener(event->{
           buscarAppoimentSucursal(event.getValue());
        });

        List<Sucursal> listaSucursales = sucursalService.buscarTodasActivas();
        comboSucursales.setValue(listaSucursales.get(0));
        buscarAppoimentSucursal(listaSucursales.get(0));

        calendar.addDatesRenderedListener(event -> {
            updateIntervalLabel(buttonDatePicker, event.getIntervalStart());
            System.out.println("dates rendered: " + event.getStart() + " " + event.getEnd());
        });





    }

    void updateIntervalLabel(HasText intervalLabel,  LocalDate intervalStart) {
        String text = "--";
        Locale locale = calendar.getLocale();


            text = intervalStart.format(DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(locale));

        intervalLabel.setText(text);
    }

    private void cargarCombos()
    {
        List<Sucursal> listaSucursales = new ArrayList<>();
        Sucursal sucursalTodas = new Sucursal();
        sucursalTodas.setName("All");
        listaSucursales.addAll(sucursalService.buscarTodasActivas());
     //   listaSucursales.add(0,sucursalTodas);
        comboSucursales.setItems(listaSucursales);
        comboSucursales.setClearButtonVisible(true);
        comboSucursales.setItemLabelGenerator(Sucursal::getName);
    }

    private void buscarAppoimentSucursal(Sucursal sucursalSelecc)
    {
        List<Cliente> clientesResultado=null;
        calendar.removeAllEntries();
        if(sucursalSelecc!=null)
        {
   /*         if(sucursalSelecc.getName().equalsIgnoreCase("All"))
            {
             clientesResultado = this.clienteService.getClienteConAppDeSucursal(null);
            }
            else {*/
                clientesResultado = this.clienteService.getClienteConAppDeSucursal(sucursalSelecc.getId());
       //     }
            for(int a = 0;a<clientesResultado.size();a++)
            {
                Cliente cliente = clientesResultado.get(a);
                if(cliente.getFecha_appoiment()!=null)
                {
                    Entry entrada = new Entry(cliente.getId()+"app");


                    Mensaje ultimoMensaje = this.mensajeService.getUltimoMensajeCliente(cliente.getId());
                    String titulo = cliente.getNombre()+" "+cliente.getApellido();
                    if(ultimoMensaje!=null && ultimoMensaje.getSentido()==Mensaje.SALIDA)
                    {
                        titulo =titulo+" "+"\u2713";
                    }
                    entrada.setTitle(titulo);

                    LocalDateTime fechaApp = cliente.getFecha_appoiment();



                    entrada.setStart(fechaApp,t);
                    entrada.setEnd(fechaApp,t);
                    ultimoMensaje = this.mensajeService.getUltimoMensajeCliente(cliente.getId());
                    if(ultimoMensaje!=null && ultimoMensaje.getSentido()==Mensaje.ENTRADA)
                    {
                        entrada.setColor("#00cc66");
                    }
                    else if(cliente.isIs_retail())
                    {

                        entrada.setColor("#ffa214");
                    }
                    else if(cliente.isIs_truck())
                    {
                        entrada.setColor("#c01cfc");
                    }
                    else {

                        entrada.setColor("#ff3333");
                    }
                    calendar.addEntry(entrada);
                }
            }

            clientesResultado = this.clienteService.getClienteSucursalConCall(sucursalSelecc.getId());
            for(int a=0;a<clientesResultado.size();a++)
            {
                Cliente cliente = clientesResultado.get(a);
                if(cliente.getCalltobemade().equalsIgnoreCase("Yes"))
                {
                    TimeZone timeZone = TimeZone.getTimeZone("Australia/Sydney");
                    Calendar c = Calendar.getInstance(timeZone);
                    LocalDateTime v=  new java.sql.Timestamp(
                            c.getTime().getTime()).toLocalDateTime();

                    v= LocalDateTime.now(zonaSydney);



                    Entry entrada = new Entry(cliente.getId()+"cal");

                    Mensaje ultimoMensaje = this.mensajeService.getUltimoMensajeCliente(cliente.getId());
                    String titulo = cliente.getNombre()+" "+cliente.getApellido();
                    if(ultimoMensaje!=null && ultimoMensaje.getSentido()==Mensaje.SALIDA)
                    {
                        titulo =titulo+" "+"\u2713";
                    }

                    entrada.setTitle(titulo);


                    if(cliente.getFecha_llamada()!=null)
                    {
                      /*  int hora =cliente.getFecha_llamada().getHour();
                        LocalDateTime fechaHoraInicio=cliente.getFecha_llamada();
                        if(hora>=12)
                        {
                           fechaHoraInicio= cliente.getFecha_llamada().plusHours(-12);
                        }*/

                        entrada.setStart(cliente.getFecha_llamada(),t);
                        entrada.setEnd(cliente.getFecha_llamada(),t);
                    }
                    else {

                        entrada.setStart(v,t);
                        entrada.setEnd(v,t);
                    }
                    ultimoMensaje = this.mensajeService.getUltimoMensajeCliente(cliente.getId());
                    if(ultimoMensaje!=null && ultimoMensaje.getSentido()==Mensaje.ENTRADA)
                    {
                        entrada.setColor("#00cc66");
                    }
                    else if(cliente.isIs_retail())
                    {

                        entrada.setColor("#0BE0E8");
                    }
                    else if(cliente.isIs_truck())
                    {

                        entrada.setColor("#000000");
                    }
                    else {

                        entrada.setColor("#0000FF");
                    }

                    calendar.addEntry(entrada);
                }
            }
        }
    }

    private void  mostrarCliente(Entry entrada)
    {
        if(entrada!=null)
        {
            String id = entrada.getId().substring(0, entrada.getId().length()-3);
            UI.getCurrent().navigate("ClientForm/"+id+"/"+ String.valueOf(Constante.OPERACIONES_ABN.MODIFICACION)+"/CalendarView");
        }
    }


}
