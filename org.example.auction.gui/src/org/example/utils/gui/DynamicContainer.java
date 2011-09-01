package org.example.utils.gui;

import java.util.IdentityHashMap;
import java.util.Map;

import name.njbartlett.osgi.vaadin.util.ConcurrentComponent;
import name.njbartlett.osgi.vaadin.util.SelectionListener;
import name.njbartlett.osgi.vaadin.util.SelectionSupport;

import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;

public class DynamicContainer extends ConcurrentComponent {
	
	final Map<ComponentFactory, Component> componentMap = new IdentityHashMap<ComponentFactory, Component>();
	final Map<ComponentFactory, ComponentInstance> instanceMap = new IdentityHashMap<ComponentFactory, ComponentInstance>();
	final SelectionSupport<?> selectionSupport;
	
	final ComponentContainer container;
	
	public DynamicContainer(ComponentContainer container) {
		this(container, null);
	}
	
	public DynamicContainer(ComponentContainer container, SelectionSupport<?> selectionSupport) {
		this.container = container;
		this.selectionSupport = selectionSupport;
		setCompositionRoot(container);
	}
	
	/**
	 * <p>
	 * Inject a contribution component factory. Example use with Declarative
	 * Services (Bnd annotations):
	 * </p>
	 * 
	 * <p>
	 * {@code @Reference(type = '*', target = "(component.factory=com.vaadin.Component/contribution)")}
	 * </p>
	 * 
	 * @param factory
	 */
	public void bindComponent(final ComponentFactory factory) {
		final ComponentInstance ci = factory.newInstance(null);
		Object o = ci.getInstance();
		if (o != null && o instanceof Component) {
			final Component c = (Component) o;
			executeUpdate(new Runnable() {
				public void run() {
					container.addComponent(c);
					subscribeSelection(c);
					componentMap.put(factory, c);
					instanceMap.put(factory, ci);
				}
			});
		} else {
			ci.dispose();
		}
	}
	
	public void unbindComponent(final ComponentFactory factory) {
		executeUpdate(new Runnable() {
			public void run() {
				ComponentInstance ci = instanceMap.remove(factory);
				Component c = componentMap.remove(factory);
				
				if (c != null) {
					container.removeComponent(c);
					unsubscribeSelection(c);
					c.detach();
				}
				
				if (ci != null)
					ci.dispose();
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	void subscribeSelection(Component c) {
		if (selectionSupport != null && c instanceof SelectionListener) {
			SelectionListener listener = (SelectionListener) c;
			selectionSupport.addSelectionListener(listener);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void unsubscribeSelection(Component c) {
		if (selectionSupport != null && c instanceof SelectionListener) {
			SelectionListener listener = (SelectionListener) c;
			selectionSupport.removeSelectionListener(listener);
		}
	}
}
