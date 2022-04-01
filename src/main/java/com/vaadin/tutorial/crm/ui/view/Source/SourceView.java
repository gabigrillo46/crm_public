package com.vaadin.tutorial.crm.ui.view.Source;

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
import com.vaadin.tutorial.crm.backend.entity.Source;
import com.vaadin.tutorial.crm.backend.service.SourceService;
import com.vaadin.tutorial.crm.ui.Constante;
import com.vaadin.tutorial.crm.ui.MainLayout;



@Route(value = "SourceView", layout = MainLayout.class)
@PageTitle("Source | Certified Autos CRM")
public class SourceView extends VerticalLayout {

    SourceService sourceService;
    private int accion = -1;
    private Source sourceActual=null;
    private Grid<Source> grid = new Grid<>(Source.class);
    private TextField filterText = new TextField();
    Span mensaje = new Span();
    Notification notification = new Notification(mensaje);
    private Button botonAddSource = new Button("Add source", event -> {
       createNewSource();
    });
    Dialog dialogoDatosSource = new Dialog();
    TextField txtNombre= new TextField("Name");
    Button botonAceptar = new Button("Submmit", event -> {
       aceptoDialogo();
    });
    Button botonCancelar = new Button("Cancel", event -> {
       cancelarDialogo();
    });

    Dialog dialogoEstaSeguro = new Dialog();
    Span mensajeEstaSeguro = new Span("Are you sure?");
    Button botonSiEstaSeguro = new Button("Yes", event -> {
       siEstaSeguroEliminar();
    });
    Button botonNoEstaSeguro = new Button("No", event -> {
       dialogoEstaSeguro.close();
    });





    public SourceView(SourceService sourceService)
    {

        this.sourceService= sourceService;
        notification.setDuration(2000);
        notification.setPosition(Notification.Position.MIDDLE);
        addClassName("source-view");
        setSizeFull();
        configureGrid();
        updateList();

        add(this.getToolbar(),grid);

        HorizontalLayout botonesAceptarCancelar = new HorizontalLayout();
        botonesAceptarCancelar.add(botonAceptar, botonCancelar);

        botonAceptar.addClickShortcut(Key.ENTER);

        dialogoDatosSource.add(txtNombre,
                botonesAceptarCancelar);

        GridContextMenu<Source> contextMenu = grid.addContextMenu();
        contextMenu.addItem("Edit", e -> {
            e.getItem().ifPresent(Source -> {
                editarSource(Source);
            });
        });

        contextMenu.addItem("Delete", event -> {
            event.getItem().ifPresent(Source->{
                abrirDialogoEstaSeguro(Source);
            });
        });

        HorizontalLayout botonAceptarCancelar = new HorizontalLayout();
        botonAceptarCancelar.add(botonSiEstaSeguro, botonNoEstaSeguro);
        dialogoEstaSeguro.add(mensajeEstaSeguro,botonAceptarCancelar);


    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());



        HorizontalLayout toolbar = new HorizontalLayout(filterText, botonAddSource);
        toolbar.addClassName("toolbar");

        return toolbar;
    }

    private void configureGrid() {
        grid.addClassName("source-grid");
        grid.setSizeFull();
        grid.setColumns("name");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        if(filterText.getValue()!=null && filterText.getValue().trim().length()>0) {
            grid.setItems(sourceService.getListoPorFiltro(filterText.getValue()));
        }
        else
        {
            grid.setItems(sourceService.buscarTodosActivos());
        }
    }

    private void createNewSource()
    {
        accion= Constante.OPERACIONES_ABN.ALTA;
        sourceActual = new Source();
        txtNombre.setValue("");
        dialogoDatosSource.open();
    }

    private void aceptoDialogo()
    {
        if(validarCampos()) {
            this.sourceActual.setName(txtNombre.getValue().trim());
            this.sourceActual.setEstado(Constante.ESTADOS_SOURCE.ACTIVO);
            sourceService.saveSource(this.sourceActual);
            mensaje.setText("Source saved");
            notification.open();
            dialogoDatosSource.close();
            updateList();
            this.sourceActual=null;
            this.accion=-1;
        }

    }

    private boolean validarCampos()
    {
        if(txtNombre.getValue()==null || (txtNombre.getValue()!=null && txtNombre.getValue().trim().length()==0))
        {
            mensaje.setText("You have to enter a name");
            notification.open();
            return false;
        }

        Source BD = sourceService.getSourcePorNombre(txtNombre.getValue().trim());
        if((BD!=null && accion == Constante.OPERACIONES_ABN.ALTA) || (BD !=null && accion== Constante.OPERACIONES_ABN.MODIFICACION && BD.getId()!=this.sourceActual.getId()))
        {
            mensaje.setText("There is other source with the same name");
            notification.open();
            return false;
        }
        return true;
    }

    private void cancelarDialogo()
    {
        dialogoDatosSource.close();
    }

    private void editarSource(Source source)
    {
        if(source != null) {
            this.sourceActual = source;
            this.accion = Constante.OPERACIONES_ABN.MODIFICACION;
            txtNombre.setValue(source.getName());
            dialogoDatosSource.open();
        }
    }

    private void abrirDialogoEstaSeguro(Source source)
    {
        this.sourceActual = source;
        dialogoEstaSeguro.open();
    }

    private void siEstaSeguroEliminar()
    {
        if(this.sourceActual!=null)
        {
            this.sourceActual.setEstado(Constante.ESTADOS_SOURCE.BAJA_LOGICA);
            this.sourceService.saveSource(this.sourceActual);
            this.mensaje.setText("Source deleted");
            this.notification.open();
            this.sourceActual=null;
            this.dialogoEstaSeguro.close();
            updateList();
        }
    }

}
