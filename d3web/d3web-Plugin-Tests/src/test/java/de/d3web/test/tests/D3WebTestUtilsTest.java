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
package de.d3web.test.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.regex.Pattern;

import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.utilities.Pair;
import de.d3web.test.D3webTestUtils;


/**
 * 
 * @author Reinhard Hatko
 * @created 25.03.2013
 */
public class D3WebTestUtilsTest {

	// Input
	// -Mess
	// --User
	// ---age
	// --Dev

	@Test
	public void testIsIgnored() {
		KnowledgeBase kb = new KnowledgeBase();
		QContainer root = new QContainer(kb, "q000");
		kb.setRootQASet(root);

		QContainer in = new QContainer(root, "Input");
		QContainer mess = new QContainer(in, "Mess");
		QContainer user = new QContainer(mess, "User");
		new QContainer(mess, "Dev");
		Question q = new QuestionNum(user, "age");

		// TO directly
		executeIgnoreTest(q, "age", false, true);
		executeIgnoreTest(q, "AGE", false, true);

		// TO directly + children (no difference)
		executeIgnoreTest(q, "age", false, true);
		executeIgnoreTest(q, "AGE", false, true);

		// parent
		executeIgnoreTest(q, "User", false, false);

		// parent + children
		executeIgnoreTest(q, "User", true, true);

		// grandparent
		executeIgnoreTest(q, "Mess", false, false);

		// GP + children
		executeIgnoreTest(q, "Mess", true, true);
		// RootQA
		executeIgnoreTest(q, "INPUT", false, false);

		// RootQA, hierarchy
		executeIgnoreTest(q, "INPUT", true, true);

		// "Uncle"
		executeIgnoreTest(q, "Dev", false, false);

		// "Uncle" + children
		executeIgnoreTest(q, "Dev", true, false);

		// empty ignore
		executeIgnoreTest(q, "", false, false);

		// something different
		executeIgnoreTest(q, "agent", false, false);
		executeIgnoreTest(q, "agent", true, false);
		
	}

	private void executeIgnoreTest(Question q, String ignoreName, boolean inHierarchy, boolean result) {
		String[] ignores = new String[] {
				ignoreName, String.valueOf(inHierarchy) };
		Collection<Pair<Pattern, Boolean>> ignorePatterns = D3webTestUtils.compileHierarchicalIgnores(new String[][] { ignores });

		assertThat(D3webTestUtils.isIgnored(q, ignorePatterns), is(result));
	}

}
