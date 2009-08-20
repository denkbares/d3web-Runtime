package de.d3web.kernel.supportknowledge.propertyCloner;

import de.d3web.kernel.supportknowledge.DCMarkup;

/**
 * PropertyCloner for de.d3web.kernel.supportknowledge.DCMarkup.
 * @see PropertyCloner
 * @author gbuscher
 */
public class DCMarkupPropertyCloner extends PropertyCloner {

	public Object cloneProperty(Object o) {
		if (o instanceof DCMarkup) {
			return ((DCMarkup) o).clone();
		}
		return null;
	}

}
