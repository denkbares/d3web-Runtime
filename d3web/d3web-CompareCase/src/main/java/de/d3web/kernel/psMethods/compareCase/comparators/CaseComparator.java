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

package de.d3web.kernel.psMethods.compareCase.comparators;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.caserepository.CaseObject;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.Unknown;
import de.d3web.kernel.psMethods.compareCase.CompareObjectsHashContainer;
import de.d3web.kernel.psMethods.compareCase.tests.utils.CaseObjectTestDummy;
import de.d3web.shared.Abnormality;
import de.d3web.shared.PSContextFinder;
import de.d3web.shared.PSMethodShared;
import de.d3web.shared.QuestionWeightValue;
import de.d3web.shared.Weight;
import de.d3web.shared.comparators.KnowledgeBaseUnknownSimilarity;
import de.d3web.shared.comparators.QuestionComparator;
import de.d3web.shared.comparators.mc.QuestionComparatorMCIndividual;
import de.d3web.shared.comparators.num.QuestionComparatorNumDivision;
import de.d3web.shared.comparators.oc.QuestionComparatorOCIndividual;
import de.d3web.shared.comparators.oc.QuestionComparatorYN;
import de.d3web.shared.comparators.text.QuestionComparatorTextIndividual;

/**
 * Class that compares two cases and returns a List of ComparatorResult objects
 * Creation date: (02.08.2001 15:53:18)
 * 
 * @author: Norman Brümmer
 */
public class CaseComparator {

	private static final double DEFAULT_UNKNOWN_FACTOR = 0.1;

	private static boolean isKnowledgeBaseUnknownSimilarityCached = false;

	private static KnowledgeBaseUnknownSimilarity knowledgeBaseUnknownSimilarity = null;

	/**
	 * Creation date: (11.08.01 16:18:47)
	 * 
	 * @return double
	 */
	private static double calcAbnormality(Abnormality abn, Value value) {
		if (value == null) {
			return 1;
		}
		else {
			return abn.getValue(value);
		}
	}

	public static double calculateSimilarityBetweenCases(CompareMode cmode, CaseObject queryCase,
			CaseObject storedCase) {

		// FOR UNIT TESTS ONLY
		if (cmode.equals(CompareMode.JUNIT_TEST)) {
			return ((CaseObjectTestDummy) queryCase)
					.getSimilarityForUnitTests((CaseObjectTestDummy) storedCase);
		}

		double reachedPoints = 0;
		double maxPoints = 0;

		Iterator<Question> queryQuestionIter = queryCase.getQuestions().iterator();
		while (queryQuestionIter.hasNext()) {
			Question queryQuestion = queryQuestionIter.next();
			QuestionComparator qcomp = getQuestionComparator(queryQuestion);
			double weight = getWeight(queryCase, queryQuestion);
			Value queryValue = queryCase.getValue(queryQuestion);
			Value storedValue = storedCase.getValue(queryQuestion);
			double abnormality = 1;
			KnowledgeSlice abnorm = queryQuestion.getKnowledge(
					PSContextFinder.getInstance()
					.findPSContext(Abnormality.class),
					PSMethodShared.SHARED_ABNORMALITY);
			if (abnorm != null) {
				abnormality = calcAbnormality((Abnormality) abnorm, storedValue);
			}

			if (!(queryValue instanceof Unknown) || !(storedValue instanceof Unknown)) {

				if (!(queryValue instanceof Unknown)) {
					if (!(storedValue instanceof Unknown)) {
						// normal comparison
						reachedPoints += compareQuestionForClustering(cmode, queryCase,
								new Object[] {
								queryQuestion, queryValue }, new Object[] {
								queryQuestion, storedValue });
						maxPoints += weight * abnormality;
					}
					else {
						if (cmode.covers(CompareMode.COMPARE_CASE_FILL_UNKNOWN)) {
							// create default unknown comparisonResult
							reachedPoints += getDefaultComparisonResultForClustering();
							double unkSim = getUnknownFactor(queryQuestion, qcomp);
							maxPoints += weight * unkSim;
						}
					}
				}
				else {
					if (cmode.covers(CompareMode.CURRENT_CASE_FILL_UNKNOWN)) {
						if (!(storedValue instanceof Unknown)) {
							// create default unknown comparisonResult
							reachedPoints += getDefaultComparisonResultForClustering();
							double unkSim = getUnknownFactor(queryQuestion, qcomp);
							maxPoints += weight * unkSim;
						}
						else if (cmode.covers(CompareMode.BOTH_FILL_UNKNOWN)) {
							// create default unknown comparisonResult
							reachedPoints += getDefaultComparisonResultForClustering();
							double unkSim = getUnknownFactor(queryQuestion, qcomp);
							maxPoints += weight * unkSim;
						}
					}
				}
			}
		}

		// look for not considered stored questions if BOTH_FILL_UNKNOWN is the
		// CompareMode
		if (cmode.equals(CompareMode.BOTH_FILL_UNKNOWN)) {
			Iterator<Question> storedQuestionsIter = storedCase.getQuestions().iterator();
			while (storedQuestionsIter.hasNext()) {
				Question storedQuestion = storedQuestionsIter.next();
				QuestionComparator qcomp = getQuestionComparator(storedQuestion);
				Value storedValue = storedCase.getValue(storedQuestion);
				if (!queryCase.getQuestions().contains(storedQuestion)
						&& !(storedValue instanceof Unknown)) {
					// create default unknown comparisonResult
					double weight = getWeight(queryCase, storedQuestion);
					reachedPoints += getDefaultComparisonResultForClustering();
					double unkSim = getUnknownFactor(storedQuestion, qcomp);
					maxPoints += weight * unkSim;
				}
			}
		}

		if (reachedPoints > maxPoints) {
			System.err.println("reached > max!  : query: " + queryCase.getId()
					+ " stored: "
					+ storedCase.getId());
		}

		return reachedPoints / maxPoints;
	}

