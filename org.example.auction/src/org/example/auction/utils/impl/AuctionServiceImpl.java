package org.example.auction.utils.impl;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.example.auction.AuctionItem;
import org.example.auction.AuctionService;

public class AuctionServiceImpl implements AuctionService {
	
	private final List<AuctionItem> items = new LinkedList<AuctionItem>();

	public AuctionItem addItem(String description, Date expiryDate, long startingPrice) {
		AuctionItemImpl item;
		
		synchronized (items) {
			String id = Integer.toString(items.size());
			item = new AuctionItemImpl(id, description, expiryDate, startingPrice);
			items.add(item);
		}
		
		fireItemAdded(item);
		return item;
	}

	protected void fireItemAdded(AuctionItemImpl item) {
		// for extensions
	}

	public List<AuctionItem> listAuctionItems() {
		synchronized (items) {
			return Collections.unmodifiableList(items);
		}
	}

	public AuctionItem findItem(Object id) {
		int index = Integer.parseInt((String) id);
		synchronized (items) {
			return items.get(index);
		}
	}
	
}
