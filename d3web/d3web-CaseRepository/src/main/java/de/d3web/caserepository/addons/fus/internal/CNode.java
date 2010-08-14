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

/*
 * Created on 21.10.2003
 */
package de.d3web.caserepository.addons.fus.internal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import de.d3web.core.knowledge.terminology.Solution;

/**
 * 21.10.2003 15:48:35
 * 
 * @author hoernlein
 */
public class CNode extends AbstractCNode {

	public final static Type OR = new ORType();
	public final static Type AND = new ANDType();
	public final static Type XOR = new XORType();
	public final static Type NONE = new NONEType();

	/**
	 * this static variable is only for persistence-use if you want to
	 * instantiate one NOFMType then use new CNode.NOFMType(int n) instead
	 */
	public final static Type NOFM = new NOFMType();

	public static abstract class Type {

		private String name;

		public String getName() {
			return name;
		}

		protected void setName(String name) {
			this.name = name;
		}

		protected abstract boolean matches(CNode node, Set<Solution> diagnoses);
	}

	/**
	 * OR "at least one node must match"
	 */
	public static class ORType extends Type {

		public ORType() {
			setName("or");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * de.d3web.caserepository.addons.fus.internal.CNode.Type#matches(de
		 * .d3web.caserepository.addons.fus.internal.CNode, java.util.Set)
		 */
		protected boolean matches(CNode node, Set<Solution> diagnoses) {
			Iterator iter = node.getNodes().iterator();
			while (iter.hasNext()) {
				AbstractCNode c = (AbstractCNode) iter.next();
				if (c.matches(diagnoses)) return true;
			}
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			return obj != null && obj instanceof ORType;
		}
	}

	/**
	 * NONE "no node may match" (NONE x y z) = (AND (NOT x) (NOT y) (NOT z))
	 */
	public static class NONEType extends Type {

		public NONEType() {
			setName("none");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * de.d3web.caserepository.addons.fus.internal.CNode.Type#matches(de
		 * .d3web.caserepository.addons.fus.internal.CNode, java.util.Set)
		 */
		protected boolean matches(CNode node, Set<Solution> diagnoses) {
			Iterator iter = node.getNodes().iterator();
			while (iter.hasNext()) {
				AbstractCNode c = (AbstractCNode) iter.next();
				if (c.matches(diagnoses)) return false;
			}
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			return obj != null && obj instanceof ANDType;
		}
	}

	/**
	 * AND "all nodes must match"
	 */
	public static class ANDType extends Type {

		public ANDType() {
			setName("and");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * de.d3web.caserepository.addons.fus.internal.CNode.ANDType#matches
		 * (de.d3web.caserepository.addons.fus.internal.CNode, java.util.Set)
		 */
		@Override
		protected boolean matches(CNode node, Set<Solution> diagnoses) {
			Iterator iter = node.getNodes().iterator();
			while (iter.hasNext()) {
				AbstractCNode c = (AbstractCNode) iter.next();
				if (!c.matches(diagnoses)) return false;
			}
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			return obj != null && obj instanceof ANDType;
		}
	}

	/**
	 * XOR "exactly one node must match"
	 */
	public static class XORType extends Type {

		public XORType() {
			setName("xor");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * de.d3web.caserepository.addons.fus.internal.CNode.Type#matches(de
		 * .d3web.caserepository.addons.fus.internal.CNode, java.util.Set)
		 */
		protected boolean matches(CNode node, Set<Solution> diagnoses) {
			int hits = 0;
			Iterator iter = node.getNodes().iterator();
			while (iter.hasNext()) {
				AbstractCNode c = (AbstractCNode) iter.next();
				if (c.matches(diagnoses)) hits++;
			}
			return hits == 1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			return obj != null && obj instanceof XORType;
		}
	}

	/**
	 * NOFM "at least n nodes must match" n must be at least 2 (for one use
	 * ORType) n must be less than (number of node) - 1 (for (number of node)
	 * use ANDType)
	 */
	public static class NOFMType extends Type {

		private int n = -1;

		private NOFMType() {
			setName("nofm");
		}

		public NOFMType(int n) {
			this();
			this.n = n;
		}

		public int getN() {
			return n;
		}

		public void setN(int n) {
			this.n = n;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * de.d3web.caserepository.addons.fus.internal.CNode.Type#matches(de
		 * .d3web.caserepository.addons.fus.internal.CNode, java.util.Set)
		 */
		protected boolean matches(CNode node, Set<Solution> diagnoses) {
			if (n <= 1 || n >= node.getNodes().size()) {
				Logger.getLogger(this.getClass().getName()).warning(
						"n must be between 2 and #nodes - 1, but is: " + n);
				return false;
			}
			int hits = 0;
			Iterator iter = node.getNodes().iterator();
			while (iter.hasNext()) {
				AbstractCNode c = (AbstractCNode) iter.next();
				if (c.matches(diagnoses)) hits++;
			}
			return hits >= n;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			return obj != null && obj instanceof NOFMType && ((NOFMType) obj).n == this.n;
		}
	}

	private Set<AbstractCNode> nodes;
	private Type type;
	private int leafCount = 0;

	private CNode() { /* hide empty constructor */
	}

	public CNode(Type type, Set<AbstractCNode> setOfCNodes) {
		this(type);
		this.nodes = setOfCNodes;
	}

	public CNode(Type type) {
		this.type = type;
		this.nodes = new HashSet<AbstractCNode>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.Train.FUS.AbstractCNode#matches(java.util.Set)
	 */
	public boolean matches(Set<Solution> diagnoses) {
		return getType().matches(this, diagnoses);
	}

	/**
	 * @return Set of AbstractCNodes
	 */
	public Set<AbstractCNode> getNodes() {
		return nodes;
	}

	/**
	 * @return CNode.Type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param node AbstractCNode
	 */
	public void addNode(AbstractCNode node) {
		if (node instanceof CLeaf) leafCount++;
		nodes.add(node);
		node.setParent(this);
	}

	/**
	 * @param node AbstractCNode
	 */
	public void removeNode(AbstractCNode node) {
		if (node instanceof CLeaf) leafCount--;
		nodes.remove(node);
		node.setParent(null);
	}

	/**
	 * Sets the type.
	 * 
	 * @param type The type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	public boolean hasLeafs() {
		if (leafCount == 0) {
			Iterator iter = nodes.iterator();
			boolean res = false;
			while (iter.hasNext()) {
				Object tempNode = iter.next();
				if (tempNode instanceof CNode) {
					res = ((CNode) tempNode).hasLeafs();
					if (res) return true;
				}
			}
			return false;
		}
		else {
			return true;
		}
	}

	public Object clone() {
		CNode temp = new CNode(type);
		Iterator iter = nodes.iterator();
		while (iter.hasNext()) {
			AbstractCNode tempNode = (AbstractCNode) iter.next();
			temp.addNode((AbstractCNode) tempNode.clone());
		}
		temp.setLeafCount(leafCount);
		return temp;
	}

	/**
	 * Sets the leafCount.
	 * 
	 * @param leafCount The leafCount to set
	 */
	public void setLeafCount(int leafCount) {
		this.leafCount = leafCount;
	}

}
