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
package de.d3web.caserepository.addons.fus;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import de.d3web.caserepository.addons.IFUSConfiguration;
import de.d3web.caserepository.addons.fus.internal.*;
import de.d3web.caserepository.addons.fus.internal.ProbabilityList.Item;
import de.d3web.core.knowledge.terminology.Solution;

/**
 * 21.10.2003 16:22:25
 * 
 * @author hoernlein
 */
public class FUSConfiguration implements IFUSConfiguration {

	private Set<Configuration> set = new HashSet<Configuration>();

	public ProbabilityList findFor(Set<Solution> setOfSolutions) {
		ProbabilityList result = new ProbabilityList();
		Iterator<Configuration> iter = set.iterator();
		while (iter.hasNext()) {
			Configuration c = iter.next();
			if (c.matches(setOfSolutions)) {
				Iterator<Item> citer = c.getCaseObjectIDProbabilityList().iterator();
				while (citer.hasNext())
					result.add(citer.next());
			}
		}
		result.normalize();
		return result;
	}

	public boolean addConfiguration(Configuration conf) {
		return set.add(conf);
	}

	public boolean removeConfiguration(Configuration conf) {
		return set.remove(conf);
	}

	public Set getConfigurations() {
		return Collections.unmodifiableSet(set);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || !(obj instanceof FUSConfiguration)) return false;

		FUSConfiguration other = (FUSConfiguration) obj;
		if (this.set.size() != other.set.size()) return false;

		Iterator iter = this.set.iterator();
		while (iter.hasNext()) {
			Configuration c = (Configuration) iter.next();
			boolean found = false;
			Iterator oiter = other.set.iterator();
			while (oiter.hasNext() && !found) {
				Configuration oc = (Configuration) oiter.next();
				if (!c.getCaseObjectIDProbabilityList().containsAll(
						oc.getCaseObjectIDProbabilityList())
						|| !oc.getCaseObjectIDProbabilityList().containsAll(
								c.getCaseObjectIDProbabilityList())) continue;
				found = equals(c.getNode(), oc.getNode());
			}
			if (!found) return false;
		}
		return true;
	}

	private boolean equals(AbstractCNode n1, AbstractCNode n2) {
		if (n1 instanceof CNode) {
			if (!(n2 instanceof CNode)) return false;
			CNode _1 = (CNode) n1;
			CNode _2 = (CNode) n2;
			if (!_1.getType().equals(_2.getType())) return false;
			if (_1.getNodes().size() != _2.getNodes().size()) return false;
			Iterator iter = _1.getNodes().iterator();
			while (iter.hasNext()) {
				AbstractCNode i = (AbstractCNode) iter.next();
				boolean found = false;
				Iterator oiter = _2.getNodes().iterator();
				while (oiter.hasNext() && !found) {
					AbstractCNode o = (AbstractCNode) oiter.next();
					found = equals(i, o);
				}
				if (!found) return false;
			}
			return true;
		}
		else if (n1 instanceof CLeaf) {
			if (!(n2 instanceof CLeaf)) return false;
			CLeaf _1 = (CLeaf) n1;
			CLeaf _2 = (CLeaf) n2;
			return _1.getType().equals(_2.getType())
					&& _1.geSolution().equals(_2.geSolution());
		}
		else {
			Logger.getLogger(this.getClass().getName()).warning(
					"no way to handle AbstractCNodes of type '" + n1.getClass() + "'");
			return false;
		}
	}

	/*
	 * <_FUSConfiguration> <Case id="fall_1"> <FUSConfiguration> <Configuration>
	 * <Node type="and"> <Node type="or"> <Leaf type="included"
	 * diagnosis="P66"/> <Leaf type="included" diagnosis="P430"/> </Node> <Leaf
	 * type="excluded" diagnosis="P74"/> </Node> <FUSs> <FUS id="fall_2"/> <FUS
	 * id="fall_3"/> </FUSs> </Configuration> </FUSConfiguration> </Case>
	 * </_FUSConfiguration>
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.XMLCodeGenerator#getXMLCode()
	 */
	public String getXMLCode() {
		if (set == null || set.isEmpty()) return "";
		else {
			StringBuffer sb = new StringBuffer();
			sb.append("<FUSConfiguration>\n");
			Iterator<Configuration> iter = set.iterator();
			while (iter.hasNext()) {
				Configuration conf = iter.next();
				sb.append("<Configuration>\n");
				if (conf.getCaseObjectIDProbabilityList() == null
						|| conf.getCaseObjectIDProbabilityList().isEmpty()) continue;
				else {

					appendXMLCode(conf.getNode(), sb);

					sb.append("<FUSs>\n");
					Iterator iter2 = conf.getCaseObjectIDProbabilityList().iterator();
					while (iter2.hasNext()) {
						ProbabilityList.Item pi = (ProbabilityList.Item) iter2.next();
						sb.append("<FUS" +
								" id=\"" + pi.getObject() + "\"" +
								" probability=\"" + pi.getProbability() + "\"" +
								"/>\n");
					}
					sb.append("</FUSs>\n");
				}
				sb.append("</Configuration>\n");
			}
			sb.append("</FUSConfiguration>\n");
			return sb.toString();
		}
	}

	private void appendXMLCode(AbstractCNode node, StringBuffer sb) {
		if (node instanceof CNode) {
			sb.append("<Node" +
					" type=\"" + ((CNode) node).getType().getName() + "\"" +
					(((CNode) node).getType() instanceof CNode.NOFMType
							? " n=\"" + ((CNode.NOFMType) (((CNode) node).getType())).getN() + "\""
							: "") +
					">\n");
			Iterator iter = ((CNode) node).getNodes().iterator();
			while (iter.hasNext())
				appendXMLCode((AbstractCNode) iter.next(), sb);
			sb.append("</Node>\n");
		}
		else if (node instanceof CLeaf) {
			sb.append("<Leaf" +
					" type=\"" + ((CLeaf) node).getType().getName() + "\"" +
					" diagnosis=\"" + ((CLeaf) node).geSolution().getId() + "\"/>\n");
		}
		else Logger.getLogger(this.getClass().getName()).warning(
				"no way to handle AbstractCNodes of type '" + node.getClass() + "'");
	}

}
