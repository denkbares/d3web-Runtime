/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.d3web.core.session;

import de.d3web.core.inference.Rule;

/**
 * Type definition for a tuple of rule and values (extensible) There will be
 * saved a rule and the values it has overwritten by firing. <br>
 * Creation date: (18.08.2000 17:38:21)
 * 
 * @author Norman Brümmer
 */
public class SymptomValue {

	private Value value = null;
	private Rule rule = null;

	public SymptomValue(Value value, Rule rule) {
		this.value = value;
		this.rule = rule;
	}

	/**
	 * 
	 * Creation date: (17.08.2000 14:36:58)
	 * 
	 * @return rule of this tuple
	 */
	public Rule getRule() {
		return rule;
	}

	/**
	 * Creation date: (17.08.2000 14:45:39)
	 * 
	 * @return value-array of this tuple
	 */
	public Value getValues() {
		return value;
	}

	public void setValues(Value value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return rule.getId() + ":" + value;
	}
}