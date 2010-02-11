/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.core.session.blackboard;

import java.util.HashMap;
import java.util.Map;

import de.d3web.core.terminology.Diagnosis;
import de.d3web.scoring.DiagnosisScore;
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