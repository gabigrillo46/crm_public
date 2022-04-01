package com.vaadin.tutorial.crm.ui.view.SmsMasivo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.backend.entity.*;
import com.vaadin.tutorial.crm.backend.service.*;
import com.vaadin.tutorial.crm.ui.Constante;
import com.vaadin.tutorial.crm.ui.MainLayout;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Route(value = "MensajesMasivos", layout = MainLayout.class)
@PageTitle("SMS Broadcast | Certified Autos CRM")
public class MensajesMasivos extends VerticalLayout {

    private TextField txtPalabra = new TextField("Word in observation field");
    private ComboBox<Sucursal> cmbSucursal = new ComboBox<>("Branch");
    private TextField txtTelefono = new TextField("Mobile");
    private Checkbox chkTentativa = new Checkbox("Tentative");
    private Checkbox chkCallToMade = new Checkbox("Call to be made");
    private Checkbox chkAppoiment = new Checkbox("Appoiment");

    private ComboBox<Template> cmbTemplate = new ComboBox<Template>("Template");

    private Button botonBuscarClientesFiltro = new Button("Search", event -> buscarClientesPorFiltro());
    private Grid<Cliente> grillaClientesFiltro = new Grid<>();
    private List<Cliente> listaClientesFiltro = new ArrayList<>();
    private List<Cliente> listaClientesFiltroSeleccionados = new ArrayList<>();
    private Button botonAgregarCliente = new Button("Add", event -> agregarCliente());
    private Button botonEliminarCliente = new Button("Remove", event -> eliminarCliente());
    private Grid<Cliente> grillaClientesSeleccionados = new Grid<>();
    private List<Cliente> listaClientesSeleccionados = new ArrayList<>();
    private List<Cliente> listaClientesSeleccionadosSeleccionados = new ArrayList<>();
    private Button botonSeleccTemplate = new Button("select template", event -> seleccionarTemplate());
    private VerticalLayout verticalSeleccCliente = new VerticalLayout();
    private VerticalLayout verticalSeleccTemplate = new VerticalLayout();
    private FormLayout formularioFiltrosCliente = new FormLayout();
    private H4 resultados = new H4();
    private H4 clientesSelecc = new H4();


    private ClienteService clienteService;
    private TemplateService templateService;
    private SucursalService sucursalService;
    private UsersService usuarioService;
    private MensajeService mensajeService;

    private Notification notification = new Notification();
    private Span mensaje = new Span();

    private ComboBox<Template> cmbTemplates = new ComboBox<>("Template");
    private TextArea txtMensajeCustom = new TextArea("Custom SMS");
    private Button botonEnviarSMS = new Button("Send SMS", event -> enviarSMS());
    private Button botonAgregarMasClientes = new Button("Add more clients", event -> agregarMasClientes());
    private H4 cantidadCLienteEnviar = new H4();
    private Grid<Cliente> grillaMensajeResultado = new Grid<>();
    private List<Cliente> listaClienteMensajeResultado = new ArrayList<>();
    private H4 resultadosMensajes = new H4();

    Users usuarioConectado=null;
    Calendar c;




