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
package de.d3web.core.inference;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.UndefinedValue;

public class PropagationEntry {

	private final TerminologyObject object;
	private final Value oldValue;
	private final Value newValue;
	private boolean strategic = false;

	public boolean isStrategic() {
		return strategic;
	}

	public void setStrategic(boolean strategic) {
		this.strategic = strategic;
	}

	public PropagationEntry(TerminologyObject object, Value oldValue, Value newValue) {
		this.object = object;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public TerminologyObject getObject() {
		return object;
	}

	public Value getOldValue() {
		return oldValue;
	}

	public boolean hasOldValue() {
		return oldValue != null && !UndefinedValue.isUndefinedValue(oldValue);
	}

	public Value getNewValue() {
		return newValue;
	}

	public boolean hasNewValue() {
		return newValue != null && !UndefinedValue.isUndefinedValue(newValue);
	}

	@Override
	public String toString() {
		String newValueS = "";
		String oldValueS = "";
		if (newValue != null) {
			newValueS = newValue.toString();
		}
		if (oldValue != null) {
			oldValueS = oldValue.toString();
		}
		return getClass().getSimpleName() + "[" + object + ":" + oldValueS + " -> " + newValueS
				+ "]" + Integer.toHexString(hashCode());
	}

}