	public static List<ComparatorResult> compareCases(CompareMode cmode, CaseObject queryCase, CaseObject storedCase) {
		List<ComparatorResult> ret = new LinkedList<ComparatorResult>();
		Iterator<Question> queryQuestionIter = queryCase.getQuestions().iterator();
		while (storedCase != null && queryQuestionIter.hasNext()) {
			Question queryQuestion = queryQuestionIter.next();

			Value queryValue = queryCase.getValue(queryQuestion);
			Value storedValue = storedCase.getValue(queryQuestion);

			if (!(queryValue instanceof Unknown) || !(storedValue instanceof Unknown)) {

				if (!(queryValue instanceof Unknown)) {
					if (!(storedValue instanceof Unknown)) {
						// normal comparison
						compareQuestion(cmode, queryCase, ret, new Object[] {
								queryQuestion,
								queryValue }, new Object[] {
								queryQuestion, storedValue });
					}
					else {
						if (cmode.covers(CompareMode.COMPARE_CASE_FILL_UNKNOWN)) {
							// create default unknown comparisonResult
							addDefaultComparisonResult(storedCase, queryQuestion,
									queryValue,
									storedValue, ret);
						}
					}
				}
				else {
					if (cmode.covers(CompareMode.CURRENT_CASE_FILL_UNKNOWN)) {
						if (!(storedValue instanceof Unknown)) {
							// create default unknown comparisonResult
							addDefaultComparisonResult(storedCase, queryQuestion,
									queryValue,
									storedValue, ret);

						}
						else if (cmode.covers(CompareMode.BOTH_FILL_UNKNOWN)) {
							// create default unknown comparisonResult
							addDefaultComparisonResult(storedCase, queryQuestion,
									queryValue,
									storedValue, ret);
						}
					}
				}
			}
		}

		// look for not considered stored questions if BOTH_FILL_UNKNOWN is the
		// CompareMode
		if (cmode.equals(CompareMode.BOTH_FILL_UNKNOWN)) {
			if (storedCase != null) {
				Iterator<Question> storedQuestionsIter = storedCase.getQuestions().iterator();
				while (storedQuestionsIter.hasNext()) {
					Question storedQuestion = storedQuestionsIter.next();
					Value storedAnswers = storedCase.getValue(storedQuestion);
					if (!queryCase.getQuestions().contains(storedQuestion)
							&& !(storedAnswers instanceof Unknown)) {
						// create default unknown comparisonResult
						addDefaultComparisonResult(storedCase, storedQuestion, null,
								storedAnswers,
								ret);
					}
				}
			}
		}
		return ret;
	}

