package org.example.auction.ui.buy;

import java.util.concurrent.atomic.AtomicReference;

import name.njbartlett.osgi.vaadin.util.ConcurrentComponent;
import name.njbartlett.osgi.vaadin.util.SelectionListener;

import org.example.auction.AuctionItem;
import org.example.auction.AuctionService;
import org.example.auction.Bid;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

@aQute.bnd.annotation.component.Component(factory = "org.example.trader.gui.panels.trade/buy", provide = {})
public class BuyPanel extends ConcurrentComponent implements SelectionListener<AuctionItem> {
	
	private final AtomicReference<AuctionService> auctionSvcRef = new AtomicReference<AuctionService>();
	
	private final BeanItemContainer<Bid> bids = new BeanItemContainer<Bid>(Bid.class);

	private final Button bidButton = new Button("Place Bid");
	private TextField txtBidAmount;
	private Table bidsTable;
	
	private AuctionItem selectedAuctionItem;
	
	
	public BuyPanel() {
		setCompositionRoot(createMainComponent());
	}
	
	private Component createMainComponent() {
		VerticalLayout bidsPanel = new VerticalLayout();
		bidsPanel.setSpacing(true);
		bidsPanel.setMargin(true);
		
		bidsTable = new Table("Bids", bids);
		bidsTable.addStyleName("h1");
		bidsTable.setSelectable(false);
		bidsTable.setImmediate(true);
		bidsTable.setVisibleColumns(new Object[] { "price", "userId", "date" });
		bidsTable.setColumnHeaders(new String[] { "Price", "User", "Date/Time" });
		bidsTable.setSizeFull();
		bidsPanel.addComponent(bidsTable);
		
		bidButton.setWidth("100%");
		bidButton.setEnabled(false);
		bidsPanel.addComponent(bidButton);
		bidsPanel.setComponentAlignment(bidButton, Alignment.MIDDLE_CENTER);
		
		final Window bidWindow = new Window("Place Bid");
		bidWindow.setModal(true);
		createBidWindowContent(bidWindow);
		
		bidButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				bidWindow.setWidth("300px");
				bidWindow.setHeight("200px");
 				getApplication().getMainWindow().addWindow(bidWindow);
 				
 				txtBidAmount.setValue("");
 				txtBidAmount.focus();
			}
		});
		return bidsPanel;
	}
	
	private void createBidWindowContent(final Window parentWindow) {
		parentWindow.setCloseShortcut(KeyCode.ESCAPE);
		
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		mainLayout.setSizeFull();
		
		txtBidAmount = new TextField("Enter Bid Amount:");
		IntegerValidator bidAmountValidator = new IntegerValidator("Not valid");
		txtBidAmount.addValidator(bidAmountValidator);
		txtBidAmount.setImmediate(true);
		txtBidAmount.setValidationVisible(true);
		txtBidAmount.setWidth("100%");
		mainLayout.addComponent(txtBidAmount);
		
		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.setSizeFull();
		Button btnCancel = new Button("Cancel");
		buttonBar.addComponent(btnCancel);
		buttonBar.setComponentAlignment(btnCancel, Alignment.MIDDLE_CENTER);
		
		final Button btnPlaceBid = new Button("BID");
		btnPlaceBid.setClickShortcut(KeyCode.ENTER);
		btnPlaceBid.addStyleName("primary");
		
		buttonBar.addComponent(btnPlaceBid);
		buttonBar.setComponentAlignment(btnPlaceBid, Alignment.MIDDLE_CENTER);
		mainLayout.addComponent(buttonBar);
		
		parentWindow.addComponent(mainLayout);
		
		btnCancel.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				getApplication().getMainWindow().removeWindow(parentWindow);
			}
		});
		btnPlaceBid.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				String value = (String) txtBidAmount.getValue();
				if (txtBidAmount.isValid() && value != null && value.length() > 0) {
					AuctionService auctionService = auctionSvcRef.get();
					if (auctionService != null) {
						try {
							selectedAuctionItem.addBid(Long.parseLong(value));
							
							getApplication().getMainWindow().removeWindow(parentWindow);
						} catch (Exception e) {
							showError("Error adding Bid", e);
						}
					} else {
						showError("Auction service not available.");
					}
				}
			}
		});
	}
	
	public void selectionChanged(AuctionItem auctionItem) {
		this.selectedAuctionItem  = auctionItem;
		
		bidButton.setEnabled(auctionItem != null);
		bids.removeAllItems();
		
		if (auctionItem != null) for (Bid bid : auctionItem.getBids()) {
			bids.addBean(bid);
		}
	}
	
	
	void showError(String message) {
		getApplication().getMainWindow().showNotification(message, Notification.TYPE_ERROR_MESSAGE);
	}

	void showError(String message, Throwable t) {
		getApplication().getMainWindow().showNotification(message, t.getClass() + ": " + t.getMessage(), Notification.TYPE_ERROR_MESSAGE);
	}

	public void handleEvent(org.osgi.service.event.Event event) {
		String itemId = (String) event.getProperty("auctionItemId");
		if (itemId != null) {
			AuctionService service = auctionSvcRef.get();
			if (service != null) {
				AuctionItem item = service.findItem(itemId);
				if (item != null) {
//					items.addBean(item);
				}
			}
		}
	}
}
