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

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.DiagnosisState.State;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.ValuedObject;
import de.d3web.core.session.blackboard.CaseDiagnosis;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.scoring.DiagnosisScore;
import de.d3web.scoring.Score;
import de.d3web.scoring.inference.PSMethodHeuristic;

/**
 * This class stores the static, non case-dependent parts of a solutions. The
 * value of a solution, i.e., its state and score, is dependent from the
 * problem-solver context. If no problem-solver context is given, then the
 * heuristic problem-solver is the default context.
 * 
 * @author joba, chris
 * @see IDObject
 * @see NamedObject
 * @see DiagnosisScore
 * @see DiagnosisState
 */
public class Solution extends NamedObject implements ValuedObject, TerminologyObject {

	/**
	 * Compares the heuristic scores of two {@link Solution} instances.
	 * For other problem-solvers a new comparator should be
	 * implemented.
	 */
	static class HeuristicSolutionComparator implements Comparator<Solution> {
		private final Session session;
		public HeuristicSolutionComparator(Session theCase) {
			this.session = theCase;
		}
		public int compare(Solution d1, Solution d2) {
			return d1.getScore(session, PSMethodHeuristic.class).compareTo(
					d2.getScore(session, PSMethodHeuristic.class));
		}
	}

	/**
	 * A solution can have a prior probability, that is taken into account by
	 * the particular problem-solvers differently. The {@link PSMethodHeuristic}
	 * , for example, adds the a-priori probability as soon as the solution
	 * receives scores from a rule.
	 */
	private Score aprioriProbability;

	/**
	 * Creates a new {@link Solution} instance with the specified unique
	 * identifier. <br>
	 * <b>Note:</b> Please use {@link KnowledgeBaseManagement} to create
	 * Diagnosis instances.
	 * 
	 * @param id
	 *            the specified unique identifier for this instance
	 */
	public Solution(String id) {
	    super(id);
	}

	private void checkForNewState(DiagnosisState oldState, DiagnosisState newStatus,
			Session theCase) {
		if (oldState != newStatus) {
			if (newStatus.hasState(State.ESTABLISHED)) {
				establish(theCase);
			}
			if (oldState.hasState(State.ESTABLISHED)) {
				deestablish(theCase);
			}
		}
	}

	/**
	 * Creates a new dynamic flyweight for this object in the context of the
	 * specified {@link Session} instance. For every new {@link Session}
	 * flyweights are created on demand for the used {@link IDObject} instances.
	 * 
	 * @param session
	 *            the specified Session instance
	 * @return a flyweight instance of this object
	 */
	public SessionObject createCaseObject(Session session) {
		return new CaseDiagnosis(this);
	}

	/**
	 * Removes this object from the established solutions in the given
	 * {@link Session} and propagates the state change.
	 * 
	 * @param session
	 *            the specified {@link Session}
	 */
	private void deestablish(Session session) {
		session.removeEstablishedSolution(this);
	}

	/**
	 * Adds this object to the list of established solutions in the given
	 * {@link Session} and propagates the state change.
	 * 
	 * @param session
	 *            the specified {@link Session}
	 */
	private void establish(Session session) {
		session.addEstablishedSolution(this);
	}

	/**
	 * Returns the prior probability of this solution. The 'probability' is
	 * represented by a {@link Score}, and the use of this probability depends
	 * on the particular {@link PSMethod}.
	 * 
	 * @return the apripori probability
	 */
	public Score getAprioriProbability() {
		return aprioriProbability;
	}

	/**
	 * Returns a comparator that compares the {@link Score} values in the
	 * context of the given {@link Session} and the {@link PSMethodHeuristic}
	 * problem-solver. For further problem-solvers, you will need to implement
	 * your own {@link Comparator} class.
	 * 
	 * @param session
	 *            the {@link Session} instance for with the current scores are
	 *            compares
	 * @return a {@link Comparator} for two {@link Solution} objects
	 */
	public static Comparator<Solution> getComparator(Session session) {
		return new HeuristicSolutionComparator(session);
	}

