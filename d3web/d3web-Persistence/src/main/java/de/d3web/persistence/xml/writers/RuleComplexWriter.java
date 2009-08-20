package de.d3web.persistence.xml.writers;
import java.util.HashMap;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.RuleAction;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.persistence.xml.writers.conditions.ConditionsPersistenceHandler;
/**
 * Generates the XML representation of a RuleComplex
 * @author Michael Scharvogel
 */
public class RuleComplexWriter implements IXMLWriter {

	public static final Class ID = RuleComplex.class;

	private HashMap actionMap = null;

	/**
		* Creates a new RuleComplexWriter initialized with the given RuleAction-Writers 
		* @param actionMap RuleAction-Writers for all needed RuleActions 
		*/
	public RuleComplexWriter(HashMap actionMap) {
		this.actionMap = actionMap;
	}

	/**
	 * Returns the XML-Writer for a RuleAction matching the given ID
	 * @param key the id of the RuleAction-Writer
	 * @return the XML-Writer for a RuleAction matching the given ID
	 */
	private IXMLWriter getXMLWriter(Object key) {
		if (actionMap.containsKey(key)) {
			return (IXMLWriter) actionMap.get(key);
		} else {
			return null;
		}
	}

	/**
	 * @see AbstractXMLWriter#getXMLString(Object)
	 */
	public java.lang.String getXMLString(java.lang.Object o) {
		StringBuffer sb = new StringBuffer();
		// Object[] theValues = null;
		// Question theQuestion = null;

		if (o == null) {
			Logger.getLogger(this.getClass().getName()).warning("null is no RuleCompex");
		} else if (!(o instanceof RuleComplex)) {
			Logger.getLogger(this.getClass().getName()).warning(o.toString() + " is no RuleComplex");
		} else {
			RuleComplex rule = (RuleComplex) o;

			sb.append(
				"<KnowledgeSlice ID='"
					+ rule.getId()
					+ "' type='RuleComplex'");
			if(!rule.isActive()) {
				sb.append(" active='" + rule.isActive() + "'");
			}
			//[TODO]: PCDATA?
			if(rule.getComment() != null) {
				sb.append(" comment='" + rule.getComment() + "'");
			}
			sb.append(">\n");
			
			// hier nun die Action ausgeben
			RuleAction action = rule.getAction();
			if (action != null) {
				IXMLWriter theWriter =
					getXMLWriter(rule.getAction().getClass());
				if (theWriter != null) {
					sb.append(theWriter.getXMLString(action));
				} else {
					sb.append(
						"<Action ID='"
							+ rule.getId()
							+ "' class='"
							+ action.getClass().toString()
							+ "'/>\n");
				}
			}

			// jetzt folgt Condition und Exception	
			AbstractCondition condition = rule.getCondition();
			if (condition != null) {
				sb.append(
					ConditionsPersistenceHandler.getInstance().toXML(
						condition));
			}

			AbstractCondition exception = rule.getException();
			if (exception != null) {
				sb.append("<Exception>");

				sb.append(
					ConditionsPersistenceHandler.getInstance().toXML(
						exception));

				sb.append("</Exception>\n");
			}

			AbstractCondition context = rule.getContext();
			if (context != null) {
				sb.append("<Context>");

				sb.append(
					ConditionsPersistenceHandler.getInstance().toXML(context));

				sb.append("</Context>\n");
			}

			sb.append("</KnowledgeSlice>\n");
		}
		return sb.toString();
	}

}