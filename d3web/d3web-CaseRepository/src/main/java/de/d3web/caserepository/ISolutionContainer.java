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

/*
 * Created on 23.09.2003
 */
package de.d3web.caserepository;

import java.util.Set;

import de.d3web.core.terminology.Diagnosis;

/**
 * 23.09.2003 11:14:43
 * @author hoernlein
 */
public interface ISolutionContainer {
	
	/**
	 * Returns a CaseObject.Solution element, containing the weight and rating for the different contexts.<br>
	 * PriorityGroup may be null. If null, getSolution returns the "end"-solution, otherwise the solution given for this
	 * priority group.<br>
	 * Return null if no solution is given.
	 * 
	 * @param d Diagnosis
	 * @param psMethodClass Class (implements PSMethod)
	 * @return CaseObject.Solution
	 */
	public CaseObject.Solution getSolution(Diagnosis d, Class psMethodClass);
    
	/**
	 * Returns all CaseObject.Solutionss set in this case
	 * @return Set
	 */
	public Set<CaseObject.Solution> getSolutions();
	
	/**
	 * Returns a Set containing all CaseObject.Solutions set in this case with given psMethodClass
	 * @return Set
	 */
	public Set<CaseObject.Solution> getSolutions(Class psMethodClass);
	
	/**
	 * adds a CaseObject.Solution
	 * @param s CaseObject.Solution
	 */
	public void addSolution(CaseObject.Solution s);
	
	/**
	 * removes a CaseObject.Solution
	 * @param s CaseObject.Solution
	 */
	public void removeSolution(CaseObject.Solution s);
	
	/**
	 * @return String
	 */
	public String getXMLCode();

}
