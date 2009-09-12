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

package de.d3web.kernel.psMethods.setCovering.persistence.writers;

import java.util.Iterator;

import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.kernel.psMethods.setCovering.SCKnowledge;
import de.d3web.kernel.psMethods.setCovering.SCRelation;
import de.d3web.persistence.xml.writers.IXMLWriter;

/**
 * This is the writer for SCRelation objects
 * 
 * @author bates
 */
public class SCRelationWriter implements IXMLWriter {

	public static final Class ID = SCRelationWriter.class;

	private static SCRelationWriter instance = null;

	private SCRelationWriter() {
	}

	public static SCRelationWriter getInstance() {
		if (instance == null) {
			instance = new SCRelationWriter();
		}
		return instance;
	}

	public String getXMLString(Object o) {
		if (o instanceof SCRelation) {
			StringBuffer sb = new StringBuffer();
			SCRelation scRelation = (SCRelation) o;

			if (scRelation.getId().equals(SCRelation.DEFAULT_RELATION)) {
				return "";
			}
			sb.append("<KnowledgeSlice id='" + scRelation.getId() + "' type='SCRelation' >\n");
			sb.append("<Source>\n");
			if (scRelation.getSourceNode() instanceof PredictedFinding) {
				sb.append(FindingWriter.getInstance().getXMLString(scRelation.getSourceNode()));
			} else if (scRelation.getSourceNode() instanceof SCDiagnosis) {
				sb.append(SCDiagnosisWriter.getInstance().getXMLString(scRelation.getSourceNode()));
			}
			sb.append("</Source>");

			sb.append("<Target>\n");
			if (scRelation.getTargetNode() instanceof PredictedFinding) {
				sb.append(FindingWriter.getInstance().getXMLString(scRelation.getTargetNode()));
			} else if (scRelation.getTargetNode() instanceof SCDiagnosis) {
				sb.append(SCDiagnosisWriter.getInstance().getXMLString(scRelation.getTargetNode()));
			}
			sb.append("</Target>\n");

			sb.append("<KnowledgeMap>\n");

			Iterator iter = scRelation.getKnowledge().iterator();
			while (iter.hasNext()) {
				SCKnowledge knowledge = (SCKnowledge) iter.next();
				sb.append("<Knowledge type='" + knowledge.verbalize() + "' value='"
						+ knowledge.getSymbol() + "' />\n");
			}

			sb.append("</KnowledgeMap>\n");
			sb.append("</KnowledgeSlice>\n");
			return sb.toString();
		}
		return null;
	}

}
