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
 * ExaminationBlock.java
 * 
 * Created on 19. März 2002, 16:36
 */

package de.d3web.caserepository.addons.train;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.ISolutionContainer;
import de.d3web.caserepository.SolutionContainerImpl;
import de.d3web.caserepository.CaseObject.Solution;
import de.d3web.caserepository.addons.IExaminationBlock;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.xml.utilities.XMLTools;

/**
 * @author betz
 */
public class ExaminationBlock implements IExaminationBlock {

	private String title = null;

	private ISolutionContainer s = new SolutionContainerImpl();
	private List contents = new Vector();
	private Map diagnosis2content = new HashMap();
	
	

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.IExaminationBlock#addSolution(de.d3web.caserepository.CaseObject.Solution)
	 */
	public void addSolution(CaseObject.Solution solution) {
		s.addSolution(solution);
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.ISolutionContainer#removeSolution(de.d3web.caserepository.CaseObject.Solution)
	 */
	public void removeSolution(CaseObject.Solution solution) {
		s.removeSolution(solution);
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.ISolutionContainer#getXMLCode()
	 */
	public String getXMLCode() {
		StringBuffer sb = new StringBuffer();
		sb.append("<ExaminationBlock>\n");
		sb.append("<EBTitle><![CDATA[" + XMLTools.prepareForCDATA(getTitle()) + "]]></EBTitle>\n");
		
		sb.append("<Containers>\n");
		Iterator iter = getContents().iterator();
		while (iter.hasNext())
		    sb.append("<Container id='" + ((QContainer) iter.next()).getId() + "'/>\n");
		sb.append("</Containers>\n");
		
		sb.append(s.getXMLCode());
		
		sb.append("<Solutioncomments>\n");
		iter = diagnosis2content.keySet().iterator();
		while (iter.hasNext()) {
		    Diagnosis d = (Diagnosis) iter.next();
		    String c = getCommentFor(d);
		    sb.append("<Solutioncomment" +
	    		" id='" + d.getId() + "'>" +
				"<![CDATA[" + XMLTools.prepareForCDATA(c) + "]]>" +
				"</Solutioncomment>\n");
		}
		sb.append("</Solutioncomments>\n");
		
		sb.append("</ExaminationBlock>\n");
		return sb.toString();
	}
	
	/* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return -1;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ExaminationBlock))
			return false;
		if (obj == this)
			return true;

		ExaminationBlock other = (ExaminationBlock) obj;
		return getContents().equals(other.getContents())
			&& s.getSolutions().containsAll(other.getSolutions())
			&& other.getSolutions().containsAll(getSolutions());
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.ISolutionContainer#getSolution(de.d3web.kernel.domainModel.Diagnosis,
	 *      java.lang.Class)
	 */
	public Solution getSolution(Diagnosis d, Class psMethodClass) {
		return s.getSolution(d, psMethodClass);
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.ISolutionContainer#getSolutions(java.lang.Class)
	 */
	public Set getSolutions(Class psMethodClass) {
		return s.getSolutions(psMethodClass);
	}

	/*(non-Javadoc)
	 * @see de.d3web.caserepository.ISolutionContainer#getSolutions()
	 */
	public Set getSolutions() {
		return s.getSolutions();
	}

	private static int idCounter = 0;
	private static String createId() {
		idCounter++;
		return "E" + idCounter;
	}

	private String id = null;
	
	/**
	 * overridden method
	 * 
	 * @see de.d3web.caserepository.addons.IExaminationBlock#getId()
	 */
	public String getId() {
		if (id == null) {
			id = createId();
		}
		return id;
	}

	/**
	 * overridden method
	 * 
	 * @see de.d3web.caserepository.addons.IExaminationBlock#addContent(de.d3web.kernel.domainModel.qasets.QContainer)
	 */
	public void addContent(QContainer q) {
		contents.add(q);
	}

	/**
	 * overridden method
	 * 
	 * @see de.d3web.caserepository.addons.IExaminationBlock#removeContent(de.d3web.kernel.domainModel.qasets.QContainer)
	 */
	public void removeContent(QContainer q) {
		contents.remove(q);
	}

	/**
	 * overridden method
	 * 
	 * @see de.d3web.caserepository.addons.IExaminationBlock#getContents()
	 */
	public List getContents() {
		return Collections.unmodifiableList(contents);
	}
	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		
		if (title == null){
			StringBuffer sb = new StringBuffer();
			Iterator iter = contents.iterator();
			while (iter.hasNext()) {
				QContainer qc = (QContainer) iter.next();
				sb.append(qc.getText());
				if (iter.hasNext())
					sb.append(", ");
			}
		return sb.toString();	
		}
		
		return title;
	}

	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

    /* (non-Javadoc)
     * @see de.d3web.caserepository.addons.IExaminationBlock#getCommentFor(de.d3web.kernel.domainModel.Diagnosis)
     */
    public String getCommentFor(Diagnosis d) {
        return (String) diagnosis2content.get(d);
    }

    /* (non-Javadoc)
     * @see de.d3web.caserepository.addons.IExaminationBlock#setCommentFor(de.d3web.kernel.domainModel.Diagnosis, java.lang.String)
     */
    public void setCommentFor(Diagnosis d, String s) {
        diagnosis2content.put(d, s);
    }

}
