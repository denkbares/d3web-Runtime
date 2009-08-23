package de.d3web.kernel.psMethods.compareCase.comparators;

/**
 * @author bates
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class CompareMode {

	private int[] value;
	private boolean isIgnoreMutualUnknowns = false;

	public static final CompareMode NO_FILL_UNKNOWN = new CompareMode(new int[] { 0, 0 });
	public static final CompareMode COMPARE_CASE_FILL_UNKNOWN = new CompareMode(new int[] { 0, 1 });
	public static final CompareMode BOTH_FILL_UNKNOWN = new CompareMode(new int[] { 1, 1 });
	public static final CompareMode CURRENT_CASE_FILL_UNKNOWN = new CompareMode(new int[] { 1, 0 });
	public static final CompareMode JUNIT_TEST = new CompareMode(new int[] { Integer.MAX_VALUE, Integer.MAX_VALUE });

	private CompareMode(int[] value) {
		this.value = value;
	}

	public int[] getValue() {
		return value;
	}

	public boolean covers(CompareMode other) {
		try {
			return (value[0] >= other.getValue()[0]) && (value[1] >= other.getValue()[1]);
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * Returns the isIgnoreMutualUnknowns.
	 * @return boolean
	 */
	public boolean isIgnoreMutualUnknowns() {
		return isIgnoreMutualUnknowns;
	}

	/**
	 * Sets the isIgnoreMutualUnknowns.
	 * @param isIgnoreMutualUnknowns The isIgnoreMutualUnknowns to set
	 */
	public void setIsIgnoreMutualUnknowns(boolean isIgnoreBooleanUnknowns) {
		this.isIgnoreMutualUnknowns = isIgnoreBooleanUnknowns;
	}

	public boolean equals(Object o) {
		try {
			CompareMode other = (CompareMode) o;
			return other.covers(this) && this.covers(other);
		} catch (Exception e) {
			return false;
		}
	}

}