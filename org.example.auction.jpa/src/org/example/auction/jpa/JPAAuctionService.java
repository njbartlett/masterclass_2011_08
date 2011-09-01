package org.example.auction.jpa;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.example.auction.AuctionItem;
import org.example.auction.AuctionService;
import org.example.auction.jpa.domain.AuctionItemImpl;
import org.osgi.framework.Constants;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

@Component(immediate = true, properties = { "label=JPA", Constants.SERVICE_PID + "=JPAAuctionService" })
public class JPAAuctionService implements AuctionService {

	private EntityManagerFactory emf;
	private EntityManager em;
	
	@Reference(target="(osgi.unit.name=Auctions)")
	public void setEntityManagerFactory(EntityManagerFactory emf) {
		this.emf = emf;
	}
	
	@Activate
	public void activate() {
		System.out.println("Activated JPA Auction Service");
		em = emf.createEntityManager();
	}
	@Deactivate
	public void deactivate() {
		if (em != null)
			em.close();
	}

	public List<AuctionItem> listAuctionItems() {
		Query query = em.createQuery("select a from AuctionItemImpl a");
		
		@SuppressWarnings("unchecked")
		List<AuctionItem> results = query.getResultList();
		
		return Collections.unmodifiableList(results);
	}
	
	public AuctionItem addItem(String description, Date expiryDate, long startingPrice) throws IOException {
		AuctionItemImpl item = new AuctionItemImpl();
		item.setDescription(description);
		item.setExpiryDate(expiryDate);
		item.setStartingPrice(startingPrice);
		
		em.getTransaction().begin();
		em.persist(item);
		em.getTransaction().commit();
		
		return item;
	}
	
	public AuctionItem findItem(Object id) {
		// TODO Auto-generated method stub
		return null;
	}
}
