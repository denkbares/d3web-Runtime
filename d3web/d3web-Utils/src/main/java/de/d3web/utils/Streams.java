package de.d3web.utils;

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
}
