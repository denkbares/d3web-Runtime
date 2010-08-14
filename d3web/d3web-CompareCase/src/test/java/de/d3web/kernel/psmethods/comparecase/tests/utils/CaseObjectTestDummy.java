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

package de.d3web.kernel.psmethods.comparecase.tests.utils;

import de.d3web.caserepository.CaseObjectImpl;
import de.d3web.core.knowledge.terminology.info.DCElement;

/**
 * This is a dummy class for Junit-tests. It is used for retrieving a similarity
 * without having to compare cases.
 * 
 * @author bruemmer
 */
public class CaseObjectTestDummy extends CaseObjectImpl {

	private double similarity = 0;
	private String id = null;

	public CaseObjectTestDummy(String id) {
		super(null);
		this.id = id;
		getDCMarkup().setContent(DCElement.IDENTIFIER, id);
	}

	public String getId() {
		return id;
	}

	public void setSimilarityForUnitTests(double similarity) {
		this.similarity = similarity;
	}

	public double getSimilarityForUnitTests(CaseObjectTestDummy other) {
		return similarity * other.similarity;
	}

	public int hashCode() {
		return id.hashCode();
	}

	public boolean equals(Object o) {
		try {
			CaseObjectTestDummy other = (CaseObjectTestDummy) o;
			return id.equals(other.id);
		}
		catch (Exception e) {
			return false;
		}
	}

	public String toString() {
		return id;
	}

}
