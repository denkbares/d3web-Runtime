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

package de.d3web.persistence.xml.shared.writers;

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.psMethods.shared.AbstractAbnormality;
import de.d3web.kernel.psMethods.shared.LocalWeight;
import de.d3web.kernel.psMethods.shared.PSMethodShared;
import de.d3web.kernel.psMethods.shared.Weight;
import de.d3web.kernel.psMethods.shared.comparators.KnowledgeBaseUnknownSimilarity;
import de.d3web.kernel.psMethods.shared.comparators.QuestionComparator;
import de.d3web.persistence.utilities.StringBufferInputStream;
import de.d3web.persistence.utilities.StringBufferStream;
import de.d3web.persistence.xml.shared.SharedPersistenceHandler;
import de.d3web.persistence.xml.writers.DCMarkupWriter;

/**
 * Writerclass used by SharedPersistenceHandler
 * Creation date: (10.08.2001 14:12:23)
 * @author: Norman Brümmer
 */
public class SharedKnowledgeWriter {

	/**
	 * Insert the method's description here.
	 * Creation date: (10.08.2001 14:16:17)
	 * @return boolean
	 */
	public static Document writeCBRKnowledge(KnowledgeBase kb) {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version='1.0' encoding='ISO-8859-1' ?>");
			sb.append(
				"<KnowledgeBase"
					+ " type='"
					+ SharedPersistenceHandler.SHARED_PERSISTENCE_HANDLER
					+ "'"
					+ " system='d3web'"
					+ ">");
			sb.append(DCMarkupWriter.getInstance().getXMLString(kb.getDCMarkup()));

			Collection kslices = kb.getAllKnowledgeSlices();

			/// System.out.println(kslices);

			double unkSim = 0.1;

			Collection knowledge = kb.getAllKnowledgeSlicesFor(PSMethodShared.class);
			if ((knowledge != null) && !knowledge.isEmpty()) {
				Iterator iter = knowledge.iterator();
				boolean found = false;
				while (!found && iter.hasNext()) {
					Object o = iter.next();
					if (o instanceof KnowledgeBaseUnknownSimilarity) {
						KnowledgeBaseUnknownSimilarity knus = (KnowledgeBaseUnknownSimilarity) o;
						unkSim = knus.getSimilarity();
						found = true;
					}
				}
			}

			sb.append("<globalUnknownSimilarity value='" + unkSim + "'/>\n");

			sb.append("<KnowledgeSlices>");

			Iterator iter = kslices.iterator();

			while (iter.hasNext()) {
				Object o = iter.next();
				if (o instanceof AbstractAbnormality) {
					AbstractAbnormality a = (AbstractAbnormality) o;
					sb.append(a.getXMLString());
				} else if (o instanceof Weight) {
					Weight w = (Weight) o;
					sb.append(w.getXMLString());
				} else if (o instanceof LocalWeight) {
					LocalWeight lw = (LocalWeight) o;
					sb.append(lw.getXMLString());
				}else if (o instanceof QuestionComparator) {
					QuestionComparator qc = (QuestionComparator) o;
					sb.append(qc.getXMLString());
				}

			}

			sb.append("</KnowledgeSlices>");
			sb.append("</KnowledgeBase>");

			// Jetzt noch rausschreiben...

			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			InputStream stream = new StringBufferInputStream(new StringBufferStream(sb));

			Document dom = builder.parse(stream);

			return dom;

		} catch (Exception x) {
			x.printStackTrace();
			return null;
		}
	}
}