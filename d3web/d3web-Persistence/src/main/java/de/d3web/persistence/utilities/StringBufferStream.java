package de.d3web.persistence.utilities;
/**
 * Special stream for StringBuffers. It can be used with StringBufferInputStream or StringBufferOutputStream
 * Creation date: (02.10.2001 10:22:58)
 * 
 * @author Christian Betz
 * @see de.d3web.persistence.utilities.StringBufferInputStream
 * @see de.d3web.persistence.utilities.StringBufferOutputStream
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