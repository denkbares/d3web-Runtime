/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.d3web.core.manage.tests;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.denkbares.plugin.test.InitPluginManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.manage.KnowledgeBaseFinder;
import de.d3web.core.manage.KnowledgeBaseUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link KnowledgeBaseFinder}
 *
 * @author Reinhard Hatko
 * @created 16.05.2013
 */
public class KnowledgeBaseFinderTest {

	private final KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();

	@Before
	public void setUp() throws IOException {
		InitPluginManager.init();
	}

	@Test
	public void testFindSuccessfullByName() {
		String kbName = "wissensbasis";
		kb.getInfoStore().addValue(MMInfo.PROMPT, kbName);
		find(kbName, Collections.singleton(kb));
	}

	@Test
	public void testFindSuccessfullByID() {
		find(KnowledgeBaseFinder.KNOWLEDGEBASE_ID, Collections.singleton(kb));
	}

	@Test
	public void testFindFailDifferentName() {
		kb.getInfoStore().addValue(MMInfo.PROMPT, "blubb");
		find("bla", Collections.emptySet());
	}

	@Test
	public void testFindFailNoName() {
		find("bla", Collections.emptySet());
	}

	private void find(String kbName, Set<NamedObject> result) {
		assertThat(new KnowledgeBaseFinder().find(kb, kbName), is(result));
	}
}
