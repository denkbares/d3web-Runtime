package de.d3web.xml.utilities;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.logging.Logger;

import org.xml.sax.InputSource;

import de.d3web.persistence.utilities.URLUtils;

/**
 * This is a helper-class that provides methods for filtering special characters from given XML-code 
 * to make it readable for XML-parsers
 * Creation date: (07.02.2002 13:43:00)
 * @author Christian Betz
 */
public class InputFilter {

	/**
	 * Creates a new InputFilter
	 */
	public InputFilter() {
		super();
	}

	/**
	 * Filters out non-parseable characters so that an XML-parser is able to read the XML-code properly
	 * currently filtered: \t
	 * Creation date: (03.10.2001 15:47:44)
	 * @return the filtered XML-code
	 * @param stg XML-code to filter
	 */
	public static String filterString(String stg) {
		StringBuffer linebuf = new StringBuffer(stg);
		StringBuffer outbuf = new StringBuffer();

		for (int i = 0; i < linebuf.length(); ++i) {
			char c = linebuf.charAt(i);
			if (c != '\t') {
				outbuf.append(c);
			}
		}

		return outbuf.toString();
	}

	/**
	 * Generates an InputSource that contains filtered XML-code
	 * Creation date: (03.10.2001 15:47:44)
	 * @return an InputSource containing the filtered XML-code
	 * @param f file containing XML-code to filter
	 */
	public static InputSource getFilteredInputSource(File f) {
		BufferedReader br = null;
		try {

			StringBuffer sb = new StringBuffer();

			br = new BufferedReader(new FileReader(f));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(filterString(line));
			}

			StringReader stgReader = new StringReader(sb.toString());

			return new InputSource(stgReader);

		} catch (Exception x) {
			Logger.getLogger(InputSource.class.getName()).throwing(InputSource.class.getName(), "getFilteredInputSource(File)", x);
			return null;
		}finally{
			if (br != null){
				try {
					br.close();
				} catch (IOException e) {
					Logger.getLogger(InputSource.class.getName()).throwing(InputSource.class.getName(), "getFilteredInputSource(File)", e);
				}
			}
		}

	}

	/**
	 * Generates an InputSource that contains filtered XML-code
	 * Creation date: (03.10.2001 15:47:44)
	 * @return an InputSource containing the filtered XML-code
	 * @param url pointing on the source containing XML-code to filter
	 */
	public static InputSource getFilteredInputSource(URL url) throws IOException {
		InputStream in = null;
		try {

			in = new BufferedInputStream(URLUtils.openStream(url));
			
			ByteArrayOutputStream cache = new ByteArrayOutputStream();
			int next;
			while ((next = in.read()) >= 0) {
				cache.write(next);
			}
			in.close();

			return new InputSource(new ByteArrayInputStream(cache.toByteArray()));

		} catch (Exception x) {
            //x.printStackTrace();
			throw new IOException(x.getMessage());
		} finally {
			if (in != null) {
				in.close();
			}
		}

	}
}