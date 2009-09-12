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

import de.d3web.kernel.domainModel.NumericalInterval;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondNumIn;
import de.d3web.persistence.xml.loader.NumericalIntervalsCodec;
import de.d3web.persistence.xml.writers.conditions.ConditionWriter;

/**
 * THis is the writer-class for CondNumIn-Objects
 * @author merz
 */
public class CondNumInWriter extends ConditionWriter {

	public String toXML(AbstractCondition ac) {
		CondNumIn cni = (CondNumIn) ac;
		NumericalInterval interval = cni.getInterval();

		String questionId = "";
		if(cni.getQuestion() != null) {
			questionId = cni.getQuestion().getId();
		}
		
		StringBuffer ret = new StringBuffer();
		ret.append("<Condition type='numIn' ID='" + questionId + "'>\n");
		ret.append(NumericalIntervalsCodec.getInstance().encode(interval));
		ret.append("</Condition>");

		return ret.toString();

	}

	/**
	 * @see ConditionWriter#getSourceObject()
	 */
	public Class getSourceObject() {
		return CondNumIn.class;
	}

}
