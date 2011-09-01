package org.example.trader.gui.tradetab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import name.njbartlett.osgi.vaadin.util.ConcurrentComponent;
import name.njbartlett.osgi.vaadin.util.SelectionListener;
import name.njbartlett.osgi.vaadin.util.SelectionSupport;

import org.example.auction.AuctionItem;
import org.example.auction.AuctionService;
import org.example.trader.gui.api.UriFragmentHandler;
import org.example.trader.gui.api.UriFragmentService;
import org.example.utils.gui.DynamicContainer;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import aQute.bnd.annotation.component.Reference;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

@aQute.bnd.annotation.component.Component(
	factory = "org.example.trader.gui.tabs/trade",
	provide = { EventHandler.class },
	properties = { EventConstants.EVENT_TOPIC + "=" + TradeTab.EVENT_TOPIC_ITEMS_NEW })
public class TradeTab extends ConcurrentComponent implements UriFragmentHandler, EventHandler {
	
	static final String EVENT_TOPIC_ITEMS_NEW = "Auctions/Items/New";
	private static final String URI_FRAGMENT_PREFIX = "auctionItem/";
	private static final int POLL_INTERVAL = 5000;

	final Map<String, AuctionService> auctions = new ConcurrentHashMap<String, AuctionService>();
	final SelectionSupport<AuctionService> svcSelection = new SelectionSupport<AuctionService>();
	final IndexedContainer itemContainer = new IndexedContainer();
	final Table itemTable = new Table("Items for Sale", itemContainer);
	
	final VerticalLayout dynamicLayout = new VerticalLayout();
	final DynamicContainer container = new DynamicContainer(dynamicLayout, svcSelection);
	final DynamicContainer buttonBar;

