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

import java.util.HashSet;
import java.util.Set;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.abnormality.Abnormality;
import de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;

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
				Set<Value> forbiddenValues = getValues(condition);
				for (Choice c : ((QuestionChoice) question).getAllAlternatives()) {
					ChoiceValue value = new ChoiceValue(c);
					if (!forbiddenValues.contains(value)) {
						return value;
					}
				}
			}
			else {
				Set<Value> allowedValues = getValues(relation.getConditionedFinding());
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
	 * Returns the values, fitting to the specified condition
	 * 
	 * @created 22.02.2014
	 * @param condition specified {@link Condition}
	 * @return Set of fitting values
	 */
	public static Set<Value> getValues(Condition condition) {
		Set<Value> result = new HashSet<Value>();
		if (condition instanceof CondOr) {
			for (Condition c : ((CondOr) condition).getTerms()) {
				result.addAll(getValues(c));
			}
		}
		else if (condition instanceof CondEqual) {
			result.add(((CondEqual) condition).getValue());
		}
		else {
			throw new IllegalArgumentException();
		}
		return result;
	}

}
