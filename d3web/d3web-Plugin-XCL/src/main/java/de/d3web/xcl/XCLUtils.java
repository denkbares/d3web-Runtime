/*
 * Copyright (C) 2014 denkbares GmbH
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
package de.d3web.xcl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.abnormality.Abnormality;
import de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.utils.Log;

/**
 * Provides static methods for XCLModels
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 22.02.2014
 */
public class XCLUtils {

	/**
	 * Returns a value fitting for the question fitting to the coverings of the
	 * solution
	 * 
	 * @created 22.02.2014
	 * @param solution specified {@link Solution}
	 * @param question specified {@link QuestionOC}
	 * @return fitting value or null
	 */
	public static Value getFittingValue(Solution solution, QuestionOC question) {
		XCLModel model = solution.getKnowledgeStore().getKnowledge(XCLModel.KNOWLEDGE_KIND);
		if (model == null) {
			return null;
		}
		Set<XCLRelation> coveringRelations = model.getCoveringRelations(question);
		for (XCLRelation relation : coveringRelations) {
			// we assume that terminology objects cannot be used in
			// contradicting and normal relations
			if (relation.hasType(XCLRelationType.contradicted)) {
				Condition condition = relation.getConditionedFinding();
				if (condition.getTerminalObjects().size() != 1) {
					throw new IllegalArgumentException();
				}
				Set<Value> forbiddenValues = getValues(condition, question);
				for (Choice c : ((QuestionChoice) question).getAllAlternatives()) {
					ChoiceValue value = new ChoiceValue(c);
					if (!forbiddenValues.contains(value)) {
						return value;
					}
				}
			}
			else {
				Set<Value> allowedValues = getValues(relation.getConditionedFinding(), question);
				if (allowedValues.size() > 0) {
					return allowedValues.iterator().next();
				}
			}
		}
		// nothing covered, return normal facts
		DefaultAbnormality abnormality = question.getInfoStore().getValue(
				BasicProperties.DEFAULT_ABNORMALITIY);
		if (abnormality == null) {
			return null;
		}
		for (Choice c : ((QuestionChoice) question).getAllAlternatives()) {
			ChoiceValue choiceValue = new ChoiceValue(c);
			if (abnormality.getValue(choiceValue) == Abnormality.A0) {
				return choiceValue;
			}
		}
		return null;
	}

	/**
	 * Returns the values, fitting to the specified condition. This method can
	 * only handle CondOr, CondEqual and CondAnd
	 * 
	 * For or conditions, it is assumed, that subconditions covering other
	 * questions evaluate to false, so e.G. no value for (F1=A AND (F1=B OR
	 * F2=C)) will be found
	 * 
	 * @created 22.02.2014
	 * @param condition specified {@link Condition}
	 * @return Set of fitting values
	 */
	public static Set<Value> getValues(Condition condition, Question question) {
		Set<Value> result = new HashSet<Value>();
		// return an empty set, if no value is covered
		if (!condition.getTerminalObjects().contains(question)) {
			return result;
		}
		if (condition instanceof CondOr) {
			for (Condition c : ((CondOr) condition).getTerms()) {
				result.addAll(getValues(c, question));
			}
		}
		else if (condition instanceof CondEqual) {
			if (question == ((CondEqual) condition).getQuestion()) {
				result.add(((CondEqual) condition).getValue());
			}
		}
		else if (condition instanceof CondAnd) {
			List<Set<Value>> subSets = new ArrayList<Set<Value>>();
			for (Condition c : ((CondOr) condition).getTerms()) {
				// if the subcondition coveres the question, collect its values
				if (c.getTerminalObjects().contains(question)) {
					subSets.add(getValues(c, question));
				}
			}
			if (subSets.size() > 0) {
				result = subSets.get(0);
				for (int i = 1; i < subSets.size(); i++) {
					result.retainAll(subSets.get(i));
				}
			}
			if (result.size() == 0) {
				Log.warning("Cannot find value for " + question + " in " + condition);
			}
		}
		else {
			Log.warning("Condition of type " + condition.getClass() + " is not supported: "
					+ condition);
		}
		return result;
	}
}
