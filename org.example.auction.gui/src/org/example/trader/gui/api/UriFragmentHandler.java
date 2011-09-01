package org.example.trader.gui.api;

public interface UriFragmentHandler {
	
	String getCurrentUriFragment();
	
	boolean handleUriFragment(String uriFragment);
	
}
