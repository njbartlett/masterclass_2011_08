package org.example.auction.basic;

import java.util.HashMap;

import junit.framework.TestCase;

public class ExampleComponentTest extends TestCase {

    public void testExample() throws Exception {
    	ExampleComponent comp = new ExampleComponent();
    	
    	HashMap<String, Object> config = new HashMap<String, Object>();
    	config.put("fileName", "example.csv");
    	comp.activate(config);
    	
    	assertEquals(2, comp.listAuctionItems().size());
    }
}
