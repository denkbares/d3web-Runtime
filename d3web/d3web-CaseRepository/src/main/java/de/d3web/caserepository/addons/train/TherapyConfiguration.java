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

/*
 * Created on 01.10.2004
 */
package de.d3web.caserepository.addons.train;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import de.d3web.caserepository.XMLCodeGenerator;
import de.d3web.caserepository.addons.ITherapyConfiguration;
import de.d3web.core.terminology.Diagnosis;

/**
 * TherapyConfiguration (in ) de.d3web.caserepository.addons.train
 * d3web-CaseRepository
 * 
 * @author hoernlein
 * @date 01.10.2004
 */
public class TherapyConfiguration implements ITherapyConfiguration {

	public static interface ITCNode extends XMLCodeGenerator {
	}

	public static class TCNode implements ITCNode {

		private int n;

		private int m;

		private Set children;

		private TCNode() { /* hide */
		}

		public TCNode(Set children, int n, int m) {
			this.children = children;
			this.n = n;
			this.m = m;
		}

		public TCNode(int n, int m) {
			this.children = new HashSet();
			this.n = n;
			this.m = m;
		}

		public void addChild(ITCNode child) {
			this.children.add(child);
		}

		public static TCNode getANDNode(Set children) {
			return new TCNode(children, children.size(), children.size());
		}

		public static TCNode getXORNode(Set children) {
			return new TCNode(children, 1, 1);
		}

		public static TCNode getEXACTNNode(Set children, int n) {
			return new TCNode(children, n, n);

		}

		public static TCNode getATLEASTNNode(Set children, int n) {
			return new TCNode(children, n, children.size());
		}

		public static TCNode getNTOMNode(Set children, int n, int m) {
			return new TCNode(children, n, m);
		}

		public Set getChildren() {
			return Collections.unmodifiableSet(children);
		}

		public int getN() {
			return n;
		}

		public int getM() {
			return m;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.d3web.caserepository.XMLCodeGenerator#getXMLCode()
		 */
		public String getXMLCode() {
			String res = "<TCNode n=\"" + n + "\" m=\"" + m + "\">\n";
			for (Iterator iter = getChildren().iterator(); iter.hasNext();)
				res += ((ITCNode) iter.next()).getXMLCode();
			return res + "</TCNode>\n";
		}

		/**
		 * overridden method
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("{");
			sb.append(n);
			sb.append("-");
			sb.append(m);
			sb.append("} ");
			for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
				sb.append(((ITCNode) iter.next()).toString());
				if (iter.hasNext()) {
					sb.append(" | ");
				}
			}
			return sb.toString();
		}

	}

	public static class TCLeaf implements ITCNode {

		private Diagnosis d;

		private TCLeaf() { /* hide */
		}

		public TCLeaf(Diagnosis d) {
			this.d = d;
		}

		public Diagnosis getDiagnosis() {
			return d;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.d3web.caserepository.XMLCodeGenerator#getXMLCode()
		 */
		public String getXMLCode() {
			return "<TCLeaf id=\"" + getDiagnosis().getId() + "\"/>\n";
		}

		/**
		 * overridden method
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return getDiagnosis().getText();
		}
	}

	private ITCNode node;

	private TherapyConfiguration() { /* hide */
	}

	public TherapyConfiguration(ITCNode node) {
		this.node = node;
	}

	public ITCNode getNode() {
		return node;
	}

	/**
	 * overridden method
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return node.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.XMLCodeGenerator#getXMLCode()
	 */
	public String getXMLCode() {
		return "<TherapyConfiguration>\n" + node.getXMLCode()
				+ "</TherapyConfiguration>\n";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof TherapyConfiguration))
			return false;

		TherapyConfiguration other = (TherapyConfiguration) obj;
		return equals(getNode(), other.getNode());
	}

	private boolean equals(ITCNode n1, ITCNode n2) {
		if (n1 instanceof TCNode) {
			if (!(n2 instanceof TCNode))
				return false;
			TCNode _1 = (TCNode) n1;
			TCNode _2 = (TCNode) n2;
			if ((_1.getN() != _2.getN()) || (_1.getM() != _2.getM()))
				return false;
			if (_1.getChildren().size() != _2.getChildren().size())
				return false;
			Iterator iter = _1.getChildren().iterator();
			while (iter.hasNext()) {
				ITCNode i = (ITCNode) iter.next();
				boolean found = false;
				Iterator oiter = _2.getChildren().iterator();
				while (oiter.hasNext() && !found) {
					ITCNode o = (ITCNode) oiter.next();
					found = equals(i, o);
				}
				if (!found)
					return false;
			}
			return true;
		} else if (n1 instanceof TCLeaf) {
			if (!(n2 instanceof TCLeaf))
				return false;
			return ((TCLeaf) n1).getDiagnosis().equals(
					((TCLeaf) n2).getDiagnosis());
		} else {
			Logger.getLogger(this.getClass().getName()).warning(
					"no way to handle ICNodes that are neither leaf nor node");
			return false;
		}
	}

}