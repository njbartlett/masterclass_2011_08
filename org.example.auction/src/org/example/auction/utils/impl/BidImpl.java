package org.example.auction.utils.impl;

import java.io.Serializable;
import java.util.Date;

import org.example.auction.AuctionItem;
import org.example.auction.Bid;

public class BidImpl implements Bid, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final String id;
	private final long price;
	private final Date date;
	private final String userId;
	private final AuctionItem auctionItem;

	public BidImpl(String id, long price, Date date, String userId, AuctionItem auctionItem) {
		this.id = id;
		this.price = price;
		this.date = date;
		this.userId = userId;
		this.auctionItem = auctionItem;
	}

	public Object getId() {
		return id;
	}

	public long getPrice() {
		return price;
	}

	public Date getDate() {
		return date;
	}

	public String getUserId() {
		return userId;
	}

	public AuctionItem getAuctionItem() {
		return auctionItem;
	}

}
