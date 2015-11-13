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

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import de.d3web.core.knowledge.KnowledgeBase;

/**
 * This TestCase extends the {@link DefaultTestCase} and basically does the same, except that it caches the {@link
 * Finding}s and {@link Check}s generated from its templates.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 12.11.15
 */
public class CachingDefaultTestCase extends DefaultTestCase {

	private final Map<KnowledgeBase, Map<Date, Collection<Finding>>> findingCache = new WeakHashMap<>();
	private final Map<KnowledgeBase, Map<Date, Collection<Check>>> checkCache = new WeakHashMap<>();

	@Override
	public void addFinding(Date date, FindingTemplate... findingTemplate) {
		findingCache.clear();
		super.addFinding(date, findingTemplate);
	}

	@Override
	public void addCheck(Date date, CheckTemplate... checkTemplate) {
		checkCache.clear();
		super.addCheck(date, checkTemplate);
	}

	@Override
	public Collection<Finding> getFindings(Date date, KnowledgeBase knowledgeBase) {
		return Collections.unmodifiableCollection(
				findingCache.computeIfAbsent(knowledgeBase, key -> new HashMap<>())
						.computeIfAbsent(date, datek -> super.getFindings(datek, knowledgeBase)));
	}

	@Override
	public Collection<Check> getChecks(Date date, KnowledgeBase knowledgeBase) {
		return Collections.unmodifiableCollection(
				checkCache.computeIfAbsent(knowledgeBase, key -> new HashMap<>())
						.computeIfAbsent(date, datek -> super.getChecks(datek, knowledgeBase)));
	}
}