	private static void addDefaultComparisonResult(CaseObject storedCase, Question queryQuestion,
			Value queryAnswers, Value storedAnswers, List<ComparatorResult> ret) {

		int weight = getWeight(storedCase, queryQuestion);
		QuestionComparator qcomp = getQuestionComparator(queryQuestion);
		ComparatorResult result = createUnknownSimResult(queryQuestion, weight, qcomp);
		result.setQueryQuestionAndAnswers(queryQuestion, queryAnswers);
		result.setStoredQuestionAndAnswers(queryQuestion, storedAnswers);
		result.setAbnormality(1);
		ret.add(result);
	}

	private static double getDefaultComparisonResultForClustering() {
		return 0;
	}

	private static double getUnknownFactor(Question queryQuestion, QuestionComparator qcomp) {
		double unkSim = -1;
		if ((qcomp != null) && (qcomp.getQuestion() != null)) {
			unkSim = qcomp.getUnknownSimilarity();
		}
		if (unkSim == -1) {
			if (isKnowledgeBaseUnknownSimilarityCached) {
				unkSim = knowledgeBaseUnknownSimilarity.getSimilarity();
			}
			else {

				Collection<KnowledgeSlice> knowledge = null;
				if (queryQuestion != null) {
					knowledge = queryQuestion.getKnowledgeBase().getAllKnowledgeSlicesFor(
							PSMethodShared.class);
				}

				boolean found = false;
				if ((knowledge != null) && !knowledge.isEmpty()) {
					Iterator<KnowledgeSlice> iter = knowledge.iterator();
					while (!found && iter.hasNext()) {
						Object o = iter.next();
						if (o instanceof KnowledgeBaseUnknownSimilarity) {
							KnowledgeBaseUnknownSimilarity knus = (KnowledgeBaseUnknownSimilarity) o;
							unkSim = knus.getSimilarity();
							found = true;

							isKnowledgeBaseUnknownSimilarityCached = true;
							knowledgeBaseUnknownSimilarity = knus;
						}
					}
				}

				if (!found) {
					isKnowledgeBaseUnknownSimilarityCached = true;
					knowledgeBaseUnknownSimilarity = new KnowledgeBaseUnknownSimilarity();
					knowledgeBaseUnknownSimilarity.setSimilarity(unkSim);
				}

			}
		}
		if (unkSim == -1) {
			unkSim = DEFAULT_UNKNOWN_FACTOR;
		}
		return unkSim;
	}

	private static double compareQuestionForClustering(CompareMode cmode, CaseObject storedCase,
			Object[] queryQuestionAndAnswers, Object[] storedQuestionAndAnswers) {

		Question question = (Question) queryQuestionAndAnswers[0];
		Value queryAnswers = (Value) queryQuestionAndAnswers[1];
		Value storedAnswers = (Value) storedQuestionAndAnswers[1];

		double weight = getWeight(storedCase, question);
		double abnormality = 1;
		double weightedSimilarity = 0;
		KnowledgeSlice abnorm = question.getKnowledge(
				PSContextFinder.getInstance().findPSContext(
				Abnormality.class), PSMethodShared.SHARED_ABNORMALITY);
		if (abnorm != null) {
			abnormality = calcAbnormality((Abnormality) abnorm, storedAnswers);
		}

		QuestionComparator qcomp = getQuestionComparator(question);
		double sim = qcomp.compare(storedAnswers, queryAnswers);
		weightedSimilarity = sim * weight;
		if (sim > 1) {
			Logger.getLogger(CaseComparator.class.getName()).severe("sim > 1 !! : " + sim);
		}
		return weightedSimilarity * abnormality;
	}

