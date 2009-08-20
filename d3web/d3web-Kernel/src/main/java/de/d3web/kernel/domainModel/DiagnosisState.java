package de.d3web.kernel.domainModel;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Stores the state of a diagnosis in context to a problem-solving method. The
 * state is computed with respect to the score a diagnosis.
 * 
 * @author joba, Christian Betz
 * @see Diagnosis
 * @see DiagnosisScore
 */
public class DiagnosisState implements java.io.Serializable, Comparable {


	/**
	 * key strings for the 'default' states. Can be used to override the interval
	 * boundaries with a properties file - see initThresholdValues()
	 */
	private static final String KEY_EXCLUDED = "excluded";
	private static final String KEY_UNCLEAR = "unclear";
	private static final String KEY_SUGGESTED = "suggested";
	private static final String KEY_ESTABLISHED = "established";

	
	/**
	 * Is holding the interval boundaries - see initThresholdValues()
	 */
	private static Map<String, Integer> stateThresholdValues = null;

	String name;
	/**
	 * The Diagnosis is meant to be excluded.
	 */
	public static DiagnosisState EXCLUDED = new DiagnosisState(KEY_EXCLUDED);
	/**
	 * The Diagnosis is meant to be unclear.
	 */
	public static DiagnosisState UNCLEAR = new DiagnosisState(KEY_UNCLEAR);
	/**
	 * The Diagnosis is meant to be suggested (nearly established).
	 */
	public static DiagnosisState SUGGESTED = new DiagnosisState(KEY_SUGGESTED);
	/**
	 * The Diagnosis is meant to be established.
	 */
	public static DiagnosisState ESTABLISHED = new DiagnosisState(
			KEY_ESTABLISHED);

	private static DiagnosisState[] allStati = new DiagnosisState[] { EXCLUDED,
			UNCLEAR, SUGGESTED, ESTABLISHED };

	/**
	 * Creates a new DiagnosisState object with the given name
	 */
	public DiagnosisState(
			String name) {
		super();
		this.name = name;
	}
	
	
	/**
	 * Creates a new DiagnosisState object with the given name, lower bound and
	 * upper bound
	 */
	public DiagnosisState(
			 Integer lowerBound,
			 Integer upperBound,
					String name) {
				
				super();
				if(stateThresholdValues == null) {
					initThresholdValues();
				}
				stateThresholdValues.put(name+"_lower", lowerBound);
				stateThresholdValues.put(name+"_upper", upperBound);
				
				this.name = name;
			}

	/**
	 * Two DiagnosisState instances are equal, if their lower- and upperBounds
	 * and names are equal.
	 */
	public boolean equals(Object o) {
		if (!(o instanceof DiagnosisState)) {
			return false;
		}
		DiagnosisState oState = (DiagnosisState) o;

		boolean lowerBoundsEqual;
		if (getLowerBound() != null) {
			lowerBoundsEqual = getLowerBound().equals(oState.getLowerBound());
		} else {
			lowerBoundsEqual = (oState.getLowerBound() == null);
		}

		boolean upperBoundsEqual;
		if (getUpperBound() != null) {
			upperBoundsEqual = getUpperBound().equals(oState.getUpperBound());
		} else {
			upperBoundsEqual = (oState.getUpperBound() == null);
		}

		boolean namesEqual;
		if (name != null) {
			namesEqual = name.equals(oState.name);
		} else {
			namesEqual = (oState.name == null);
		}

		return lowerBoundsEqual && upperBoundsEqual && namesEqual;
	}

	private Integer getUpperBound() {
		if (stateThresholdValues == null) {
			initThresholdValues();
		}
		return stateThresholdValues.get(this.name + "_upper");
	}

	private Integer getLowerBound() {
		if (stateThresholdValues == null) {
			initThresholdValues();
		}
		return stateThresholdValues.get(this.name + "_lower");
	}

	/**
	 * Checks if the score is in the bounds of the Diagnosis State
	 */
	public boolean checkState(double score) {
		boolean ret = true;
		if (getLowerBound() != null && (score < getLowerBound().intValue())) {
			ret = false;
		}
		if (getUpperBound() != null && (score >= getUpperBound().intValue())) {
			ret = false;
		}
		return ret;
	}

