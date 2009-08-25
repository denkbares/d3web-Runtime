package de.d3web.kernel.psMethods.setCovering.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * @author bruemmer
 * 
 */
public class SortedList extends LinkedList {

	// [MISC]: bates: maybe an insertion-sort will speed up sorting

	private Comparator comparator = null;

	public SortedList(Comparator comparator) {
		super();
		this.comparator = comparator;
	}

	public SortedList(Comparator comparator, Collection coll) {
		super(coll);
		this.comparator = comparator;
		sort();
	}

	private void sort() {
		Collections.sort(this, comparator);
	}

	public boolean add(Object o) {
		boolean ok = super.add(o);
		if (ok) {
			sort();
		}
		return ok;
	}

}
