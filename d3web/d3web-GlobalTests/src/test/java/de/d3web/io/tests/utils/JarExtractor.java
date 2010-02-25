package de.d3web.io.tests.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Peter Klügl
 * 
 *         Helper class to extract files of a JAR/Zip archive
 */
public class JarExtractor {

	/**
	 * extracts the file of a given url to the given directory
	 * 
	 * @param url
	 *            file
	 * @param dir
	 *            destination
	 * @throws IOException 
	 * @throws  
	 */
	public static void extract(File zipFile, File dir) throws IOException {
		/** For a given Zip file, process each entry. */
		if (!zipFile.exists()) {
			throw new RuntimeException("File not found: " + zipFile.getAbsolutePath());
		}
		URI uri = dir.toURI();
		ZipFile zippy = new ZipFile(zipFile);
		Enumeration<? extends ZipEntry> all = zippy.entries();
		while (all.hasMoreElements()) {
			extractSingleFile(zippy, uri,  (all.nextElement()));
		}

	}

	/**
	 * Process one file from the zip, given its name. create the file on disk.
	 */
	private static void extractSingleFile(ZipFile zipFile, URI baseURI,
			ZipEntry entry) throws IOException {
		String zipName = entry.getName();
		zipName = URLEncoder.encode(zipName, "UTF-8");

		byte[] b = new byte[8092];

		URI fileURI = URI.create(zipName);
		URI uri = baseURI.resolve(fileURI);

		File outputFile = new File(URLDecoder.decode(uri.getPath(), "UTF-8"));

		// double-check that the file is in the zip
		// if a directory, mkdir it (remember to
		// create intervening subdirectories if needed!)

		if (entry.isDirectory()) {
			outputFile.mkdirs();
			return;
		}

		// Else must be a file; open the file for output

		// all parents there?
		File parent = outputFile.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}

		FileOutputStream os = new FileOutputStream(outputFile);
		InputStream is = zipFile.getInputStream(entry);
		int n = 0;
		while ((n = is.read(b)) > 0)
			os.write(b, 0, n);
		is.close();
		os.close();

	}

}
