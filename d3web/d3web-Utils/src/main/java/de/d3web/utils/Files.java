package de.d3web.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

public class Files {

	/**
	 * Create a new temporary directory. Use {@link #recursiveDelete(File)} to
	 * clean this directory up since it isn't deleted automatically
	 * 
	 * @return the new directory
	 * @throws IOException if there is an error creating the temporary directory
	 */
	public static File createTempDir() throws IOException {
		final File sysTempDir = new File(System.getProperty("java.io.tmpdir"));
		File newTempDir;
		final int maxAttempts = 100;
		int attemptCount = 0;
		do {
			attemptCount++;
			if (attemptCount > maxAttempts) {
				throw new IOException(
						"The highly improbable has occurred! Failed to " +
								"create a unique temporary directory after " +
								maxAttempts + " attempts.");
			}
			String dirName = UUID.randomUUID().toString();
			newTempDir = new File(sysTempDir, dirName);
		} while (newTempDir.exists());

		if (newTempDir.mkdirs()) {
			return newTempDir;
		}
		else {
			throw new IOException(
					"Failed to create temp dir named " +
							newTempDir.getAbsolutePath());
		}
	}

	/**
	 * Recursively delete file or directory
	 * 
	 * @param fileOrDir the file or dir to delete
	 * @return true iff all files are successfully deleted
	 */
	public static boolean recursiveDelete(File fileOrDir) {
		if (fileOrDir.isDirectory()) {
			// recursively delete contents
			for (File innerFile : fileOrDir.listFiles()) {
				if (!recursiveDelete(innerFile)) {
					return false;
				}
			}
		}

		return fileOrDir.delete();
	}

	/**
	 * Returns the contents of the specified file as a byte[].
	 * 
	 * @created 01.10.2013
	 * @param input the input file to read from
	 * @return the content of the file
	 * @throws IOException if the specified file cannot be read completely
	 */
	public static byte[] getBytes(File file) throws IOException {
		return Streams.getBytesAndClose(new FileInputStream(file));
	}

	/**
	 * Returns the contents of the specified file as a String.
	 * 
	 * @created 01.10.2013
	 * @param input the input file to read from
	 * @return the content of the file
	 * @throws IOException if the specified file cannot be read completely
	 */
	public static String getText(File file) throws IOException {
		return Streams.getTextAndClose(new FileInputStream(file));
	}
}
