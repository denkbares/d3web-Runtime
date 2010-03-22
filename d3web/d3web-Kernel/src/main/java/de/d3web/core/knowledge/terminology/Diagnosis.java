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

package de.d3web.core.knowledge.terminology;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.D3WebCase;
import de.d3web.core.session.Value;
import de.d3web.core.session.ValuedObject;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.blackboard.CaseDiagnosis;
import de.d3web.core.session.blackboard.XPSCaseObject;
import de.d3web.scoring.DiagnosisScore;
import de.d3web.scoring.HDTType;
import de.d3web.scoring.Score;
import de.d3web.scoring.inference.PSMethodHeuristic;

/**
 * This class stores the static, non case-dependent parts of a solutions. You're
 * able to retrieve the score and state of a diagnosis through specified
 * methods. The state and score of a diagnosis is dependent from the
 * problem-solver context. If no problem-solver context is given the heuristic
 * problem-solver is assumed to be the context.
 * 
 * @author joba, chris
 * @see IDObject
 * @see NamedObject
 * @see DiagnosisScore
 * @see DiagnosisState
 */
public class Diagnosis extends NamedObject implements ValuedObject, TerminologyObject {

	/**
	 * Compares the heuristic scores of two Diagnosis instances. 
	 * For other problem-solvers a new comparator should be 
	 * implemented.
	 */
	static class DiagComp implements Comparator<Diagnosis> {
		private XPSCase theCase;
		public DiagComp(XPSCase theCase) {
			this.theCase = theCase;
		}
		public int compare(Diagnosis d1, Diagnosis d2) {
			return d1.getScore(theCase, PSMethodHeuristic.class).compareTo(
					d2.getScore(theCase, PSMethodHeuristic.class));
		}
	}

	/**
	 * A diagnosis can have a prior probability, that is taken
	 * into account by the particular problem-solvers differently.
	 * The {@link PSMethodHeuristic}, for example, adds the apriori 
	 * probability as soon as the diagnosis receives scores from a rule. 
	 */
	private Score aprioriProbability;

	/**
	 * Defines the role of this diagnosis in the context of
	 * the Heuristic Decision Tree pattern.
	 */
	private HDTType hdtType = null;

	/**
	 * Creates a new Diagnosis instance with a predefined
	 * identifier. <br>
	 * <b>Note:</b> Please use {@link KnowledgeBaseManagement}
	 * to create Diagnosis instances.
	 * @param id the unique identifier for this object
	 */
	public Diagnosis(String id) {
	    super(id);
	}

	/**
	 * [FIXME]:?:CHECK FOR STATE UNCLEAR!!!
	 */
	private void checkForNewState(DiagnosisState oldState, DiagnosisState newStatus,
			XPSCase theCase, Class context) {
		try {
			if (oldState != newStatus) {
				if (newStatus == DiagnosisState.ESTABLISHED) {
					establish(theCase);
				}
				if (oldState == DiagnosisState.ESTABLISHED) {
					deestablish(theCase);
				}
			}
		} catch (Exception e) {
			theCase.trace("<<EXP>> " + e + " in Diagnosis.checkForNewState(" + newStatus + ", "
					+ theCase + ", " + context + ")");
		}
	}

	/**
	 * Creates a new dynamic flyweight for this object. For every
	 * new {@link XPSCase} flyweights are created on demand for the
	 * used {@link IDObject} instances. This method is only used 
	 * in the context of the d3web-Kernel project.
	 * @return a flyweight instance of this object.
	 */
	public XPSCaseObject createCaseObject(XPSCase session) {
		return new CaseDiagnosis(this);
	}

	/**
	 * Removes this object from the established diagnoses in the 
	 * given {@link XPSCase} and propagates the state change.
	 * @param theCase the specified {@link XPSCase}
	 */
	private void deestablish(XPSCase theCase) {
		theCase.trace("ziehe etablierte Diagnosis " + getId() + " zurueck.");
		((D3WebCase) theCase).removeEstablishedDiagnoses(this);
	}

	/**
	 * Adds this object to the list of established diagnoses in
	 * the given {@link XPSCase} and propagated the state change.
	 * @param theCase the specified {@link XPSCase}
	 */
	private void establish(XPSCase theCase) {
		theCase.trace("etabliere Diagnose: " + getId());
		((D3WebCase) theCase).addEstablishedDiagnoses(this);
	}

	/**
	 * Returns the prior probability of this diagnosis.
	 * The 'probability' is represented by a {@link Score}, and
	 * the use of this probability depends on the particular
	 * {@link PSMethod}.
	 * @return the apripori probability
	 */
	public Score getAprioriProbability() {
		return aprioriProbability;
	}

