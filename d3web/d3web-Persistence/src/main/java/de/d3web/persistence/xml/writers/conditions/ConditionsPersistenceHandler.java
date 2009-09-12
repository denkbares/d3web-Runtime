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

package de.d3web.persistence.xml.writers.conditions;

import java.util.HashMap;
import java.util.Map;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;

/**
 * This singleton class handles the XML-code generation for rule-conditions
 * @author merz
 */
public class ConditionsPersistenceHandler {

	private static ConditionsPersistenceHandler instance = null;
	private Map writers = new HashMap();

	/**
	 * @return the one and only instance of this handler
	 */
	public static ConditionsPersistenceHandler getInstance() {
		if (instance == null)
			instance = new ConditionsPersistenceHandler();
		return instance;
	}

	/**
	 * generates XML-code for the given AbstractCondition
	 * @param ac the condition to generate XML-code for
	 * @return the generated XML-code as String
	 */
	public String toXML(AbstractCondition ac) {
		if(ac == null) return null;
		ConditionWriter temp = (ConditionWriter) writers.get(ac.getClass());
		return temp.toXML(ac);
	}

	/**
	 * Adds a needed ConditionWriter (as "child")
	 * @param cw the writer to put to internal Map
	 */
	public void add(ConditionWriter cw) {
		this.writers.put(cw.getSourceObject(), cw);
	}

	/**
	 * Removed a registered ConditionWriter from the internal Map
	 * @param cw the ConditionWriter to remove
	 */
	public void remove(ConditionWriter cw) {
		this.writers.remove(cw.getSourceObject());
	}
}
