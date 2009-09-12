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

package de.d3web.persistence.utilities;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * This is a special OutputStream encapsulating a StringBufferStream
 * Creation date: (02.10.2001 10:23:23)
 * @author Christian Betz
 */
public class StringBufferOutputStream extends OutputStream {
	private StringBufferStream sbs;
	private Charset charset;

	/**
	 * Creates a new StringBufferOutputStream with given StringBufferStream
	 */
	public StringBufferOutputStream(StringBufferStream theSBS) {
		this(theSBS, Charset.forName("ISO-8859-1"));
	}

	/**
	 * Creates a new StringBufferOutputStream with given StringBufferStream
	 * and the given Charset.
	 */
	public StringBufferOutputStream(StringBufferStream theSBS, Charset charset) {
		super();
		sbs = theSBS;
		this.charset = charset;
	}

	/**
	 * Writes the specified byte to this output stream. The general 
	 * contract for <code>write</code> is that one byte is written 
	 * to the output stream. The byte to be written is the eight 
	 * low-order bits of the argument <code>b</code>. The 24 
	 * high-order bits of <code>b</code> are ignored.
	 * <p>
	 * Subclasses of <code>OutputStream</code> must provide an 
	 * implementation for this method. 
	 *
	 * @param      b   the <code>byte</code>.
	 * @exception  IOException  if an I/O error occurs. In particular, 
	 *             an <code>IOException</code> may be thrown if the 
	 *             output stream has been closed.
	 */
	public void write(int b) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(1);
		bb.put((byte) b);
		bb.flip();
		CharBuffer cb = charset.decode(bb);
		if (cb.length() > 0) {
			sbs.getSb().append(cb.charAt(0));
		}
	}
	

}
