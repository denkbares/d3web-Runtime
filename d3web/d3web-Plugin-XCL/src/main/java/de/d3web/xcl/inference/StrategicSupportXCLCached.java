/*
 * Copyright (C) 2012 denkbares GmbH, Germany
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
package de.d3web.xcl.inference;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.StrategicSupport;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.abnormality.Abnormality;
import de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.xcl.XCLContributedModelSet;
import de.d3web.xcl.XCLModel;
import de.d3web.xcl.XCLRelation;

/**
 * 
 * @author volker_belli
 * @created 27.05.2012
 */
public class StrategicSupportXCLCached implements StrategicSupport {

	private static void extractOrs(Collection<Condition> conds, Condition conditionedFinding) {
		// if the condition is a condNot,
		// try to replace it with condOr or condAnd
		if (conditionedFinding instanceof CondNot) {
			CondNot condNot = (CondNot) conditionedFinding;
			Condition subCondition = condNot.getTerms().get(0);
			if (subCondition instanceof CondEqual) {
				CondEqual condEqual = (CondEqual) subCondition;
				Value value = condEqual.getValue();
				if (condEqual.getQuestion() instanceof QuestionOC && value instanceof ChoiceValue) {
					ChoiceValue cv = (ChoiceValue) value;
					List<Condition> terms = new LinkedList<Condition>();
					QuestionOC oc = (QuestionOC) condEqual.getQuestion();
					for (Choice c : oc.getAllAlternatives()) {
						if (!cv.getChoiceID().equals(new ChoiceID(c))) {
							terms.add(new CondEqual(oc, new ChoiceValue(c)));
						}
					}
					conditionedFinding = new CondOr(terms);
				}
			}
			// use De Morgan
			else if (subCondition instanceof CondAnd) {
				CondAnd condAnd = (CondAnd) subCondition;
				List<Condition> terms = new LinkedList<Condition>();
				for (Condition c : condAnd.getTerms()) {
					terms.add(new CondNot(c));
				}
				conditionedFinding = new CondOr(terms);
			}
			else if (subCondition instanceof CondOr) {
				CondOr condOr = (CondOr) subCondition;
				List<Condition> terms = new LinkedList<Condition>();
				for (Condition c : condOr.getTerms()) {
					terms.add(new CondNot(c));
				}
				conditionedFinding = new CondAnd(terms);
			}
		}
		if (conditionedFinding instanceof CondOr) {
			for (Condition condition : ((CondOr) conditionedFinding).getTerms()) {
				extractOrs(conds, condition);
			}
		}
		else if ((conditionedFinding instanceof CondAnd)
				&& ((CondAnd) conditionedFinding).getTerms().size() == 1) {
			extractOrs(conds, ((CondAnd) conditionedFinding).getTerms().get(0));
		}
		// replace condequals of normal answers with null
		else if (conditionedFinding instanceof CondEqual) {
			CondEqual condEqual = (CondEqual) conditionedFinding;
			DefaultAbnormality abnormalityStore = condEqual.getQuestion().getInfoStore().getValue(
					BasicProperties.DEFAULT_ABNORMALITIY);
			double abnormality = Abnormality.A5;
			if (abnormalityStore != null) {
				abnormality = abnormalityStore.getValue(condEqual.getValue());
			}
			if (abnormality == Abnormality.A0) {
				conds.add(null);
			}
			else {
				conds.add(condEqual);
			}
		}
		else {
			conds.add(conditionedFinding);
		}

	}

	private static Collection<Question> getRelevantQuestions(Collection<? extends TerminologyObject> qasets) {
		TerminologyObject[] array = qasets.toArray(new TerminologyObject[qasets.size()]);
		Set<Question> result = new HashSet<Question>();
		collectRelevantQuestions(array, result);
		return result;
	}

	private static void collectRelevantQuestions(TerminologyObject[] qasets, Collection<Question> result) {
		for (TerminologyObject qaset : qasets) {
			if (qaset instanceof Question) {
				boolean ignore = // false;
				(qaset instanceof QuestionChoice)
						&& ((QuestionChoice) qaset).getAllAlternatives().size() <= 1;
				// unfortunately, in rare cases, ignoring irrelevant questions
				// will result in slightly different information gain
				if (!ignore) {
					result.add((Question) qaset);
				}
			}
			TerminologyObject[] children = qaset.getChildren();
			collectRelevantQuestions(children, result);
		}
	}

	@SuppressWarnings("unchecked")
	private static final List<Collection<Condition>> emptyCombination = Collections.unmodifiableList(Arrays.<Collection<Condition>> asList(Collections.<Condition> emptyList()));

	private static final Map<Object, List<Collection<Condition>>> nullCombinationCache = new HashMap<Object, List<Collection<Condition>>>();

	private List<Collection<Condition>> getCombinations(LinkedList<Collection<Condition>> conditionsForQuestions) {
		boolean allNull = true;
		for (Collection<Condition> conditions : conditionsForQuestions) {
			if (conditions.size() != 1 || !conditions.contains(null)) {
				allNull = false;
				break;
			}
		}
		if (allNull) {
			Object key = new Integer(conditionsForQuestions.size());
			List<Collection<Condition>> result = nullCombinationCache.get(key);
			if (result == null) {
				result = calcCombinations(conditionsForQuestions);
				nullCombinationCache.put(key, result);
			}
			return result;
		}
		else {
			return calcCombinations(conditionsForQuestions);
		}
	}

