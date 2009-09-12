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

package de.d3web.kernel.psMethods.combinied;
import java.util.Collection;
import java.util.LinkedList;

import de.d3web.kernel.psMethods.PSMethod;
import de.d3web.kernel.psMethods.PSMethodAdapter;

/**
 * A generic class for combining different problem solving methods.
 * Can be used for information retrieval based upon multiple problem solving methods states.
 * Creation date: (03.01.2002 16:08:27)
 * @author Christian Betz
 */
public abstract class PSMethodCombined extends PSMethodAdapter {
	private Collection<PSMethod> psmethods;

	public void addPSMethod(PSMethod newPSMethod) {
		psmethods.add(newPSMethod);
	}

	public PSMethodCombined() {
		setPSMethods(new LinkedList<PSMethod>());
	}

	/**
	 * @return a List of all PSMethods this PSM combines
	 */
	public Collection<PSMethod> getPSMethods() {
		return psmethods;
	}

	/**
	 * @param a List of all PSMethods this PSM should combine
	 */
	public void setPSMethods(Collection<PSMethod> newPSMethods) {
		psmethods = newPSMethods;
	}
}