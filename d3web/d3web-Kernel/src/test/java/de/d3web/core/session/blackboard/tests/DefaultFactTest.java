/*
 * Copyright (C) 2010 denkbares GmbH, Würzburg, Germany
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

package de.d3web.core.session.blackboard.tests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Unit test for {@link DefaultFact}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 26.08.2010
 */
public class DefaultFactTest {

	private static KnowledgeBase kb;

	QuestionOC materialQuestion;
	ChoiceValue wood;
	Fact material_is_wood;

	/**
	 * 
	 * @created 26.08.2010
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();

		materialQuestion = new QuestionOC(kb.getRootQASet(),
				"materialQuestion", "Steel", "Wood", "Plastic");
		wood = new ChoiceValue(KnowledgeBaseUtils.findChoice(materialQuestion, "Wood"));

		material_is_wood = FactFactory.createUserEnteredFact(materialQuestion, wood);
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.blackboard.DefaultFact#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		assertThat(material_is_wood.hashCode(), is(not(0)));
	}

	/**
	 * Tests that DefaultFact throws a NullPointerException if some or all of
	 * the paramters are null
	 * 
	 * @created 26.08.2010
	 */
	@Test(expected = NullPointerException.class)
	public void testDefaultFactThrowsNullPointerException() {
		new DefaultFact(null, null, null, null);
	}

	/**
	 * Test all getters of {@link DefaultFact}
	 */
	@Test
	public void testGetters() {
		assertThat(material_is_wood.getPSMethod() instanceof PSMethodUserSelected, is(true));
		assertThat(material_is_wood.getSource() instanceof PSMethodUserSelected, is(true));
		assertThat(material_is_wood.getValue(), is(equalTo((Value) wood)));
		assertThat(material_is_wood.getTerminologyObject(),
				is(equalTo((TerminologyObject) materialQuestion)));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.blackboard.DefaultFact#toString()}.
	 */
	@Test
	public void testToString() {
		assertThat(material_is_wood.toString(),
				is("materialQuestion = Wood [User selections / User selections]"));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.blackboard.DefaultFact#equals(java.lang.Object)}
	 * .
	 */
	@Test
	public void testEqualsObject() {
		assertThat(material_is_wood.equals(material_is_wood), is(true));
		assertThat(material_is_wood.equals(null), is(false));
		assertThat(material_is_wood.equals(new Object()), is(false));
	}

}
