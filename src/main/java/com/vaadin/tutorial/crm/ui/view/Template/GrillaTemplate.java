package com.vaadin.tutorial.crm.ui.view.Template;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.backend.entity.Template;
import com.vaadin.tutorial.crm.backend.service.TemplateService;
import com.vaadin.tutorial.crm.ui.Constante;
import com.vaadin.tutorial.crm.ui.MainLayout;

import java.util.List;

@Route(value = "GrillaTemplate", layout = MainLayout.class)
@PageTitle("Templates | Certified Autos CRM")
public class GrillaTemplate extends VerticalLayout {

    private TemplateService templateService;
    private Notification notification;
    private Grid<Template> grid = new Grid<>(Template.class);
    private TextField txtFiltro = new TextField("Name");
    private Button botonBuscarPorFiltro = new Button("Search", event ->buscarPorFiltro());
    private Button botonCrearNuevoTemplate = new Button("New", event -> crearNuevoTemplate());

    public GrillaTemplate(TemplateService templateService)
    {
        this.templateService=templateService;
        this.setSizeFull();

        this.configureGrid();

        HorizontalLayout hBotones = new HorizontalLayout();
        hBotones.add(botonBuscarPorFiltro,botonCrearNuevoTemplate);

        this.add(txtFiltro,hBotones,grid);
    }

    private void buscarPorFiltro()
    {
        String nombre=null;
        if(txtFiltro.getValue()!=null && txtFiltro.getValue().trim().length()>0)
        {
            nombre = txtFiltro.getValue().trim();
        }
        List<Template> listaTemplateResultado =this.templateService.getListaTemplatePorFiltro(nombre);
        this.grid.setItems(listaTemplateResultado);
    }

    private void configureGrid() {

        grid.setSizeFull();
        grid.removeAllColumns();
        grid.addColumn(
                Template -> Template.getNombre())
                .setHeader("Name").setResizable(true);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.addItemDoubleClickListener(event -> {
            mostrarTemplate(event.getItem());
        });
    }

    private void mostrarTemplate(Template template)
    {
        if(template !=null) {
            UI.getCurrent().navigate("DatosTemplate/"+String.valueOf(template.getId())+"/"+ String.valueOf(Constante.OPERACIONES_ABN.MODIFICACION)+"/"+"GrillaTemplate");
        }
        buscarPorFiltro();
    }

    private void crearNuevoTemplate()
    {
        UI.getCurrent().navigate("DatosTemplate");
        buscarPorFiltro();
    }
}
