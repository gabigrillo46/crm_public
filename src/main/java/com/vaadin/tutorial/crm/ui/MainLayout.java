package com.vaadin.tutorial.crm.ui;

import com.github.appreciated.app.layout.addons.notification.DefaultNotificationHolder;
import com.github.appreciated.app.layout.addons.notification.component.NotificationButton;
import com.github.appreciated.app.layout.addons.notification.entity.DefaultNotification;
import com.github.appreciated.app.layout.component.appbar.AppBarBuilder;
import com.github.appreciated.app.layout.component.applayout.LeftLayouts;
import com.github.appreciated.app.layout.component.builder.AppLayoutBuilder;
import com.github.appreciated.app.layout.component.menu.left.builder.LeftAppMenuBuilder;
import com.github.appreciated.app.layout.component.menu.left.items.LeftHeaderItem;
import com.github.appreciated.app.layout.component.menu.left.items.LeftNavigationItem;
import com.github.appreciated.app.layout.component.router.AppLayoutRouterLayout;
import com.github.appreciated.app.layout.entity.DefaultBadgeHolder;
import com.github.appreciated.app.layout.entity.Section;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.PWA;
import com.vaadin.tutorial.crm.backend.entity.Users;
import com.vaadin.tutorial.crm.backend.service.UsersService;
import com.vaadin.tutorial.crm.ui.view.Calendar.CalendarView;
import com.vaadin.tutorial.crm.ui.view.Clientes.ClienteView;
import com.vaadin.tutorial.crm.ui.view.Clientes.SaleView;
import com.vaadin.tutorial.crm.ui.view.SmsMasivo.MensajesMasivos;
import com.vaadin.tutorial.crm.ui.view.Source.SourceView;
import com.vaadin.tutorial.crm.ui.view.Sucursal.SucursalView;
import com.vaadin.tutorial.crm.ui.view.Template.GrillaTemplate;
import com.vaadin.tutorial.crm.ui.view.dashboard.DashboardView;
import com.vaadin.tutorial.crm.ui.view.users.UsersView;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@CssImport("./styles/my.css")
@PWA(name = "CertifiedCRM",
        shortName = "CRM")
public class MainLayout extends  AppLayoutRouterLayout<LeftLayouts.LeftResponsiveHybrid>{

       /**
        * Do not initialize here. This will lead to NPEs
         */
        private DefaultNotificationHolder notifications;
        private DefaultBadgeHolder badge;

    LeftNavigationItem mensajesMasivos =null;



        public MainLayout(UsersService usersService) {

            mensajesMasivos=new LeftNavigationItem("Broadcast SMS", VaadinIcon.RSS_SQUARE.create(), MensajesMasivos.class);
            mensajesMasivos.setVisible(false);


            notifications = new DefaultNotificationHolder(newStatus -> {
            });
            badge = new DefaultBadgeHolder(0);
            for (int i = 1; i < 1; i++) {
                notifications.add(new DefaultNotification("Test title" + i, "A rather long test description ..............." + i));
            }


// Image as a file resource

// Show the image in the application
            Image image = new Image("src/main/webapp/images/LOGO.jpg", "");

            Anchor logout = new Anchor("logout", "Logout");

            String currentUserName="";
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication instanceof AnonymousAuthenticationToken)) {
                currentUserName = authentication.getName();
                Users ususario =usersService.buscarPorNombreUsuario(currentUserName);
                if(ususario!=null)
                {
                    if(true)//ususario.getUsername().equalsIgnoreCase("gaby"))
                    {
                        mensajesMasivos.setVisible(true);
                    }
                    currentUserName= ususario.getNombre()+" "+ususario.getApellido();
                }
            }
            init(AppLayoutBuilder.get(LeftLayouts.LeftResponsiveHybrid.class)
                    .withTitle(currentUserName)
                    .withAppBar(AppBarBuilder.get()
                            .add(logout)
                            .add(new NotificationButton<>(VaadinIcon.BELL, notifications))
                            .build())
                    .withAppMenu(LeftAppMenuBuilder.get()
                           // .add(image)
                            .addToSection(Section.HEADER,
                                    new LeftHeaderItem("Certified Autos", null, null)
                            )
                            .add(new LeftNavigationItem("Home", VaadinIcon.HOME.create(), DashboardView.class),
                                    new LeftNavigationItem("Users", VaadinIcon.USER.create(), UsersView.class),
                                    new LeftNavigationItem("Branch offices", VaadinIcon.OFFICE.create(), SucursalView.class),
                                    new LeftNavigationItem("Clients", VaadinIcon.USERS.create(), ClienteView.class),
                                    new LeftNavigationItem("Source", VaadinIcon.LOCATION_ARROW_CIRCLE.create(), SourceView.class),
                                    new LeftNavigationItem("Client Form", VaadinIcon.FORM.create(), SaleView.class),
                                    new LeftNavigationItem("Calendar", VaadinIcon.CALENDAR.create(), CalendarView.class),
                                    new LeftNavigationItem("Template", VaadinIcon.EDIT.create(), GrillaTemplate.class),
                                    mensajesMasivos

                                    /*,
                                    LeftSubMenuBuilder.get("My Submenu", VaadinIcon.PLUS.create())
                                            .add(LeftSubMenuBuilder.get("My Submenu", VaadinIcon.PLUS.create())
                                                            .add(new LeftNavigationItem("Charts", VaadinIcon.SPLINE_CHART.create(), SucursalView.class),
                                                                    new LeftNavigationItem("Contact", VaadinIcon.CONNECT.create(),  SucursalView.class),
                                                                    new LeftNavigationItem("More", VaadinIcon.COG.create(),  SucursalView.class))
                                                            .build(),





                                                    new LeftNavigationItem("Contact1", VaadinIcon.CONNECT.create(),  SucursalView.class),
                                                    new LeftNavigationItem("More1", VaadinIcon.COG.create(),  SucursalView.class))
                                            .build()*/)
                            .build())
                    .build());
        }

}