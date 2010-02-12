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

package de.d3web.core.io.utilities;
/**
 * Special stream for StringBuffers. It can be used with StringBufferInputStream or StringBufferOutputStream
 * Creation date: (02.10.2001 10:22:58)
 * 
 * @author Christian Betz
 * @see de.d3web.core.io.utilities.StringBufferInputStream
 * @see de.d3web.core.io.utilities.StringBufferOutputStream
 */
public class StringBufferStream {
	private StringBuffer sb;

	/**
	 * Creates a new StringBufferStream with empty internal StringBuffer
	 */
	public StringBufferStream() {
		super();
		sb = new StringBuffer();
	}

	/**
	 * Creates a new StringBufferStream filled with given StringBuffer
	 */
	public StringBufferStream(StringBuffer theStringBuffer) {
		super();
		sb = theStringBuffer;
	}

	/**
	 * Creation date: (02.10.2001 10:29:36)
	 * @return internal StringBuffer
	 */
	java.lang.StringBuffer getSb() {
		return sb;
	}
}