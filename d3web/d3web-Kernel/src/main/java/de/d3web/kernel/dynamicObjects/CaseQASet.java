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

package de.d3web.kernel.dynamicObjects;
import java.util.LinkedList;
import java.util.List;

import de.d3web.kernel.domainModel.QASet;
/**
 * Stores the dynamic, user specific values for an QASet
 * object. It corresponds to the static QASet object.<br>
 * Value to be stored:<br>
 * <li> counter : a counter for the number of deriving causes. 
 * @author Christian Betz, joba, norman
 * @see QASet
 */
public abstract class CaseQASet extends XPSCaseObject {
	private List proReasons = null;
	private List contraReasons = null;

	public CaseQASet(QASet qaset) {
		super(qaset);

		proReasons = new LinkedList();
		contraReasons = new LinkedList();
	}

	/**
	 * Adds a contra reason to contrareason list
	 * Creation date: (26.10.2000 11:07:21)
	 * @param reason contra reason (RuleContraIndication) that blocks this QASet until
	 * 		  it has been undone.
	 */
	public void addContraReason(Object reason) {
		contraReasons.add(reason);
	}

	/**
	 * Adds a pro reason to this CaseQASet
	 * Creation date: (26.10.2000 11:06:47)
	 * @param reason reason to add to pro reason list (either RuleQASet for activation or
	 *		  KnowledgeBase in case of initialization).
	 */
	public void addProReason(Object reason) {
		proReasons.add(reason);
	}

	/**
	 * @return a List of contraReasons for this CaseQASet
	 */
	public List getContraReasons() {
		return contraReasons;
	}

	/**
	 * @return a List of proReasons for this CaseQASet
	 */
	public List getProReasons() {
		return proReasons;
	}

	/**
	 * Creation date: (26.10.2000 11:10:37)
	 * @return true, iff there are any contraReasons
	 */
	public boolean hasContraReason() {
		return !contraReasons.isEmpty();
	}

	/**
	 * Creation date: (26.10.2000 11:10:19)
	 * @return true, iff there are any proReasons
	 */
	public boolean hasProReason() {
		return !proReasons.isEmpty();
	}

	/**
	 * removes a contraindication rule from this CaseQASet (if rule is undone)
	 * Creation date: (26.10.2000 11:08:59)
	 * @param reason reason (rule) to remove from contra reason list
	 */
	public void removeContraReason(Object reason) {
		contraReasons.remove(reason);
	}

	/**
	 * Creation date: (26.10.2000 11:08:59)
	 * @param reason reason (rule or KnowledgeBase (when answered)) to remove from pro reason list
	 */
	public void removeProReason(Object reason) {
		//XPSCase.trace(WBObjekt.getId() + ": removed pro Reason: " + reason.toString());	
		proReasons.remove(reason);
	}
}