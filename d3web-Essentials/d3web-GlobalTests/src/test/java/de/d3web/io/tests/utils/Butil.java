/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.io.tests.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Butil {

	/**
	 * Reads from a Reader with UTF8 encoding.
	 * 
	 * @param org
	 * @return
	 */
	public static String readString(InputStream s) {
		StringBuilder buffi = new StringBuilder();
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(s, "UTF8"));
			String b = r.readLine();
			while (b != null) {
				buffi.append(b);
				b = r.readLine();
			}
			return buffi.toString();
		}
		catch (IOException e) {
			// Nothing
		}
		return null;
	}
}
