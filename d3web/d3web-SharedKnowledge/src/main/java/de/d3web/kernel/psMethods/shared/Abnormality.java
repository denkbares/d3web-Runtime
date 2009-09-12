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

package de.d3web.kernel.psMethods.shared;
import java.util.Enumeration;
import java.util.Hashtable;

import de.d3web.kernel.domainModel.Answer;
/**
 * Represents the abnormality of a symptom
 * Creation date: (06.08.2001 15:51:58)
 * @author: Norman Brümmer
 */
public class Abnormality extends AbstractAbnormality {

	private Hashtable values = new Hashtable();

	/**
	 * with this method you can add an answer-abnorm.Value pair
	 * Creation date: (06.08.2001 16:25:46)
	 * @param ans de.d3web.kernel.domainModel.Answer
	 * @param value double
	 */
	public void addValue(Answer ans, double value) {
		values.put(ans, new Double(value));
	}

	/**
	 * Returns the abnormality to the given answer
	 * Creation date: (06.08.2001 16:28:14)
	 * @return double
	 * @param ans de.d3web.kernel.domainModel.Answer
	 */
	public double getValue(Answer ans) {
		Double ret = (Double) values.get(ans);
		if (ret != null)
		{
			return ret.doubleValue();
		}

		return A0;
	}

	/**
	 * Returns the XML representation of this abnormality object
	 * Creation date: (09.08.2001 00:23:52)
	 * @return java.lang.String
	 */
	public String getXMLString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getXMLStringHeader());

		sb.append("<values>\n");
	
		Enumeration answers = values.keys();
		while (answers.hasMoreElements()) {
			Answer ans = (Answer) answers.nextElement();
			sb.append(
				"<abnormality ID='"
					+ ans.getId()
					+ "' value='"
					+ convertValueToConstantString(((Double) values.get(ans)).doubleValue())
					+ "'/>\n");
		}
		
		sb.append("</values>\n");

		sb.append(getXMLStringFooter());
		return sb.toString();
	}

}