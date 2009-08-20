package de.d3web.kernel.dynamicObjects;

import java.util.HashMap;
import java.util.Map;

import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisScore;
/**
 * Stores the dynamic, user specific values of a diagnosis object. It
 * corresponds to the static Diagnosis object. <br>
 * Examplary values to be stored: <br>
 * <li>score for each applied problem-solver
 * <li>state for each applied problem-solver
 * 
 * @author Christian Betz, joba
 * @see Diagnosis
 */
public class CaseDiagnosis extends XPSCaseObject {

	private Map value;

	/**
	 * Creates a new user-case specific diagnosis object. It stores the scores
	 * depended from the problem-solving methods used in this case.
	 * 
	 * @param diagnosis
	 *            the static diagnosis object related to this object
	 */
	public CaseDiagnosis(Diagnosis diagnosis) {
		super(diagnosis);
		value = new HashMap();
	}

	/**
	 * Returns the current value (e.g. score) of the diagnosis with respect to
	 * the specified PSMethod-context.
	 * 
	 * @param context
	 * @return the current value of the diagnosis (e.g. DisgnosisScore,
	 *         DignosisState)
	 */
	public Object getValue(Class context) {
		Object o = value.get(context);
		if (o == null) {
			DiagnosisScore d = new DiagnosisScore(((Diagnosis) getSourceObject())
					.getAprioriProbability());
			setValue(d, context);
			return d;
		}
		return o;
	}

	/**
	 * Sets the specified value of the diagnosis with repect to the specified
	 * PSMethod-context.
	 * 
	 * @param theValue
	 *            the specified value of the diagnosis
	 * @param context
	 *            the specified PSMethod-context
	 */
	public void setValue(Object theValue, Class context) {
		value.put(context, theValue);
	}

	/**
	 * @return the score of this CaseDiagnosis. if it is null a new
	 *         DiagnosisScore object will be created considering the apriori
	 *         probability of the static diagnosis.
	 * @deprecated use getValue instead
	 */
	public DiagnosisScore getScore(Class context) {
		Object o = getValue(context);
		if (o == null) {
			DiagnosisScore d = new DiagnosisScore(((Diagnosis) getSourceObject())
					.getAprioriProbability());
			setScore(d, context);
			o = d;
		}
		return (DiagnosisScore) o;
	}

	/**
	 * @deprecated use setValue instead
	 */
	public void setScore(DiagnosisScore score, Class context) {
		setValue(score, context);
	}

}