package de.d3web.xcl;

import de.d3web.core.knowledge.terminology.info.Property;

/**
 * @author Veronika Oschmann (denkbares GmbH)
 * @created 28.09.23
 */
public class XCLProperties {

	/**
	 * Property to mark a choice as irrelevant for cost benefit calculation.
	 */
	public static final Property<Boolean> NO_BENEFIT_CHOICE = Property.getProperty("noBenefit", Boolean.class);

}
