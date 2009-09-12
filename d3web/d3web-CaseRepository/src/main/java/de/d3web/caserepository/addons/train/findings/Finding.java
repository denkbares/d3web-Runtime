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
 * Created on 20.07.2004 by Chris
 * 
 */
package de.d3web.caserepository.addons.train.findings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Finding: Representing a single finding consisting of a textual representation
 * and the respective knowledge slices.
 * 
 * A Finding is related to a single QContainer (as a finding is a result of an
 * examination)
 * 
 * Possible knowledge slices are: - the causal diagnostic link (diagnosis +
 * rating) - the symptoms hidden by this finding
 * 
 * @author Chris 20.07.2004
 */
public class Finding {

	private String textualContent = null;

	private Collection knowledgeSlices = new ArrayList(1);

	/**
	 * 
	 *  
	 */
	public Finding() {
		super();
	}

	/**
	 * @return Returns the knowledgeSlices.
	 */
	public Collection<FindingDiagnosisRelation> getKnowledgeSlices() {
		return knowledgeSlices;
	}

	/**
	 * This method makes sure that there's only one knowledgeSlice per Diagnosis
	 * @param knowledgeSlice
	 *            The knowledgeSlices to add.
	 */
	public void addKnowledgeSlice(FindingDiagnosisRelation knowledgeSlice) {
		Iterator iter = getKnowledgeSlices().iterator();
		while (iter.hasNext()) {
			FindingDiagnosisRelation rating = (FindingDiagnosisRelation) iter
					.next();
			if (rating.getDiagnosis().equals(knowledgeSlice.getDiagnosis())) {
				iter.remove();
			}
		}
		this.knowledgeSlices.add(knowledgeSlice);
	}

	/**
	 * @return Returns the textualContent.
	 */
	public String getTextualContent() {
		return textualContent;
	}

	/**
	 * @param textualContent
	 *            The textualContent to set.
	 */
	public void setTextualContent(String textualContent) {
		this.textualContent = textualContent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.addons.IContents#getXMLCode()
	 */
	public String getXMLCode() {
		StringBuffer sb = new StringBuffer();
		sb.append("<Finding>\n");
		sb.append("<Content><![CDATA[" + getTextualContent()
				+ "]]></Content>\n");
		Iterator iter = getKnowledgeSlices().iterator();
		while (iter.hasNext()) {
			FindingDiagnosisRelation ks = (FindingDiagnosisRelation) iter
					.next();
			sb.append(ks.getXMLCode());
		}
		sb.append("</Finding>");
		return sb.toString();
	}

	/**
	 * overridden method
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		return getTextualContent() /*+ "(" + getKnowledgeSlices() + ")"*/;
	}
	
}