	public TradeTab() {
		HorizontalSplitPanel mainPanel = new HorizontalSplitPanel();
		mainPanel.setHeight("100%");
		
		VerticalLayout leftPanel = new VerticalLayout();
		leftPanel.setMargin(true);
		leftPanel.setSpacing(true);
		leftPanel.setHeight("100%");
		
		itemContainer.addContainerProperty("auction", AuctionService.class, null);
		itemContainer.addContainerProperty("id", String.class, null);
		itemContainer.addContainerProperty("description", String.class, null);
		itemContainer.addContainerProperty("startingPrice", Long.class, null);
		itemContainer.addContainerProperty("item", AuctionItem.class, null);
		
		itemTable.addStyleName("h1");
//		itemTable.addStyleName("noheader");
		itemTable.setSelectable(true);
		itemTable.setImmediate(true);
		itemTable.setVisibleColumns(new String[] { "description", "startingPrice" });
		itemTable.setColumnHeaders(new String[] { "Description", "Starting Price" });
		itemTable.setSizeFull();
		
		itemTable.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				Application application = getApplication();
				if (application instanceof UriFragmentService)
					((UriFragmentService) application).setUriFragment(getCurrentUriFragment(), false);
				
				Object itemId = itemTable.getValue();
				AuctionItem item = itemId != null ? (AuctionItem) itemTable.getContainerProperty(itemId, "item").getValue() : null;
				for (Iterator<Component> iter = dynamicLayout.getComponentIterator(); iter.hasNext(); ) {
					Component component = iter.next();
					if (component instanceof SelectionListener<?>) {
						((SelectionListener<AuctionItem>) component).selectionChanged(item); 
					}
				}

			}
		});
		
		leftPanel.addComponent(itemTable);
		leftPanel.setExpandRatio(itemTable, 1f);
		
		// Button panel
		VerticalLayout buttonBarLayout = new VerticalLayout();
		buttonBar = new DynamicContainer(buttonBarLayout);
		buttonBar.setWidth("100%");
		leftPanel.addComponent(buttonBar);

		// Progress Indicator (hidden)
		ProgressIndicator progress = new ProgressIndicator(ProgressIndicator.SIZE_UNDEFINED);
		progress.addStyleName("hidden");
		progress.setPollingInterval(POLL_INTERVAL);
		leftPanel.addComponent(progress);
		leftPanel.setExpandRatio(progress, 0f);
		
		mainPanel.addComponent(leftPanel);
		mainPanel.addComponent(container);
		
		Table table = new Table();
		table.setSizeFull();
		
		setCompositionRoot(mainPanel);
		setCaption("Trade");
		setSizeFull();
	}
	
	@Reference(type = '*', target="(component.factory=org.example.trader.gui.panels.trade.commands/*)")
	protected void bindCommandFactory(ComponentFactory factory) {
		buttonBar.bindComponent(factory);
	}
	protected void unbindCommandFactory(ComponentFactory factory) {
		buttonBar.unbindComponent(factory);
	}

	@Reference(type = '*', target = "(component.factory=org.example.trader.gui.panels.trade/*)")
	protected void bindComponent(ComponentFactory factory) {
		container.bindComponent(factory);
	}
	protected void unbindComponent(ComponentFactory factory) {
		container.unbindComponent(factory);
	}

	@Reference(type = '*')
	public void addAuction(final AuctionService auction, Map<String, Object> properties) {
		String pid = (String) properties.get(Constants.SERVICE_PID);
		if (pid != null)
			auctions.put(pid, auction);
		
		final Collection<AuctionItem> items = auction.listAuctionItems();
		executeUpdate(new Runnable() {
			public void run() {
				for (AuctionItem item : items) {
					addAuctionItem(auction, item);
				}
			}
		});
	}
	public void removeAuction(final AuctionService auction, Map<String, Object> properties) {
		String pid = (String) properties.get(Constants.SERVICE_PID);
		if (pid != null)
			auctions.remove(pid);
		
		executeUpdate(new Runnable() {
			public void run() {
				List<Object> itemIds = new ArrayList<Object>(itemContainer.getItemIds());
				for (Object itemId : itemIds) {
					AuctionService itemAuction = (AuctionService) itemContainer.getContainerProperty(itemId, "auction").getValue();
					if (auction == itemAuction)
						itemContainer.removeItem(itemId);
				}
			}
		});
	}
	
	void addAuctionItem(AuctionService auction, AuctionItem item) {
		Object itemId = itemContainer.addItem();
		
		itemContainer.getContainerProperty(itemId, "id").setValue(itemId);
		itemContainer.getContainerProperty(itemId, "auction").setValue(auction);
		itemContainer.getContainerProperty(itemId, "item").setValue(item);
		itemContainer.getContainerProperty(itemId, "description").setValue(item.getDescription());
		itemContainer.getContainerProperty(itemId, "startingPrice").setValue(item.getStartingPrice());
	}
	
	public String getCurrentUriFragment() {
		String fragment = URI_FRAGMENT_PREFIX;
		Object itemId = itemTable.getValue();
		if (itemId != null) {
			String auctionItemId = (String) itemContainer.getContainerProperty(itemId, "id").getValue();
			fragment += auctionItemId;
		}
		return fragment;
	}
	
	public boolean handleUriFragment(String uriFragment) {
		if (uriFragment != null && uriFragment.startsWith(URI_FRAGMENT_PREFIX)) {
			String itemId = uriFragment.substring(URI_FRAGMENT_PREFIX.length());
			itemTable.select(itemId.length() > 0 ? itemId : null);
			return true;
		}
		return false;
	}
	
	public void handleEvent(org.osgi.service.event.Event event) {
		if (event.getTopic().startsWith(EVENT_TOPIC_ITEMS_NEW)) {
			String pid = (String) event.getProperty("auctionPid");
			AuctionService auction = auctions.get(pid);
			if (auction != null) {
				String itemId = (String) event.getProperty("auctionItemId");
				AuctionItem item = auction.findItem(itemId);
				if (item != null) {
					addAuctionItem(auction, item);
				}
			}
		}
	}

}
