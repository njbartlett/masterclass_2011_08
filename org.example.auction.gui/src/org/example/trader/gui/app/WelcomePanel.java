package org.example.trader.gui.app;

import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;

public class WelcomePanel extends CustomComponent {
	public WelcomePanel(Application app) {
		setCaption("Welcome");
		Embedded pic = new Embedded(null, new ClassResource("/images/welcome.jpg", app));
		setCompositionRoot(pic);
	}
}
