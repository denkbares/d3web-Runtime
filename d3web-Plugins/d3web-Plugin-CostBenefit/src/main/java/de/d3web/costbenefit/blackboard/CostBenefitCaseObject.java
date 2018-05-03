/*
 * Copyright (C) 2010 denkbares GmbH
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.costbenefit.blackboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.denkbares.utils.Log;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.StrategicSupport;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionZC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.protocol.TextProtocolEntry;
import de.d3web.costbenefit.CostBenefitUtil;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.costbenefit.model.SearchModel;
import de.d3web.costbenefit.model.Target;
import de.d3web.costbenefit.model.ids.Node;
import de.d3web.costbenefit.session.protocol.CalculatedTargetEntry;
import de.d3web.xcl.XCLUtils;

/**
 * CaseObject for CostBenefit
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class CostBenefitCaseObject implements SessionObject {

	private QContainer[] currentSequence;
	private SearchModel searchModel;
	private List<Fact> indicatedFacts = new LinkedList<>();
	private int currentPathIndex = -1;
	private Set<Solution> undiscriminatedSolutions = new HashSet<>();
	private Set<Target> discriminatingTargets = new HashSet<>();
	private final Session session;
	private boolean abortedManuallySetTarget = false;
	private boolean manualMode = false;
	private Set<TerminologyObject> conflictingObjects = new HashSet<>();
	private QContainer unreachedTarget = null;
	private final Set<QContainer> watchedQContainers = new HashSet<>();

	private static final Pattern PATTERN_OK_CHOICE = Pattern.compile("^(.*#)?ok$",
			Pattern.CASE_INSENSITIVE);

	public CostBenefitCaseObject(Session session) {
		this.session = session;
	}

	public Session getSession() {
		return session;
	}

	public QContainer[] getCurrentSequence() {
		return currentSequence;
	}

	public boolean hasCurrentSequence() {
		return this.currentSequence != null;
	}

	public void setCurrentSequence(QContainer[] newSequence) {
		this.currentSequence = Arrays.copyOf(newSequence, newSequence.length);
		Log.fine("new cost benefit sequence: " + Arrays.asList(newSequence));
		unreachedTarget = null;
	}

	public SearchModel getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(SearchModel searchModel) {
		this.searchModel = searchModel;
	}

	public int getCurrentPathIndex() {
		return currentPathIndex;
	}

	public void setCurrentPathIndex(int currentPathIndex) {
		this.currentPathIndex = currentPathIndex;
	}

	public void incCurrentPathIndex() {
		currentPathIndex++;
		Log.fine("next qcontianer: "
				+ (currentPathIndex >= currentSequence.length
				? null
				: currentSequence[currentPathIndex]));
	}

	/**
	 * Resets the path
	 */
	public void resetPath() {
		if (currentSequence != null && currentSequence.length > currentPathIndex) {
			unreachedTarget = currentSequence[currentSequence.length - 1];
		}
		currentSequence = null;
		for (Fact fact : indicatedFacts) {
			session.getBlackboard().removeInterviewFact(fact);
		}
		indicatedFacts = new LinkedList<>();
		this.currentPathIndex = -1;
		abortedManuallySetTarget = false;
	}

	public void setIndicatedFacts(List<Fact> indicatedFacts) {
		this.indicatedFacts = indicatedFacts;
	}

	public List<Fact> getIndicatedFacts() {
		return Collections.unmodifiableList(indicatedFacts);
	}

	public boolean removeIndicatedFact(Fact fact) {
		return indicatedFacts.remove(fact);
	}

	public Set<Solution> getUndiscriminatedSolutions() {
		return undiscriminatedSolutions;
	}

	public void setUndiscriminatedSolutions(Set<Solution> undiscriminatedSolutions) {
		this.undiscriminatedSolutions = undiscriminatedSolutions;
	}

	public void setDiscriminatingTargets(Set<Target> allDiscriminatingTargets) {
		this.discriminatingTargets = allDiscriminatingTargets;
	}

	public Set<Target> getDiscriminatingTargets() {
		return discriminatingTargets;
	}

	public QContainer getCurrentQContainer() {
		if (currentSequence == null) return null;
		if (currentPathIndex == -1) {
			Log.warning("Sequence was generated and is accessed, but is not activated yet: "
					+ Arrays.toString(currentSequence));
			return null;
		}
		return currentSequence[currentPathIndex];
	}

	public boolean isAbortedManuallySetTarget() {
		return abortedManuallySetTarget;
	}

	public void setAbortedManuallySetTarget(boolean abortedManuallySetTarget) {
		this.abortedManuallySetTarget = abortedManuallySetTarget;
	}

	/**
	 * Returns true if the manual mode is turned on. In manual mode no paths were calculated automatically. Instead, the
	 * already calculated paths were completed as usual, but requiring to have other strategic solvers to append
	 * additional interview items, or manually starts the calculation for a new path.
	 *
	 * @return if manual mode is turned on
	 */
	public boolean isManualMode() {
		return manualMode;
	}

	/**
	 * Turns on/off the manual mode. In manual mode no paths were calculated automatically. Instead, the already
	 * calculated paths were completed as usual, but requiring to have other strategic solvers to append additional
	 * interview items, or manually starts the calculation for a new path.
	 *
	 * @param manualMode true if manual mode should be turned on
	 */
	public void setManualMode(boolean manualMode) {
		this.manualMode = manualMode;
	}

	/**
	 * Returns the questions, having a negative influence on the sprint group.
	 *
	 * @return Set containing the conflicting objects (Questions)
	 * @created 16.12.2013
	 */
	public Set<TerminologyObject> getConflictingObjects() {
		return Collections.unmodifiableSet(conflictingObjects);
	}

	public void setConflictingObjects(Set<TerminologyObject> conflictingObjects) {
		this.conflictingObjects = conflictingObjects;
	}

	public QContainer getUnreachedTarget() {
		return unreachedTarget;
	}

	public void resetUnreachedTarget() {
		this.unreachedTarget = null;
	}

	/**
	 * This method is just for refreshing the path of the CaseObject, the Facts get pushed into the blackboard by
	 * calculateNewPath
	 */
	public void activateNextQContainer() {
		if (currentSequence == null) return;
		// only check if the current one is done
		// (or no current one has been activated yet)
		if (this.getCurrentPathIndex() == -1
				|| CostBenefitUtil.isDone(currentSequence[this.getCurrentPathIndex()],
				session)) {
			this.incCurrentPathIndex();
			if (this.getCurrentPathIndex() >= currentSequence.length) {
				this.resetPath();
				return;
			}
			QContainer qc = currentSequence[this.getCurrentPathIndex()];
			// normally ok questions are made undone when starting a sequence,
			// but one item can occur more than once in a sequence, so it's
			// questions have to be handled earlier.
			// TODO maybe just make the questions of the previous container
			// undone? all others should be made undone earlier
			for (int i = 0; i < this.getCurrentPathIndex(); i++) {
				if (currentSequence[0] == qc) {
					retractQuestions(qc, session);
				}
			}
			if (!new Node(qc, null).isApplicable(session)) {
				this.resetPath();
				return;
			}
			// check if the rest of the path is applicable
			else if (!CostBenefitUtil.checkPath(Arrays.asList(currentSequence), session,
					this.getCurrentPathIndex(), false)) {
				this.resetPath();
				return;
			}
			// when activating the next qcontainer, which is applicable, check
			// if it is already done and fire state transition and move to next
			// QContainer if necessary
			if (CostBenefitUtil.isDone(qc, session)) {
				StateTransition st = StateTransition.getStateTransition(qc);
				if (st != null) st.fire(session);
				// remove indication
				for (Fact fact : this.getIndicatedFacts()) {
					if (fact.getTerminologyObject() == qc) {
						session.getBlackboard().removeInterviewFact(fact);
					}
				}
				activateNextQContainer();
			}
		}
	}

	/**
	 * Retracts a subset of the questions of the specified container within the specified session. All questions that
	 * are retractable (mostly "ok"-questions) will be retracted, all others will remain untouched.
	 *
	 * @param container the container to retract the contained questions
	 * @param session   the session to work on
	 */
	private void retractQuestions(TerminologyObject container, Session session) {
		Blackboard blackboard = session.getBlackboard();
		for (TerminologyObject nob : container.getChildren()) {
			if (nob instanceof Question) {
				// restrict to retractable questions
				Question question = (Question) nob;
				if (!isRetractableQuestion(question)) continue;
				for (PSMethod contributing : blackboard.getContributingPSMethods(question)) {
					// and also remove only facts from source solvers, not derived values
					if (contributing.hasType(PSMethod.Type.source)) {
						blackboard.removeValueFact(blackboard.getValueFact(question, contributing));
					}
				}
			}
			retractQuestions(nob, session);
		}
	}

	/**
	 * Returns true if the specified question should be retracted by the cots/benefit problem solver to prepare the
	 * qContainer to be re-asked. Usually all "ok"-questions questions will be retracted.
	 *
	 * @param question the question to be checked for retracting
	 * @return true if the question should be retracted
	 */
	private boolean isRetractableQuestion(Question question) {
		if (question instanceof QuestionOC) {
			if (question instanceof QuestionZC) return true;
			List<Choice> choices = ((QuestionOC) question).getAllAlternatives();
			if (choices.size() == 1
					&& PATTERN_OK_CHOICE.matcher(choices.get(0).getName()).matches()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Activates a ready-made path by indicating its questionnaires and storing it into the specified case object
	 *
	 * @param psmethod the ps method
	 * @param path     the path to be activated
	 * @created 08.03.2011
	 */
	public void activatePath(Collection<QContainer> path, PSMethod psmethod) {
		QContainer[] currentSequence = new QContainer[path.size()];
		int i = 0;
		List<Fact> facts = new LinkedList<>();
		for (QContainer qContainer : path) {
			currentSequence[i] = qContainer;
			retractQuestions(currentSequence[i], session);
			Fact fact = FactFactory.createFact(qContainer,
					new Indication(State.MULTIPLE_INDICATED, i), new Object(), psmethod);
			facts.add(fact);
			session.getBlackboard().addInterviewFact(fact);
			i++;
		}
		this.setCurrentSequence(currentSequence);
		this.setCurrentPathIndex(-1);
		this.setIndicatedFacts(facts);
		checkWatchedQContainers();
	}

	/**
	 * Returns if the undiscriminated solutions have changed since the last use of the search algorithm. This indicates
	 * that a new search should be performed to adapt to the new diagnostic situation.
	 *
	 * @return if the undiscriminated solutions have been changed
	 * @created 08.03.2011
	 */
	public boolean hasChangedUndiscriminatedSolutions() {
		// if the current set of undiscriminated solutions is null
		// this indicated that we will not check for undiscriminated solutions
		// at all
		if (this.getUndiscriminatedSolutions() == null) return false;

		// otherwise calculate the current solution to be discriminated and
		// compare them to the previous ones
		HashSet<Solution> currentSolutions = new HashSet<>();
		for (StrategicSupport strategicSupport : CostBenefitUtil.getStrategicSupports(session)) {
			currentSolutions.addAll(strategicSupport.getUndiscriminatedSolutions(session));
		}
		final Set<Solution> previousSolutions = this.getUndiscriminatedSolutions();
		if (!previousSolutions.containsAll(currentSolutions)) {
			String message = "The sprint group has increased/changed.\nPrevious group: "
					+ previousSolutions
					+ "\nActual group: " + currentSolutions;
			this.getSession().getProtocol().addEntry(
					new TextProtocolEntry(
							this.getSession().getPropagationManager().getPropagationTime(),
							message));
			Log.warning(message);
		}
		Set<TerminologyObject> conflictingQuestions = CostBenefitUtil.calculatePossibleConflictingQuestions(
				this.getSession(), currentSolutions);
		if (!this.getConflictingObjects().containsAll(conflictingQuestions)) {
			this.setConflictingObjects(conflictingQuestions);
			String message = "The following questions decreased the covering value of the sprint group: "
					+ conflictingQuestions;
			this.getSession().getProtocol().addEntry(
					new TextProtocolEntry(
							this.getSession().getPropagationManager().getPropagationTime(),
							message));
			Log.warning(message);
		}
		return !previousSolutions.equals(currentSolutions);
	}

	public Set<QContainer> getWatchedQContainers() {
		return watchedQContainers;
	}

	public void addWatch(QContainer watchedQContainer) {
		watchedQContainers.add(watchedQContainer);
	}

	private void checkWatchedQContainers() {
		if (searchModel == null) return;
		if (searchModel.getBestCostBenefitTarget() == null) return;
		if (Collections.disjoint(watchedQContainers, searchModel.getBestCostBenefitTarget().getQContainers())) return;

		List<Solution> sprintGroup = XCLUtils.getSprintGroup(session);
		Date date = new Date(session.getPropagationManager().getPropagationTime());
		session.getProtocol().addEntry(new CalculatedTargetEntry(
				searchModel.getBestCostBenefitTarget(), searchModel.getTargets(), date, sprintGroup));
	}
}
