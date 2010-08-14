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

package de.d3web.core.io.utilities;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.xml.sax.InputSource;

/**
 * This is a helper-class that provides methods for filtering special characters
 * from given XML-code to make it readable for XML-parsers Creation date:
 * (07.02.2002 13:43:00)
 * 
 * @author Christian Betz
 */
public class InputFilter {

	/**
	 * Creates a new InputFilter
	 */
	public InputFilter() {
		super();
	}

	/**
	 * Filters out non-parseable characters so that an XML-parser is able to
	 * read the XML-code properly currently filtered: \t Creation date:
	 * (03.10.2001 15:47:44)
	 * 
	 * @return the filtered XML-code
	 * @param stg XML-code to filter
	 */
	public static String filterString(String stg) {
		StringBuffer linebuf = new StringBuffer(stg);
		StringBuffer outbuf = new StringBuffer();

		for (int i = 0; i < linebuf.length(); ++i) {
			char c = linebuf.charAt(i);
			if (c != '\t') {
				outbuf.append(c);
			}
		}

		return outbuf.toString();
	}

	/**
	 * Generates an InputSource that contains filtered XML-code Creation date:
	 * (03.10.2001 15:47:44)
	 * 
	 * @return an InputSource containing the filtered XML-code
	 * @param url pointing on the source containing XML-code to filter
	 */
	public static InputSource getFilteredInputSource(URL url) throws IOException {
		InputStream in = null;
		try {

			in = new BufferedInputStream(URLUtils.openStream(url));

			ByteArrayOutputStream cache = new ByteArrayOutputStream();
			int next;
			while ((next = in.read()) >= 0) {
				cache.write(next);
			}
			in.close();

			return new InputSource(new ByteArrayInputStream(cache.toByteArray()));

		}
		finally {
			if (in != null) {
				in.close();
			}
		}

	}
}