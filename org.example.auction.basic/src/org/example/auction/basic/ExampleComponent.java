package org.example.auction.basic;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.csv.CSVParser;
import org.example.auction.AuctionService;
import org.example.auction.utils.impl.AuctionServiceImpl;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Reference;
import aQute.bnd.annotation.metatype.Configurable;
import aQute.bnd.annotation.metatype.Meta;

@Component(provide = AuctionService.class)
public class ExampleComponent extends AuctionServiceImpl {
	
	interface Config {
		String label();
		@Meta.AD(min = "100", max = "1000", description = "This is the timeout", deflt = "500")
		int timeout();
		File fileName();
		FileType fileType();
	}
	enum FileType {
		csv, xml;
	}
	
	@Activate
	protected void activate(Map<String, Object> props) throws IOException {
		System.out.println("Activated ExampleComponent");
		
		Config config = Configurable.createConfigurable(Config.class, props);
		File fileName = config.fileName();
		if (fileName == null) fileName = new File("example.csv");
		System.out.println("Configured filename: " + fileName);
		FileReader reader = new FileReader(fileName);
		CSVParser parser = new CSVParser(reader);
		
		String[] line;
		while ((line = parser.getLine()) != null) {
			String priceStr = line[0];
			String description = line[1];
			
			addItem(description, new Date(), Long.parseLong(priceStr));
		}
	}

	@Reference
	public void setTask(Runnable task) {
		System.out.println("Bound to a task");
	}

}