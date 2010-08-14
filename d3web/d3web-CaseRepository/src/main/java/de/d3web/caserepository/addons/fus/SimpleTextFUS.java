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
 * Created on 14.04.2004
 */
package de.d3web.caserepository.addons.fus;

import java.util.Set;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.ISolutionContainer;
import de.d3web.caserepository.SolutionContainerImpl;
import de.d3web.caserepository.XMLCodeGenerator;
import de.d3web.caserepository.CaseObject.Solution;
import de.d3web.core.io.utilities.XMLTools;

/**
 * SimpleTextFUS (in ) de.d3web.caserepository.addons.fus d3web-CaseRepository
 * 
 * @author hoernlein @date 14.04.2004
 */
public class SimpleTextFUS implements XMLCodeGenerator, ISolutionContainer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.XMLCodeGenerator#getXMLCode()
	 */
	public String getXMLCode() {
		return "<SimpleTextFUS"
				+ " dtrid=\""
				+ getSolutionsTreeRootId()
				+ "\""
				+ " ttrid=\""
				+ getTherapiesTreeRootId()
				+ "\">\n"
				+ "<Name><![CDATA["
				+ XMLTools.prepareForCDATA(getName())
				+ "]]></Name>\n"
				+ "<Text><![CDATA["
				+ XMLTools.prepareForCDATA(getText())
				+ "]]></Text>\n"
				+ myISC.getXMLCode()
				+ "</SimpleTextFUS>\n";
	}

	private String name;
	private String text;
	private ISolutionContainer myISC;
	private String diagnosesTreeRootId = null;
	private String therapiesTreeRootId = null;

	public SimpleTextFUS(
			String name,
			String text,
			ISolutionContainer s,
			String diagnosesTreeRootId,
			String therapiesTreeRootId) {
		this();
		this.name = name;
		this.text = text;
		this.myISC = s;
		setSolutionsTreeRootId(diagnosesTreeRootId);
		setTherapiesTreeRootId(therapiesTreeRootId);
	}

	public SimpleTextFUS() {
		super();
		this.myISC = new SolutionContainerImpl();
	}

	public String getName() {
		return name;
	}

	public String getText() {
		return text;
	}

	public String getSolutionsTreeRootId() {
		return diagnosesTreeRootId;
	}

	public String getTherapiesTreeRootId() {
		return therapiesTreeRootId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.caserepository.ISolutionContainer#getSolution(de.d3web.kernel
	 * .domainModel.Diagnosis, java.lang.Class)
	 */
	public Solution getSolution(de.d3web.core.knowledge.terminology.Solution d, Class psMethodClass) {
		return myISC.getSolution(d, psMethodClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.ISolutionContainer#getSolutions()
	 */
	public Set<CaseObject.Solution> getSolutions() {
		return myISC.getSolutions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.caserepository.ISolutionContainer#getSolutions(java.lang.Class)
	 */
	public Set<CaseObject.Solution> getSolutions(Class psMethodClass) {
		return myISC.getSolutions(psMethodClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.d3web.caserepository.ISolutionContainer#addSolution(de.d3web.
	 * caserepository.CaseObject.Solution)
	 */
	public void addSolution(Solution s) {
		myISC.addSolution(s);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.d3web.caserepository.ISolutionContainer#removeSolution(de.d3web.
	 * caserepository.CaseObject.Solution)
	 */
	public void removeSolution(Solution s) {
		myISC.removeSolution(s);
	}

	/**
	 * @param diagnosesTreeRootId The diagnosesTreeRootId to set.
	 */
	public void setSolutionsTreeRootId(String diagnosesTreeRootId) {
		if (diagnosesTreeRootId != null && diagnosesTreeRootId.trim().equals("")) this.diagnosesTreeRootId = null;
		else this.diagnosesTreeRootId = diagnosesTreeRootId;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param text The text to set.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @param therapiesTreeRootId The therapiesTreeRootId to set.
	 */
	public void setTherapiesTreeRootId(String therapiesTreeRootId) {
		if (therapiesTreeRootId != null && therapiesTreeRootId.trim().equals("")) this.therapiesTreeRootId = null;
		else this.therapiesTreeRootId = therapiesTreeRootId;
	}

}
