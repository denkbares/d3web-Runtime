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
package de.d3web.core.knowledge.terminology.tests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.AbstractTerminologyObject;

/**
 * Unit test for {@link AbstractTerminologyObject}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 23.08.2010
 */
public class NamedObjectTest {

	private class NamedObjectMock extends AbstractTerminologyObject {

		/**
		 * @param id
		 */
		public NamedObjectMock(String id) {
			super(id);
		}

	}

	NamedObjectMock parent;

	NamedObjectMock childOne;
	NamedObjectMock childTwo;
	NamedObjectMock childThree;

	NamedObjectMock linkedChildOne;
	NamedObjectMock linkedChildTwo;
	NamedObjectMock linkedChildThree;

	@Before
	public void setUp() {
		// initialize parent namedObject
		parent = new NamedObjectMock("parent");

		// initialize the children
		childOne = new NamedObjectMock("childOne");
		parent.addChild(childOne);
		childTwo = new NamedObjectMock("childTwo");
		parent.addChild(childTwo);
		childThree = new NamedObjectMock("childThree");
		parent.addChild(childThree);

		// initialize the linked children
		linkedChildOne = new NamedObjectMock("linkedChildOne");
		parent.addLinkedChild(linkedChildOne);
		linkedChildTwo = new NamedObjectMock("linkedChildTwo");
		parent.addLinkedChild(linkedChildTwo);
		linkedChildThree = new NamedObjectMock("linkedChildThree");
		parent.addLinkedChild(linkedChildThree);
	}

	/**
	 * Summary: Test if all children and linked children are present and in
	 * correct order
	 * 
	 * @created 23.08.2010
	 */
	@Test
	public void testCorrectInitialization() {
		// children count = number of children + number of linked children
		assertThat(parent.getNumberOfChildren(), is(6));

		// verify all children and their correct ordering
		TerminologyObject[] children = parent.getChildren();
		assertThat(children[0], is(equalTo((TerminologyObject) childOne)));
		assertThat(children[1], is(equalTo((TerminologyObject) childTwo)));
		assertThat(children[2], is(equalTo((TerminologyObject) childThree)));
		assertThat(children[3], is(equalTo((TerminologyObject) linkedChildOne)));
		assertThat(children[4], is(equalTo((TerminologyObject) linkedChildTwo)));
		assertThat(children[5], is(equalTo((TerminologyObject) linkedChildThree)));

		// verify that every child has only one (to correct!) parent
		for (TerminologyObject terminologyObjectChild : children) {
			TerminologyObject[] parents = terminologyObjectChild.getParents();
			assertThat(parents.length, is(1));
			assertThat(parents[0], is(equalTo((TerminologyObject) parent)));
		}

		// verify all linked children
		Collection<AbstractTerminologyObject> linkedChildren = parent.getLinkedChildren();
		assertThat(linkedChildren.size(), is(3));

		assertThat(linkedChildren.contains(linkedChildOne), is(true));
		assertThat(linkedChildren.contains(linkedChildTwo), is(true));
		assertThat(linkedChildren.contains(linkedChildThree), is(true));

		// verify that every linked child has only one (to correct!) parent
		for (TerminologyObject namedObjectLinkedChild : linkedChildren) {
			TerminologyObject[] parents = namedObjectLinkedChild.getParents();
			assertThat(parents.length, is(1));
			assertThat(parents[0], is(equalTo((TerminologyObject) parent)));
		}
	}

	/**
	 * Summary: Tests the AbstractTerminologyObject#setChildren(java.util.List) method which
	 * deletes all current children of the AbstractTerminologyObject Instance and inserts all
	 * the NamedObjects from the given list as new children
	 * 
	 * @see AbstractTerminologyObject#setChildren(java.util.List)
	 * 
	 * @created 23.08.2010
	 */
	@Test
	public void testSetChildrenByList() {
		// create and instantiate the new children
		NamedObjectMock newChildOne = new NamedObjectMock("newChildOne");
		NamedObjectMock newChildTwo = new NamedObjectMock("newChildTwo");
		// create a list of the new two children
		List<AbstractTerminologyObject> newChildren = new LinkedList<AbstractTerminologyObject>();
		newChildren.add(newChildOne);
		newChildren.add(newChildTwo);
		// now set the list as new children of the parent
		parent.setChildren(newChildren);
		// verify that the "new" children of the parent consist only of
		// "newChildOne" and "newChildTwo"
		assertThat(parent.getNumberOfChildren(), is(2));
		TerminologyObject[] children = parent.getChildren();
		assertThat(children[0], is(equalTo((TerminologyObject) newChildOne)));
		assertThat(children[1], is(equalTo((TerminologyObject) newChildTwo)));
	}

