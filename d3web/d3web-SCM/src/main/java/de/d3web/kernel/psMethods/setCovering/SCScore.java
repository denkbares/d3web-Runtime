package de.d3web.kernel.psMethods.setCovering;

/**
 * This class describes the covering-scores (as knowledge) for SCRelations
 * 
 * @author bates
 */
public final class SCScore implements SCKnowledge {

	private int value;

	public SCScore(int value) {
		this.value = value;
	}

	public static final SCScore P1 = new SCScore(1);
	public static final SCScore P2 = new SCScore(2);
	public static final SCScore P3 = new SCScore(3);
	public static final SCScore P4 = new SCScore(4);
	public static final SCScore P5 = new SCScore(5);
	public static final SCScore P6 = new SCScore(6);
	public static final SCScore P7 = new SCScore(7);

	/**
	 * @return the value as Integer-Object
	 */
	public Object getValue() {
		return new Integer(this.value);
	}

	public String getSymbol() {
		return getValue().toString();
	}

	/**
	 * @see SCKnowledge#verbalize()
	 */
	public String verbalize() {
		return "score";
	}
}
