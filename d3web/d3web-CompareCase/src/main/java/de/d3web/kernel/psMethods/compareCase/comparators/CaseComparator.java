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
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.answers.AnswerUnknown;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionMC;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.domainModel.qasets.QuestionOC;
import de.d3web.kernel.domainModel.qasets.QuestionText;
import de.d3web.kernel.domainModel.qasets.QuestionYN;
import de.d3web.kernel.psMethods.compareCase.CompareObjectsHashContainer;
import de.d3web.kernel.psMethods.compareCase.tests.utils.CaseObjectTestDummy;
import de.d3web.kernel.psMethods.shared.Abnormality;
import de.d3web.kernel.psMethods.shared.PSContextFinder;
import de.d3web.kernel.psMethods.shared.PSMethodShared;
import de.d3web.kernel.psMethods.shared.QuestionWeightValue;
import de.d3web.kernel.psMethods.shared.Weight;
import de.d3web.kernel.psMethods.shared.comparators.KnowledgeBaseUnknownSimilarity;
import de.d3web.kernel.psMethods.shared.comparators.QuestionComparator;
import de.d3web.kernel.psMethods.shared.comparators.mc.QuestionComparatorMCIndividual;
import de.d3web.kernel.psMethods.shared.comparators.num.QuestionComparatorNumDivision;
import de.d3web.kernel.psMethods.shared.comparators.oc.QuestionComparatorOCIndividual;
import de.d3web.kernel.psMethods.shared.comparators.oc.QuestionComparatorYN;
import de.d3web.kernel.psMethods.shared.comparators.text.QuestionComparatorTextIndividual;

/**
 * Class that compares two cases and returns a List of ComparatorResult objects
 * Creation date: (02.08.2001 15:53:18)
 * 
 * @author: Norman Brümmer
 */
public class CaseComparator {

	private static final AnswerUnknown UNKNOWN_ANSWER = new AnswerUnknown();

	private static final double DEFAULT_UNKNOWN_FACTOR = 0.1;

	private static boolean isKnowledgeBaseUnknownSimilarityCached = false;

	private static KnowledgeBaseUnknownSimilarity knowledgeBaseUnknownSimilarity = null;

