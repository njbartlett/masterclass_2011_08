package org.example.trader.gui.tradetab;

import java.util.Collection;

import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;

public class CountColumnGenerator implements ColumnGenerator {
	
	private final Object targetId;

	public CountColumnGenerator(Object targetId) {
		this.targetId = targetId;
	}

	public Component generateCell(Table source, Object itemId, Object columnId) {
		Property targetProp = source.getItem(itemId).getItemProperty(targetId);
		Object targetValue = targetProp.getValue();
		
		int count;
		if (targetValue instanceof Object[])
			count = ((Object[]) targetValue).length;
		else if (targetValue instanceof Collection<?>)
			count = ((Collection<?>) targetValue).size();
		else
			count = 0;
		
		Label label = new Label(Integer.toString(count));
		label.addStyleName("column-type-value");
		label.addStyleName("column-" + (String) columnId);
		return label;
	}

}
