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

package de.d3web.persistence.xml.writers.conditions.nonTerminalWriters;

import java.util.Iterator;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondNot;
import de.d3web.persistence.xml.writers.conditions.ConditionWriter;
import de.d3web.persistence.xml.writers.conditions.ConditionsPersistenceHandler;

/**
 * This is the writer-class for CondNot-Objects
 * @author merz
 */
public class CondNotWriter extends ConditionWriter {

	AbstractCondition absCond;

	public String toXML(AbstractCondition ac) {
		CondNot cn = (CondNot) ac;

		String ret = "<Condition type='not'>\n";

		Iterator iter = cn.getTerms().iterator();
		while (iter.hasNext()) {
			absCond = (AbstractCondition) iter.next();
			ret += ConditionsPersistenceHandler.getInstance().toXML(absCond);
		}

		return ret + "</Condition>\n";
	}
	/**
	 * @see ConditionWriter#getSourceObject()
	 */
	public Class getSourceObject() {
		return CondNot.class;
	}

}
