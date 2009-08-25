package de.d3web.kernel.psMethods.compareCase.facade;

import java.util.Comparator;

/**
 * Compares SimpleResult objects by similarity
 * @author bruemmer
 */
public class SimpleResultComparator implements Comparator {

	private static SimpleResultComparator instance = null;

	private SimpleResultComparator() {
	}

	public static SimpleResultComparator getInstance() {
		if (instance == null) {
			instance = new SimpleResultComparator();
		}
		return instance;
	}

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object arg0, Object arg1) {
		try {
			SimpleResult simRes0 = (SimpleResult) arg0;
			SimpleResult simRes1 = (SimpleResult) arg1;

			if (simRes0.getSimilarity() > simRes1.getSimilarity()) {
				return -1;
			} else if (simRes0.getSimilarity() < simRes1.getSimilarity()) {
				return 1;
			} else
				return 0;

		} catch (Exception e) {
			return 0;
		}
	}

}
