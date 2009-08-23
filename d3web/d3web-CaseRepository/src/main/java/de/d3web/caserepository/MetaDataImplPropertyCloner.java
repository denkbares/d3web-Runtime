package de.d3web.caserepository;

import de.d3web.kernel.supportknowledge.propertyCloner.PropertyCloner;

/**
 * PropertyCloner for de.d3web.caserepository.MetaDataImpl.
 * @see PropertyCloner
 * @author gbuscher
 */
public class MetaDataImplPropertyCloner extends PropertyCloner {

	public Object cloneProperty(Object o) {
		if (o instanceof MetaDataImpl) {
			return ((MetaDataImpl) o).clone();
		}
		return null;
	}

}
