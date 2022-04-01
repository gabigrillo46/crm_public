package com.vaadin.tutorial.crm.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.tutorial.crm.ui.view.Login.LoginView;
import org.springframework.stereotype.Component;

@Component 
public class ConfigureUIServiceInitListener  implements VaadinServiceInitListener {

	@Override
	public void serviceInit(ServiceInitEvent event) {
		event.getSource().addUIInitListener(uiEvent -> {
			final UI ui = uiEvent.getUI();
			ui.addBeforeEnterListener(this::authenticateNavigation);
		});
	}

	private void authenticateNavigation(BeforeEnterEvent event) {
		if (!LoginView.class.equals(event.getNavigationTarget())
		    && !SecurityUtils.isUserLoggedIn()) {
			if(event.getNavigationTarget().getName().endsWith("FormClient"))
			{
				System.out.println("Retorna porque es FormClient");
				return;
			}
			if(event.getNavigationTarget().getName().endsWith("FormClientCertified"))
			{
				System.out.println("Retorna porque es FormClientCertified");
				return;
			}
			System.out.println("redirecciona ["+event.getNavigationTarget().getName());
				event.rerouteTo(LoginView.class);
		}
	}
}