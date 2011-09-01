package org.example.tests;

import java.util.Properties;

import junit.framework.TestCase;

import org.example.auction.AuctionService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class ExampleTest extends TestCase {

    private final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

    public void testExample() throws Exception {
    	ServiceReference[] refs;
    	
		refs = context.getAllServiceReferences("org.example.auction.AuctionService", null);
		assertNull(refs);
		
		ServiceRegistration reg = context.registerService(Runnable.class.getName(), new Runnable() {
			@Override
			public void run() {
			}
		}, null);

		refs = context.getAllServiceReferences("org.example.auction.AuctionService", null);
		assertEquals(1, refs.length);
		
		reg.unregister();
		refs = context.getAllServiceReferences("org.example.auction.AuctionService", null);
		assertNull(refs);
		
		
		ServiceReference cfgAdmRef = context.getServiceReference(ConfigurationAdmin.class.getName());
		ConfigurationAdmin configAdmin = (ConfigurationAdmin) context.getService(cfgAdmRef);
		Configuration config = configAdmin.getConfiguration("org.example.auction.basic.ExampleComponent", null);
		Properties props = new Properties();
		props.put("fileName", "example2.csv");
		config.update(props);
		
		// sleeeeep
		
		AuctionService auction = (AuctionService) context.getService(context.getServiceReference(AuctionService.class.getName()));
		auction.listAuctionItems();
		// ...
    	
		/*
    	assertNotNull(refs);
    	assertEquals(1, refs.length);
    	
    	AuctionService auction = (AuctionService) context.getService(refs[0]);
    	assertEquals(2, auction.listAuctionItems().size());
    	
    	Bundle bundle = findBundle("org.example.auction.basic");
    	bundle.stop();
    	
    	refs = context.getAllServiceReferences("org.example.auction.AuctionService", null);
    	assertNull(refs);
    	*/
    }

	private Bundle findBundle(String bsn) {
		Bundle[] bundles = context.getBundles();
		for (Bundle bundle : bundles) {
			if (bsn.equals(bundle.getSymbolicName())) {
				return bundle;
			}
		}
		return null;
	}
}
