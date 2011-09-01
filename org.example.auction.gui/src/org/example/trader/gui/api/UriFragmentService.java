package org.example.trader.gui.api;

public interface UriFragmentService {
	String getUriFragment();
	void setUriFragment(String uriFragment, boolean fireEvent);
}
