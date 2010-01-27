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
	
	// contains the exported resources of the plugin, initialized lazy
	private Resource[] resources = null;
	

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
		if (this.resources == null) {
			Collection<Resource> result = new LinkedList<Resource>();
			result = getResourcePaths(this.descriptor.getLocation());
			this.resources = result.toArray(new Resource[result.size()]);
		}
		return this.resources;
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
				Collection<File> files = new LinkedList<File>();
				collectFiles(pluginFile, files);
				int prefixLen = pluginFile.getCanonicalPath().length() + 1;
				for (File file : files) {
					String relativePath = file.getCanonicalPath().substring(prefixLen);
					relativePath = relativePath.replace(File.separatorChar, '/');
					if (matchesExports(relativePath)) {
						URL resourceUrl = file.toURI().toURL();
						result.add(new JPFResource(resourceUrl, relativePath));
					}
				}
			}
			else {
				ZipFile zipfile = new ZipFile(pluginFile);
				try {
					Enumeration<? extends ZipEntry> entries = zipfile.entries();
					while (entries.hasMoreElements()) {
						ZipEntry entry = entries.nextElement();
						String relativePath = entry.getName();
						if (matchesExports(relativePath)) {
							URL resourceUrl = new URL(pluginUrl, relativePath);
							result.add(new JPFResource(resourceUrl, relativePath));
						}
					}
				}
				finally {
					if (zipfile != null) zipfile.close();
				}
			}
		}
		catch (IOException e) {
			Logger.getLogger(getClass().getName()).warning(
					"cannot read resources from plugin '" + pluginUrl + "': " + e);
		}
		catch (URISyntaxException e) {
			Logger.getLogger(getClass().getName()).warning(
					"cannot read resources from plugin '" + pluginUrl + "': " + e);
		}
		return result;
	}

	private boolean matchesExports(String relativePath) {
//		 PathResolver pathResolver = manager.getPathResolver();
//		 for (Library library : this.descriptor.getLibraries()) {
//			 result.add(new JPFResource(pathResolver.get, library));
//		 }
		 
		 // TODO: use export filter to determine if the resource is exported
		 return true;
	}

	/**
	 * Recursively collects all files that are in the specified directory and
	 * adds them to the given collection. Please not that directories are not
	 * added.
	 * 
	 * @param directory
	 *            the directory to collect the files from
	 * @param result
	 *            the container to add the collected files into
	 */
	private void collectFiles(File directory, Collection<File> result) {
		File[] files = directory.listFiles();
		if (files == null) return;
		for (File file : files) {
			// if directory collect recursively
			if (file.isDirectory()) {
				collectFiles(file, result);
			}
			// if file add it
			else if (file.isFile()) {
				result.add(file);
			}
		}
	}
}
