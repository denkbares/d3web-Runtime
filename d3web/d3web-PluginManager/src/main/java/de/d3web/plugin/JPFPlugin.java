package de.d3web.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.java.plugin.PluginManager;
import org.java.plugin.registry.PluginDescriptor;

public class JPFPlugin implements Plugin {

	private final PluginManager manager;
	private final PluginDescriptor descriptor;

	public JPFPlugin(PluginManager manager, PluginDescriptor descriptor) {
		this.manager = manager;
		this.descriptor = descriptor;
	}

	@Override
	public String getPluginID() {
		return this.descriptor.getId();
	}

	@Override
	public Resource[] getResources() {
		Collection<Resource> result = new LinkedList<Resource>();
//		PathResolver pathResolver = manager.getPathResolver();
//		for (Library library : this.descriptor.getLibraries()) {
//			result.add(new JPFResource(pathResolver.get, library));
//		}
		result = getResourcePaths(this.descriptor.getLocation());
		return result.toArray(new Resource[result.size()]);
	}

	private Collection<Resource> getResourcePaths(URL pluginUrl) {
		
		Collection<Resource> result = new LinkedList<Resource>();
		try {
			File pluginFile = new File(pluginUrl.toURI().getPath()).getParentFile();
	
			if (!pluginFile.exists()) {
				throw new IllegalStateException(
						"Invalid plugin access due to internal error. Cannot find plugin location");
			}

			if (pluginFile.isDirectory()) {
				File[] files = pluginFile.listFiles();
				for (File file : files) {
					String relativePath = file.getCanonicalPath().substring(pluginFile.getCanonicalPath().length()+1).replace(File.separatorChar, '/');
					result.add(new JPFResource(file.toURI().toURL(), relativePath));
				}
			}
			else {
				ZipFile zipfile = new ZipFile(pluginFile);
				try {
					Enumeration<? extends ZipEntry> entries = zipfile.entries();
					while (entries.hasMoreElements()) {
						ZipEntry entry = entries.nextElement();
						result.add(new JPFResource(new URL(pluginUrl, entry.getName()), entry.getName()));
					}
				}
				finally {
					if (zipfile != null) zipfile.close();
				}
			}
		}
		catch (IOException e) {
			Logger.getLogger(getClass().getName()).warning("cannot read resources from plugin '"+pluginUrl+"': "+e);
		}
		catch (URISyntaxException e) {
			Logger.getLogger(getClass().getName()).warning("cannot read resources from plugin '"+pluginUrl+"': "+e);
		}
		return result;
	}
}
