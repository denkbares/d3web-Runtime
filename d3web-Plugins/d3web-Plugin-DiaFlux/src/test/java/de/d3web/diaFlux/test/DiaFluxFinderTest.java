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
package de.d3web.diaFlux.test;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.denkbares.plugin.test.InitPluginManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.diaFlux.flow.DiaFluxFinder;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link DiaFluxFinder}
 *
 * @author Reinhard Hatko
 * @created 16.05.2013
 */
public class DiaFluxFinderTest {

	private static final String FLOW_NAME = "Flow";
	private KnowledgeBase kb;
	private Flow flow;

	@Before
	public void setUp() throws IOException {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		flow = FlowFactory.createFlow(kb, FLOW_NAME, Collections.emptyList(), Collections.emptyList());
	}

	@Test
	public void testFindSuccessfullByName() {
		find(FLOW_NAME, Collections.singleton(flow));
	}

	@Test
	public void testFindFailDifferentName() {
		find("bla", Collections.emptySet());
	}

	private void find(String name, Set<NamedObject> result) {
		assertThat(new DiaFluxFinder().find(kb, name), is(result));
	}
}
