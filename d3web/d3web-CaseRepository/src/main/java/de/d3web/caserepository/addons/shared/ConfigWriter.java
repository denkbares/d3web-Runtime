/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

/*
 * Created on 24.11.2003
 */
package de.d3web.caserepository.addons.shared;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter;
import de.d3web.config.Config;

/**
 * 24.11.2003 11:40:42
 * 
 * @author hoernlein
 */
public class ConfigWriter implements CaseObjectListAdditionalWriter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter#getTag()
	 */
	public String getTag() {
		return "_Config";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter#getXMLCode
	 * (de.d3web.caserepository.CaseObject)
	 */
	public String getXMLCode(CaseObject object) {
		StringBuffer sb = new StringBuffer();
		Config c = object.getConfig();
		if (c != null) de.d3web.config.persistence.ConfigWriter.write(c, sb);
		return sb.toString();
	}

}
