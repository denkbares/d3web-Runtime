/*
 * Created on 30.09.2003
 */
package de.d3web.caserepository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.d3web.caserepository.CaseObject.Solution;
import de.d3web.kernel.domainModel.Diagnosis;

/**
 * 30.09.2003 12:09:05
 * @author hoernlein
 */
public class SolutionContainerImpl implements ISolutionContainer {

    public static String getXMLCode(Collection solutions) {
        StringBuffer sb = new StringBuffer();
        sb.append("<Solutions>\n");
        Iterator iter = solutions.iterator();
        while (iter.hasNext()) {
            CaseObject.Solution s = (CaseObject.Solution) iter.next();
            sb.append("<Solution" +
                    " id=\"" + s.getDiagnosis().getId() + "\"" +
                    " weight=\"" + s.getWeight() + "\"" +
                    " psmethod=\"" + s.getPSMethodClass().getName() + "\"" +
                    " state=\"" + s.getState().toString() + "\"" +
            "/>\n");
        }
        sb.append("</Solutions>\n");
        return sb.toString();
    }
    
	private Set solutions = new HashSet();

	/*
	 * (non-Javadoc)
	 * @see de.d3web.caserepository.ISolutionContainer#addSolution(de.d3web.caserepository.CaseObject.Solution)
	 */
	public void addSolution(CaseObject.Solution solution) {
		solutions.add(solution);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.d3web.caserepository.ISolutionContainer#removeSolution(de.d3web.caserepository.CaseObject.Solution)
	 */
	public void removeSolution(CaseObject.Solution solution) {
		solutions.remove(solution);
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.CaseObject#getSolutions()
	 */
	public Set getSolutions() {
		return Collections.unmodifiableSet(solutions);
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.CaseObject#getSolution(de.d3web.kernel.domainModel.Diagnosis, java.lang.Class)
	 */
	public Solution getSolution(Diagnosis d, Class psMethodClass) {
		Iterator iter = solutions.iterator();
		while (iter.hasNext()) {
			CaseObject.Solution s = (CaseObject.Solution) iter.next();
			if (s.getDiagnosis().equals(d) && s.getPSMethodClass().equals(psMethodClass))
				return s;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.CaseObject#getSolutions(java.lang.Class)
	 */
	public Set getSolutions(Class psMethodClass) {
		Set result = new HashSet();
		Iterator iter = solutions.iterator();
		while (iter.hasNext()) {
			CaseObject.Solution s = (CaseObject.Solution) iter.next();
			if (s.getPSMethodClass() != null && s.getPSMethodClass().equals(psMethodClass))
				result.add(s);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.ISolutionContainer#getXMLCode()
	 */
	public String getXMLCode() {
	    return getXMLCode(solutions);
	}

}
