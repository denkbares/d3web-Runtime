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

import java.util.Iterator;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerUnknown;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondEqual;
import de.d3web.persistence.xml.writers.conditions.ConditionWriter;
/**
 * This is the writer-class for CondEqual-Objects
 * @author merz
 */
public class CondEqualWriter extends ConditionWriter {

	public String toXML(AbstractCondition ac) {
		CondEqual ce = (CondEqual) ac;

		String questionId = "";
		if(ce.getQuestion() != null) {
			questionId = ce.getQuestion().getId();
		}
		
		String ret =
			"<Condition type='equal' ID='"
				+ questionId
				+ "' value='";

		Iterator iter = ce.getValues().iterator();

		if (iter.hasNext()) {
			ret += this.getId(iter.next());
		}

		while (iter.hasNext()) {
			ret += "," + this.getId(iter.next());
		}

		ret += "'/>\n";

		return ret;
	}

	/**
	 * @see ConditionWriter#getSourceObject()
	 */
	public Class getSourceObject() {
		return CondEqual.class;
	}

	private String getId(Object answer) {
		if (answer instanceof AnswerChoice)
			return ((AnswerChoice) answer).getId();
		else if (answer instanceof AnswerUnknown)
			return ((AnswerUnknown) answer).getId();
		else {
			Logger.getLogger(this.getClass().getName()).warning(
				"Could not convert "
					+ answer
					+ ". Check "
					+ this.getClass()
					+ ".getId(Object)!");
			return answer.toString();
		}
	}

}
