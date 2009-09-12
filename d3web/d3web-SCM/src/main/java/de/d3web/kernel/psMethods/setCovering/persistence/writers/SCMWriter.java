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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.psMethods.setCovering.PSMethodSetCovering;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.kernel.psMethods.setCovering.SCRelation;
import de.d3web.kernel.psMethods.setCovering.persistence.SCMPersistenceHandler;
import de.d3web.persistence.xml.writers.IXMLWriter;

/**
 * This is the super writer for all scm-knowledge
 * 
 * @author bates
 */
public class SCMWriter implements IXMLWriter {

	private Map writers = null;

	private static SCMWriter instance = null;

	private SCMWriter() {
		writers = new HashMap();
		writers.put(PredictedFinding.class, FindingWriter.getInstance());
		writers.put(SCDiagnosis.class, SCDiagnosisWriter.getInstance());
		writers.put(SCRelation.class, SCRelationWriter.getInstance());
	}

	public static SCMWriter getInstance() {
		if (instance == null) {
			instance = new SCMWriter();
		}
		return instance;
	}

	private IXMLWriter getWriter(Class key) {
		return (IXMLWriter) writers.get(key);
	}

	/**
	 * o has to be a knowledge base if you want to generate XML-code for the
	 * whole SCM knowledge. If it is not, this writer tries to find a proper
	 * writer for your object
	 */
	public String getXMLString(Object o) {
		try {
			if (o instanceof KnowledgeBase) {
				return generateKnowledgeBaseXML((KnowledgeBase) o).toString();
			} else {
				IXMLWriter writer = getWriter(o.getClass());
				if (writer != null) {
					return writer.getXMLString(o);
				}
				return null;
			}
		} catch (Exception x) {
			return null;
		}
	}

	private StringBuffer generateKnowledgeBaseXML(KnowledgeBase kb) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='ISO-8859-1' ?>\n");
		sb.append("<KnowledgeBase type='" + SCMPersistenceHandler.SCM_PERSISTENCE_HANDLER
				+ "' system='d3web'>\n");

		IXMLWriter scRelationWriter = getWriter(SCRelation.class);

		sb.append("<KnowledgeSlices>\n");

		Collection relations = new HashSet(kb.getAllKnowledgeSlicesFor(PSMethodSetCovering.class));

		// SCRelations
		Iterator iter = relations.iterator();
		while (iter.hasNext()) {
			SCRelation relation = (SCRelation) iter.next();
			if (!relation.getId().equals(SCRelation.DEFAULT_RELATION)) {
				String relationXML = scRelationWriter.getXMLString(relation);
				sb.append(relationXML);
				// System.out.println("Writing relation " + relation.getId() +
				// "=" + relation);
			}
		}

		sb.append("</KnowledgeSlices>\n");

		sb.append("</KnowledgeBase>\n");
		return sb;
	}

}
