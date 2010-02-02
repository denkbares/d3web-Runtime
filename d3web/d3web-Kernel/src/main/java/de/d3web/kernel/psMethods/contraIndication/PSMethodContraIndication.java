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

package de.d3web.kernel.psMethods.contraIndication;

import de.d3web.core.session.blackboard.Fact;
import de.d3web.kernel.psMethods.PSMethodRulebased;

/**
 * Problem solving method for contraindicating QASets
 * This Class is a singleton.
 * Creation date: (03.11.2000 01:08:25)
 * @author Norman Brümmer
 */
public class PSMethodContraIndication extends PSMethodRulebased {

	private static PSMethodContraIndication instance = null;

	/**
	 * Creation date: (04.12.2001 12:36:25)
	 * @return de.d3web.kernel.psMethods.contraIndication.PSMethodContraIndication
	 */
	public static PSMethodContraIndication getInstance() {
		if (instance == null) {
			instance = new PSMethodContraIndication();
		}
		return instance;
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		// because any number of contra-indications is the same
		// we simply deliver the first fact as the result
		return facts[0];
	}

}