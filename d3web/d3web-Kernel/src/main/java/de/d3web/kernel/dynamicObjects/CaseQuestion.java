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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.psMethods.suppressAnswer.ActionSuppressAnswer;
/**
 * Stores the dynamic, user specific values for an Question object. It
 * corresponds to the static Question object. <br>
 * Values to be stored: <br>
 * <li>Current value corresponding to a given user case.
 * <li>History of values the question was assigned to
 * <li>Set of suppress rules which do suppress a subset of the possible answers
 * (alternatives)
 * 
 * @author Christian Betz, joba, norman
 * @see Question
 */
public abstract class CaseQuestion extends CaseQASet {
	private List valueHistory = null;
	private Set suppressRules = null;
	private boolean unknownVisible;

	public CaseQuestion(Question question) {
		super(question);
		valueHistory = new LinkedList();
		suppressRules = new HashSet();
		unknownVisible = true;
	}

	/**
	 * adds a RuleComplex with an action that can suppress alternatives Creation
	 * date: (28.08.2000 13:59:33)
	 */
	public void addRuleSuppress(RuleComplex rule) {
		suppressRules.add(rule);
	}

	/**
	 * @return alternatives which are not suppressed by suppress rules
	 */
	public List getMergedSuppressAlternatives() {
		//TreeSet suppSet = new TreeSet();
		Iterator rule = suppressRules.iterator();
		List result = new LinkedList<Answer>();
		while (rule.hasNext()) {
			RuleComplex r = (RuleComplex) rule.next();
			ActionSuppressAnswer tempAction = (ActionSuppressAnswer) r.getAction();
			if (tempAction != null) {
				Iterator supp = (tempAction).getSuppress().iterator();
				while (supp.hasNext()) {
					result.add(supp.next());
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @return a history stack that contains a map of rule/value pairs. These
	 *         pairs inform about which value has been overridden by which rule.
	 */
	public List getValueHistory() {
		return valueHistory;
	}

	/**
	 * Creation date: (13.09.2000 14:02:25)
	 * 
	 * @return true iff the "unknown" alternative should be visible e.g. in an
	 *         dialog
	 */
	public boolean isUnknownVisible() {
		return unknownVisible;
	}

	/**
	 * removes a suppress rule (that has been added by addRuleSuppress())
	 * Creation date: (28.08.2000 14:00:52)
	 */
	public void removeRuleSuppress(RuleComplex rule) {
		suppressRules.remove(rule);
	}

	/**
	 * Sets a boolean flag that decides if the "unknown" alternative is visible
	 * e.g. in a dialog. Creation date: (13.09.2000 14:02:25)
	 */
	public void setUnknownVisible(boolean unknownVisible) {
		this.unknownVisible = unknownVisible;
	}
}