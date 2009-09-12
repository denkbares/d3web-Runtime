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
