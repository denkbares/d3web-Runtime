/*
 * Created on 23.09.2003
 */
package de.d3web.caserepository;

import java.util.Set;

import de.d3web.kernel.domainModel.Diagnosis;

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
