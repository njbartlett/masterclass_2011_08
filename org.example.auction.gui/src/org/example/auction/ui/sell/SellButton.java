package org.example.auction.ui.sell;

import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.example.auction.AuctionService;

import aQute.bnd.annotation.component.Reference;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

@aQute.bnd.annotation.component.Component(factory = "org.example.trader.gui.panels.trade.commands/sell")
public class SellButton extends CustomComponent {
	
	final Button button;
	
	final Window window = new Window("Sell an Item");
	final IndexedContainer auctionContainer = new IndexedContainer();
	final Select auctionSelect = new Select("Auction", auctionContainer);
	final TextField txtDescription = new TextField("Item Description");
	final TextField txtPrice = new TextField("Starting Price");
	final Button btnSell = new Button("Sell");
	
	final Map<AuctionService, String> auctions = new IdentityHashMap<AuctionService, String>();
	AuctionService selectedAuction = null;

	public SellButton() {
		button = new Button("Sell");
		button.setIcon(new ThemeResource("buttons/add.png"));
		
		button.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				auctionContainer.removeAllItems();
				synchronized (this) {
					boolean first = true;
					for (Entry<AuctionService, String> entry : auctions.entrySet()) {
						Object itemId = auctionContainer.addItem();
						auctionContainer.getContainerProperty(itemId, "name").setValue(entry.getValue());
						auctionContainer.getContainerProperty(itemId, "auction").setValue(entry.getKey());
						
						if (first)
							auctionSelect.setValue(itemId);
						first = false;
					}
				}
				
				window.setWidth("50%");
				window.center();
				getApplication().getMainWindow().addWindow(window);
			}
		});
		
		setCompositionRoot(button);
		
		// Setup the sell window
		window.setCloseShortcut(KeyCode.ESCAPE);
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		auctionContainer.addContainerProperty("name", String.class, "");
		auctionContainer.addContainerProperty("auction", AuctionService.class, null);
		
		auctionSelect.setItemCaptionPropertyId("name");
		auctionSelect.setNullSelectionAllowed(false);
		auctionSelect.setWidth("100%");
		auctionSelect.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				Object itemId = auctionSelect.getValue();
				selectedAuction = itemId != null ? (AuctionService) auctionContainer.getContainerProperty(itemId, "auction").getValue() : null;
				btnSell.setEnabled(selectedAuction != null);
			}
		});
		layout.addComponent(auctionSelect);
		
		txtDescription.setWidth("100%");
		layout.addComponent(txtDescription);
		
		txtPrice.setWidth("100%");
		layout.addComponent(txtPrice);
		
		btnSell.setEnabled(selectedAuction != null);
		layout.addComponent(btnSell);
		layout.setComponentAlignment(btnSell, Alignment.MIDDLE_RIGHT);
		
		btnSell.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					String description = (String) txtDescription.getValue();
					String priceStr = (String) txtPrice.getValue();
					long price = Long.parseLong(priceStr);
					selectedAuction.addItem(description, new Date(), price);
					
					getApplication().getMainWindow().removeWindow(window);
					getApplication().getMainWindow().showNotification("Auction item saved.", Notification.TYPE_TRAY_NOTIFICATION);
				} catch (Exception e) {
					window.showNotification("Error", e.getLocalizedMessage(), Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});
		btnSell.setClickShortcut(KeyCode.ENTER);
		
		window.setContent(layout);
	}
	
	@Reference(type = '*')
	protected synchronized void addAuction(AuctionService auction, Map<String, Object> properties) {
		String label = (String) properties.get("label");
		if (label == null) label = "<unknown>";
		auctions.put(auction, label);
	}
	protected synchronized void removeAuction(AuctionService auction) {
		auctions.remove(auction);
	}


}
