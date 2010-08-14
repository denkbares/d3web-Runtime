package de.d3web.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

public class JPFResource implements Resource {

	private final URL url;
	private final String relativePath;

	public JPFResource(URL url, String relativePath) {
		this.url = url;
		this.relativePath = relativePath;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return url.openStream();
	}

	@Override
	public String getPathName() {
		return relativePath;
	}

	@Override
	public long getSize() {
		int size = -1;
		try {
			URLConnection connection = url.openConnection();
			size = connection.getContentLength();
		}
		catch (IOException e) {
			Logger.getLogger(getClass().getName()).warning(
					"cannot open resource to determine content size: " + e);
		}
		return size;
	}

}
