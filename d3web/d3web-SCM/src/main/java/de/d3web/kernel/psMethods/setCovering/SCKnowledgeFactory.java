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

package de.d3web.kernel.psMethods.setCovering;

/**
 * This factory produces knowledge for SCRelations
 * 
 * @author bates
 */
public class SCKnowledgeFactory {

	/**
	 * creates an SCKnowledge-Object from the given type and value. This method
	 * will primary be used in SCMLoader
	 * 
	 * @param type
	 *            the knowledge type (e.g. score)
	 * @param value
	 *            its value as String
	 * @return the generated SCKnowledge-Object
	 */
	public static SCKnowledge createSCKnowledge(String type, String value) {
		SCKnowledge ret = null;
		if (type.equalsIgnoreCase("score")) {
			ret = new SCScore(Integer.parseInt(value));
		} else if (type.equalsIgnoreCase("probability")) {
			ret = SCProbability.getProbabilityBySymbol(value);
		} else if (type.equalsIgnoreCase("confirmationCategory")) {
			ret = SCProbability.getProbabilityBySymbol(value);
		}
		return ret;
	}

}