	/**
	 * Summary: Tests the AbstractTerminologyObject#setParents(java.util.List) method which
	 * removes all the parents of the current AbstractTerminologyObject instance and inserts
	 * all the NamedObjects from the given list as new parents.
	 * <p>
	 * Decouple the child "linkedChildOne" from its current parent "parent" and
	 * give it two new parents: "newParentOne" and "newParentTwo"
	 * 
	 * @see AbstractTerminologyObject#setParents(java.util.List)
	 * 
	 * @created 23.08.2010
	 */
	@Test
	public void testSetParentsByList() {
		// create and instantiate the new parents
		NamedObjectMock newParentOne = new NamedObjectMock("newParentOne");
		NamedObjectMock newParentTwo = new NamedObjectMock("newParentTwo");
		// create a list of the two new parents
		List<AbstractTerminologyObject> newParents = new LinkedList<AbstractTerminologyObject>();
		newParents.add(newParentOne);
		newParents.add(newParentTwo);
		// now set the list as new parents of the child "linkedChildOne"
		linkedChildOne.setParents(newParents);

		// now verify the success of the operation
		assertThat(parent.getNumberOfChildren(), is(5)); // old parent
		assertThat(newParentOne.getNumberOfChildren(), is(1));
		assertThat(newParentTwo.getNumberOfChildren(), is(1));

		TerminologyObject[] parents = linkedChildOne.getParents();
		assertThat(parents.length, is(2));
		assertThat(parents[0], is(equalTo((TerminologyObject) newParentOne)));
		assertThat(parents[1], is(equalTo((TerminologyObject) newParentTwo)));
	}

	/**
	 * Summary: Tests the AbstractTerminologyObject#moveChildToPosition(AbstractTerminologyObject, int)
	 * method which moves a specified child to a new position in the
	 * childrens-list
	 * 
	 * @see AbstractTerminologyObject#moveChildToPosition(AbstractTerminologyObject, int)
	 * 
	 * @created 23.08.2010
	 */
	@Test
	public void testMoveChildToPosition() {
		// move linkedChildTwo (currently at index 4) to position at index 1
		parent.moveChildToPosition(linkedChildTwo, 1);
		// verify all the new positions:
		TerminologyObject[] children = parent.getChildren();
		assertThat(children[0], is(equalTo((TerminologyObject) childOne)));
		assertThat(children[1], is(equalTo((TerminologyObject) linkedChildTwo)));
		assertThat(children[2], is(equalTo((TerminologyObject) childTwo)));
		assertThat(children[3], is(equalTo((TerminologyObject) childThree)));
		assertThat(children[4], is(equalTo((TerminologyObject) linkedChildOne)));
		assertThat(children[5], is(equalTo((TerminologyObject) linkedChildThree)));

		// now move the childOne (currently at index 0) at the end of the list
		parent.moveChildToPosition(childOne, Integer.MAX_VALUE);
		// verify all the new positions:
		children = parent.getChildren();
		assertThat(children[0], is(equalTo((TerminologyObject) linkedChildTwo)));
		assertThat(children[1], is(equalTo((TerminologyObject) childTwo)));
		assertThat(children[2], is(equalTo((TerminologyObject) childThree)));
		assertThat(children[3], is(equalTo((TerminologyObject) linkedChildOne)));
		assertThat(children[4], is(equalTo((TerminologyObject) linkedChildThree)));
		assertThat(children[5], is(equalTo((TerminologyObject) childOne)));
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
		assertThat(parent.getNumberOfChildren(), is(5));
		// the child can't be removed twice, therefore this method should fail
		assertThat(parent.removeChild(childThree), is(false));

		// now try to remove the linked child "linkedChildOne"
		parent.removeLinkedChild(linkedChildOne);
		// this operation should remove the "linkedChild" completely from the
		// parents children and linkedChildren
		assertThat(parent.getLinkedChildren().size(), is(2));// dropped from 3
		assertThat(parent.getNumberOfChildren(), is(4));// dropped from 5
	}

}