	/**
	 * Compares this object with the specified object for order. Returns a
	 * negative integer, zero, or a positive integer as this object is less
	 * than, equal to, or greater than the specified object.
	 * <p>
	 * 
	 * The implementor must ensure <tt>sgn(x.compareTo(y)) ==
	 * -sgn(y.compareTo(x))</tt>
	 * for all <tt>x</tt> and <tt>y</tt>. (This implies that
	 * <tt>x.compareTo(y)</tt> must throw an exception iff
	 * <tt>y.compareTo(x)</tt> throws an exception.)
	 * <p>
	 * 
	 * The implementor must also ensure that the relation is transitive:
	 * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
	 * <tt>x.compareTo(z)&gt;0</tt>.
	 * <p>
	 * 
	 * Finally, the implementer must ensure that <tt>x.compareTo(y)==0</tt>
	 * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
	 * all <tt>z</tt>.
	 * <p>
	 * 
	 * It is strongly recommended, but <i>not</i> strictly required that
	 * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>. Generally speaking, any
	 * class that implements the <tt>Comparable</tt> interface and violates
	 * this condition should clearly indicate this fact. The recommended
	 * language is "Note: this class has a natural ordering that is inconsistent
	 * with equals."
	 * 
	 * @param o
	 *            the Object to be compared.
	 * @return a negative integer, zero, or a positive integer as this object is
	 *         less than, equal to, or greater than the specified object.
	 * 
	 * @throws ClassCastException
	 *             if the specified object's type prevents it from being
	 *             compared to this Object.
	 */
	public int compareTo(java.lang.Object o) {
		DiagnosisState ds = (DiagnosisState) o;
		// everything is "more" than null
		if (o == null)
			return 1;
		// both are equal!
		if (this.equals(ds)) {
			return 0;
		}
		// everything except "null" and "UNCLEAR" is more than "UNCLEAR". This
		// may not be null! o==Unclear is handled by equality!
		if (o.equals(UNCLEAR))
			return 1;
		if (this.equals(UNCLEAR))
			return -1;

		// Handle the rest according to its position in the allStati-Array
		int mypos = -1, opos = -1;

		for (int i = 0; i < allStati.length; i++) {
			if (allStati[i].equals(this))
				mypos = i;
			if (allStati[i].equals(ds))
				opos = i;
		}
		// either one is not in the allStati-Array (null is handled above): so
		// it's not an allowed DiagnosisState!
		if (mypos == -1 || opos == -1) {
			throw new ClassCastException();
		}
		if (mypos < opos)
			return -1;
		if (mypos > opos)
			return 1;
		return 0;
	}

	/**
	 * @return all possible DiagnosisStates
	 */
	public static de.d3web.kernel.domainModel.DiagnosisState[] getAllStati() {
		return allStati;
	}

	public java.lang.String getName() {
		return name;
	}

	/**
	 * @return the first status in allStati, for which checkState returns true;
	 */
	public static DiagnosisState getState(double score) {
		for (int i = 0; i < allStati.length; i++) {
			if (allStati[i].checkState(score)) {
				return allStati[i];
			}
		}
		return null;
	}

	/**
	 * @return the first status in allStati, for which checkState returns true
	 *         for the score of the given DiagnosisScore;
	 */
	public static DiagnosisState getState(DiagnosisScore diagnosisScore) {
		if (diagnosisScore == null) {
			return null;
		}
		return getState(diagnosisScore.getScore());
	}

	public String toString() {
		return getName();
	}

	/**
	 * This method is called immediately after an object of this class is
	 * deserialized. To avoid that several instances of a unique object are
	 * created, this method returns the current unique instance that is equal to
	 * the object that was deserialized.
	 * 
	 * @author georg
	 */
	private Object readResolve() {
		Iterator iter = Arrays.asList(allStati).iterator();
		while (iter.hasNext()) {
			DiagnosisState d = (DiagnosisState) iter.next();
			if (d.equals(this)) {
				return d;
			}
		}
		return this;
	}

