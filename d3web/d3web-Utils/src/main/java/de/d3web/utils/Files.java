package de.d3web.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.d3web.collections.Matrix;
import de.d3web.strings.StringFragment;
import de.d3web.strings.Strings;

public class Files {

	private static final int TEMP_DIR_ATTEMPTS = 1000;

	/**
	 * Create a new temporary directory. Use {@link #recursiveDelete(File)} to clean this directory
	 * up since it isn't deleted automatically
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
	 * @param file the input file to read from
	 * @return the content of the file
	 * @throws IOException if the specified file cannot be read completely
	 * @created 01.10.2013
	 */
	public static byte[] getBytes(File file) throws IOException {
		return Streams.getBytesAndClose(new FileInputStream(file));
	}

	/**
	 * Returns the contents of the specified file as a String.
	 *
	 * @param file the input file to read from
	 * @return the content of the file
	 * @throws IOException if the specified file cannot be read completely
	 * @created 01.10.2013
	 */
	public static String getText(File file) throws IOException {
		return Streams.getTextAndClose(new FileInputStream(file));
	}

	public static List<String> getLines(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		try {
			List<String> result = new LinkedList<String>();
			String line;
			while ((line = br.readLine()) != null) {
				result.add(line);
			}
			return result;
		}
		finally {
			br.close();
		}
	}

	public static Matrix<String> getCSVCells(File file) throws IOException {
		return getCSVCells(file, ",");
	}

	public static Matrix<String> getCSVCells(File file, String splitSymbol) throws IOException {
		List<String> lines = getLines(file);
		Matrix<String> matrix = new Matrix<String>();
		int row = 0;
		for (String line : lines) {
			List<StringFragment> fragments = Strings.splitUnquoted(line, splitSymbol);
			int col = 0;
			for (StringFragment fragment : fragments) {
				String raw = fragment.getContent().trim();
				matrix.set(row, col, Strings.unquote(raw));
				col++;
			}
			row++;
		}
		return matrix;
	}

	/**
	 * Returns the file extension without the leading ".". If the file has no ".", the empty String
	 * is returned. if the file is null, null is returned.
	 *
	 * @param filename the file to get the extension from
	 * @return the extension of the specified file
	 * @created 15.02.2014
	 */
	public static String getExtension(String filename) {
		return getExtension(new File(filename));
	}

	/**
	 * Returns the file extension without the leading ".". If the file has no ".", the empty String
	 * is returned. if the file is null, null is returned.
	 *
	 * @param file the file to get the extension from
	 * @return the extension of the specified file
	 * @created 15.02.2014
	 */
	public static String getExtension(File file) {
		if (file == null) return null;

		String name = file.getName();
		int index = name.lastIndexOf('.');
		if (index == -1) return "";

		return name.substring(index + 1);
	}

	/**
	 * Returns true if the specified file has one of the specified file extensions. The extensions
	 * are tested case insensitive.
	 *
	 * @param fileName the abstract path of the file to be tested
	 * @param extensions the extensions to be tested for
	 * @return if the file has any of the specified extensions
	 */
	public static boolean hasExtension(String fileName, String... extensions) {
		for (String extension : extensions) {
			if (fileName.length() <= extension.length()) continue;
			if (Strings.endsWithIgnoreCase(fileName, extension)
					&& fileName.charAt(fileName.length() - extension.length() - 1) == '.') {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the specified file has one of the specified file extensions. The extensions
	 * are tested case insensitive.
	 *
	 * @param file the file to be tested
	 * @param extensions the extensions to be tested for
	 * @return if the file has any of the specified extensions
	 */
	public static boolean hasExtension(File file, String... extensions) {
		return hasExtension(file.getName(), extensions);
	}

	/**
	 * Recursively gets all files matching the specified {@link FileFilter}. If no filter is
	 * specified, all files recursively contained in the specified directory are returned.
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
			}
			else if (filter == null || filter.accept(f)) {
				files.add(f);
			}
		}
		return files;
	}
}
