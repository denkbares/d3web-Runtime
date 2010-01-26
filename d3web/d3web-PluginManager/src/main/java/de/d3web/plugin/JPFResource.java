package de.d3web.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import org.java.plugin.PathResolver;
import org.java.plugin.registry.Library;

public class JPFResource implements Resource {

	private final PathResolver pathResolver;
	private final Library library;
	
	public JPFResource(PathResolver pathResolver, Library library) {
		this.pathResolver = pathResolver;
		this.library = library;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		URL url = pathResolver.resolvePath(library, library.getPath());
		return url.openStream();
	}

	@Override
	public String getPathName() {
		return library.getPath();
	}

	@Override
	public long getSize() {
		URL url = pathResolver.resolvePath(library, library.getPath());
		int size  = -1;
		try {
			URLConnection connection = url.openConnection();
			size = connection.getContentLength();
		}
		catch (IOException e) {
			Logger.getLogger(getClass().getName()).warning("cannot open resource to determine content size: " + e);
		}
		return size;
	}

}
