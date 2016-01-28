package de.d3web.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;

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
	 * <p>
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

	/**
	 * Returns the lines of the specified file as a list of Strings.
	 *
	 * @param file the input file to read from
	 * @return the lines of the file
	 * @throws IOException if the specified file cannot be read completely
	 * @created 01.10.2013
	 */
	public static List<String> getLines(File file) throws IOException {
		Reader reader = new FileReader(file);
		try {
			return getLines(reader);
		}
		finally {
			reader.close();
		}
	}

	/**
	 * Reads a properties file into a newly created Properties objects and returns the Properties
	 * objects.
	 *
	 * @param file the properties file to be read
	 * @return the loaded file content
	 * @throws IOException if the file cannot be loaded or the file's content cannot be parsed
	 */
	public static Properties getProperties(File file) throws IOException {
		Properties properties = new Properties();
		InputStream in = new FileInputStream(file);
		try {
			properties.load(in);
		}
		finally {
			in.close();
		}
		return properties;
	}

	/**
	 * Reads and rewrites the a properties file, adding one entry, overwriting all existing entries
	 * with the specified key. It preserves all other lines, including comments and the order of the
	 * lines. Only the lines with the specified key will be modified, where the first one is
	 * overwritten, and succeeding ones (if there are any) will be deleted. If there is no such line
	 * contained, the new property will be appended to the end of the file.
	 *
	 * @param file the properties file to be updated
	 * @param key the key to be overwritten or added
	 * @param value the (new) value for the key
	 * @throws IOException if the properties file could not been read or written
	 */
	public static void updatePropertiesFile(File file, String key, String value) throws IOException {
		updatePropertiesFile(file, Collections.singletonMap(key, value));
	}

	/**
	 * Reads and rewrites the a properties file, adding the specified entries, overwriting all
	 * existing entries that have one of the specified keys. It preserves all other lines, including
	 * comments and the order of the lines. Only the lines with the specified key will be modified
	 * (preserving their order), where the first one is overwritten, and succeeding ones (if there
	 * are any) will be deleted. If there are no such lines contained for some of the specified
	 * entries, the remaining entries will be appended to the end of the file.
	 * <p>
	 * The method is also capable to delete entries, if the key occurs in the specified entries with
	 * value null.
	 *
	 * @param file the properties file to be updated
	 * @param entries the keys to be overwritten with their (new) values
	 * @throws IOException if the properties file could not been read or written
	 */
	public static void updatePropertiesFile(File file, Map<String, String> entries) throws IOException {
		// create well-encoded lines to be added;
		// preserve null to mark lines to be deleted
		Map<String, String> linesToAdd = new HashMap<>();
		for (Entry<String, String> entry : entries.entrySet()) {
			String newLine = null;
			if (entry.getValue() != null) {
				Properties newProperty = new Properties();
				newProperty.put(entry.getKey(), entry.getValue());
				StringWriter newLineBuffer = new StringWriter();
				newProperty.store(newLineBuffer, null);
				newLine = newLineBuffer.toString().replaceAll("(?m)^#.*$[\n\r]*", "").trim();
			}
			linesToAdd.put(entry.getKey(), newLine);
		}

		List<String> lines;
		if (file.exists()) {
			// read the properties file and iterate each line,
			// preserving comments and order
			lines = getLines(file);
			ListIterator<String> lineIterator = lines.listIterator();
			while (lineIterator.hasNext()) {
				String line = lineIterator.next();
				// parse each line as a property
				Properties parsedLine = new Properties();
				parsedLine.load(new StringReader(line));

				// if the lines specifies the key, it will be overwritten
				if (!parsedLine.isEmpty()) {
					String key = (String) parsedLine.keySet().iterator().next();
					if (linesToAdd.containsKey(key)) {
						String newLine = linesToAdd.get(key);
						if (newLine == null) {
							// if already replaced one line with the key,
							// remove duplicate lines with same key
							lineIterator.remove();
						}
						else {
							// overwrite the first line with the specified key
							lineIterator.set(newLine);
							// and remove the line to mark it as added
							linesToAdd.remove(key);
						}
					}
				}
			}

			// we append the new lines at the end, for all not inserted lines to add
			linesToAdd.values().stream().filter(Objects::nonNull).forEach(lines::add);
		}
		else {
			// Create a new empty list of lines
			lines = new ArrayList<>(linesToAdd.values());
		}

		// and finally write the lines back to disc
		try (FileOutputStream out = new FileOutputStream(file)) {
			out.write(Strings.concat("\n", lines).getBytes());
		}
	}

	/**
	 * Returns the lines of the specified file as a list of Strings.
	 *
	 * @param reader the input file to read from
	 * @return the lines of the file
	 * @throws IOException if the specified file cannot be read completely
	 * @created 15.12.2014
	 */
	public static List<String> getLines(Reader reader) throws IOException {
		BufferedReader br = new BufferedReader(reader);
		List<String> result = new LinkedList<String>();
		String line;
		while ((line = br.readLine()) != null) {
			result.add(line);
		}
		return result;
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
		if (filename == null) return null;
		int index = filename.lastIndexOf('.');
		if (index == -1) return "";
		return filename.substring(index + 1);
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
		return getExtension(file.getName());
	}

	/**
	 * Returns true if the specified file has one of the specified file extensions. The extensions
	 * are tested case insensitive. The specified extension must contain only the characters after
	 * the separating ".", not the "." itself. The characters are compared case insensitive.
	 *
	 * @param fileName the abstract path of the file to be tested
	 * @param extensions the extensions to be tested for
	 * @return if the file has any of the specified extensions
	 */
	public static boolean hasExtension(String fileName, String... extensions) {
		if (fileName == null) return false;
		if (extensions == null) return false;
		for (String extension : extensions) {
			if (extension == null) continue;
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
	 * are tested case insensitive. The specified extension must contain only the characters after
	 * the separating ".", not the "." itself. The characters are compared case insensitive.
	 *
	 * @param file the file to be tested
	 * @param extensions the extensions to be tested for
	 * @return if the file has any of the specified extensions
	 * @throws NullPointerException if the array of extensions is null or if any of the contained
	 * extension is null
	 */
	public static boolean hasExtension(File file, String... extensions) {
		return file != null && hasExtension(file.getName(), extensions);
	}

	/**
	 * Returns the file path without its extension and without the "." before the extension. If the
	 * file has no ".", the original file path is returned. if the file is null, null is returned.
	 *
	 * @param filename the file to remove the extension from
	 * @return the path of the specified file without the extension
	 * @created 15.02.2014
	 */
	public static String stripExtension(String filename) {
		if (filename == null) return null;
		int index = filename.lastIndexOf('.');
		if (index == -1) return filename;
		return filename.substring(0, index);
	}

	/**
	 * Returns the file path without its extension and without the "." before the extension. If the
	 * file has no ".", the original file path is returned. if the file is null, null is returned.
	 *
	 * @param file the file to remove the extension from
	 * @return the path of the specified file without the extension
	 * @created 15.02.2014
	 */
	public static String stripExtension(File file) {
		if (file == null) return null;
		return stripExtension(file.getPath());
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
