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

/*
 * Created on 30.09.2003
 */
package de.d3web.caserepository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 30.09.2003 12:09:05
 * 
 * @author hoernlein
 */
public class SolutionContainerImpl implements ISolutionContainer {

	public static String getXMLCode(Collection<CaseObject.Solution> solutions) {
		StringBuffer sb = new StringBuffer();
		sb.append("<Solutions>\n");
		Iterator<CaseObject.Solution> iter = solutions.iterator();
		while (iter.hasNext()) {
			CaseObject.Solution s = iter.next();
			sb.append("<Solution" +
					" id=\"" + s.getSolution().getId() + "\"" +
					" weight=\"" + s.getWeight() + "\"" +
					" psmethod=\"" + s.getPSMethodClass().getName() + "\"" +
					" state=\"" + s.getState().toString() + "\"" +
					"/>\n");
		}
		sb.append("</Solutions>\n");
		return sb.toString();
	}

	private Set<CaseObject.Solution> solutions = new HashSet<CaseObject.Solution>();

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.d3web.caserepository.ISolutionContainer#addSolution(de.d3web.
	 * caserepository.CaseObject.Solution)
	 */
	@Override
	public void addSolution(CaseObject.Solution solution) {
		solutions.add(solution);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getSolutions()
	 */
	@Override
	public Set<CaseObject.Solution> getSolutions() {
		return Collections.unmodifiableSet(solutions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.ISolutionContainer#getXMLCode()
	 */
	@Override
	public String getXMLCode() {
		return getXMLCode(solutions);
	}

}
