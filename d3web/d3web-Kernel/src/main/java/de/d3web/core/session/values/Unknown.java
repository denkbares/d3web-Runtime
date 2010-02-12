package de.d3web.core.session.values;

import de.d3web.core.session.Value;


/**
 * This class represents the 'unknown' answer given by a user.
 * @author joba
 */
public class Unknown implements Value {

	@Override
	public Object getValue() {
		// TODO: find a better implementation of unknown choices
		return "UNKNOWN";
	}

	@Override
	public int compareTo(Value o) {
		if (o instanceof Unknown)
			return 0;
		else
			return -1;
	}
	
	
}
