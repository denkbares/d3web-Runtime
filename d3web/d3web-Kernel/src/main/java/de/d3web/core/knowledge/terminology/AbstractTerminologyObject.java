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
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.DefaultInfoStore;
import de.d3web.core.knowledge.DefaultKnowledgeStore;
import de.d3web.core.knowledge.InfoStore;
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
 * @see de.d3web.kernel.misc.PropertiesAdapter
 */
public abstract class AbstractTerminologyObject implements TerminologyObject {

	/**
	 * Representing a short name of the object.
	 */
	private final String name;

	/**
	 * The knowledge base this object belongs to.
	 */
	private final KnowledgeBase knowledgeBase;

	/**
	 * The parents of this object (including the linked parents).
	 */
	private final List<AbstractTerminologyObject> parents = new LinkedList<AbstractTerminologyObject>();

	/**
	 * The children of this object (including the linked children).
	 */
	private final List<AbstractTerminologyObject> children = new LinkedList<AbstractTerminologyObject>();

	private final InfoStore infoStore = new DefaultInfoStore();

	private final KnowledgeStore knowledgeStore = new DefaultKnowledgeStore();

	/**
	 * Creates a new {@link AbstractTerminologyObject} instance with a given
	 * name and inserts the newly created object to the KnowledgeBase.
	 * 
	 * @param id The global identifier of the instance
	 * @throws NullPointerException if kb or name is null
	 */
	public AbstractTerminologyObject(KnowledgeBase kb, String name) {
		if (kb == null) {
			throw new NullPointerException("KnowledgeBase of an id object must not be null");
		}
		if (name == null) {
			throw new NullPointerException("Name of an id object must not be null");
		}
		this.knowledgeBase = kb;
		this.name = name;
		kb.getManager().putTerminologyObject(this);
	}

	@Override
	@Deprecated
	public String getId() {
		return getName();
	}

	/**
	 * Appends the specified {@link AbstractTerminologyObject} as a child to the
	 * list of children. This object is also linked as a parent to the specified
	 * child.
	 * 
	 * @param child a new child of this {@link AbstractTerminologyObject}
	 * @see #addParent(AbstractTerminologyObject parent)
	 */
	protected void addChild(AbstractTerminologyObject child) {
		if (!children.contains(child)) {
			addParentChildLink(this, child);
		}
	}

	private static void addParentChildLink(AbstractTerminologyObject parent, AbstractTerminologyObject child) {
		if (parent.getKnowledgeBase() != child.getKnowledgeBase()) {
			throw new IllegalArgumentException("KnowledgeBase beetween parent and child differs");
		}
		parent.children.add(child);
		child.parents.add(parent);
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

	/**
	 * The text of a {@link AbstractTerminologyObject} is the name or a short
	 * description of the object. Please keep it brief and use other fields for
	 * longer content (e.g., prompt for {@link Question}, and comments for
	 * {@link Solution}).
	 * 
	 * @return the name of this object
	 */
	@Override
	public String getName() {
		return name;
	}

	private static void removeParentChildLink(AbstractTerminologyObject parent,
			AbstractTerminologyObject child) {
		parent.children.remove(child);
		child.parents.remove(parent);
	}

	/**
	 * Removes the specified instance from the children of this instance.
	 * 
	 * @param child the specified child to be removed from the list
	 */
	protected boolean removeChild(AbstractTerminologyObject child) {
		if (children.contains(child)) {
			removeParentChildLink(this, child);
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Checks, if other object is an NamedObject and if it contains the same ID.
	 * 
	 * @return true, if equal
	 * @param other Object to compare for equality
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		else if ((other == null) || (getClass() != other.getClass())) {
			return false;
		}
		else {
			NamedObject otherIDO = (NamedObject) other;
			if ((getName() != null) && (otherIDO.getName() != null)) {
				return getName().equals(otherIDO.getName());
			}
			else {
				return super.equals(other);
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public TerminologyObject[] getParents() {
		return parents.toArray(new TerminologyObject[parents.size()]);
	}

	@Override
	public TerminologyObject[] getChildren() {
		return children.toArray(new TerminologyObject[children.size()]);
	}

	/**
	 * Inserts the child at the specified position. If it is already contained
	 * in another position, this position will be overwritten
	 * 
	 * @created 15.02.2011
	 * @param child
	 * @param pos
	 */
	protected void addChild(AbstractTerminologyObject child, int pos) {
		children.remove(child);
		children.add(pos > children.size() ? children.size() : pos, child);
	}

	@Override
	public InfoStore getInfoStore() {
		return infoStore;
	}

	@Override
	public void destroy() {
		for (AbstractTerminologyObject object : new ArrayList<AbstractTerminologyObject>(parents)) {
			removeParentChildLink(object, this);
		}
		knowledgeBase.getManager().remove(this);
	}

	@Override
	public KnowledgeStore getKnowledgeStore() {
		return knowledgeStore;
	}
}