package de.d3web.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
			//noinspection ConstantConditions
			for (File innerFile : fileOrDir.listFiles()) {
				if (!recursiveDelete(innerFile)) {
					return false;
				}
			}
		}
		return fileOrDir.delete();
	}

	/**
	 * Copies the source file to the target file. If the target file already exists it will be
	 * overwritten. If any of the specified files denote a folder, an IOException is thrown. If the
	 * path of the target file does not exists, the required parent folders will be created.
	 *
	 * @param source the source file to read from
	 * @param target the target file
	 * @throws IOException if the file cannot be copied
	 */
	public static void copy(File source, File target) throws IOException {
		FileInputStream in = new FileInputStream(source);
		target.getAbsoluteFile().getParentFile().mkdirs();
		FileOutputStream out = new FileOutputStream(target);
		Streams.streamAndClose(in, out);
	}

	/**
	 * Checks if two files have the same fingerprint, including the timestamp, so it appears that
	 * they have the same content, without fully reading the files contents! This method is much
	 * quicker that {#hasEqualContent} but you cannot be sure if the content really differs if the
	 * method return true.
	 * <p/>
	 * Returns true if both files exists, both denote a file (not a directory), and the file seems
	 * to be identical.
	 *
	 * @param file1 the first file to compare
	 * @param file2 the second file to compare
	 * @return if both files seems to be identical
	 */
	@SuppressWarnings("RedundantIfStatement")
	public static boolean hasEqualFingerprint(File file1, File file2) throws IOException {
		if (!file1.isFile()) return false;
		if (!file2.isFile()) return false;
		if (file1.length() != file2.length()) return false;
		if (file1.lastModified() != file2.lastModified()) return false;
		return true;
	}

	/**
	 * Checks if two files have the same content. Returns true if both files exists, both denote a
	 * file (not a directory), and the bytes of each file are identical.
	 *
	 * @param file1 the first file to compare
	 * @param file2 the second file to compare
	 * @return if both files have the same content
	 */
	public static boolean hasEqualContent(File file1, File file2) throws IOException {
		if (!file1.isFile()) return false;
		if (!file2.isFile()) return false;
		if (file1.length() != file2.length()) return false;

		FileInputStream in1 = null, in2 = null;
		try {
			in1 = new FileInputStream(file1);
			in2 = new FileInputStream(file2);
			while (true) {
				int byte1 = in1.read();
				int byte2 = in2.read();
				if (byte1 != byte2) return false;
				if (byte1 == -1) return true;
			}
		}
		finally {
			if (in1 != null) in1.close();
			if (in2 != null) in2.close();
		}
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
		if (list != null) for (File f : list) {
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
