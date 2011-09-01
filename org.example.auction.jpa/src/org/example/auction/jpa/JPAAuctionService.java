package org.example.auction.jpa;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.example.auction.AuctionItem;
import org.example.auction.AuctionService;
import org.example.auction.jpa.domain.AuctionItemImpl;
import org.osgi.framework.Constants;
import org.osgi.service.jpa.EntityManagerFactoryBuilder;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

@Component(immediate = true, properties = { "label=JPA",
		Constants.SERVICE_PID + "=JPAAuctionService" })
public class JPAAuctionService implements AuctionService {

	private EntityManagerFactoryBuilder emfb;
	private EntityManager em;

	@Reference(target = "(osgi.unit.name=Auctions)")
	public void setEntityManagerFactoryBuilder(EntityManagerFactoryBuilder emfb) {
		this.emfb = emfb;
	}

	@Activate
	public void activate() {
		System.out.println("Activated JPA Auction Service");

		Map<String, Object> props = new HashMap<String, Object>();
		props.put("javax.persistence.jdbc.driver",
				"org.apache.derby.jdbc.ClientDriver");
		props.put("javax.persistence.jdbc.url",
				"jdbc:derby://localhost/masterclass;create=true");
		props.put("javax.persistence.jdbc.user", "APP");
		props.put("javax.persistence.jdbc.password", "APP");
		props.put("eclipselink.ddl-generation", "create-tables");
		
		EntityManagerFactory emf = emfb.createEntityManagerFactory(props);

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

	public AuctionItem addItem(String description, Date expiryDate,
			long startingPrice) throws IOException {
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
