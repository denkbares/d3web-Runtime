/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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

package de.d3web.core.knowledge.terminology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.KnowledgeContainer;
import de.d3web.core.knowledge.terminology.info.Properties;
import de.d3web.core.knowledge.terminology.info.PropertiesContainer;
import de.d3web.core.session.CaseObjectSource;
import de.d3web.core.session.XPSCase;

/**
 * NamedObject is parent of knowledge-base objects such as QASet, Question,
 * Diagnosis or Answer. <BR>
 * 
 * It provides a map, to store information relevant to the problem-solving
 * methods used in the knowledge base. Each problem-solver should use this map
 * to store the knowledge it needs for this single NamedObject. <BR>
 * A NamedObject contains a Map (name: properties) to store additional
 * properties dynamically. <BR>
 * Further there is a property "timeValued", which indicates if this Object is
 * able to change over time or not (default: not timeValued).
 * 
 * @author joba, chris, hoernlein
 * @see de.d3web.core.knowledge.terminology.IDObject
 * @see de.d3web.kernel.misc.PropertiesAdapter
 */
public abstract class NamedObject extends IDObject implements CaseObjectSource,
		KnowledgeContainer, PropertiesContainer {

	private static final long serialVersionUID = -3561319946758513307L;

	private Properties properties;

	/**
	 * Representing a short name of the object.
	 */
	private String name;

	/**
	 * The knowledge base this object belongs to.
	 */
	private KnowledgeBase knowledgeBase;

	/**
	 * The parents of this object  (including the linked parents).
	 */
	private List<NamedObject> parents;

	/**
	 * The children of this object (including the linked children).
	 */
	private List<NamedObject> children;

	/**
	 * The linked children of this object (also included in 'children')
	 */
	private Collection<NamedObject> linkedChildren;

	/**
	 * The linked parents of this object (also included in 'parents')
	 */
	private Collection<NamedObject> linkedParents;

	/**
	 * Knowledge storage of this object: Problem-solving knowledge,
	 * that is related to this object, is stored with the combined key
	 * {@link PSMethod} and {@link MethodKind}: In general, a {@link List} of
	 * {@link KnowledgeSlice} instances is returned. 
	 * The map has to be transient, so that huge knowledge bases can be
	 * serialized!
	 */
	private transient Map<Class<? extends PSMethod>, Map<MethodKind, KnowledgeSlice>> knowledgeMap;

	public static final int BEFORE = 1;

	public static final int AFTER = 2;

	private void init() {
		// unsynchronized version, allows null values
		knowledgeMap = new HashMap<Class <? extends PSMethod>, Map<MethodKind, KnowledgeSlice>>();

		children = new ArrayList<NamedObject>();
		parents = new ArrayList<NamedObject>();
		linkedChildren = new ArrayList<NamedObject>();
		linkedParents = new ArrayList<NamedObject>();
		properties = new Properties();
	}

	/**
	 * Creates a new {@link NamedObject} instance with a given ID.
	 * 
	 * @param id The global identifier of the instance
	 */
	public NamedObject(String id) {
		super(id);
		init();
	}

	/**
	 * Returns the {@link Properties} instance of this {@link NamedObject}.
	 * The properties are stored in a map, dynamically storing arbitrary data.
	 * 
	 * @return the properties of this instance
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Sets the {@link Properties} instance of this {@link NamedObject}.
	 * The properties are stored in a map, dynamically storing arbitrary data.
	 * 
	 * @param properties the properties of this instance 
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	/**
	 * Appends the specified {@link NamedObject} as a child to
	 * the list of children. This object is also linked as a 
	 * parent to the specified child. 
	 * 
	 * @param child a new child of this {@link NamedObject}
	 * @see #addParent(NamedObject parent)
	 */
	public void addChild(NamedObject child) {
		if (!hasChild(child)) {
			addParentChildLink(this, child);
		}
	}

	/**
	 * Appends the specified {@link NamedObject} as a 'linked 
	 * child' to the list of children. A linked child relation
	 * denotes, that the link is not static, but the child is
	 * located at another position in the hierarchy.
	 * This object is also linked as a linked parent 
	 * to the specified child. 
	 * 
	 * @param child the new linked child
	 */
	public void addLinkedChild(NamedObject child) {
		if (!hasChild(child)) {
			addChild(child);
		}
		if (!getLinkedChildren().contains(child)) {
			linkedChildren.add(child);
			child.addLinkedParent(this);
		}
	}

	/**
	 * Adds the specified {@link NamedObject} as a child to
	 * the list of children. The position of the insertion is
	 * specified by the {@link NamedObject} childToMarkPosition:
	 * For position=1 the new child is added right before this 
	 * instance; for position=2 the new child is added right after 
	 * the instance. 
	 * This object is also linked as a parent to the specified child. 
	 * 
	 * @see #addParent(NamedObject parent)
	 * @param childToAdd the new child instance
	 * @param childToMarkPosition an already existing child, where 
	 *        the new child should be added before or after
	 * @param position with values 1 (before childToMarkPosition) 
	 *        and 2 (after childToMarkPosition)
	 */
	public void addChild(NamedObject childToAdd,
			NamedObject childToMarkPosition, int position) {
		if (!hasChild(childToAdd)) {
			children.add(children.indexOf(childToMarkPosition) + position - 1,
					childToAdd);
			childToAdd.parents.add(this);
		}
	}

	/**
	 * Adds the specified {@link NamedObject} as a linked child to
	 * the list of children. The position of the insertion is
	 * specified by the {@link NamedObject} childToMarkPosition:
	 * For position=1 the new child is added right before this 
	 * instance; for position=2 the new child is added right after 
	 * the instance. 
	 * This object is also linked as a linked parent to the 
	 * specified child. 
	 * 
	 * @see #addParent(NamedObject parent)
	 * @param childToAdd the new linked child instance
	 * @param childToMarkPosition an already existing child, where 
	 *        the new child should be added before or after
	 * @param position with values 1 (before childToMarkPosition) 
	 *        and 2 (after childToMarkPosition)
	 */	public void addLinkedChild(NamedObject childToAdd,
			NamedObject childToMarkPosition, int position) {
		if (!hasChild(childToAdd)) {
			addChild(childToAdd);
		}
		if (!getLinkedChildren().contains(childToAdd)) {
			linkedChildren.add(childToAdd);
		}
	}

	/**
	 * Adds a new {@link KnowledgeSlice} instance to the 
	 * knowledge storage of this {@link NamedObject} instance.
	 * The knowledge is added to the given {@link PSMethod} 
	 * context with the specified {@link MethodKind} as key.
	 *   
	 * @param poblemsolver the {@link PSMethod} context of 
	 *        the added knowledge
	 * @param knowlegeSlice the piece of knowledge to be added
	 * @param knowledgeContext The context, in which the knowledge acts
	 */
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
	 * Adds this NamedObject as parent of new children.
	 */
	private synchronized void addToNewChildren(List<NamedObject> children) {
		if (children != null) {
			for (NamedObject namedObject : children) {
				namedObject.addParent(this);
			}
		}
	}

	/**
	 * Adds the specified list of {@link NamedObject} instances
	 * as parents to the list of parents. 
	 * The objects are also linked as children to the specified 
	 * parent. 
	 * 
	 * @param newParents the list parents to be added 
	 */
	private synchronized void addToNewParents(List<NamedObject> newParents) {
		if (newParents != null) {
			for (NamedObject parent : newParents) {
				parent.addChild(this);
			}
		}
	}

	/**
	 * Adds this {@link NamedObject} as a child to the specified 
	 * parent and the specified parent is added to the 'parents' 
	 * list of this instance. This instance is appended to the 
	 * parent's list of children.
	 * 
	 * @param parent a new parent of this instance
	 */
	public synchronized void addParent(NamedObject parent) {
		if (!hasParent(parent)) {
			addParentChildLink(parent, this);
		}
	}

	private static void addParentChildLink(NamedObject parent, NamedObject child) {
		parent.children.add(child);
		child.parents.add(parent);
	}

	/**
	 * Adds this {@link NamedObject} as a linked child to the specified 
	 * parent and the specified parent is added to the 'linked parents' 
	 * list of this instance. This instance is appended to the 
	 * linked parent's list of children.
	 * 
	 * @param parent a new linked parent of this instance
	 */
	public synchronized void addLinkedParent(NamedObject parent) {
		if (!hasParent(parent)) {
			addParent(parent);
		}
		if (!getLinkedParents().contains(parent)) {
			linkedParents.add(parent);
			parent.addLinkedChild(this);
		}
	}

	/**
	 * Returns all children (including the linked ones) of this
	 * {@link NamedObject} instance.
	 * 
	 * @return all children of this object
	 */
	public List<? extends NamedObject> getChildren() {
		return children;
	}

	/**
	 * Returns the list of knowledge slices for a given problem-solver class
	 * and the specified context of the problem-solving method ({@link MethodKind}).
	 *
	 * @param problemsolver 
	 *            the given problem-solver class, for which the knowledge should
	 *            be retrieved ({@link PSMethod})
	 * @param kind the context of the knowledge (e.g. MethodKind.FORWARD 
	 *        or MethodKind.BACKWARD)
	 */
	public KnowledgeSlice getKnowledge(Class<? extends PSMethod> problemsolver,
			MethodKind kind) {
		Map<MethodKind, KnowledgeSlice> o = knowledgeMap.get(problemsolver);
		if (o != null)
			return o.get(kind);
		else
			return null;
	}

	/**
	 * Gives the knowledge base to which this instance belongs to.
	 * 
	 * @return the knowledge base this object belongs to
	 */
	public KnowledgeBase getKnowledgeBase() {
		return knowledgeBase;
	}

	/**
	 * Returns the list of {@link NamedObject} instances, that are the 
	 * parents of this instance.
	 * 
	 * @return the list of parents of this object
	 */
	public List<? extends NamedObject> getParents() {
		return parents;
	}

	/**
	 * Checks, whether this instance has the specified 
	 * {@link NamedObject} as parent.
	 * 
	 * @param namedObject the specified object that is possibly a parent
	 * @return true, if namedObject is a parent of this instance
	 */
	public boolean hasParent(NamedObject namedObject) {
		if (getParents() == null)
			return false;
		return getParents().contains(namedObject);
	}

	/**
	 * Tests, if the specified object is a child of this instance.
	 * 
	 * @param child the object to test
	 * @return true if the specified object is a child of this instance
	 */
	public boolean hasChild(NamedObject child) {
		return child.hasParent(this);
	}

	/**
	 * The text of a {@link NamedObject} is the name or a short 
	 * description of the object. Please keep it brief and use other  
	 * fields for longer content (e.g., prompt for {@link Question},  
	 * and comments for {@link Diagnosis}).
	 * 
	 * @return the name of this object
	 */
	public String getName() {
		return name;
	}

	private static void removeParentChildLink(NamedObject parent,
			NamedObject child) {
		parent.children.remove(child);
		child.parents.remove(parent);
	}

	/**
	 * Removes the specified instance from the children
	 * of this instance. 
	 * 
	 * @param child the specified child to be removed from the list
	 */
	public void removeChild(NamedObject child) {
		if (hasChild(child)) {
			removeParentChildLink(this, child);
		}
	}

	/**
	 * Removes the specified instance from the linked children
	 * of this instance. 
	 * 
	 * @param child the specified linked child to be removed from the list
	 */
	public void removeLinkedChild(NamedObject child) {
		if (getLinkedChildren().contains(child)) {
			linkedChildren.remove(child);
			child.removeLinkedParent(this);

			removeParentChildLink(this, child);
		}
	}

	/**
	 * Removes the specified {@link KnowledgeSlice} instance from the 
	 * knowledge storage of this {@link NamedObject} instance.
	 * The knowledge is assumed to be contained in the given {@link PSMethod} 
	 * context with the specified {@link MethodKind} as key.
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
			// NamedObject
			getKnowledgeBase().removeKnowledge(problemsolver,
					knowledgeSlice);
		}
	}

	/**
	 * Returns all {@link KnowledgeSlice} instances contained
	 * in the knowledge storage of this object.
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
	 * Returns all {@link KnowledgeSlice} instances in all {@link PSMethod}
	 * contexts, that have the specified {@link MethodKind} as key.
	 * 
	 * @return all {@link KnowledgeSlice} instances of this instance with the 
	 *         specified {@link MethodKind} key
	 */	
	public Collection<KnowledgeSlice> getAllKnowledge(MethodKind methodKind) {
		Collection<KnowledgeSlice> result = new ArrayList<KnowledgeSlice>();
		for (Class<? extends PSMethod> problemsolverKeyClass : knowledgeMap.keySet()) {
			KnowledgeSlice knowledgeSlice = knowledgeMap.get(problemsolverKeyClass).get(methodKind);
			if (knowledgeSlice!=null) {
				result.add(knowledgeSlice);
			}
		}
		return result;
	}

	/**
	 * Erase them all: Removes all {@link KnowledgeSlice} instances contained
	 * over the entire knowledge storage.
	 * This means, that <i>all</i> knowledge slices are deleted from the 
	 * {@link KnowledgeBase}, where this instance is contained.  
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
	 * {@link KnowledgeSlice} instance with the {@link PSMethod} 
	 * context and the given {@link MethodKind} key  
	 * is only removed from the knowledge storage, but not globally
	 * from the {@link KnowledgeBase}.
	 * Use with care!  
	 * 
	 * @param poblemsolver the {@link PSMethod} context of the knowledge to be removed
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
				NamedObject child = children.get(0);
				child.removeParent(this);
			}
		}
	}

	/**
	 * Removes this NamedObject as children of parents.
	 */
	private synchronized void removeAllParents() {
		if (parents != null) {
			while (!parents.isEmpty()) {
				NamedObject parent = parents.get(0);
				removeParent(parent);
			}
		}
	}

	/**
	 * Removes the specified {@link NamedObject} instance from
	 * the list of parents.
	 *   
	 * @param parent the instance to be removed from the list of parents
	 */
	public synchronized void removeParent(NamedObject parent) {
		if (hasParent(parent)) {
			parents.remove(parent);
			removeParentChildLink(parent, this);
		}
	}

	/**
	 * Removes the specified {@link NamedObject} instance from
	 * the list of linked parents.
	 *   
	 * @param parent the instance to be removed from the list of linked parents
	 */
	public synchronized void removeLinkedParent(NamedObject parent) {
		if (getLinkedParents().contains(parent)) {
			linkedParents.remove(parent);
			parent.removeLinkedChild(this);
			removeParentChildLink(parent, this);
		}
	}

	/**
	 * Sets the specified list of {@link NamedObject} instances as the
	 * complete list of children of this object.
	 * 
	 * @param the new list of children of this instance
	 */
	public void setChildren(List<NamedObject> children) {
		removeAllChildren();
		addToNewChildren(children);
		this.children = children;
	}

	/**
	 * Relates this instance to the specified {@link KnowledgeBase}.
	 * 
	 * @param knowledgeBase the new knowledge base this object is related to
	 * @throws KnowledgeBaseObjectModificationException
	 *         if a {@link KnowledgeBase} has been already defined previously
	 */
	public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
		if (this.knowledgeBase == null) {
			this.knowledgeBase = knowledgeBase;
		} else if (!knowledgeBase.equals(getKnowledgeBase())) {
			throw new IllegalStateException(
					"KnowledgeBase already defined!");
		}
	}

	/**
	 * Sets the specified list of {@link NamedObject} instances as the
	 * complete list of parents of this object.
	 * 
	 * @param the new list of parents of this instance
	 */
	public void setParents(List<NamedObject> parents) {
		removeAllParents(); // from the old parents
		addToNewParents(parents); // to the new parents
		this.parents = parents; // NB: set the parents here!
		// due to the hasParent/hasChild check!!
	}

	/**
	 * Sets the new text of this object.
	 * The text of a {@link NamedObject} is the name or a short 
	 * description of the object. Please keep it brief and use other  
	 * fields for longer content (e.g., prompt for {@link Question},  
	 * and comments for {@link Diagnosis}).
	 * 
	 * @param text the new text of this instance
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the specified values for this object in the context of
	 * a specified {@link XPSCase}.
	 * 
	 * @param theCase the context, the values are stored 
	 * @param values the values of this instance to be set
	 */
	public abstract void setValue(XPSCase theCase, Object[] values);

	public String toString() {
		return getName();
	}

	/**
	 * Returns the linked children of this instance.
	 * A linked child relation
	 * denotes, that the link is not static, but the child is
	 * located at another position in the hierarchy.
	 * This object is also linked as a linked parent 
	 * to the specified child. 
	 * 
	 * @return the linked children of this instance
	 */
	public Collection<NamedObject> getLinkedChildren() {
		return linkedChildren;
	}

	/**
	 * Returns the linked parents of this instance.
	 * A linked parent relation
	 * denotes, that the link is not static, but the parent 
	 * is located at another position in the hierarchy.
	 * This object is also linked as a linked child
	 * to the specified parent. 
	 * 
	 * @return the linked children of this instance
	 */
	private Collection<NamedObject> getLinkedParents() {
		return linkedParents;
	}

}