package de.d3web.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public class Files {

	private static final int TEMP_DIR_ATTEMPTS = 1000;

	/**
	 * Create a new temporary directory. Use {@link #recursiveDelete(File)} to
	 * clean this directory up since it isn't deleted automatically
	 * 
	 * @return the new directory
	 * @throws IOException if there is an error creating the temporary directory
	 */
	public static File createTempDir() throws IOException {
		File baseDir = new File(System.getProperty("java.io.tmpdir"));
		baseDir.mkdirs();
		if (!baseDir.isDirectory()) {
			throw new IOException("Failed to access temp directory");
		}
		String baseName = System.currentTimeMillis() + "-";

		for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
			File tempDir = new File(baseDir, baseName + counter);
			if (tempDir.mkdir()) {
				return tempDir;
			}
		}
		throw new IOException("Failed to create temp directory");

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
	 * @param file the input file to read from
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
	 * @param file the input file to read from
	 * @return the content of the file
	 * @throws IOException if the specified file cannot be read completely
	 */
	public static String getText(File file) throws IOException {
		return Streams.getTextAndClose(new FileInputStream(file));
	}

	/**
	 * Returns the file extension without the leading ".". If the file has no
	 * ".", the empty String is returned. if the file is null, null is returned.
	 * 
	 * @created 15.02.2014
	 * @param filename the file to get the extension from
	 * @return the extension of the specified file
	 */
	public static String getExtension(String filename) {
		return getExtension(new File(filename));
	}

	/**
	 * Returns the file extension without the leading ".". If the file has no
	 * ".", the empty String is returned. if the file is null, null is returned.
	 * 
	 * @created 15.02.2014
	 * @param file the file to get the extension from
	 * @return the extension of the specified file
	 */
	public static String getExtension(File file) {
		if (file == null) return null;

		String name = file.getName();
		int index = name.lastIndexOf('.');
		if (index == -1) return "";

		return name.substring(index + 1);
	}

	/**
	 * Recursively gets all files matching the specified {@link FileFilter}.
	 * If no filter is specified, all files recursively contained in the specified
	 * directory are returned.
	 *
	 * @param root the root directory
	 * @param filter filters the files
	 * @return all files matching the specified filter in the specified directory (recursively)
	 */
	public static Collection<File> recursiveGet(File root, FileFilter filter) {
		Collection<File> files = new LinkedList<File>();
		File[] list = root.listFiles();
		for (File f : list) {
			if (f.isDirectory()) {
				files.addAll(recursiveGet(f, filter));
			} else if (filter == null || filter.accept(f)) {
				files.add(f);
			}
		}
		return files;
	}

}
