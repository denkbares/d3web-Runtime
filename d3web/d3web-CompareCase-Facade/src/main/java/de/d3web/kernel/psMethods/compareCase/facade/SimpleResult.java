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

package de.d3web.kernel.psMethods.compareCase.facade;
import java.util.Collection;

import de.d3web.caserepository.CaseObject;
import de.d3web.core.terminology.info.DCElement;

/**
 * Creation date: (22.08.01 00:57:47)
 * @author: Norman Brümmer
 */
public class SimpleResult {
	private CaseObject theCase = null;
	private double similarity = 0;
	private Collection diagnoses = null;

	/**
	 * SimpleResult constructor comment.
	 */
	public SimpleResult(CaseObject sc, double sim, Collection diagnoses) {
		super();
		initialize(sc, sim, diagnoses);
	}

	public void initialize(CaseObject sc, double sim, Collection diagnoses) {
		theCase = sc;
		similarity = sim;
		this.diagnoses = diagnoses;
	}

	/**
	 * Creation date: (29.11.2001 14:43:28)
	 * @return boolean
	 * @param o java.lang.Object
	 */
	public boolean equals(Object o) {
		try {
			SimpleResult res = (SimpleResult) o;
			return (res.getCase().getId().equals(theCase.getId()) && (res.getSimilarity() == similarity));
		} catch (Exception x) {
			return false;
		}

	}

	/**
	 * Creation date: (22.08.01 01:56:49)
	 * @return de.d3web.kernel.psMethods.compareCase.StaticCase
	 */
	public CaseObject getCase() {
		return theCase;
	}

	/**
	 * Creation date: (23.08.2001 17:49:58)
	 * @return java.util.List
	 */
	public Collection getDiagnoses() {
		return diagnoses;
	}

	/**
	 * Creation date: (22.08.01 01:56:49)
	 * @return double
	 */
	public double getSimilarity() {
		return similarity;
	}

	/**
	 * Creation date: (22.08.01 17:14:05)
	 * @return java.lang.String
	 */
	public String toString() {
		String title = theCase.getDCMarkup().getContent(DCElement.TITLE);
		if (title == null) {
			title = "noname";
		}
		return title + ": " + similarity + "\n";
	}
}