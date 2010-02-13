package de.d3web.core.utilities;

import java.util.Arrays;

/**
 * This class implements a typed, null-save tuple of a number of other objects.
 * 
 * @author volker_belli
 * 
 */
public class Tuple {

	private final Object[] items;

	public Tuple(Object... items) {
		this.items = items;
	}

	public Object get(int index) {
		return items[index];
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof Tuple)) return false;
		Tuple o = (Tuple) other;
		return Arrays.equals(this.items, o.items);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.items);
	}
}
