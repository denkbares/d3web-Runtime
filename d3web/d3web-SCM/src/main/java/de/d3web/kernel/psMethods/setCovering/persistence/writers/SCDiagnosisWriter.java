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

import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.persistence.xml.writers.IXMLWriter;

/**
 * This is the writer class for SCDiagnosis objects
 * 
 * @author bates
 */
public class SCDiagnosisWriter implements IXMLWriter {

	public static final Class ID = SCDiagnosisWriter.class;

	private static SCDiagnosisWriter instance = null;

	private SCDiagnosisWriter() {
	}

	public static SCDiagnosisWriter getInstance() {
		if (instance == null) {
			instance = new SCDiagnosisWriter();
		}
		return instance;
	}

	public String getXMLString(Object o) {
		if (o instanceof SCDiagnosis) {
			SCDiagnosis scDiagnosis = (SCDiagnosis) o;
			StringBuffer sb = new StringBuffer();
			sb.append("<SCNode type='SCDiagnosis' " + "apriori='"
					+ scDiagnosis.getAprioriProbability() + "' >\n");
			sb.append("<Diagnosis id='" + scDiagnosis.getNamedObject().getId() + "' " + " />\n");
			sb.append("</SCNode>\n");
			return sb.toString();
		}
		return null;
	}

}
