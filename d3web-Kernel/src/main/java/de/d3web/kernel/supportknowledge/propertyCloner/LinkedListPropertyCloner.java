package de.d3web.kernel.supportknowledge.propertyCloner;

import java.util.LinkedList;
import java.util.List;

/**
 * PropertyCloner for java.util.List.
 * @see PropertyCloner
 * @author gbuscher
 */
public class LinkedListPropertyCloner extends PropertyCloner {

	public Object cloneProperty(Object o) {
		if (o instanceof List) {
			return new LinkedList((List) o);
		}
		return null;
	}

}
