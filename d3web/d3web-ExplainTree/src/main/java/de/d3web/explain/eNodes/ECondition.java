/*
 * EConditiion.java
 *
 * Created on 27. MÃ¤rz 2002, 16:15
 */

package de.d3web.explain.eNodes;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.explain.ExplanationFactory;
import de.d3web.kernel.domainModel.ValuedObject;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondKnown;
import de.d3web.kernel.domainModel.ruleCondition.NoAnswerException;
import de.d3web.kernel.domainModel.ruleCondition.NonTerminalCondition;
import de.d3web.kernel.domainModel.ruleCondition.TerminalCondition;
import de.d3web.kernel.domainModel.ruleCondition.UnknownAnswerException;
/**
 *
 * @author  betz
 */
public class ECondition {

	private AbstractCondition condition = null;	// the "original" condition object
	private List activeParts = null;


	/**
	 * returns an ECondition only, if the condition "cond" is active (can fire), else null	 * @param factory ExplanationFactory	 * @param cond AbstractCondition	 * @return ECondition	 */
	public static ECondition createECondition(
		ExplanationFactory factory,
		AbstractCondition cond) {

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
	private ECondition(ExplanationFactory factory, AbstractCondition cond) {
		setCondition(cond);
		init(factory);
	}

	private void init(ExplanationFactory factory) {
		List aParts = new LinkedList();
		if (getCondition() instanceof NonTerminalCondition) {
			Iterator iter = ((NonTerminalCondition) getCondition()).getTerms().iterator();
			while (iter.hasNext()) {
				AbstractCondition cond = (AbstractCondition) iter.next();
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
	public AbstractCondition getCondition() {
		return condition;
	}

	/**
	 * Sets the condition.
	 * @param condition The condition to set
	 */
	private void setCondition(AbstractCondition condition) {
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
					Iterator iter2 = ((CondEqual)getCondition()).getValues().iterator();
					while(iter2.hasNext()) {
						sb.append(((AnswerChoice)iter2.next()).getId());
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