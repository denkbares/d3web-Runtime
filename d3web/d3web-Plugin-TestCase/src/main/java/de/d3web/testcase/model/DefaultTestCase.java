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
import java.util.List;
import java.util.TreeSet;

import de.d3web.collections.DefaultMultiMap;
import de.d3web.collections.MultiMap;
import de.d3web.collections.MultiMaps;
import de.d3web.core.knowledge.KnowledgeBase;

/**
 * Default implementation of the TestCase interface.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 27.10.15
 */
public class DefaultTestCase implements TestCase {

	private MultiMap<Date, FindingTemplate> findingTemplates = new DefaultMultiMap<Date, FindingTemplate>(MultiMaps.treeFactory(), MultiMaps.linkedFactory());

	private MultiMap<Date, CheckTemplate> checkTemplates = new DefaultMultiMap<Date, CheckTemplate>(MultiMaps.treeFactory(), MultiMaps.linkedFactory());

	private Date startDate = new Date(0);

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void addFinding(Date date, FindingTemplate... findingTemplate) {
		for (FindingTemplate template : findingTemplate) {
			findingTemplates.put(date, template);
		}
	}

	public void addCheck(Date date, CheckTemplate... checkTemplate) {
		for (CheckTemplate template : checkTemplate) {
			checkTemplates.put(date, template);
		}
	}

	public Collection<FindingTemplate> getFindingTemplates(Date date) {
		return findingTemplates.getValues(date);
	}

	public Collection<CheckTemplate> getCheckTemplates(Date date) {
		return checkTemplates.getValues(date);
	}

	@Override
	public Collection<Date> chronology() {
		TreeSet<Date> chronology = new TreeSet<>();
		chronology.addAll(findingTemplates.keySet());
		chronology.addAll(checkTemplates.keySet());
		return chronology;
	}

	@Override
	public Collection<Finding> getFindings(Date date, KnowledgeBase knowledgeBase) {
		List<Finding> findings = new ArrayList<>();
		for (FindingTemplate findingTemplate : getFindingTemplates(date)) {
			try {
				findings.add(findingTemplate.toFinding(knowledgeBase));
			}
			catch (TransformationException eígnore) {
				// use {@link #check(KnowledgeBase)} to catch this...
			}
		}
		return findings;
	}

	@Override
	public Collection<Check> getChecks(Date date, KnowledgeBase knowledgeBase) {
		List<Check> checks = new ArrayList<>();
		for (CheckTemplate checkTemplate : getCheckTemplates(date)) {
			try {
				checks.add(checkTemplate.toCheck(knowledgeBase));
			}
			catch (TransformationException ignore) {
				// use {@link #check(KnowledgeBase)} to catch this...
			}
		}
		return checks;
	}

	@Override
	public Collection<String> check(KnowledgeBase knowledgeBase) {
		List<String> messages = new ArrayList<>();
		for (FindingTemplate findingTemplate : findingTemplates.valueSet()) {
			try {
				findingTemplate.toFinding(knowledgeBase);
			}
			catch (TransformationException e) {
				messages.add(e.getMessage());
			}
		}
		for (CheckTemplate checkTemplate : checkTemplates.valueSet()) {
			try {
				checkTemplate.toCheck(knowledgeBase);
			}
			catch (TransformationException e) {
				messages.add(e.getMessage());
			}
		}
		return messages;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}



}
