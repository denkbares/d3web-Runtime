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

package de.d3web.kernel.psMethods.suppressAnswer;

import de.d3web.kernel.psMethods.PSMethodRulebased;
/**
 * Mechanism to suppress answers of questions via rules
 * Creation date: (28.08.00 18:04:09)
 * @author joba, norman
 */
public class PSMethodSuppressAnswer extends PSMethodRulebased {
	private static PSMethodSuppressAnswer instance = null;

	/**
	 * @return the one and only instance of this PSMethod
	 * Creation date: (04.12.2001 12:36:25)
	 */
	public static PSMethodSuppressAnswer getInstance() {
		if (instance == null) {
			instance = new PSMethodSuppressAnswer();
		}
		return instance;
	}

}