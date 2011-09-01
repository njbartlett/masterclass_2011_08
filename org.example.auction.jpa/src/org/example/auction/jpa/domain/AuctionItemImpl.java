package org.example.auction.jpa.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.example.auction.AuctionItem;
import org.example.auction.Bid;
import org.example.auction.InvalidBidException;

@Entity
public class AuctionItemImpl implements AuctionItem {

	@Id
	@GeneratedValue
	private Integer id;

	private String description;
	
	@Temporal(TemporalType.DATE)
	private Date expiryDate;
	private long startingPrice;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public long getStartingPrice() {
		return startingPrice;
	}

	public void setStartingPrice(long startingPrice) {
		this.startingPrice = startingPrice;
	}

	public Bid addBid(long price) throws InvalidBidException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Bid> getBids() {
		// TODO Auto-generated method stub
		return null;
	}

}