	/**
	 * Creation date: (11.08.01 16:18:47)
	 * 
	 * @return double
	 */
	private static double calcAbnormality(Abnormality abn, Collection answers) {
		if (answers == null) {
			return 1;
		}

		double ret = 0;
		Iterator iter = answers.iterator();
		while (iter.hasNext()) {
			Answer ans = (Answer) iter.next();
			double val = abn.getValue(ans);
			if (val > ret) {
				ret = val;
			}
		}
		return ret;
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

		Iterator queryQuestionIter = queryCase.getQuestions().iterator();
		while (queryQuestionIter.hasNext()) {
			Question queryQuestion = (Question) queryQuestionIter.next();
			QuestionComparator qcomp = getQuestionComparator(queryQuestion);
			double weight = getWeight(queryCase, queryQuestion);
			Collection queryAnswers = queryCase.getAnswers(queryQuestion);
			Collection storedAnswers = storedCase.getAnswers(queryQuestion);

			double abnormality = 1;
			List abnorm = queryQuestion.getKnowledge(PSContextFinder.getInstance()
					.findPSContext(Abnormality.class), PSMethodShared.SHARED_ABNORMALITY);
			if (abnorm != null) {
				abnormality = calcAbnormality((Abnormality) abnorm.get(0), storedAnswers);
			}

			if (!isUnknown(queryAnswers) || !isUnknown(storedAnswers)) {

				if (!isUnknown(queryAnswers)) {
					if (!isUnknown(storedAnswers)) {
						// normal comparison
						reachedPoints += compareQuestionForClustering(cmode, queryCase,
								new Object[]{queryQuestion, queryAnswers}, new Object[]{
										queryQuestion, storedAnswers});
						maxPoints += weight * abnormality;
					} else {
						if (cmode.covers(CompareMode.COMPARE_CASE_FILL_UNKNOWN)) {
							// create default unknown comparisonResult
							reachedPoints += getDefaultComparisonResultForClustering();
							double unkSim = getUnknownFactor(queryQuestion, qcomp);
							maxPoints += weight * unkSim;
						}
					}
				} else {
					if (cmode.covers(CompareMode.CURRENT_CASE_FILL_UNKNOWN)) {
						if (!isUnknown(storedAnswers)) {
							// create default unknown comparisonResult
							reachedPoints += getDefaultComparisonResultForClustering();
							double unkSim = getUnknownFactor(queryQuestion, qcomp);
							maxPoints += weight * unkSim;
						} else if (cmode.covers(CompareMode.BOTH_FILL_UNKNOWN)) {
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
			Iterator storedQuestionsIter = storedCase.getQuestions().iterator();
			while (storedQuestionsIter.hasNext()) {
				Question storedQuestion = (Question) storedQuestionsIter.next();
				QuestionComparator qcomp = getQuestionComparator(storedQuestion);
				Collection storedAnswers = storedCase.getAnswers(storedQuestion);
				if (!queryCase.getQuestions().contains(storedQuestion) && !isUnknown(storedAnswers)) {
					// create default unknown comparisonResult
					double weight = getWeight(queryCase, storedQuestion);
					reachedPoints += getDefaultComparisonResultForClustering();
					double unkSim = getUnknownFactor(storedQuestion, qcomp);
					maxPoints += weight * unkSim;
				}
			}
		}

		if (reachedPoints > maxPoints) {
			System.err.println("reached > max!  : query: " + queryCase.getId() + " stored: "
					+ storedCase.getId());
		}

		return reachedPoints / maxPoints;
	}

	public static List compareCases(CompareMode cmode, CaseObject queryCase, CaseObject storedCase) {
		List ret = new LinkedList();
		Iterator queryQuestionIter = queryCase.getQuestions().iterator();
		while (storedCase != null && queryQuestionIter.hasNext()) {
			Question queryQuestion = (Question) queryQuestionIter.next();

			Collection queryAnswers = queryCase.getAnswers(queryQuestion);
			Collection storedAnswers = storedCase.getAnswers(queryQuestion);

			if (!isUnknown(queryAnswers) || !isUnknown(storedAnswers)) {

				if (!isUnknown(queryAnswers)) {
					if (!isUnknown(storedAnswers)) {
						// normal comparison
						compareQuestion(cmode, queryCase, ret, new Object[]{queryQuestion,
								queryAnswers}, new Object[]{queryQuestion, storedAnswers});
					} else {
						if (cmode.covers(CompareMode.COMPARE_CASE_FILL_UNKNOWN)) {
							// create default unknown comparisonResult
							addDefaultComparisonResult(storedCase, queryQuestion, queryAnswers,
									storedAnswers, ret);
						}
					}
				} else {
					if (cmode.covers(CompareMode.CURRENT_CASE_FILL_UNKNOWN)) {
						if (!isUnknown(storedAnswers)) {
							// create default unknown comparisonResult
							addDefaultComparisonResult(storedCase, queryQuestion, queryAnswers,
									storedAnswers, ret);

						} else if (cmode.covers(CompareMode.BOTH_FILL_UNKNOWN)) {
							// create default unknown comparisonResult
							addDefaultComparisonResult(storedCase, queryQuestion, queryAnswers,
									storedAnswers, ret);
						}
					}
				}
			}
		}

		// look for not considered stored questions if BOTH_FILL_UNKNOWN is the
		// CompareMode
		if (cmode.equals(CompareMode.BOTH_FILL_UNKNOWN)) {
			if (storedCase != null) {
				Iterator storedQuestionsIter = storedCase.getQuestions().iterator();
				while (storedQuestionsIter.hasNext()) {
					Question storedQuestion = (Question) storedQuestionsIter.next();
					Collection storedAnswers = storedCase.getAnswers(storedQuestion);
					if (!queryCase.getQuestions().contains(storedQuestion)
							&& !isUnknown(storedAnswers)) {
						// create default unknown comparisonResult
						addDefaultComparisonResult(storedCase, storedQuestion, null, storedAnswers,
								ret);
					}
				}
			}
		}
		return ret;
	}

	private static void addDefaultComparisonResult(CaseObject storedCase, Question queryQuestion,
			Collection queryAnswers, Collection storedAnswers, List ret) {

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
			} else {

				Collection knowledge = null;
				if (queryQuestion != null) {
					knowledge = queryQuestion.getKnowledgeBase().getAllKnowledgeSlicesFor(
							PSMethodShared.class);
				}

				boolean found = false;
				if ((knowledge != null) && !knowledge.isEmpty()) {
					Iterator iter = knowledge.iterator();
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
		List queryAnswers = new LinkedList((Collection) queryQuestionAndAnswers[1]);
		List storedAnswers = new LinkedList((Collection) storedQuestionAndAnswers[1]);

		double weight = getWeight(storedCase, question);
		double abnormality = 1;
		double weightedSimilarity = 0;
		List abnorm = question.getKnowledge(PSContextFinder.getInstance().findPSContext(
				Abnormality.class), PSMethodShared.SHARED_ABNORMALITY);
		if (abnorm != null) {
			abnormality = calcAbnormality((Abnormality) abnorm.get(0), storedAnswers);
		}

		QuestionComparator qcomp = getQuestionComparator(question);
		double sim = qcomp.compare(storedAnswers, queryAnswers);
		weightedSimilarity = sim * weight;
		if (sim > 1) {
			Logger.getLogger(CaseComparator.class.getName()).severe("sim > 1 !! : " + sim);
		}
		return weightedSimilarity * abnormality;
	}

	private static void compareQuestion(CompareMode cmode, CaseObject storedCase, List ret,
			Object[] queryQuestionAndAnswers, Object[] storedQuestionAndAnswers) {

		Question question = (Question) queryQuestionAndAnswers[0];
		List queryAnswers = (List) queryQuestionAndAnswers[1];
		List storedAnswers = new LinkedList((Collection) storedQuestionAndAnswers[1]);
		double abnormality = 1;
		List abnorm = question.getKnowledge(PSContextFinder.getInstance().findPSContext(
				Abnormality.class), PSMethodShared.SHARED_ABNORMALITY);
		if (abnorm != null) {
			abnormality = calcAbnormality((Abnormality) abnorm.get(0), storedAnswers);
		}
		int weight = getWeight(storedCase, question);
		QuestionComparator qcomp = getQuestionComparator(question);
		ComparatorResult result = CompareObjectsHashContainer.getInstance().getComparatorResult(
				question.getId());
		if (!isUnknown(storedAnswers) && !isUnknown(queryAnswers)) {
			result.setSimilarity(qcomp.compare(storedAnswers, queryAnswers));
			result.setMaxPoints(weight);
			result.setReachedPoints(weight * result.getSimilarity());
		} else {
			result = createUnknownSimResult(question, weight, qcomp);
		}
		result.setQueryQuestionAndAnswers(question, queryAnswers);
		result.setStoredQuestionAndAnswers(question, storedAnswers);
		result.setAbnormality(abnormality);
		ret.add(result);
	}

	private static boolean isUnknown(Collection answers) {
		return (answers == null) || (answers.isEmpty()) || answers.contains(UNKNOWN_ANSWER);
	}

	public static QuestionComparator getQuestionComparator(Question q) {
		Object o = q.getKnowledge(PSContextFinder.getInstance().findPSContext(
				QuestionComparator.class), PSMethodShared.SHARED_SIMILARITY);
		QuestionComparator qcomp = null;
		if (o == null) {
			// default similarity !
			qcomp = addDefaultKnowledge(q);
		} else {
			try{
			qcomp = (QuestionComparator) ((List) o).get(0);
			} catch (IndexOutOfBoundsException e){
				return addDefaultKnowledge(q);
			}
		}
		return qcomp;
	}
	
	public static QuestionComparator addDefaultKnowledge(Question q) {

		QuestionComparator qc = null;

		if (q instanceof QuestionYN) {
			qc = new QuestionComparatorYN();
		} else if (q instanceof QuestionOC) {
			qc = new QuestionComparatorOCIndividual();
		} else if (q instanceof QuestionMC) {
			qc = new QuestionComparatorMCIndividual();
		} else if (q instanceof QuestionNum) {
			qc = new QuestionComparatorNumDivision();
		} else if (q instanceof QuestionText) {
			qc = new QuestionComparatorTextIndividual();
		}

		if (qc != null) {
			qc.setQuestion(q);
		}
		return qc;
	}

	public static int getWeight(CaseObject aCase, Question q) {
		Object o = q.getKnowledge(PSContextFinder.getInstance().findPSContext(Weight.class),
				PSMethodShared.SHARED_WEIGHT);

		Weight qWeight = null;
		if (o == null) {
			// default knowledge !
			qWeight = addDefaultWeight(q);
		} else {
			qWeight = (Weight) ((List) o).get(0);
			if (qWeight == null) {
				qWeight = addDefaultWeight(q);
			}
		}
		Collection establishedDiagnoses = getEstablishedDiagnoses(aCase);

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

	private static Collection getEstablishedDiagnoses(CaseObject aCase) {
		List establishedDiagnoses = new LinkedList();
		Iterator iter = aCase.getSolutions().iterator();
		while (iter.hasNext()) {
			CaseObject.Solution sol = (CaseObject.Solution) iter.next();
			if (DiagnosisState.ESTABLISHED.equals(sol.getState())) {
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