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
package de.d3web.core.knowledge.terminology;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.KnowledgeStore;
import de.d3web.core.knowledge.TerminologyObject;

/**
 * Unit test for {@link AbstractTerminologyObject}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 23.08.2010
 */
public class AbstractTerminologyObjectTest {

	private class NamedObjectMock extends AbstractTerminologyObject {

		/**
		 * @param id
		 */
		public NamedObjectMock(KnowledgeBase kb, String name) {
			super(kb, name);
		}

		@Override
		public KnowledgeStore getKnowledgeStore() {
			// not tested
			return null;
		}

	}

	NamedObjectMock parent;

	NamedObjectMock childOne;
	NamedObjectMock childTwo;
	NamedObjectMock childThree;

	private KnowledgeBase kb;

	@Before
	public void setUp() {

		kb = new KnowledgeBase();
		// initialize parent namedObject
		parent = new NamedObjectMock(kb, "parent");

		// initialize the children
		childOne = new NamedObjectMock(kb, "childOne");
		parent.addChild(childOne);
		childTwo = new NamedObjectMock(kb, "childTwo");
		parent.addChild(childTwo);
		childThree = new NamedObjectMock(kb, "childThree");
		parent.addChild(childThree);
	}

	/**
	 * Summary: Test if all children are present and in correct order
	 * 
	 * @created 23.08.2010
	 */
	@Test
	public void testCorrectInitialization() {
		// children count = number of children + number of linked children
		assertThat(parent.getChildren().length, is(3));

		// verify all children and their correct ordering
		TerminologyObject[] children = parent.getChildren();
		assertThat(children[0], is(equalTo((TerminologyObject) childOne)));
		assertThat(children[1], is(equalTo((TerminologyObject) childTwo)));
		assertThat(children[2], is(equalTo((TerminologyObject) childThree)));

		// verify that every child has only one (to correct!) parent
		for (TerminologyObject terminologyObjectChild : children) {
			TerminologyObject[] parents = terminologyObjectChild.getParents();
			assertThat(parents.length, is(1));
			assertThat(parents[0], is(equalTo((TerminologyObject) parent)));
		}
	}

	/**
	 * Summary: Tests the method
	 * {@link AbstractTerminologyObject#addChild(AbstractTerminologyObject, int)}
	 * which allows to insert children at specific positions in the children
	 * list.
	 * 
	 * @see AbstractTerminologyObject#addChild(AbstractTerminologyObject, int)
	 * 
	 * @created 23.08.2010
	 */
	@Test
	public void testAddChildAtPosition() {
		TerminologyObject[] children = parent.getChildren();
		assertThat(children[0], is(equalTo((TerminologyObject) childOne)));
		assertThat(children[1], is(equalTo((TerminologyObject) childTwo)));
		assertThat(children[2], is(equalTo((TerminologyObject) childThree)));

		// now move the childOne (currently at index 0) to index 1, between the
		// other two children
		parent.removeChild(childOne);
		parent.addChild(childOne, 1);
		// verify all the new positions:
		children = parent.getChildren();
		assertThat(children[0], is(equalTo((TerminologyObject) childTwo)));
		assertThat(children[1], is(equalTo((TerminologyObject) childOne)));
		assertThat(children[2], is(equalTo((TerminologyObject) childThree)));
	}

	/**
	 * Tests the methods to remove children
	 * 
	 * @see AbstractTerminologyObject#removeChild(AbstractTerminologyObject)
	 * @see AbstractTerminologyObject#removeLinkedChild(AbstractTerminologyObject)
	 * 
	 * @created 23.08.2010
	 */
	@Test
	public void testChildRemoving() {
		// try to remove the "childThree"
		assertThat(parent.removeChild(childThree), is(true));
		// the number of children should have decreased
		assertThat(parent.getChildren().length, is(2));
		// the child can't be removed twice, therefore this method should fail
		assertThat(parent.removeChild(childThree), is(false));
	}

}