	/**
	 * This method initializes the boundaries of the numerical intervalls of the
	 * solution states they can be defined in a properties file - if not the
	 * defaults are set
	 * 
	 */
	private void initThresholdValues() {

		stateThresholdValues = new HashMap<String, Integer>();
		ResourceBundle bundle = null;

		try {
			bundle = ResourceBundle.getBundle("config");
		} catch (Exception e) {
			// TODO: no properties file found - loading defaults
		}

		/**
		 * reading values from properties file
		 */
		if (bundle != null) {
			Enumeration<String> keys = bundle.getKeys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				String value = bundle.getString(key);
				Integer i = null;
				try {
					i = Integer.parseInt(value);
				} catch (Exception e) {
					// TODO: handle exception
					// no valid number given as threshold value
				}
				if (i != null) {
					stateThresholdValues.put(key, i);
				}
			}
		}

		/**
		 * storing defaults for undefined values
		 * 
		 */

		// established upper is null
		if (!stateThresholdValues.containsKey(KEY_ESTABLISHED + "_upper")) {
			stateThresholdValues.put(KEY_ESTABLISHED + "_upper", null);
		}

		// setting established lower equal to suggested upper
		if (stateThresholdValues.containsKey(KEY_ESTABLISHED + "_lower")
				|| stateThresholdValues.containsKey(KEY_SUGGESTED + "_upper")) {
			Integer est_low = stateThresholdValues.get(KEY_ESTABLISHED
					+ "_lower");
			Integer sug_upp = stateThresholdValues
					.get(KEY_SUGGESTED + "_upper");
			if (est_low != null) {
				stateThresholdValues.put(KEY_SUGGESTED + "_upper", est_low);
			} else if (sug_upp != null) {
				stateThresholdValues.put(KEY_ESTABLISHED + "_lower", sug_upp);
			}
		} else {
			// default value:
			int established_lower_default = 42;

			stateThresholdValues.put(KEY_SUGGESTED + "_upper", new Integer(
					established_lower_default));
			stateThresholdValues.put(KEY_ESTABLISHED + "_lower", new Integer(
					established_lower_default));
		}

		// setting suggested lower equal to unclear upper
		if (stateThresholdValues.containsKey(KEY_SUGGESTED + "_lower")
				|| stateThresholdValues.containsKey(KEY_UNCLEAR + "_upper")) {
			Integer sug_low = stateThresholdValues
					.get(KEY_SUGGESTED + "_lower");
			Integer unc_upp = stateThresholdValues.get(KEY_UNCLEAR + "_upper");
			if (sug_low != null) {
				stateThresholdValues.put(KEY_UNCLEAR + "_upper", sug_low);
			} else if (unc_upp != null) {
				stateThresholdValues.put(KEY_SUGGESTED + "_lower", unc_upp);
			}
		} else {
			// default value:
			int suggested_lower_default = 10;

			stateThresholdValues.put(KEY_UNCLEAR + "_upper", new Integer(
					suggested_lower_default));
			stateThresholdValues.put(KEY_SUGGESTED + "_lower", new Integer(
					suggested_lower_default));
		}

		// setting unclear lower equal to excluded upper
		if (stateThresholdValues.containsKey(KEY_UNCLEAR + "_lower")
				|| stateThresholdValues.containsKey(KEY_EXCLUDED + "_upper")) {
			Integer unc_low = stateThresholdValues.get(KEY_UNCLEAR + "_lower");
			Integer exc_upp = stateThresholdValues.get(KEY_EXCLUDED + "_upper");
			if (unc_low != null) {
				stateThresholdValues.put(KEY_EXCLUDED + "_upper", unc_low);
			} else if (exc_upp != null) {
				stateThresholdValues.put(KEY_UNCLEAR + "_lower", exc_upp);
			}
		} else {
			// default value:
			int unclear_lower_default = -41;

			stateThresholdValues.put(KEY_EXCLUDED + "_upper", new Integer(
					unclear_lower_default));
			stateThresholdValues.put(KEY_UNCLEAR + "_lower", new Integer(
					unclear_lower_default));
		}

		// excluded lower is null
		if (!stateThresholdValues.containsKey(KEY_EXCLUDED + "_lower")) {
			stateThresholdValues.put(KEY_EXCLUDED + "_lower", null);
		}

	}
}