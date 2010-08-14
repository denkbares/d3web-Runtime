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

package de.d3web.core.knowledge.terminology.info;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 
 * @author bates, hoernlein
 */
public class MMInfoStorage {

	private Set<MMInfoObject> mmios = new LinkedHashSet<MMInfoObject>();

	public void addMMInfo(MMInfoObject mmio) {
		mmios.add(mmio);
	}

	public void removeMMInfo(MMInfoObject mmio) {
		mmios.remove(mmio);
	}

	public void clear() {
		mmios.clear();
	}

	/**
	 * @return Set (with static order) of all MMInfoObjects which DCMarkup
	 *         matching dcData
	 */
	public Set<MMInfoObject> getMMInfo(DCMarkup dcMarkup) {
		Set<MMInfoObject> result = new LinkedHashSet<MMInfoObject>();
		Iterator<MMInfoObject> iter = mmios.iterator();
		while (iter.hasNext()) {
			MMInfoObject mmio = iter.next();
			if (mmio.matches(dcMarkup)) result.add(mmio);
		}
		return result;
	}

	/**
	 * @return Set (with static order) of all DCMarkups
	 */
	public Set<DCMarkup> getAllDCMarkups() {
		Set<DCMarkup> result = new LinkedHashSet<DCMarkup>();
		for (MMInfoObject mmi : mmios) {
			DCMarkup markup = mmi.getDCMarkup();
			result.add(markup);
		}
		return result;
	}

}