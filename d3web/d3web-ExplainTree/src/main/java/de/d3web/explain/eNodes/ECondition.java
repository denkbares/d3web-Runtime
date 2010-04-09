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
 * EConditiion.java
 *
 * Created on 27. März 2002, 16:15
 */

package de.d3web.explain.eNodes;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondKnown;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.NonTerminalCondition;
import de.d3web.core.inference.condition.TerminalCondition;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.session.Value;
import de.d3web.core.session.ValuedObject;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.explain.ExplanationFactory;
/**
 *
 * @author  betz
 */
public class ECondition {

	private Condition condition = null;	// the "original" condition object
	private List activeParts = null;


	/**
	 * returns an ECondition only, if the condition "cond" is active (can fire), else null	 * @param factory ExplanationFactory	 * @param cond AbstractCondition	 * @return ECondition	 */
	public static ECondition createECondition(
		ExplanationFactory factory,
		Condition cond) {

		if (cond == null) {
			return null;
		}

		boolean isActive = false;
		try {
			isActive = cond.eval(factory.getXPSCase());
		} catch (NoAnswerException e) {
		} catch (UnknownAnswerException e) {
		}
		if (isActive) {
			ECondition eCond = new ECondition(factory, cond);
			return eCond;
		}
		return null;
	}


	/** Creates a new instance of EConditiion */
	private ECondition(ExplanationFactory factory, Condition cond) {
		setCondition(cond);
		init(factory);
	}

	private void init(ExplanationFactory factory) {
		List aParts = new LinkedList();
		if (getCondition() instanceof NonTerminalCondition) {
			Iterator iter = ((NonTerminalCondition) getCondition()).getTerms().iterator();
			while (iter.hasNext()) {
				Condition cond = (Condition) iter.next();
				ECondition eCond = createECondition(factory, cond);
				if (eCond != null) {
					aParts.add(eCond);
				}
			}
			setActiveParts(aParts);
		}
	}

	/**
	 * Gets the condition.
	 * @return Returns a AbstractCondition
	 */
	public Condition getCondition() {
		return condition;
	}

	/**
	 * Sets the condition.
	 * @param condition The condition to set
	 */
	private void setCondition(Condition condition) {
		this.condition = condition;
	}

	/**
	 * Gets the activeParts.
	 * @return Returns a List
	 */
	public List getActiveParts() {
		return activeParts;
	}

	/**
	 * Sets the activeParts.
	 * @param activeParts The activeParts to set
	 */
	private void setActiveParts(List activeParts) {
		this.activeParts = activeParts;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<cond type='" + getCondition().getClass().getName() + "'>\n");
		if (getCondition() instanceof TerminalCondition) {
			Iterator iter =
				((TerminalCondition) getCondition()).getTerminalObjects().iterator();
			while (iter.hasNext()) {
				ValuedObject elem = (ValuedObject) iter.next();
				sb.append("<target id='" + elem.getId() + "'");
				sb.append(" value='");
				if (getCondition() instanceof CondEqual) {
					Value answer = ((CondEqual) getCondition()).getValue();
					if (answer != null) {
						sb.append(((ChoiceValue) answer).getAnswerChoiceID());
					}
				} else if (getCondition() instanceof CondKnown) {
					sb.append("known");
				}
				sb.append("'");
				sb.append("/>\n");
			}
		} else if (activeParts != null) {
			Iterator iterator = activeParts.iterator();
			while (iterator.hasNext()) {
				ECondition cond = (ECondition) iterator.next();
				sb.append(cond.toString());
			}
		}
		sb.append("</cond>\n");
		return sb.toString();
	}
	
//	private void showRuleCondition(AbstractCondition con) {
//		TerminalCondition con;
//		if (con instanceof CondEqual) {
//			CondEqual ce = (CondEqual)con;
//			sb.append("<target id='" + ce.getQuestion().getId() + "'");
//			sb.append(" value='");
//			Iterator iter = ce.getValues().iterator();
//			while(iter.hasNext()) {
//			        sb.append(((AnswerChoice)iter.next()).getId());
//			}
//			sb.append("'");
//			sb.append("/>\n");
//		} else if (con instanceof CondKnown) {
//			sb.append("<target id='" + con.getQuestion().getId() + "'");
//			sb.append(" value='known'/>\n");
//		} if (con instanceof CondDState) {
//		}
//	}
	
}