	private static List<Collection<Condition>> calcCombinations(LinkedList<Collection<Condition>> conditionsForQuestions) {
		if (conditionsForQuestions.isEmpty()) {
			return emptyCombination;
		}
		Collection<Condition> firstConditions = conditionsForQuestions.poll();
		List<Collection<Condition>> restresult = calcCombinations(conditionsForQuestions);
		// in ≥ 90% of the cases both restresult and first contains 1 element
		// in ≥ 50% of the cases restresult contains one empty list
		List<Collection<Condition>> result = new LinkedList<Collection<Condition>>();
		// if (restresult == emptyCombination && first.size() == 1) {
		// result.add(first);
		// return result;
		// }
		for (Collection<Condition> restCombination : restresult) {
			for (Condition firstCondition : firstConditions) {
				List<Condition> item = new LinkedList<Condition>();
				item.add(firstCondition);
				item.addAll(restCombination);
				result.add(item);
			}
		}
		return result;
	}

	// private static long excludeTime = 0;
	// private static long potsTime = 0;
	// private static int countAllQuestions = 0;
	// private static int countRelevantQuestions = 0;
	//
	// public static void initStats() {
	// excludeTime = 0;
	// potsTime = 0;
	// countAllQuestions = 0;
	// countRelevantQuestions = 0;
	// }
	//
	// public static void showStats() {
	// System.out.println("init excludingQuestions: " + excludeTime + "ms");
	// System.out.println("init pots: " + potsTime + "ms");
	// System.out.println("average relavant questions: "
	// + Math.round(countRelevantQuestions / (float) countAllQuestions *
	// 1000000) / 10000f
	// + "%");
	//
	// }

	@Override
	public Collection<Question> getDiscriminatingQuestions(
			Collection<Solution> solutions, Session session) {
		Set<Question> coveredSymptoms = new HashSet<Question>();
		for (Solution solution : solutions) {
			KnowledgeSlice ks = solution.getKnowledgeStore().getKnowledge(XCLModel.KNOWLEDGE_KIND);
			if (ks == null) continue;
			XCLModel model = (XCLModel) ks;
			for (TerminologyObject nob : model.getCoveredSymptoms()) {
				if (nob instanceof Question) {
					coveredSymptoms.add((Question) nob);
				}
			}
			for (TerminologyObject nob : model.getNegativeCoveredSymptoms()) {
				if (nob instanceof Question) {
					coveredSymptoms.add((Question) nob);
				}
			}
		}
		return coveredSymptoms;
	}

	private Map<Question, Set<XCLRelation>> getExcludingQuestion(Collection<Solution> solutions, Collection<Question> questions) {
		Map<Question, Set<XCLRelation>> excludingQuestions = new HashMap<Question, Set<XCLRelation>>();
		for (Question q : questions) {
			XCLContributedModelSet knowledge = q.getKnowledgeStore().getKnowledge(
					XCLContributedModelSet.KNOWLEDGE_KIND);
			if (knowledge != null) {
				for (XCLModel model : knowledge.getModels()) {
					if (solutions.contains(model.getSolution())) {
						for (XCLRelation relation : model.getContradictingRelations()) {
							Collection<? extends TerminologyObject> relationObjects =
									relation.getConditionedFinding().getTerminalObjects();
							if (relationObjects.contains(q)) {
								Set<XCLRelation> conditions = excludingQuestions.get(q);
								if (conditions == null) {
									conditions = new HashSet<XCLRelation>();
									excludingQuestions.put((Question) q, conditions);
								}
								conditions.add(relation);
							}
						}
					}
				}
			}
		}

		return excludingQuestions;
	}

	private static class WeightSum {

		private float value = 0f;
	}

