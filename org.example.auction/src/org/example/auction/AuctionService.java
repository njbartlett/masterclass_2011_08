package org.example.auction;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface AuctionService {

	/**
	 * Add a new item to the auction.
	 * @param item The item to insert
	 * @return The ID of the new item
	 * @throws IOException
	 */
	AuctionItem addItem(String description, Date expiryDate, long startingPrice) throws IOException;
	
	/**
	 * Find an item by its ID
	 * @param id The item ID
	 * @return The matching item, or null.
	 */
	AuctionItem findItem(Object id);
	
	/**
	 * Get a list of all current items IDs in the auction.
	 * @return
	 */
	List<AuctionItem> listAuctionItems();

}