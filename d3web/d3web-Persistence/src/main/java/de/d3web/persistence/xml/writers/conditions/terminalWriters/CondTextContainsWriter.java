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

package de.d3web.persistence.xml.writers.conditions.terminalWriters;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondTextContains;
import de.d3web.persistence.xml.writers.conditions.ConditionWriter;
import de.d3web.xml.utilities.XMLTools;

/**
 * This is the writer-class for CondTextContains-Objects
 * @author merz
 */
public class CondTextContainsWriter extends ConditionWriter {

	public String toXML(AbstractCondition ac) {
		CondTextContains ctc = (CondTextContains) ac;

		String questionId = "";
		if(ctc.getQuestion() != null) {
			questionId = ctc.getQuestion().getId();
		}
		
		return "<Condition type='textContains' ID='"
			+ questionId
			+ "'>\n" +			"<Value>" +			"<![CDATA[" + XMLTools.prepareForCDATA(ctc.getValue()) + "]]>" +			"</Value>\n" +			"</Condition>\n";
	}

	/**
	 * @see ConditionWriter#getSourceObject()
	 */
	public Class getSourceObject() {
		return CondTextContains.class;
	}

}
