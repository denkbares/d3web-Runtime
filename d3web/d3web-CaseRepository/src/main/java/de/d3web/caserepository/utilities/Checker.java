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
 * Created on 26.11.2003
 */
package de.d3web.caserepository.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;

/**
 * 26.11.2003 16:20:17
 * 
 * @author hoernlein
 */
public class Checker {

	public static boolean checkForOldFormat(Object o) throws IOException {
		if (o instanceof InputStream)
		// [FIXME]:marty:the problem is that the InputStream would need to be
		// reset ...
		// so, we just assume, that anyone who packages cases into a zipped file
		// uses
		// the new format, and knows what he's doing ...
		// return new Checker().checkForOldFormat(new BufferedReader(new
		// InputStreamReader((InputStream) o)));
		return false;
		else if (o instanceof File) return new Checker().checkForOldFormat(new BufferedReader(
				new FileReader((File) o)));
		else if (o instanceof String) return new Checker().checkForOldFormat(new BufferedReader(
				new StringReader((String) o)));
		else if (o instanceof URL) return checkForOldFormat(((URL) o).openStream());
		throw new IOException("can't handle objects of type " + o.getClass());
	}

	private boolean inSolution = false;
	private boolean inUser = false;
	private boolean hasUser = false;
	private boolean inSystem = false;
	private boolean hasSystem = false;

	private boolean checkForOldFormat(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		while (line != null) {
			if (line.indexOf("<Solution ") != -1 && !inSolution) inSolution = true;
			if (line.indexOf("<User>") != -1 && !inUser) inUser = true;
			if (line.indexOf("</User>") != -1 && inUser) hasUser = true;
			if (line.indexOf("<System ") != -1 && !inSystem) inSystem = true;
			if (line.indexOf("</System>") != -1 && inSystem) hasSystem = true;
			if (line.indexOf("</Solution>") != -1 && inSolution) {
				inSolution = false;
			}

			if (hasUser && hasSystem) return true;

			line = reader.readLine();
		}
		return false;
	}

}