	private static void compareQuestion(CompareMode cmode, CaseObject storedCase, List<ComparatorResult> ret,
			Object[] queryQuestionAndAnswers, Object[] storedQuestionAndAnswers) {

		Question question = (Question) queryQuestionAndAnswers[0];
		Value queryValue = (Value) queryQuestionAndAnswers[1];
		Value storedValue = (Value) storedQuestionAndAnswers[1];
		double abnormality = 1;
		KnowledgeSlice abnorm = question.getKnowledge(
				PSContextFinder.getInstance().findPSContext(
				Abnormality.class), PSMethodShared.SHARED_ABNORMALITY);
		if (abnorm != null) {
			abnormality = calcAbnormality((Abnormality) abnorm, storedValue);
		}
		int weight = getWeight(storedCase, question);
		QuestionComparator qcomp = getQuestionComparator(question);
		ComparatorResult result = CompareObjectsHashContainer.getInstance().getComparatorResult(
				question.getId());
		if (!(storedValue instanceof Unknown) && !(queryValue instanceof Unknown)) {
			result.setSimilarity(qcomp.compare(storedValue, queryValue));
			result.setMaxPoints(weight);
			result.setReachedPoints(weight * result.getSimilarity());
		}
		else {
			result = createUnknownSimResult(question, weight, qcomp);
		}
		result.setQueryQuestionAndAnswers(question, queryValue);
		result.setStoredQuestionAndAnswers(question, storedValue);
		result.setAbnormality(abnormality);
		ret.add(result);
	}

	public static QuestionComparator getQuestionComparator(Question q) {
		KnowledgeSlice o = q.getKnowledge(PSContextFinder.getInstance().findPSContext(
				QuestionComparator.class), PSMethodShared.SHARED_SIMILARITY);
		QuestionComparator qcomp = null;
		if (o == null) {
			// default similarity !
			qcomp = addDefaultKnowledge(q);
		}
		else {
			qcomp = (QuestionComparator) o;
		}
		return qcomp;
	}

	public static QuestionComparator addDefaultKnowledge(Question q) {

		QuestionComparator qc = null;

		if (q instanceof QuestionYN) {
			qc = new QuestionComparatorYN();
		}
		else if (q instanceof QuestionOC) {
			qc = new QuestionComparatorOCIndividual();
		}
		else if (q instanceof QuestionMC) {
			qc = new QuestionComparatorMCIndividual();
		}
		else if (q instanceof QuestionNum) {
			qc = new QuestionComparatorNumDivision();
		}
		else if (q instanceof QuestionText) {
			qc = new QuestionComparatorTextIndividual();
		}

		if (qc != null) {
			qc.setQuestion(q);
		}
		return qc;
	}

	public static int getWeight(CaseObject aCase, Question q) {
		KnowledgeSlice o = q.getKnowledge(PSContextFinder.getInstance().findPSContext(
				Weight.class),
				PSMethodShared.SHARED_WEIGHT);

		Weight qWeight = null;
		if (o == null) {
			// default knowledge !
			qWeight = addDefaultWeight(q);
		}
		else {
			qWeight = (Weight) o;
		}
		Collection<Solution> establishedDiagnoses = getEstablishedDiagnoses(aCase);

		int weight = qWeight.getMaxDiagnosisWeightValueFromDiagnoses(establishedDiagnoses);
		if (weight == -1) {
			weight = qWeight.getQuestionWeightValue().getValue();
		}
		return weight;
	}

	private static Weight addDefaultWeight(Question q) {

		Weight w = new Weight();

		QuestionWeightValue qww = new QuestionWeightValue();
		qww.setQuestion(q);
		qww.setValue(Weight.G4);

		w.setQuestionWeightValue(qww);

		return w;
	}

	private static Collection<Solution> getEstablishedDiagnoses(CaseObject aCase) {
		List<Solution> establishedDiagnoses = new LinkedList<Solution>();
		Iterator<CaseObject.Solution> iter = aCase.getSolutions().iterator();
		while (iter.hasNext()) {
			CaseObject.Solution sol = iter.next();
			if (new Rating(Rating.State.ESTABLISHED).equals(sol.getState())) {
				establishedDiagnoses.add(sol.getDiagnosis());
			}
		}
		return establishedDiagnoses;
	}

	private static ComparatorResult createUnknownSimResult(Question question, int weight,
			QuestionComparator qcomp) {
		ComparatorResult result = CompareObjectsHashContainer.getInstance().getComparatorResult(
				question.getId());

		double unkSim = getUnknownFactor(question, qcomp);

		result.setMaxPoints(weight * unkSim);
		result.setReachedPoints(0);
		result.setSimilarity(0);

		return result;
	}

}