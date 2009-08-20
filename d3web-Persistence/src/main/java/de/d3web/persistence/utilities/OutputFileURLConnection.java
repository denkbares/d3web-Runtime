package de.d3web.persistence.utilities;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URL;
import java.security.Permission;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import sun.net.www.MessageHeader;
import sun.net.www.ParseUtil;
import sun.net.www.URLConnection;

/**
 * This is a URLConnection that is adapted to the needs of JARWriter.
 */
public class OutputFileURLConnection extends URLConnection {

	static String CONTENT_LENGTH = "content-length";
	static String CONTENT_TYPE = "content-type";
	static String TEXT_HTML = "text/html";

	private String contentType;
	private InputStream is;
	private OutputStream os;
	private File file;
	private String filename;
	private boolean isDirectory;
	private boolean exists;
	private List files;
	private long length;
	private boolean initializedHeaders;
	private Permission permission;

	/**
	 * Creates a new OutputFileURLConnection
	 */
	public OutputFileURLConnection(URL url) {
		super(url);
		isDirectory = false;
		exists = false;
		length = 0L;
		initializedHeaders = false;
	}

	/**
	 * If not already connected, this method "connects" to the URL specified by the Constructor.
	 */
	public void connect() throws IOException {
		if (!connected) {
			file = URLUtils.getFile(url);
			filename = file.toString();
			isDirectory = file.isDirectory();
			if (isDirectory)
				files = Arrays.asList(file.list());
			connected = true;
		}
	}

	private void initializeHeaders() {
		try {
			connect();
			exists = file.exists();
		} catch (IOException ioexception) {
			Logger.getLogger(OutputFileURLConnection.class.getName()).throwing(OutputFileURLConnection.class.getName(), "initializeHeaders", ioexception);
		}
		if (!initializedHeaders || !exists) {
			length = file.length();
			if (!isDirectory) {
				FileNameMap filenamemap =
					java.net.URLConnection.getFileNameMap();
				contentType = filenamemap.getContentTypeFor(filename);
				if (contentType != null)
					properties.add(CONTENT_TYPE, contentType);
				properties.add(CONTENT_LENGTH, String.valueOf(length));
			} else {
				properties.add(CONTENT_LENGTH, TEXT_HTML);
			}
			initializedHeaders = true;
		}
	}

	/**
	 * @see java.net.URLConnection#getHeaderField(java.lang.String)
	 */
	public String getHeaderField(String s) {
		initializeHeaders();
		return super.getHeaderField(s);
	}

	/**
	 * @see java.net.URLConnection#getHeaderField(int)
	 */
	public String getHeaderField(int i) {
		initializeHeaders();
		return super.getHeaderField(i);
	}
	/**
	 * @see java.net.URLConnection#getContentLength()
	 */
	public int getContentLength() {
		initializeHeaders();
		return super.getContentLength();
	}

	/**
	 * @see java.net.URLConnection#getHeaderFieldKey(int)
	 */
	public String getHeaderFieldKey(int i) {
		initializeHeaders();
		return super.getHeaderFieldKey(i);
	}
	/**
	 * @return the connection´s properties
	 */
	public MessageHeader getProperties() {
		initializeHeaders();
		return super.getProperties();
	}

	/**
	 * @return an BufferedInputStream from specified URL
	 */
	public synchronized InputStream getInputStream() throws IOException {
		connect();
		if (is == null && !isDirectory) {
			is = new BufferedInputStream(new FileInputStream(filename));
		}
		return is;
	}

	/**
	 * @return a FIleOutputStream to specified URL
	 */
	public OutputStream getOutputStream() throws IOException {
		connect();
		if (os == null) {
			os = new FileOutputStream(filename);
		}
		return os;
	}

	/**
	 * @return a write-permission to the specified URL
	 */
	public Permission getPermission() throws IOException {
		if (permission == null) {
			String s = ParseUtil.decode(url.getPath());
			if (File.separatorChar == '/')
				permission = new FilePermission(s, "write");
			// give write permission
			else
				permission =
					new FilePermission(
						s.replace('/', File.separatorChar),
						"write");
		}
		return permission;
	}
}
