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

package de.d3web.explain.eNodes.reasons;

import de.d3web.explain.ExplanationFactory;
import de.d3web.explain.eNodes.EReason;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.psMethods.PSMethodInit;
import de.d3web.kernel.psMethods.userSelected.PSMethodUserSelected;

public class EPSMethodReason extends EReason {

	private Class context = null;


	/**
	 * Constructor for ERuleReason. +
	 * @param qaSetReason
	 */
	public EPSMethodReason (ExplanationFactory factory, QASet.Reason qaSetReason) {
		super(factory);
		setContext(qaSetReason.getProblemSolverContext());
	}
	
	
	
	/** Getter for property context.
	 * @return Value of property context.
	 */
	public Class getContext() {
		return context;
	}

	/** Setter for property context.
	 * @param context New value of property context.
	 */
	private void setContext(Class context) {
		this.context = context;
	}

	
	

	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (getContext() == PSMethodUserSelected.class) {
			sb.append("... benutzerselektiert");
		} else if (getContext() == PSMethodInit.class) {
			sb.append("... Startfrageklasse");
		} else {
			sb.append("[PROBLEM in EPSMethodReason.toString] "+getContext());
		}
		return sb.toString();
	}

}