	/**
	 * Returns the computed score of this {@link Solution} for a specified
	 * {@link Session} and a specified {@link PSMethod} context. The score of a
	 * solution is only valid in the context of <b>one</b> {@link PSMethod}, and
	 * can differ for other {@link PSMethod} instances.
	 * 
	 * @param session
	 *            the session in which the score is valid
	 * @param context
	 *            the {@link PSMethod} context the score is valid for
	 * @return the score of the {@link Solution} in the context of a
	 *         {@link Session} and {@link PSMethod} class
	 */
	public DiagnosisScore getScore(Session session, Class<? extends PSMethod> context) {
		return (DiagnosisScore) ((CaseDiagnosis) session.getCaseObject(this)).getValue(context);
	}

	/**
	 * @deprecated please use the corresponding method {@link Session}
	 *             .getState(...)
	 */
	@Deprecated
	public DiagnosisState getState(Session session, Class<? extends PSMethod> context) {
		// TODO: this is wrong! getState computes the real state every time, but this method should return the stored value of its CaseDiagnosis instance
		return session.getPSMethodInstance(context).getState(session, this);
	}

	/**
	 * Returns the <b>combined</b> state of this instance. The combined state is
	 * the maximum state value of all {@link PSMethod} instances for this
	 * {@link Solution}. The maximum is defined by the following order:
	 * <ol>
	 * <li>State.EXCLUDED
	 * <li>State.ESTABLISHED
	 * <li>State.SUGGESTED
	 * <li>State.UNCLEAR
	 * </ol>
	 * 
	 * @param session
	 *            the case the state should be computed for
	 * @return the combined state of this Diagnosis
	 * @deprecated please use corresponding method {@link Session}
	 *             .getState(Solution solution)
	 */
	@Deprecated
	public DiagnosisState getState(Session session) {
		return session.getState(this);
	}

	/**
	 * Sets the new apriori probability of this instance.
	 * The value is fixed to the predefined {@link Score} values:
	 * P5, P4, P3, P2, N2, N3, N4, N5.
	 * <p>Creation date: (25.09.00 15:13:34)
	 * @param newAprioriPropability the new apriori probability of this instance
	 * @throws IllegalArgumentException if the newAprioriProbability is not valid
	 */
	public void setAprioriProbability(Score newAprioriProbability) throws IllegalArgumentException {
		// check if legal probability entry
		if (!Score.APRIORI.contains(newAprioriProbability) && (newAprioriProbability != null)) {
			throw new IllegalArgumentException(newAprioriProbability
					+ " not a valid apriori probability.");
		} else
			aprioriProbability = newAprioriProbability;
	}

	/**
	 * Sets the knowledge base instance, to which this object belongs to. This
	 * method also adds this object to the knowledge base (reverse link). <br>
	 * <b>Note:</b> Currently, this object is not removed from a previously
	 * registered knowledge base.
	 * 
	 * @param knowledgeBase
	 *            the knowledge base, to which this object belongs to
	 */
	@Override
	public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
		super.setKnowledgeBase(knowledgeBase);
		// maybe somebody should remove this object from the old
		// knowledge base if available
		getKnowledgeBase().add(this);
	}

	@Override
	@Deprecated
	public void setValue(Session theCase, Value value) {
		setValue(theCase, value, null);
	}

	/**
	 * Please use: {@link Session}.setValue. <BR>
	 * Sets the specified value for this instance in the specified
	 * {@link Session} instance and {@link PSMethod} context.
	 * 
	 * @param session
	 *            the specified session
	 * @param value
	 *            the specified value
	 * @param context
	 *            the specified PSMethod context
	 * @author joba
	 * @date 15.04.2010
	 */
	public void setValue(Session session, Value value, Class<? extends PSMethod> context) {
		DiagnosisScore diagnosisScore = null;
		DiagnosisState oldState = session.getState(this, context);
		if (value != null) {
			if (value instanceof DiagnosisScore) {
				diagnosisScore = (DiagnosisScore) value;
				((CaseDiagnosis) session.getCaseObject(this)).setValue(diagnosisScore,
						context);
			}
			else if (value instanceof DiagnosisState) {
				((CaseDiagnosis) session.getCaseObject(this)).setValue(value, context);
			}
		}
		DiagnosisState newState = session.getState(this, context);
		// this does simply a check if state has changed
		checkForNewState(oldState, newState, session);
	}

	@Override
	public String toString() {
		return super.toString();
	}

}