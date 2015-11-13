/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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

package de.d3web.testcase.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.session.ValueUtils;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;

/**
 * This interface extends the normal {@link TestCase} interface with getters for {@link FindingTemplate}s and
 * {@link CheckTemplate}s. This way, we can also provide a default implementation of the {@link #check(KnowledgeBase)}
 * method.
 * The templates are used to generate the actual {@link Finding}s {@link Check}s.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 11.11.15
 */
public interface TemplateTestCase extends TestCase {

	Collection<FindingTemplate> getFindingTemplates(Date date);

	Collection<CheckTemplate> getCheckTemplates(Date date);

	@Override
	default Collection<String> check(KnowledgeBase knowledgeBase) {
		List<String> messages = new ArrayList<>();
		for (Date date : chronology()) {

			for (FindingTemplate findingTemplate : getFindingTemplates(date)) {
				try {
					findingTemplate.toFinding(knowledgeBase);
				}
				catch (TransformationException e) {
					messages.add(e.getMessage());
				}
			}
			for (CheckTemplate checkTemplate : getCheckTemplates(date)) {
				try {
					checkTemplate.toCheck(knowledgeBase);
				}
				catch (TransformationException e) {
					messages.add(e.getMessage());
				}
			}
		}
		return messages;
	}

	@Override
	default Collection<Finding> getFindings(Date date, KnowledgeBase knowledgeBase) {
		Collection<FindingTemplate> findingTemplates = getFindingTemplates(date);
		return toFindings(findingTemplates, knowledgeBase);
	}

	static Collection<Finding> toFindings(Collection<FindingTemplate> findingTemplates, KnowledgeBase knowledgeBase) {
		LinkedHashMap<TerminologyObject, Finding> findingMap = new LinkedHashMap<>();
		for (FindingTemplate findingTemplate : findingTemplates) {
			try {
				Finding finding = findingTemplate.toFinding(knowledgeBase);
				TerminologyObject object = finding.getTerminologyObject();
				Finding existingFinding = findingMap.get(object);
				if (object instanceof QuestionMC
						&& existingFinding != null
						&& (existingFinding.getValue() instanceof MultipleChoiceValue
						|| existingFinding.getValue() instanceof ChoiceValue)) {
					MultipleChoiceValue mergedValues = ValueUtils.mergeChoiceValuesOR((QuestionMC) object,
							existingFinding.getValue(), finding.getValue());
					DefaultFinding mergedFinding = new DefaultFinding(object, mergedValues);
					findingMap.replace(object, mergedFinding);
				}
				else {
					findingMap.put(object, finding);
				}
			}
			catch (TransformationException ignore) {
				// use {@link #check(KnowledgeBase)} to catch this...
			}
		}
		return findingMap.values();
	}

	@Override
	default Collection<Check> getChecks(Date date, KnowledgeBase knowledgeBase) {
		Collection<CheckTemplate> checkTemplates = getCheckTemplates(date);
		return toChecks(checkTemplates, knowledgeBase);
	}

	static Collection<Check> toChecks(Collection<CheckTemplate> checkTemplates, KnowledgeBase knowledgeBase) {
		List<Check> checks = new ArrayList<>();
		for (CheckTemplate checkTemplate : checkTemplates) {
			try {
				checks.add(checkTemplate.toCheck(knowledgeBase));
			}
			catch (TransformationException ignore) {
				// use {@link #check(KnowledgeBase)} to catch this...
			}
		}
		return checks;
	}
}
