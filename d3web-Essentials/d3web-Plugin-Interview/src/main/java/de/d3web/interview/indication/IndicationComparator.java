/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.d3web.interview.indication;

import java.util.Comparator;

import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;

/**
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 17.06.2013
 */
public class IndicationComparator implements Comparator<Indication> {

	@Override
	public int compare(Indication arg0, Indication arg1) {
		if (arg0 == null && arg1 == null) {
			return 0;
		}
		else if (arg0 == null) {
			return 1;
		}
		else if (arg1 == null) {
			return -1;
		}
		else {
			State otherState = arg1.getState();
			int otherOrdinal = getOrdinal(otherState);
			int ordinal = getOrdinal(arg0.getState());
			if (otherOrdinal == ordinal) {
				return Double.compare(arg0.getSorting(), arg1.getSorting());
			}
			else {
				return otherOrdinal - ordinal;
			}
		}
	}

	private static int getOrdinal(State state) {
		if (state == State.REPEATED_INDICATED) {
			return State.INDICATED.ordinal();
		}
		else {
			return state.ordinal();
		}
	}
}
