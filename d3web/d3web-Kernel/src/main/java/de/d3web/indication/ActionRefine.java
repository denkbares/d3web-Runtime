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

package de.d3web.indication;
import java.util.LinkedList;

import de.d3web.core.inference.RuleAction;
import de.d3web.core.knowledge.terminology.Diagnosis;
import de.d3web.core.knowledge.terminology.QASet;

/**
 * ActionRefine represents the indication of a QASet in order to refine an esteblished diagnosis.
 * Creation date: (21.02.2002 13:14:06)
 * @author Christian Betz
 */
public class ActionRefine extends ActionNextQASet {

	private static final long serialVersionUID = -986240968780275101L;
	private Diagnosis target = null;

	/**
	 * Creates a new ActionRefine for the given corresponding rule
	 */
	public ActionRefine() {
		super();
	}

	/**
	 * @return the Diagnosis to refine
	 */
	public Diagnosis getTarget() {
		return target;
	}

	/**
	 * sets the Diagnosis to refine
	 */
	public void setTarget(de.d3web.core.knowledge.terminology.Diagnosis newTarget) {
		target = newTarget;
	}
	
	public RuleAction copy() {
		ActionRefine a = new ActionRefine();
		a.setRule(getCorrespondingRule());
		a.setQASets(new LinkedList<QASet>(getQASets()));
		a.setTarget(getTarget());
		return a;
	}
	
	public int hashCode() {
		int hash = 0;
		if (getQASets() != null)
			hash +=getQASets().hashCode();
		if (getTarget() != null)
			hash += getTarget().hashCode();
		return hash;
	}
	
	public boolean equals(Object o) {
		if (o==this) 
			return true;
		if (o instanceof ActionRefine) {
			ActionRefine a = (ActionRefine)o;
			return (isSame(a.getQASets(), getQASets()) &&
					isSame(a.getTarget(), getTarget()));
		}
		else
			return false;
	}
	private boolean isSame(Object obj1, Object obj2) {
		if(obj1 == null && obj2 == null) return true;
		if(obj1 != null && obj2 != null) return obj1.equals(obj2);
		return false;
	}
}