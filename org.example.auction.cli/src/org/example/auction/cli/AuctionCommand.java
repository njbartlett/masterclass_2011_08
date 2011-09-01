package org.example.auction.cli;

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.felix.shell.Command;
import org.example.auction.AuctionItem;
import org.example.auction.AuctionService;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component
public class AuctionCommand implements Command {
	
	private final Collection<AuctionService> auctions = new CopyOnWriteArrayList<AuctionService>();
	
	public void execute(String line, PrintStream out, PrintStream err) {
		out.println("There are " + auctions.size() + " auction(s).");
		for (AuctionService auction : auctions) {
			List<AuctionItem> items = auction.listAuctionItems();
			if (items != null) {
				out.println("There are " + items.size() + " items in this auction.");
				for (AuctionItem item : items) {
					String description = item.getDescription();
					out.println("Item: " + description);
				}
			} else {
				out.println("Item list was null.");
			}
		}
	}
	
	@Reference(type = '*')
	public void addAuctionService(AuctionService auction) {
		System.out.println("bound AuctionService to AuctionCommand");
		auctions.add(auction);
	}
	public void removeAuctionService(AuctionService auction) {
		System.out.println("UNbound AuctionService to AuctionCommand");
		auctions.remove(auction);
	}
	
	@Override
	public String getName() {
		return "auction";
	}

	@Override
	public String getShortDescription() {
		return "whatever";
	}

	@Override
	public String getUsage() {
		return "work it out yourself dummy";
	}
	
}