	@Override
	public double getInformationGain(Collection<? extends QASet> qasets,
			Collection<Solution> solutions, Session session) {
		Map<Collection<Condition>, WeightSum> map = new HashMap<Collection<Condition>, WeightSum>();
		Collection<Question> questions = getRelevantQuestions(qasets);

		// long time1 = System.currentTimeMillis();
		Map<Question, Set<XCLRelation>> excludingQuestions =
				getExcludingQuestion(solutions, questions);

		// long time2 = System.currentTimeMillis();
		// excludeTime += (time2 - time1);

		float totalweight = 0;
		for (Solution solution : solutions) {
			XCLModel model = solution.getKnowledgeStore().getKnowledge(XCLModel.KNOWLEDGE_KIND);
			if (model == null) continue;
			LinkedList<Set<Condition>> conditionsForQuestions = new LinkedList<Set<Condition>>();
			for (Question q : questions) {
				Set<Condition> set = null;
				Set<XCLRelation> coveringRelations = model.getCoveringRelations(q);
				if (coveringRelations != null) {
					for (XCLRelation r : coveringRelations) {
						set = lazyAddAll(set, getExtractedOrs(r));
					}
				}
				Set<XCLRelation> negativeRelations = model.getNegativeCoveringRelations(q);
				if (negativeRelations != null) {
					for (XCLRelation r : negativeRelations) {
						set = lazyAddAll(set, getNegatedExtractedOrs(r));
					}
				}
				else {
					negativeRelations = Collections.emptySet();
				}
				if (set == null) {
					set = NULL_SET;
				}
				// cover all conditions used in contrarelations of other
				// XCLModels
				Set<XCLRelation> excludingRelations = excludingQuestions.get(q);
				if (excludingRelations != null) {
					for (XCLRelation r : excludingRelations) {
						if (negativeRelations.contains(r)) {
							continue;
						}
						set = lazyAddAll(set, getExtractedOrs(r));
					}
				}
				conditionsForQuestions.add(set);
			}

			// multiply possible value sets to get pots
			// and add solution probabilities to these pots
			Number apriori = solution.getInfoStore().getValue(BasicProperties.APRIORI);
			float weight = (apriori == null) ? 1f : apriori.floatValue();
			totalweight += weight;
			fillSolutionIntoPots(weight, conditionsForQuestions, map);
		}

		// long time3 = System.currentTimeMillis();
		// potsTime += (time3 - time2);

		// calculate information gain
		// Russel & Norvig p. 805
		double sum = 0;
		for (WeightSum weight : map.values()) {
			double p = (double) weight.value / totalweight;
			sum += (-1) * p * Math.log10(p) / Math.log10(2);
		}
		return sum;
	}

	private void fillSolutionIntoPots(float weight, List<? extends Collection<Condition>> conditionsForQuestions, Map<Collection<Condition>, WeightSum> map) {
		List<Collection<Condition>> combinations =
				getCombinations(new LinkedList<Collection<Condition>>(conditionsForQuestions));
		for (Collection<Condition> pot : combinations) {
			WeightSum weightSum = map.get(pot);
			if (weightSum == null) {
				weightSum = new WeightSum();
				map.put(pot, weightSum);
			}
			weightSum.value += weight;
		}

	}

	private static final Set<Condition> NULL_SET = Collections.unmodifiableSet(new HashSet<Condition>(
			Arrays.asList((Condition) null)));

	/**
	 * Adds item to a source set that may be null and returns the resulting set.
	 * The specified source set is used destructively. If the source set is null
	 * a new one will be created. Otherwise the specified one may be altered and
	 * returned. For optimization reasons, we also consider the
	 * {@link #NULL_SET} to be specified, that is a unmodifiable set containing
	 * exactly one value 'null'.
	 * 
	 * @created 27.05.2012
	 * @param source the source set to be changed
	 * @param items the items to be added
	 * @return the resulting set, may be the source one or a newly created one
	 */
	private static final Set<Condition> lazyAddAll(Set<Condition> source, Collection<Condition> items) {
		if (items == null || items.isEmpty()) return source;
		if (source == null) return new HashSet<Condition>(items);
		if (source == NULL_SET) {
			source = new HashSet<Condition>(items);
			source.add(null);
			return source;
		}
		source.addAll(items);
		return source;
	}

	private final Map<Condition, Collection<Condition>> extractedOrCache = new HashMap<Condition, Collection<Condition>>();
	private final Map<Condition, Collection<Condition>> negatedExtractedOrCache = new HashMap<Condition, Collection<Condition>>();

	/**
	 * Returns the extracted ors for the condition of a specified xcl relation.
	 * It is only created on demand, otherwise a cached value is returned.
	 */
	private Collection<Condition> getExtractedOrs(XCLRelation r) {
		Condition condition = r.getConditionedFinding();
		Collection<Condition> result = extractedOrCache.get(condition);
		if (result == null) {
			result = new HashSet<Condition>();
			extractOrs(result, condition);
			result = Collections.unmodifiableCollection(result);
			extractedOrCache.put(condition, result);
		}
		return result;
	}

	/**
	 * Returns the extracted ors for the negated condition of a specified xcl
	 * relation. It is only created on demand, otherwise a cached value is
	 * returned.
	 */
	private Collection<Condition> getNegatedExtractedOrs(XCLRelation r) {
		Condition condition = r.getConditionedFinding();
		Collection<Condition> result = negatedExtractedOrCache.get(condition);
		if (result == null) {
			result = new HashSet<Condition>();
			extractOrs(result, new CondNot(condition));
			result = Collections.unmodifiableCollection(result);
			negatedExtractedOrCache.put(condition, result);
		}
		return result;
	}

	@Override
	public Collection<Solution> getUndiscriminatedSolutions(Session session) {
		List<Solution> solutions = session.getBlackboard().getSolutions(State.ESTABLISHED);
		if (solutions.size() > 0) {
			return new HashSet<Solution>(solutions);
		}
		solutions = session.getBlackboard().getSolutions(State.SUGGESTED);
		if (solutions.size() > 0) {
			return new HashSet<Solution>(solutions);
		}
		solutions = session.getBlackboard().getSolutions(State.UNCLEAR);
		return new HashSet<Solution>(solutions);
	}

}