	/**
	 * Returns a comparator that compares the {@link Score} values 
	 * in the context of the given {@link XPSCase} and the
	 * {@link PSMethodHeuristic} solver.
	 * For other problem-solvers, you will need to implement your 
	 * own {@link Comparator}.
	 * @param theCase the case the scores are computed for
	 * @return a comparator for two Diagnosis objects
	 */
	public static Comparator<Diagnosis> getComparator(XPSCase theCase) {
		return new DiagComp(theCase);
	}

	/**
	 * Returns the computed score of this {@link Diagnosis} for a specified
	 * {@link XPSCase} and a specified {@link PSMethod} context.
	 * The score of a diagnosis is only valid in the context of <b>one</b> 
	 * {@link PSMethod}, and can differ for other {@link PSMethod} 
	 * instances. 
	 * @param theCase the context case of the score
	 * @param context the {@link PSMethod} context the score is valid for
	 * @return the score of the Diagnosis in the context of an {@link XPSCase} and {@link PSMethod} class 
	 */
	public DiagnosisScore getScore(XPSCase theCase, Class context) {
		return (DiagnosisScore) ((CaseDiagnosis) theCase.getCaseObject(this)).getValue(context);
	}

	/**
	 * Returns the derived state of this {@link Diagnosis} for a specified
	 * {@link XPSCase} and a specified {@link PSMethod} context.
	 * The state of a diagnosis is only valid in the context of <b>one</b> 
	 * {@link PSMethod}, and can differ for other {@link PSMethod} 
	 * instances. 
	 * For {@link PSMethodHeuristic} the state is derived by the {@link Score}
	 * of the {@link Diagnosis}.
	 * @param theCase the context case of the state
	 * @param context the {@link PSMethod} context the state is valid for
	 * @return the state of the Diagnosis in the context of a given {@link XPSCase} and {@link PSMethod} class
	 */
	public DiagnosisState getState(XPSCase theCase, Class context) {
		// TODO: this is wrong! getState computes the real state every time, but this method should return the stored value of its CaseDiagnosis instance
		return theCase.getPSMethodInstance(context).getState(theCase, this);
	}

	/**
	 * Returns the <b>combined</b> state of this diagnosis. The combined state
	 * is the maximum state value of all {@link PSMethod} instances for this diagnosis.
	 * The maximum is defined by the following order:
	 * <ol>
	 * <li> DiagnosisState.EXCLUDED
	 * <li> DiagnosisState.ESTABLISHED
	 * <li> DiagnosisState.SUGGESTED
	 * <li> DiagnosisState.UNCLEAR
	 * </ol>
	 * @param theCase the case the state should be computed for
	 * @return the combined state of this Diagnosis
	 */
	public DiagnosisState getState(XPSCase theCase) {
		DiagnosisState state = DiagnosisState.UNCLEAR;
		for (PSMethod psm : theCase.getUsedPSMethods()) {
			DiagnosisState psState = psm.getState(theCase, this);
			if (psState == null) continue;
			if (DiagnosisState.EXCLUDED.equals(psState)) return DiagnosisState.EXCLUDED;
			if (psState.compareTo(state) > 0) {
				state = psState;
			}
		}
		return state;
	}

	
	
	/**
	 * Removes the specified child from this object. The 
	 * double-linked children/parent links are removed as well. 
	 * @param theDiagnosis a child of this instance
	 * @return true, if the deletion was successful
	 */
	public synchronized boolean removeChildren(Diagnosis diagnosis) {
		if (removeFromList(diagnosis, getChildren()))
			diagnosis.removeParent(this);
		return false;
	}

	private boolean removeFromList(Diagnosis diagnosis, List<? extends NamedObject> list) {
		if (list.contains(diagnosis)) {
			list.remove(diagnosis);
			return true;
		}
		return false;
	}

	/**
	 * Removes the specified child from this object. The 
	 * double-linked children/parent links are removed as well. 
	 * @param theDiagnosis a parent of this object
	 * @return true, if the removal was successful
	 */
	public synchronized boolean removeParent(Diagnosis diagnosis) {
		if (removeFromList(diagnosis, getParents()))
			diagnosis.removeChildren(this);
		return false;
	}

