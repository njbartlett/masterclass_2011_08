package org.example.auction;


import java.util.Date;
import java.util.List;

/**
 * @author Neil Bartlett
 *
 */
public interface AuctionItem {
	
	Object getId();
	
	String getDescription();
	
	Bid addBid(long price) throws InvalidBidException;
	
	List<Bid> getBids();
	
	Date getExpiryDate();
	
	long getStartingPrice();
}