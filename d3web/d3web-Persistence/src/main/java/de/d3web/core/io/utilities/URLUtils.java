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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author gbuscher
 */
public class URLUtils {

	/**
	 * FilenameConversionException
	 * 
	 * @author Chris 06.04.2005
	 */
	public static class FilenameConversionException extends RuntimeException {

		private static final long serialVersionUID = -1003777396080211319L;

		/**
		 * 
		 */
		public FilenameConversionException() {
			super();
		}

		/**
		 * @param arg0
		 */
		public FilenameConversionException(String arg0) {
			super(arg0);
		}

		/**
		 * @param arg0
		 * @param arg1
		 */
		public FilenameConversionException(String arg0, Throwable arg1) {
			super(arg0, arg1);
		}

		/**
		 * @param arg0
		 */
		public FilenameConversionException(Throwable arg0) {
			super(arg0);
		}

	}

	/**
	 * Returns an InputStream for the given URL without using any cache.
	 */
	public static InputStream openStream(URL url) throws IOException {
		URLConnection connection = url.openConnection();
		connection.setUseCaches(false);
		return connection.getInputStream();
	}

	/**
	 * @param url
	 * @return
	 */
	public static File getFile(URL url) {
		try {
			// absolute paths only!
			return new File(url.toURI());
		}
		catch (URISyntaxException e) {
			return new File(url.getPath());
			// throw new FilenameConversionException(e);
		}
		catch (IllegalArgumentException x) {
			// relative paths...
			return new File(getJarFileLocation(url));
		}
	}

	/**
	 * If the given location is a "jar:"-location, a cleaned file-location will
	 * be returned. Example: "jar:file://xyz.jar!/" is going to be
	 * "file://xyz.jar".
	 */
	private static String getJarFileLocation(URL url) {
		String PRAEFIX = "jar:";
		String location = url.getProtocol() + ":" + url.getPath();
		try {
			if (location.startsWith(PRAEFIX)) {
				location = location.substring(PRAEFIX.length());
				while ((location.startsWith("/")) || (location.startsWith("\\"))) {
					location = location.substring(1);
				}
				location = location.substring(0, location.indexOf('!'));
				return new URL(location).getPath();
			}
		}
		catch (MalformedURLException ex) {
		}
		return url.getPath();
	}

}
