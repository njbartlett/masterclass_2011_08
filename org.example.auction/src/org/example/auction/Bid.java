package org.example.auction;

import java.util.Date;

/**
 * @author Neil Bartlett
 */
public interface Bid {

	Object getId();
	
	long getPrice();
	
	Date getDate();
	
	String getUserId();
	
	AuctionItem getAuctionItem();

}