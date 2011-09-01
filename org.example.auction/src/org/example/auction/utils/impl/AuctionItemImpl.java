package org.example.auction.utils.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.example.auction.AuctionItem;
import org.example.auction.Bid;
import org.example.auction.InvalidBidException;

public class AuctionItemImpl implements AuctionItem, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final String id;
	private final String description;
	private final Date expiryDate;
	private final long startingPrice;
	
	private final List<Bid> bids = new LinkedList<Bid>();

	public AuctionItemImpl(String id, String description, Date expiryDate, long startingPrice) {
		this.id = id;
		this.description = description;
		this.expiryDate = expiryDate;
		this.startingPrice = startingPrice;
	}

	public Bid addBid(long price) throws InvalidBidException {
		BidImpl bid;
		synchronized (bids) {
			String bidId = id + "_" + bids.size();
			
			bid = new BidImpl(bidId, price, new Date(), "TODO", this);
			bids.add(bid);
		}
		return bid;
	}

	public List<Bid> getBids() {
		synchronized (bids) {
			return Collections.unmodifiableList(bids);
		}
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public long getStartingPrice() {
		return startingPrice;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AuctionItem [id=").append(id)
				.append(", description=").append(description)
				.append(", startingPrice=").append(startingPrice).append("]");
		return builder.toString();
	}
	
	

}