    public MensajesMasivos(ClienteService clienteService, TemplateService templateService,
                           SucursalService sucursalService, UsersService usuarioService,
                           MensajeService mensajeService) {
        this.clienteService = clienteService;
        this.templateService = templateService;
        this.sucursalService = sucursalService;
        this.usuarioService = usuarioService;
        this.mensajeService = mensajeService;

        this.configureGrid();
        this.cargarCombos();

        notification.add(mensaje);
        notification.setDuration(2000);


        notification.setPosition(Notification.Position.MIDDLE);

        formularioFiltrosCliente.setMaxWidth("900px");
        formularioFiltrosCliente.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));

        txtPalabra.setMaxWidth("300px");
        txtTelefono.setMaxWidth("300px");
        cmbSucursal.setMaxWidth("300px");


        formularioFiltrosCliente.add(txtPalabra, txtTelefono, cmbSucursal, chkAppoiment, chkCallToMade, chkTentativa);

        HorizontalLayout hBotonesAgregarQuitarClientes = new HorizontalLayout();
        hBotonesAgregarQuitarClientes.add(botonAgregarCliente, botonEliminarCliente, botonSeleccTemplate, clientesSelecc);
        hBotonesAgregarQuitarClientes.setDefaultVerticalComponentAlignment(Alignment.END);

        HorizontalLayout hBotonBuscarResultado = new HorizontalLayout();
        hBotonBuscarResultado.add(botonBuscarClientesFiltro, resultados);

        verticalSeleccCliente.add(formularioFiltrosCliente,
                hBotonBuscarResultado, grillaClientesFiltro,
                hBotonesAgregarQuitarClientes, grillaClientesSeleccionados);

        verticalSeleccCliente.setSizeFull();
        verticalSeleccTemplate.setVisible(false);

        cmbTemplate.addValueChangeListener(event -> eligioTemplate());

        HorizontalLayout hBotonesEnviarSMS = new HorizontalLayout();
        hBotonesEnviarSMS.add(botonEnviarSMS,botonAgregarMasClientes);

        cmbTemplate.setMaxWidth("300px");
        txtMensajeCustom.setMaxWidth("600px");

        FormLayout formulario =new FormLayout();
        formulario.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1));
        formulario.add(cmbTemplate,txtMensajeCustom);
        cmbTemplate.setMaxWidth("400px");
        txtMensajeCustom.setMaxWidth("600px");
        formulario.setMaxWidth("800px");

        txtMensajeCustom.setVisible(false);
        verticalSeleccTemplate.setSizeFull();
        resultadosMensajes.setText("Output:");
        resultadosMensajes.setVisible(false);
        grillaMensajeResultado.setVisible(false);
        verticalSeleccTemplate.add(cantidadCLienteEnviar,formulario,hBotonesEnviarSMS,resultadosMensajes, grillaMensajeResultado);

        this.setSizeFull();
        add(verticalSeleccCliente, verticalSeleccTemplate);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            usuarioConectado =this.usuarioService.buscarPorNombreUsuario(currentUserName);
        }

        TimeZone timeZone = TimeZone.getTimeZone("Australia/Sydney");
        c = Calendar.getInstance(timeZone);

    }

    private void cargarCombos() {
        cmbTemplate.setItems(this.templateService.getListaTemplateActivos());
        cmbTemplate.setItemLabelGenerator(Template::getNombre);
        cmbSucursal.setItems(this.sucursalService.buscarTodasActivas());
        cmbSucursal.setItemLabelGenerator(Sucursal::getName);
        Template temp = new Template();
        temp.setNombre("Custom");
        List<Template> listaTemplate = new ArrayList<>();
        listaTemplate.add(temp);
        listaTemplate.addAll(this.templateService.getListaTemplateActivos());
        cmbTemplate.setItems(listaTemplate);
        cmbTemplate.setItemLabelGenerator(Template::getNombre);
    }

    private void eligioTemplate()
    {
        if(this.cmbTemplate.getValue()!=null)
        {
            if(this.cmbTemplate.getValue().getId()==null)
            {
                txtMensajeCustom.setVisible(true);
                return;
            }
            else
            {
                txtMensajeCustom.setVisible(false);
            }
        }
        txtMensajeCustom.setVisible(false);
    }


    private void configureGrid() {
        grillaClientesFiltro.setSizeFull();
        grillaClientesFiltro.removeAllColumns();
        grillaClientesFiltro.addColumn(
                Cliente -> Cliente.getNombre() + " " + Cliente.getApellido())
                .setHeader("Full Name").setResizable(true);
        grillaClientesFiltro.addColumn(
                Cliente -> Cliente.getMovil())
                .setHeader("Mobile").setResizable(true);
        grillaClientesFiltro.getColumns().forEach(col -> col.setAutoWidth(true));

        grillaClientesFiltro.setSelectionMode(Grid.SelectionMode.MULTI);
        grillaClientesFiltro.asMultiSelect().addValueChangeListener(event -> multiSeleccCliente(event.getValue()));


        grillaClientesSeleccionados.setSizeFull();
        grillaClientesSeleccionados.removeAllColumns();
        grillaClientesSeleccionados.addColumn(
                Cliente -> Cliente.getNombre() + " " + Cliente.getApellido())
                .setHeader("Full Name").setResizable(true);
        grillaClientesSeleccionados.addColumn(
                Cliente -> Cliente.getMovil())
                .setHeader("Mobile").setResizable(true);
        grillaClientesSeleccionados.getColumns().forEach(col -> col.setAutoWidth(true));

        grillaClientesSeleccionados.setSelectionMode(Grid.SelectionMode.MULTI);
        grillaClientesSeleccionados.asMultiSelect().addValueChangeListener(event -> multiSeleccClienteSelecc(event.getValue()));


        grillaClientesFiltro.setSizeFull();
        grillaClientesFiltro.removeAllColumns();
        grillaClientesFiltro.addColumn(
                Cliente -> Cliente.getNombre() + " " + Cliente.getApellido())
                .setHeader("Full Name").setResizable(true);
        grillaClientesFiltro.addColumn(
                Cliente -> Cliente.getMovil())
                .setHeader("Mobile").setResizable(true);
        grillaClientesFiltro.getColumns().forEach(col -> col.setAutoWidth(true));

        grillaClientesFiltro.setSelectionMode(Grid.SelectionMode.MULTI);
        grillaClientesFiltro.asMultiSelect().addValueChangeListener(event -> multiSeleccCliente(event.getValue()));


        grillaMensajeResultado.setSizeFull();
        grillaMensajeResultado.removeAllColumns();
        grillaMensajeResultado.addColumn(
                Cliente -> Cliente.getNombre() + " " + Cliente.getApellido())
                .setHeader("Full Name").setResizable(true);
        grillaMensajeResultado.addColumn(
                Cliente -> Cliente.getMovil())
                .setHeader("Mobile").setResizable(true);
        grillaMensajeResultado.addColumn(
                Cliente -> Cliente.getObservacion())
                .setHeader("Message").setResizable(true);
        grillaMensajeResultado.getColumns().forEach(col -> col.setAutoWidth(true));

    }

    private void multiSeleccCliente(Set<Cliente> listaClientesSelecc) {
        this.listaClientesFiltroSeleccionados.clear();
        for (Cliente cli : listaClientesSelecc) {
            this.listaClientesFiltroSeleccionados.add(cli);
        }
    }

    private void multiSeleccClienteSelecc(Set<Cliente> listaClientesSelecc) {
        this.listaClientesSeleccionadosSeleccionados.clear();
        for (Cliente cli : listaClientesSelecc) {
            this.listaClientesSeleccionadosSeleccionados.add(cli);
        }
    }

    private void agregarCliente() {

        for(int a=0;a<this.listaClientesFiltroSeleccionados.size();a++)
        {
            Cliente clienteSelecc = this.listaClientesFiltroSeleccionados.get(a);
            if(clienteSelecc!=null)
            {
                boolean encontrado = false;
                for(int b=0;b<this.listaClientesSeleccionados.size();b++)
                {
                    Cliente clienteYaSeleccionado = this.listaClientesSeleccionados.get(b);
                    if(clienteYaSeleccionado.getId()==clienteSelecc.getId())
                    {
                        encontrado=true;
                    }
                }
                if(encontrado==false)
                {
                    this.listaClientesSeleccionados.add(clienteSelecc);
                }
            }
        }
        this.grillaClientesSeleccionados.setItems(this.listaClientesSeleccionados);
        this.clientesSelecc.setText("Selected Clients: "+this.listaClientesSeleccionados.size());
    }

    private void eliminarCliente() {
        for(int a =0;a<this.listaClientesSeleccionadosSeleccionados.size();a++)
        {
               Cliente clienteSeleccSelecc = this.listaClientesSeleccionadosSeleccionados.get(a);
               if(clienteSeleccSelecc!=null)
               {
                   this.listaClientesSeleccionados.remove(clienteSeleccSelecc);
               }
        }
        this.grillaClientesSeleccionados.setItems(this.listaClientesSeleccionados);
        this.clientesSelecc.setText("Selected Clients: "+this.listaClientesSeleccionados.size());
    }

    private void seleccionarTemplate() {
        if(this.listaClientesSeleccionados.size()==0)
        {
            mensaje.setText("You have to select at least one client.");
            notification.open();
            return;
        }
        this.verticalSeleccCliente.setVisible(false);
        this.verticalSeleccTemplate.setVisible(true);
        cantidadCLienteEnviar.setText("Messages will be sent to "+this.listaClientesSeleccionados.size()+" clients");
    }

    private void buscarClientesPorFiltro() {
        String palabraObservacion = null;
        String telefono = null;
        Long idSucursal = null;
        String calltobemade = null;
        Integer appoimentCheck = null;
        Integer Lost =null;
        Integer sale = null;

        if (txtPalabra.getValue() != null && txtPalabra.getValue().trim().length() > 0) {
            palabraObservacion = txtPalabra.getValue().trim();
        }

        if (txtTelefono.getValue() != null && txtTelefono.getValue().trim().length() > 0) {
            telefono = txtTelefono.getValue().trim();
        }

        if (cmbSucursal.getValue() != null) {
            idSucursal = cmbSucursal.getValue().getId();
        }
        if (chkCallToMade.getValue()) {
            calltobemade = "Yes";
        }
        if (chkAppoiment.getValue()) {
            appoimentCheck = Integer.valueOf(1);
        }
       // Lost = Constante.ESTADOS_CLIENTES.ACTIVO;
        sale = Constante.ESTADOS_CLIENTES.ACTIVO;
        //este es el que manda, si es tentative, vamos a desactivar los otros filtros
        if (chkTentativa.getValue()) {
            appoimentCheck = Integer.valueOf(0);
            calltobemade = "No";
        }

        if (telefono != null && palabraObservacion == null && idSucursal == null) {
            this.listaClientesFiltro=clienteService.getClientesPorTelefono(telefono);
        } else {
            this.listaClientesFiltro = clienteService.getClientesPorFiltro(null, palabraObservacion, telefono, idSucursal, calltobemade, appoimentCheck, Lost, sale, null, null, null);
        }
        this.grillaClientesFiltro.setItems(this.listaClientesFiltro);
        if(this.listaClientesFiltro.size()==0)
        {
            this.resultados.setText("No results found");
        }
        else
        {
            this.resultados.setText("Quantity: "+this.listaClientesFiltro.size());
        }
    }

    private void enviarSMS()
    {
        if(this.cmbTemplate.getValue() == null)
        {
            mensaje.setText("You have to selec a template");
            notification.open();
            return;
        }

        if(this.cmbTemplate.getValue().getId()==null && txtMensajeCustom.getValue().trim().length()==0)
        {
            mensaje.setText("you have to enter a custom sms");
            notification.open();
            return;
        }

        String mensajeTexto ="";
        if(this.cmbTemplate.getValue()!=null && this.cmbTemplate.getValue().getId()==null && txtMensajeCustom.getValue().trim().length()>0)
        {
            mensajeTexto=txtMensajeCustom.getValue().trim();
        }
        else
        {
            Template tempSelecc = this.cmbTemplate.getValue();
            if(tempSelecc!=null)
            {
                mensajeTexto = tempSelecc.getMensaje();
            }
        }
        if(mensajeTexto.trim().length()==0)
        {
            mensaje.setText("The message can't be empty");
            notification.open();
            return;
        }
        this.listaClienteMensajeResultado.clear();
        SenderSMSTelstra senderSMSTelstra = new SenderSMSTelstra();
        for(int l=0;l<this.listaClientesSeleccionados.size();l++)
        {

            Cliente clienteAEnviarSMS = this.listaClientesSeleccionados.get(l);
            if(clienteAEnviarSMS.getMovil().trim().length()==0)
            {
                clienteAEnviarSMS.setObservacion("The client has not defined the mobile");
                this.listaClienteMensajeResultado.add(clienteAEnviarSMS);
                continue;
            }
            String mensajeTexto2 = "Hi "+clienteAEnviarSMS.getNombre()+" "+mensajeTexto;
            String numero = clienteAEnviarSMS.getMovil();
            String repuesta =senderSMSTelstra.enviarSMSTelstra(numero, mensajeTexto2);
            if(repuesta.trim().length()>0)
            {
                JsonObject jsonObject = JsonParser.parseString(repuesta.trim()).getAsJsonObject();
                String codigo=jsonObject.get("code").getAsString();
                if(codigo!=null && codigo.trim().length()>0)
                {
                    clienteAEnviarSMS.setObservacion(codigo);
                }
                else {
                    clienteAEnviarSMS.setObservacion(repuesta);
                }
                this.listaClienteMensajeResultado.add(clienteAEnviarSMS);
            }
            else
            {
                Mensaje mensajeNuevo = new Mensaje();
                mensajeNuevo.setMensaje(mensajeTexto);
                mensajeNuevo.setNumero_destino(numero);
                mensajeNuevo.setUsuario(this.usuarioConectado);
                mensajeNuevo.setSentido(Mensaje.SALIDA);
                LocalDateTime ahora = c.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                mensajeNuevo.setFecha_hora(ahora);
                if (clienteAEnviarSMS != null && clienteAEnviarSMS.getId() != null) {
                    mensajeNuevo.setCliente(clienteAEnviarSMS);
                    this.mensajeService.saveMensaje(mensajeNuevo);
                }
                this.listaClientesSeleccionados.remove(clienteAEnviarSMS);
            }
        }
        if(this.listaClienteMensajeResultado.size()>0)
        {
            this.grillaMensajeResultado.setVisible(true);
            this.resultadosMensajes.setVisible(true);
        }
        else
        {
            this.grillaMensajeResultado.setVisible(false);
            this.resultadosMensajes.setVisible(false);
        }
        this.grillaMensajeResultado.setItems(this.listaClienteMensajeResultado);
        this.grillaClientesSeleccionados.setItems(this.listaClientesSeleccionados);
        if(this.listaClienteMensajeResultado.size()>0)
        {
            mensaje.setText("Messages sent but with some errors");
        }
        else
        {
            mensaje.setText("Messages sent successfully");
        }
        notification.open();

    }

    private void agregarMasClientes()
    {
        this.verticalSeleccTemplate.setVisible(false);
        this.verticalSeleccCliente.setVisible(true);
    }


}
