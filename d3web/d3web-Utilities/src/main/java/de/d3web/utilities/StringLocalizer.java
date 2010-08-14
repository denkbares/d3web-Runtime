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

package de.d3web.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class StringLocalizer {

	public static String[] getLocalizedStrings(ResourceBundle bundle, String prefix, String[] strings) {
		List<String> result = new ArrayList<String>();
		for (String string : strings) {
			result.add(getLocalizedString(bundle, prefix, string));
		}
		return result.toArray(new String[0]);
	}

	public static String getLocalizedString(ResourceBundle bundle, String prefix, String string) {
		try {
			return bundle.getString(prefix + string);
		}
		catch (Exception e) {
			return string;
		}
	}

}
