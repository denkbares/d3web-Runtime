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

package de.d3web.persistence.xml.writers;

import java.util.logging.Logger;

import de.d3web.kernel.domainModel.Num2ChoiceSchema;
/**
 * This writer generates an XML output for a given 
 * Num2ChoiceSchema.
 * @author baumeister
 *
 */
public class SchemaWriter implements IXMLWriter {
	
	public static final String ID = SchemaWriter.class.getName();

	/**
	 * @see AbstractXMLWriter#getXMLString(java.lang.Object)
	 */
	public String getXMLString(Object o) {
		StringBuffer sb = new StringBuffer();

		if ((o == null) || (!(o instanceof Num2ChoiceSchema))) {
			Logger.getLogger(this.getClass().getName()).warning("No schema given for " + o);
		} else {

			Num2ChoiceSchema schema = (Num2ChoiceSchema) o;

			sb.append(
				"<KnowledgeSlice ID='"
					+ schema.getId()
					+ "' type='Schema'>");
			sb.append(
				"\n<Question ID='"
					+ schema.getQuestion().getId()
					+ "'/>");
			sb.append(
				"\n<LeftClosedInterval value='"
					+ arrayToString(schema.getSchemaArray())
					+ "'/>");
			sb.append("\n</KnowledgeSlice>\n");
		}

		return sb.toString();
	}

	private static String arrayToString(Double[] array) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			if (i == 0)
				sb.append(array[i]);
			else
				sb.append(" " + array[i]);
		}
		return sb.toString();
	}
}