	/**
	 * Sets the new apriori probability of this instance. 
	 * The value is fixed to the predefined {@link Score} values: 
	 * P5, P4, P3, P2, N2, N3, N4, N5. 
	 * <p>Creation date: (25.09.00 15:13:34)
	 * @param newAprioriPropability the new apriori probability of this instance
	 */
	public void setAprioriProbability(Score newAprioriProbability) throws Exception {
		// check if legal probability entry
		if (!Score.APRIORI.contains(newAprioriProbability) && (newAprioriProbability != null)) {
			throw new Exception(newAprioriProbability
					+ " not a valid apriori probability.");
		} else
			aprioriProbability = newAprioriProbability;
	}

	/**
	 * Returns the role of this diagnosis in the context of
	 * the Heuristic Decision Tree pattern. Usually not
	 * useful for {@link PSMethod}s except {@link PSMethodHeuristic}.  
	 * @return the type of the heuristic decision tree
	 */
	public HDTType getHdtType() {
		return hdtType;
	}

	/**
	 * Sets the role of this diagnosis in the context of
	 * the Heuristic Decision Tree pattern. Usually not
	 * useful for {@link PSMethod}s except {@link PSMethodHeuristic}.  
	 * @param newHdtType the type of the heuristic decision tree
	 */
	public void setHdtType(HDTType hdtType) {
		this.hdtType = hdtType;
	}

	/**
	 * Sets the knowledge base instance, to which this object belongs 
	 * to. This method also adds this object to the knowledge base 
	 * (reverse link). 
	 * <br><b>Note:</b> Currently, this object is not removed from a 
	 * previously registered knowledge base.
	 * @param newKnowledgeBase the knowledge base, to which this object belongs to 
	 */
	public void setKnowledgeBase(KnowledgeBase kb) {
		super.setKnowledgeBase(kb);
		// maybe somebody should remove this object from the old
		// knowledge base if available
		getKnowledgeBase().add(this);
	}

	/**
	 * This is the official method to change the state of the Diagnosis. The
	 * first Object in the values array must be an DiagnosisScore Object.
	 */
	@Deprecated
	public void setValue(XPSCase theCase, Object[] values) {
		setValue(theCase, (Value)values[0], null);
	}

	public void setValue(XPSCase theCase, Value value, Class context) {
		try {
			DiagnosisScore diagnosisScore = null;
			DiagnosisState oldState = getState(theCase, context);
			if (value != null)  {
				if (value instanceof DiagnosisScore) {
					diagnosisScore = (DiagnosisScore) value;
					((CaseDiagnosis) theCase.getCaseObject(this)).setValue(diagnosisScore, context);
				} else if (value instanceof DiagnosisState) {
					((CaseDiagnosis) theCase.getCaseObject(this)).setValue(value, context);
				}
			}
			DiagnosisState newState = getState(theCase, context);
			// this does simply a check if state has changed
			checkForNewState(oldState, newState, theCase, context);
			// d3web.debug
			notifyListeners(theCase, this);

		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).throwing(this.getClass().getName(),
					"setValue(XPSCase, values(" + value + ")", ex);
		}
	}

	/**
	 * This method officially changes the state of the Diagnosis using the
	 * standard setValue method signature.
	 * The value is set in the context of an {@link XPSCase} instance, and in the 
	 * context of a {@link PSMethod} class context (the {@link PSMethod} responsible
	 * for deriving this state). 
	 * The value array usually contains only one element: the first element of the
	 * array is required to be either a {@link DiagnosisScore} or 
	 * {@link DiagnosisState} instance.
	 * @param theCase the context in which the new value was derived
	 * @param values an array, where the first element holds the new value of the Diagnosis
	 * @param context the {@link PSMethod} class that set the new value 
	 */
	@Deprecated
	public void setValue(XPSCase theCase, Object[] values, Class context) {
		try {
			DiagnosisScore diagnosisScore = null;
			DiagnosisState oldState = getState(theCase, context);
			if ((values != null) && (values.length > 0)) {
				Object value = values[0];
				if (value instanceof DiagnosisScore) {
					diagnosisScore = (DiagnosisScore) values[0];
					((CaseDiagnosis) theCase.getCaseObject(this)).setValue(diagnosisScore, context);
				} else if (value instanceof DiagnosisState) {
					((CaseDiagnosis) theCase.getCaseObject(this)).setValue(value, context);
				}
			}
			DiagnosisState newState = getState(theCase, context);
			// this does simply a check if state has changed
			checkForNewState(oldState, newState, theCase, context);
			// d3web.debug
			notifyListeners(theCase, this);

		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).throwing(this.getClass().getName(),
					"setValue(XPSCase, values(" + values[0] + ")", ex);
		}
	}

	/**
	 * Returns a simple {@link String} representation of this object.
	 * Delegates to {@link NamedObject}.toString().
	 * @return a String representation of this object
	 */
	public String toString() {
		return super.toString();
	}

}