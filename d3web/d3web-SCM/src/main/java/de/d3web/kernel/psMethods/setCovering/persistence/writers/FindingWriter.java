package de.d3web.kernel.psMethods.setCovering.persistence.writers;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.persistence.xml.BasicPersistenceHandler;
import de.d3web.persistence.xml.writers.IXMLWriter;
import de.d3web.persistence.xml.writers.conditions.ConditionsPersistenceHandler;

/**
 * Writer for findings
 * 
 * @author bates
 */
public class FindingWriter implements IXMLWriter {

	public static final Class ID = FindingWriter.class;

	private static FindingWriter instance = null;

	public static FindingWriter getInstance() {
		if (instance == null) {
			instance = new FindingWriter();
		}
		return instance;
	}

	private FindingWriter() {
		new BasicPersistenceHandler();
	}

	/**
	 * @return the XML-representation of a finding
	 */
	public String getXMLString(Object o) {
		if (o instanceof PredictedFinding) {
			PredictedFinding finding = (PredictedFinding) o;

			StringBuffer sb = new StringBuffer();
			sb.append("<SCNode type='Finding' id='" + finding.getId() + "' >\n");

			AbstractCondition condition = finding.getCondition();
			String conditionXML = ConditionsPersistenceHandler.getInstance().toXML(condition);
			sb.append(conditionXML);

			sb.append("</SCNode>\n");

			return sb.toString();
		}
		return null;
	}

}
