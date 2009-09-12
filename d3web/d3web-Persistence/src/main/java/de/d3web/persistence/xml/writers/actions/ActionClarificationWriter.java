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

package de.d3web.persistence.xml.writers.actions;
import de.d3web.kernel.psMethods.nextQASet.ActionClarify;
import de.d3web.kernel.psMethods.nextQASet.ActionNextQASet;

/**
 * Generates the XML representation of an ActionClarification Object
 * @author Michael Scharvogel
 */
public class ActionClarificationWriter extends ActionNextQASetWriter {

	public static final Class ID = ActionClarify.class;

	protected String getTarget(ActionNextQASet anqas) {
		if(((ActionClarify) anqas).getTarget() == null) {
			return "<targetDiagnosis ID='"
			+ "'/>\n";
		} else 
			return "<targetDiagnosis ID='"
				+ ((ActionClarify) anqas).getTarget().getId()
				+ "'/>\n";
	}

	protected String getNextQASetType() {
		return "ActionClarify";
	}
}
