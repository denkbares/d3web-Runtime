package de.d3web.persistence.utilities;

import java.io.IOException;

/**
 * This is a special InputStream encapsulating a StringBufferStream
 * Creation date: (02.10.2001 10:23:49)
 * @author Christian Betz
 */
public class StringBufferInputStream extends java.io.InputStream {
	private int readPosition = 0;
	private StringBufferStream sbs;

	/**
	 * Creates a new StringBufferInputStream with the given StringBufferStream
	 */
	public StringBufferInputStream(StringBufferStream theSBS) {
		super();
		sbs = theSBS;
	}

	/**
	 * Reads the next byte of data from the input stream. The value byte is
	 * returned as an <code>int</code> in the range <code>0</code> to
	 * <code>255</code>. If no byte is available because the end of the stream
	 * has been reached, the value <code>-1</code> is returned. This method
	 * blocks until input data is available, the end of the stream is detected,
	 * or an exception is thrown.
	 *
	 * <p> A subclass must provide an implementation of this method.
	 *
	 * @return     the next byte of data, or <code>-1</code> if the end of the
	 *             stream is reached.
	 * @exception  IOException  if an I/O error occurs.
	 */
	public int read() throws IOException {
		if (readPosition >= sbs.getSb().length()) {
			return -1;
		}
		return sbs.getSb().charAt(readPosition++);
	}
}