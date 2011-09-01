package org.example.trader.gui.app;

import java.util.Iterator;

import org.example.trader.gui.api.UriFragmentHandler;
import org.example.trader.gui.api.UriFragmentService;
import org.example.utils.gui.DynamicContainer;
import org.osgi.service.component.ComponentFactory;

import aQute.bnd.annotation.component.Reference;

import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedEvent;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@aQute.bnd.annotation.component.Component(factory = "com.vaadin.Application/trader", provide = {})
public class TraderApplication extends Application implements UriFragmentService {
	
	private final UriFragmentUtility uriFragmentUtil = new UriFragmentUtility();
	
	private final TabSheet tabs = new TabSheet();
	private final DynamicContainer tabContainer;
	
	boolean fragmentChangeEvent = false;
	
	
	public TraderApplication() {
		setTheme("masterclass");
		
		tabs.addTab(new WelcomePanel(this));
		tabs.setSizeFull();
		
		tabContainer = new DynamicContainer(tabs);
		tabContainer.setSizeFull();
	}

	@Override
	public void init() {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(false);
		layout.setHeight("100%");
		
		Component banner = createBanner();
		layout.addComponent(banner);
		layout.setExpandRatio(banner, 0f);
		
		layout.addComponent(tabContainer);
		layout.setExpandRatio(tabContainer, 1f);
		tabContainer.setSizeFull();
		
		Window mainWindow = new Window("Trader", layout);
		mainWindow.setSizeFull();
		mainWindow.addComponent(uriFragmentUtil);
		setMainWindow(mainWindow);
		
		tabs.addListener(new TabSheet.SelectedTabChangeListener() {
			public void selectedTabChange(SelectedTabChangeEvent event) {
				if (fragmentChangeEvent)
					return;
				Component tab = tabs.getSelectedTab();
				if (tab instanceof UriFragmentHandler) {
					String uriFragment = ((UriFragmentHandler) tab).getCurrentUriFragment();
					uriFragmentUtil.setFragment(uriFragment, false);
				}
			}
		});
		
		uriFragmentUtil.addListener(new FragmentChangedListener() {
			public void fragmentChanged(FragmentChangedEvent source) {
				String fragment = uriFragmentUtil.getFragment();
				System.out.println("### Handling fragment change: " + fragment);
				
				Component changeTo = null;
				Iterator<Component> iter = tabs.getComponentIterator();
				while (iter.hasNext()) {
					Component comp = iter.next();
					if (comp instanceof UriFragmentHandler) {
						if (((UriFragmentHandler) comp).handleUriFragment(fragment)) {
							changeTo = comp;
						}
					}
				}
				
				if (changeTo != null) {
					try {
						fragmentChangeEvent = true;
						tabs.setSelectedTab(changeTo);
					} finally {
						fragmentChangeEvent = false;
					}
				}
			}
		});
	}
	
	private com.vaadin.ui.Component createBanner() {
		HorizontalLayout layout = new HorizontalLayout();
		
		Embedded image = new Embedded(null, new ClassResource("/images/banner.png", this));
		image.setHeight("100px");
		layout.addComponent(image);
		
		return layout;
	}
	
	@Reference(type = '*', target="(component.factory=org.example.trader.gui.tabs/*)")
	public void bindTab(ComponentFactory factory) {
		tabContainer.bindComponent(factory);
	}

	public void unbindTab(ComponentFactory factory) {
		tabContainer.unbindComponent(factory);
	}

	public String getUriFragment() {
		return uriFragmentUtil.getFragment();
	}

	public void setUriFragment(String uriFragment, boolean fireEvent) {
		uriFragmentUtil.setFragment(uriFragment, fireEvent);
	}
	
}