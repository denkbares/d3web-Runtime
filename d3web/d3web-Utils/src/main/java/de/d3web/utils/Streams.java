package de.d3web.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility class for stream handling
 * 
 * @author Volker Belli (denkbares GmbH)
 * @created 09.09.2013
 */
public class Streams {

	/**
	 * Streams the specified inputStream to the specified outputStream and
	 * returns after the stream has completely been written. Before the method
	 * returns, both stream will be closed.
	 * 
	 * @created 09.09.2013
	 * @param inputStream the source stream to read the data from
	 * @param outputStream the target stream to write the data to
	 * @throws IOException if any of the streams has an error
	 */
	public static void streamAndClose(InputStream inputStream, OutputStream outputStream) throws IOException {
		try {
			Streams.stream(inputStream, outputStream);
		}
		finally {
			inputStream.close();
			outputStream.close();
		}
	}

	/**
	 * Streams the specified inputStream to the specified outputStream and
	 * returns after the stream has completely been written.
	 * 
	 * @created 09.09.2013
	 * @param inputStream the source stream to read the data from
	 * @param outputStream the target stream to write the data to
	 * @throws IOException if any of the streams has an error
	 */
	public static void stream(InputStream inputStream, OutputStream outputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
	}

	/**
	 * Creates a asynchronous streaming task from the specified source
	 * {@link InputStream} to the specified target {@link OutputStream}.
	 * 
	 * @created 27.04.2011
	 * @param inputStream the source stream
	 * @param outputStream the target stream
	 */
	public static void streamAsync(InputStream inputStream, OutputStream outputStream) {
		final InputStream in = inputStream;
		final OutputStream out = outputStream;
		Thread thread = new Thread("asynchronous streaming task") {

			@Override
			public void run() {
				try {
					stream(in, out);
				}
				catch (IOException e) {
					throw new IllegalStateException("unexpected error while piping streams", e);
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Returns the contents of the specified input stream as a byte[].
	 * 
	 * @created 01.10.2013
	 * @param input the input stream to read from
	 * @return the content of the stream
	 * @throws IOException if the specified streams cannot be read completely
	 */
	public static byte[] getBytes(InputStream input) throws IOException {
		ByteArrayOutputStream result = getContent(input);
		return result.toByteArray();
	}

	/**
	 * Returns the contents of the specified input stream as a byte[]. The
	 * method closes the specified stream before it returns the contents.
	 * 
	 * @created 01.10.2013
	 * @param input the input stream to read from
	 * @return the content of the stream
	 * @throws IOException if the specified streams cannot be read completely
	 */
	public static byte[] getBytesAndClose(InputStream input) throws IOException {
		try {
			return getBytes(input);
		}
		finally {
			input.close();
		}
	}

	/**
	 * Returns the contents of the specified input stream as a String.
	 * 
	 * @created 01.10.2013
	 * @param input the input stream to read from
	 * @return the content of the stream
	 * @throws IOException if the specified streams cannot be read completely
	 */
	public static String getText(InputStream input) throws IOException {
		ByteArrayOutputStream result = getContent(input);
		return result.toString();
	}

	/**
	 * Returns the contents of the specified input stream as a String. The
	 * method closes the specified stream before it returns the contents.
	 * 
	 * @created 01.10.2013
	 * @param input the input stream to read from
	 * @return the content of the stream
	 * @throws IOException if the specified streams cannot be read completely
	 */
	public static String getTextAndClose(InputStream input) throws IOException {
		try {
			return getText(input);
		}
		finally {
			input.close();
		}
	}

	private static ByteArrayOutputStream getContent(InputStream input) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream(input.available());
		stream(input, result);
		result.close();
		return result;
	}

}
