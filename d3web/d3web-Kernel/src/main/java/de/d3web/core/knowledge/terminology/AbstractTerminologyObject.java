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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.DefaultInfoStore;
import de.d3web.core.knowledge.InfoStore;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.KnowledgeContainer;
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
public abstract class AbstractTerminologyObject implements TerminologyObject,
		KnowledgeContainer {

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
	private List<AbstractTerminologyObject> parents;

	/**
	 * The children of this object (including the linked children).
	 */
	private List<AbstractTerminologyObject> children;

	/**
	 * Knowledge storage of this object: Problem-solving knowledge, that is
	 * related to this object, is stored with the combined key {@link PSMethod}
	 * and {@link MethodKind}: In general, a {@link List} of
	 * {@link KnowledgeSlice} instances is returned. The map has to be
	 * transient, so that huge knowledge bases can be serialized!
	 */
	private transient Map<Class<? extends PSMethod>, Map<MethodKind, KnowledgeSlice>> knowledgeMap;

	private final InfoStore infoStore = new DefaultInfoStore();

	private void init() {
		// unsynchronized version, allows null values
		knowledgeMap = new HashMap<Class<? extends PSMethod>, Map<MethodKind, KnowledgeSlice>>();

		children = new LinkedList<AbstractTerminologyObject>();
		parents = new LinkedList<AbstractTerminologyObject>();
	}

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
		init();
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
	public void addChild(AbstractTerminologyObject child) {
		if (!hasChild(child)) {
			addParentChildLink(this, child);
		}
	}

	/**
	 * Adds a new {@link KnowledgeSlice} instance to the knowledge storage of
	 * this {@link AbstractTerminologyObject} instance. The knowledge is added
	 * to the given {@link PSMethod} context with the specified
	 * {@link MethodKind} as key.
	 * 
	 * @param poblemsolver the {@link PSMethod} context of the added knowledge
	 * @param knowlegeSlice the piece of knowledge to be added
	 * @param knowledgeContext The context, in which the knowledge acts
	 */
	@Override
	public synchronized void addKnowledge(Class<? extends PSMethod> problemsolver,
			KnowledgeSlice knowledgeSlice, MethodKind knowledgeContext) {
		/* make sure, that a storage for the problem-solver is available */
		if (knowledgeMap.get(problemsolver) == null) {
			// for rules (default) two types (FORWARD and BACKWARD) of
			// knowledge are required
			Map<MethodKind, KnowledgeSlice> kinds =
					new HashMap<MethodKind, KnowledgeSlice>(2);
			knowledgeMap.put(problemsolver, kinds);
		}
		Map<MethodKind, KnowledgeSlice> storage = (knowledgeMap
				.get(problemsolver));

		storage.put(knowledgeContext, knowledgeSlice);

		if (getKnowledgeBase() != null) {
			getKnowledgeBase().addKnowledge(problemsolver, knowledgeSlice,
					knowledgeContext);
		}
	}

	/**
	 * Adds this AbstractTerminologyObject as parent of new children.
	 */
	private synchronized void addToNewChildren(List<AbstractTerminologyObject> children) {
		if (children != null) {
			for (AbstractTerminologyObject namedObject : children) {
				namedObject.addParent(this);
			}
		}
	}

	/**
	 * Adds the specified list of {@link AbstractTerminologyObject} instances as
	 * parents to the list of parents. The objects are also linked as children
	 * to the specified parent.
	 * 
	 * @param newParents the list parents to be added
	 */
	private synchronized void addToNewParents(List<AbstractTerminologyObject> newParents) {
		if (newParents != null) {
			for (AbstractTerminologyObject parent : newParents) {
				parent.addChild(this);
			}
		}
	}

	/**
	 * Adds this {@link AbstractTerminologyObject} as a child to the specified
	 * parent and the specified parent is added to the 'parents' list of this
	 * instance. This instance is appended to the parent's list of children.
	 * 
	 * @param parent a new parent of this instance
	 */
	public synchronized void addParent(AbstractTerminologyObject parent) {
		if (!hasParent(parent)) {
			addParentChildLink(parent, this);
		}
	}

	private static void addParentChildLink(AbstractTerminologyObject parent, AbstractTerminologyObject child) {
		parent.children.add(child);
		child.parents.add(parent);
	}

	/**
	 * Returns the list of knowledge slices for a given problem-solver class and
	 * the specified context of the problem-solving method ({@link MethodKind}).
	 * 
	 * @param problemsolver the given problem-solver class, for which the
	 *        knowledge should be retrieved ({@link PSMethod})
	 * @param kind the context of the knowledge (e.g. MethodKind.FORWARD or
	 *        MethodKind.BACKWARD)
	 */
	@Override
	public KnowledgeSlice getKnowledge(Class<? extends PSMethod> problemsolver,
			MethodKind kind) {
		Map<MethodKind, KnowledgeSlice> o = knowledgeMap.get(problemsolver);
		if (o != null) {
			return o.get(kind);
		}
		else {
			return null;
		}
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
	 * Checks, whether this instance has the specified
	 * {@link AbstractTerminologyObject} as parent.
	 * 
	 * @param namedObject the specified object that is possibly a parent
	 * @return true, if namedObject is a parent of this instance
	 */
	public boolean hasParent(TerminologyObject namedObject) {
		if (getParents() == null) {
			return false;
		}
		return Arrays.asList(getParents()).contains(namedObject);
	}

	/**
	 * Tests, if the specified object is a child of this instance.
	 * 
	 * @param child the object to test
	 * @return true if the specified object is a child of this instance
	 */
	public boolean hasChild(AbstractTerminologyObject child) {
		return child.hasParent(this);
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
	public boolean removeChild(AbstractTerminologyObject child) {
		if (hasChild(child)) {
			removeParentChildLink(this, child);
			return true;
		}
		return false;
	}

	/**
	 * Removes the specified {@link KnowledgeSlice} instance from the knowledge
	 * storage of this {@link AbstractTerminologyObject} instance. The knowledge
	 * is assumed to be contained in the given {@link PSMethod} context with the
	 * specified {@link MethodKind} as key.
	 * 
	 * @param problemsolver the {@link PSMethod} context
	 * @param knowledgeSlice the element to be removed
	 * @param knowledgeContext the {@link MethodKind} key of the context
	 */
	public synchronized void removeKnowledge(Class<? extends PSMethod> problemsolver,
			KnowledgeSlice knowledgeSlice, MethodKind knowledgeContext) {
		removeLocalKnowledge(problemsolver, knowledgeSlice,
				knowledgeContext);
		if (getKnowledgeBase() != null) {
			// FIXME: the slice must not be removed if it is used at any other
			// AbstractTerminologyObject
			getKnowledgeBase().removeKnowledge(problemsolver,
					knowledgeSlice, knowledgeContext);
		}
	}

	/**
	 * Returns all {@link KnowledgeSlice} instances contained in the knowledge
	 * storage of this object.
	 * 
	 * @return all {@link KnowledgeSlice} instances of this instance
	 */
	public Collection<KnowledgeSlice> getAllKnowledge() {
		Collection<KnowledgeSlice> result = new ArrayList<KnowledgeSlice>();
		for (Class<? extends PSMethod> problemsolverKeyClass : knowledgeMap.keySet()) {
			result.addAll(knowledgeMap.get(problemsolverKeyClass).values());
		}
		return result;
	}

	/**
	 * Erase them all: Removes all {@link KnowledgeSlice} instances contained
	 * over the entire knowledge storage. This means, that <i>all</i> knowledge
	 * slices are deleted from the {@link KnowledgeBase}, where this instance is
	 * contained.
	 * 
	 * @return the collection of removed {@link KnowledgeSlice} instances
	 */
	public synchronized Collection<KnowledgeSlice> removeAllKnowledge() {
		Collection<KnowledgeSlice> result = new ArrayList<KnowledgeSlice>();
		for (Class<? extends PSMethod> problemsolverKeyClass : knowledgeMap.keySet()) {
			Map<MethodKind, KnowledgeSlice> map = knowledgeMap
					.get(problemsolverKeyClass);
			for (MethodKind methodKind : new ArrayList<MethodKind>(map.keySet())) {
				KnowledgeSlice slice = map.get(methodKind);
				removeKnowledge(problemsolverKeyClass, slice, methodKind);
				result.add(slice);
			}
		}
		return result;
	}

	/**
	 * Similar to the method removeKnowledge, but the specified
	 * {@link KnowledgeSlice} instance with the {@link PSMethod} context and the
	 * given {@link MethodKind} key is only removed from the knowledge storage,
	 * but not globally from the {@link KnowledgeBase}. Use with care!
	 * 
	 * @param poblemsolver the {@link PSMethod} context of the knowledge to be
	 *        removed
	 * @param knowlegeSlice the {@link KnowledgeSlice} to be removed
	 * @param knowledgeContext the {@link MethodKind} key of the knowledge
	 */
	private synchronized boolean removeLocalKnowledge(Class<? extends PSMethod> problemsolver,
			KnowledgeSlice knowledgeSlice, MethodKind knowledgeContext) {
		// List knowledgeSlices;

		/* make sure, that a storage for the problem-solver is available */
		if (knowledgeMap.get(problemsolver) == null) {
			return false;
		}

		Map<MethodKind, KnowledgeSlice> storage = (knowledgeMap.get(problemsolver));

		/* make sure, that a storage for the kind of knowledge is available */
		if (storage.get(knowledgeContext) == null) {
			return false;
		}
		// return if there is another knowledgeslice stored
		if (storage.get(knowledgeContext) != knowledgeSlice) {
			return false;
		}
		else {
			storage.remove(knowledgeContext);
			return true;
		}
	}

	private synchronized void removeAllChildren() {
		if ((children != null)) {
			while (!children.isEmpty()) {
				AbstractTerminologyObject child = children.get(0);
				child.removeParent(this);
			}
		}
	}

	/**
	 * Removes this AbstractTerminologyObject as children of parents.
	 */
	private synchronized void removeAllParents() {
		if (parents != null) {
			while (!parents.isEmpty()) {
				AbstractTerminologyObject parent = parents.get(0);
				removeParent(parent);
			}
		}
	}

	/**
	 * Removes the specified {@link AbstractTerminologyObject} instance from the
	 * list of parents.
	 * 
	 * @param parent the instance to be removed from the list of parents
	 */
	public synchronized void removeParent(AbstractTerminologyObject parent) {
		if (hasParent(parent)) {
			parents.remove(parent);
			removeParentChildLink(parent, this);
		}
	}

	/**
	 * Sets the specified list of {@link AbstractTerminologyObject} instances as
	 * the complete list of children of this object.
	 * 
	 * @param the new list of children of this instance
	 */
	public void setChildren(List<AbstractTerminologyObject> children) {
		removeAllChildren();
		addToNewChildren(children);
		this.children = children;
	}

	/**
	 * Sets the specified list of {@link AbstractTerminologyObject} instances as
	 * the complete list of parents of this object.
	 * 
	 * @param the new list of parents of this instance
	 */
	public void setParents(List<AbstractTerminologyObject> parents) {
		removeAllParents(); // from the old parents
		addToNewParents(parents); // to the new parents
		this.parents = parents; // NB: set the parents here!
		// due to the hasParent/hasChild check!!
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

	public int getNumberOfChildren() {
		return children != null ? children.size() : 0;
	}

	public void moveChildToPosition(AbstractTerminologyObject child, int pos) {
		if (children.remove(child)) {
			children.add(pos > children.size() ? children.size() : pos, child);
		}
	}

	@Override
	public InfoStore getInfoStore() {
		return infoStore;
	}
}