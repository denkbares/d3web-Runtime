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

package de.d3web.kernel.psMethods.setCovering;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.psMethods.MethodKind;

/**
 * This is the super class for the basic SCM-Classes
 * 
 * @author bates
 */
public abstract class SCNode {

	private NamedObject namedObject = null;

	public void setNamedObject(NamedObject namedObject) {
		this.namedObject = namedObject;
	}

	public NamedObject getNamedObject() {
		return namedObject;
	}

	public abstract String getId();

	public List getRelationsFromParents() {
		if (namedObject != null) {
			List relations = namedObject.getKnowledge(PSMethodSetCovering.class,
					MethodKind.BACKWARD);
			List ret = new LinkedList();

			if (relations == null)
				return null;

			Iterator relIter = relations.iterator();
			while (relIter.hasNext()) {
				SCRelation relation = (SCRelation) relIter.next();
				if (relation.getTargetNode().equals(this)) {
					ret.add(relation);
				}
			}

			return ret;
		}
		return null;
	}

	public List getRelationsToChildren() {
		if (namedObject != null) {
			List relations = namedObject.getKnowledge(PSMethodSetCovering.class,
					MethodKind.FORWARD);
			List ret = new LinkedList();

			if (relations == null)
				return null;

			Iterator relIter = relations.iterator();
			while (relIter.hasNext()) {
				SCRelation relation = (SCRelation) relIter.next();
				if (relation.getSourceNode().equals(this)) {
					ret.add(relation);
				}
			}

			return ret;
		}
		return null;
	}

	public abstract boolean isLeaf();

	public int hashCode() {
		if (getId() == null) {
			return 0;
		}
		return getId().hashCode();
	}

	public boolean equals(Object o) {
		try {
			SCNode otherNode = (SCNode) o;
			return otherNode.getId().equals(getId());
		} catch (Exception e) {
			return false;
		}
	}

	public String toString() {
		return getId();
	}

	public abstract String verbalize();
}
