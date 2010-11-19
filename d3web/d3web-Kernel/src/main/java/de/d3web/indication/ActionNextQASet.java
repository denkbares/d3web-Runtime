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

package de.d3web.indication;

import java.util.List;

import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.utilities.HashCodeUtils;

/**
 * This abstract class is representing the Action of an indication. Specialize
 * this in order to implement a new indication type. <br>
 * Creation date: (19.06.2001 18:21:07)
 * 
 * @author joba, Christian Betz
 */
public abstract class ActionNextQASet extends PSAction {

	private List<QASet> qasets;

	/**
	 * Indicates interview objects to be presented by the dialog as specified by
	 * the action.
	 */
	@Override
	public void doIt(Session session, Object source, PSMethod psmethod) {
		for (QASet qaset : getQASets()) {
			Fact fact = FactFactory.createFact(session, qaset, getIndication(),
					source, psmethod);
			session.getBlackboard().addInterviewFact(fact);
		}
	}

	/**
	 * Return the indication state of the corresponding action.
	 * 
	 * @created 18.11.2010
	 * @return the indication state of the action
	 */
	protected abstract Indication getIndication();

	/**
	 * @return List of QASets this action can indicate
	 */
	public List<QASet> getQASets() {
		return qasets;
	}

	/**
	 * @return all objects participating on the action.<BR>
	 *         same as getQASets()
	 */
	@Override
	public List<QASet> getBackwardObjects() {
		return getQASets();
	}

	/**
	 * sets a List of QASets that this Action can activate
	 */
	public void setQASets(List<QASet> qasets) {
		this.qasets = qasets;
	}

	/**
	 * Deactivates all activated QASets
	 */
	@Override
	public void undo(Session session, Object source, PSMethod psmethod) {
		// New handling of indications: Notify blackboard of indication and let
		// the blackboard do all the work
		for (QASet qaset : getQASets()) {
			session.getBlackboard().removeInterviewFact(qaset, source);
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "@"
				+ Integer.toHexString(hashCode()) + getQASets();
	}

	@Override
	public int hashCode() {

		int result = HashCodeUtils.SEED;
		result = HashCodeUtils.hash(result, getQASets());
		result = HashCodeUtils.hash(result, getIndication());

		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o == null) {
			return false;
		}

		if (getClass() != o.getClass()) {
			return false;
		}

		ActionNextQASet a = (ActionNextQASet) o;
		return isSame(a.getQASets(), getQASets()) && isSame(a.getIndication(), getIndication());
	}

	protected boolean isSame(Object obj1, Object obj2) {
		if (obj1 == null && obj2 == null) {
			return true;
		}
		if (obj1 != null && obj2 != null) {
			return obj1.equals(obj2);
		}
		return false;
	}

}