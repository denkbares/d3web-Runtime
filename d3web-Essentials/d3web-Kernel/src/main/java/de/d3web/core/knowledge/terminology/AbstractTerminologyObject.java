/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import de.d3web.core.knowledge.DefaultKnowledgeStore;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.KnowledgeStore;
import de.d3web.core.knowledge.TerminologyObject;

/**
 * AbstractTerminologyObject is parent of knowledge-base objects such as QASet,
 * Question, Diagnosis or Answer. <BR>
 * 
 * It provides a map, to store information relevant to the problem-solving
 * methods used in the knowledge base. Each problem-solver should use this map
 * to store the knowledge it needs for this single AbstractTerminologyObject. <BR>
 * A AbstractTerminologyObject contains a Map (name: properties) to store
 * additional properties dynamically. <BR>
 * Further there is a property "timeValued", which indicates if this Object is
 * able to change over time or not (default: not timeValued).
 * 
 * @author joba, chris, hoernlein
 * @see de.d3web.core.knowledge.terminology.NamedObject
 */
public abstract class AbstractTerminologyObject extends AbstractNamedObject implements TerminologyObject {

	/**
	 * The knowledge base this object belongs to.
	 */
	private final KnowledgeBase knowledgeBase;

	/**
	 * The parents of this object (including the linked parents).
	 */
	private final List<AbstractTerminologyObject> parents = new LinkedList<>();

	/**
	 * The children of this object (including the linked children).
	 */
	private final List<AbstractTerminologyObject> children = new ArrayList<>();

	/**
	 * The children again as a set to speed up the very frequent contains
	 * checks. We create it lazy.
	 */
	private Set<AbstractTerminologyObject> childrenSet = null;

	private final KnowledgeStore knowledgeStore = new DefaultKnowledgeStore();

	private static final int BOUNDARY = 10;

	/**
	 * Creates a new AbstractTerminologyObject instance with a given
	 * name and inserts the newly created object to the KnowledgeBase.
	 * 
	 * @param name the global name and identifier of the instance
	 * @throws NullPointerException if kb or name is null
	 */
	public AbstractTerminologyObject(KnowledgeBase kb, String name) {
		super(name);
		if (kb == null) {
			throw new NullPointerException("KnowledgeBase of an id object must not be null");
		}
		if (name == null) {
			throw new NullPointerException("Name of an id object must not be null");
		}
		this.knowledgeBase = kb;
		kb.getManager().putTerminologyObject(this);
	}

	/**
	 * Appends the specified AbstractTerminologyObject as a child to the
	 * list of children. This object is also linked as a parent to the specified
	 * child.
	 * 
	 * @param child a new child of this AbstractTerminologyObject
	 */
	protected void addChild(AbstractTerminologyObject child) {
		addChild(child, children.size());
	}

	/**
	 * Appends the specified AbstractTerminologyObject as a child to the
	 * list of children at the given position. This object is also linked as a parent
	 * to the specified child.
	 * 
	 * @created 15.02.2011
	 * @param child the child you want to add to this  AbstractTerminologyObject
	 * @param pos the position you want to add the child in this AbstractTerminologyObject's list of children
	 */
	protected void addChild(AbstractTerminologyObject child, int pos) {
		if (!containsChild(child)) {
			addParentChildLink(this, child, pos);
		}
	}

	private static void addParentChildLink(AbstractTerminologyObject parent, AbstractTerminologyObject child, int pos) {
		if (parent.getKnowledgeBase() != child.getKnowledgeBase()) {
			throw new IllegalArgumentException("Knowledge base beetween parent and child differs");
		}
		parent.children.add(pos, child);
		if (parent.childrenSet == null) {
			if (parent.children.size() > BOUNDARY) {
				parent.childrenSet = new HashSet<>(parent.children);
			}
		}
		else {
			parent.childrenSet.add(child);
		}
		child.parents.add(parent);
	}

	private boolean containsChild(AbstractTerminologyObject child) {
		if (childrenSet == null) return children.contains(child);
		else return childrenSet.contains(child);
	}

	/**
	 * Gives the knowledge base to which this instance belongs to.
	 * 
	 * @return the knowledge base this object belongs to
	 */
	@Override
	public KnowledgeBase getKnowledgeBase() {
		return knowledgeBase;
	}

	private static boolean removeParentChildLink(AbstractTerminologyObject parent,
			AbstractTerminologyObject child) {
		child.parents.remove(parent);
		if (parent.childrenSet != null) {
			parent.childrenSet.remove(child);
			if (parent.childrenSet.isEmpty()) {
				parent.childrenSet = null;
			}
		}
		return parent.children.remove(child);
	}

	/**
	 * Removes the specified instance from the children of this instance.
	 * 
	 * @param child the specified child to be removed from the list
	 */
	protected boolean removeChild(AbstractTerminologyObject child) {
		return removeParentChildLink(this, child);
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Checks, if other object is of same class and has the same name.
	 * 
	 * @return true, if equal
	 * @param other Object to compare for equality
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null) return false;
		return (getClass() == other.getClass())
				&& name.equals(((AbstractTerminologyObject) other).name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@NotNull
	@Override
	public TerminologyObject[] getParents() {
		return parents.toArray(new TerminologyObject[0]);
	}

	@NotNull
	@Override
	public TerminologyObject[] getChildren() {
		return children.toArray(new TerminologyObject[0]);
	}

	@Override
	public void destroy() {
		for (AbstractTerminologyObject object : new ArrayList<>(parents)) {
			removeParentChildLink(object, this);
		}
		knowledgeBase.getManager().remove(this);
	}

	@Override
	public KnowledgeStore getKnowledgeStore() {
		return knowledgeStore;
	}
}