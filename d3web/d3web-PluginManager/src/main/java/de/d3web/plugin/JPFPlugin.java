package de.d3web.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.java.plugin.registry.Library;
import org.java.plugin.registry.PluginDescriptor;

public class JPFPlugin implements Plugin {

	private final class ResourceFilter {
		private boolean isPublic;
		private final Set<String> entries;

		public ResourceFilter(Library lib) {
			entries = new HashSet<String>();
			for (String exportPrefix : lib.getExports()) {
				if ("*".equals(exportPrefix)) { //$NON-NLS-1$
					isPublic = true;
					entries.clear();
					break;
				}
				if (!lib.isCodeLibrary()) {
					exportPrefix = exportPrefix.replace('\\', '.');
					exportPrefix = exportPrefix.replace('/', '.');
					if (exportPrefix.startsWith(".")) { //$NON-NLS-1$
						exportPrefix = exportPrefix.substring(1);
					}
				}
				entries.add(exportPrefix);
			}
		}

		public boolean isClassVisible(String className) {
			if (isPublic) {
				return true;
			}
			if (entries.isEmpty()) {
				return false;
			}
			if (entries.contains(className)) {
				return true;
			}
			int p = className.lastIndexOf('.');
			if (p == -1) {
				return false;
			}
			return entries.contains(className.substring(0, p) + ".*"); //$NON-NLS-1$
		}

		public boolean isResourceVisible(String resPath) {
			// quick check
			if (isPublic) {
				return true;
			}
			if (entries.isEmpty()) {
				return false;
			}
			// translate "path spec" -> "full class name"
			String str = resPath.replace('\\', '.').replace('/', '.');
			if (str.startsWith(".")) { //$NON-NLS-1$
				str = str.substring(1);
			}
			if (str.endsWith(".")) { //$NON-NLS-1$
				str = str.substring(0, str.length() - 1);
			}
			return isClassVisible(str);
		}
	}

	private final PluginDescriptor descriptor;

	// contains the exported resources of the plugin, initialized lazy
	private Resource[] resources = null;
	// contains the export filter of the plugin, initialized lazy
	private ArrayList<ResourceFilter> resourceFilters = null;

	public JPFPlugin(PluginDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public String getPluginID() {
		return this.descriptor.getId();
	}

	@Override
	public Resource[] getResources() {
		if (this.resources == null) {
			// create all resources
			Collection<Resource> result = new LinkedList<Resource>();
			result = getResourcePaths(this.descriptor.getLocation());
			this.resources = result.toArray(new Resource[result.size()]);
		}
		return this.resources;
	}

	private Collection<Resource> getResourcePaths(URL pluginUrl) {

		Collection<Resource> result = new LinkedList<Resource>();
		try {
			String fileString = URLDecoder.decode(pluginUrl.getFile(), "UTF-8");
			if (fileString.startsWith("file:/")) {
				fileString = fileString.substring("file:/".length());
			}
			fileString = fileString.replace(".jar!", ".jar");
			File pluginFile = new File(fileString).getParentFile();
			File pluginFileLinux=new File("/"+fileString).getParentFile();
			if (!pluginFile.exists()) {
				if (!pluginFileLinux.exists()){
				throw new IllegalStateException(
						"Invalid plugin access due to internal error. Cannot find plugin location");
				} else {
					pluginFile=pluginFileLinux;
				}
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
		return result;
	}

	/**
	 * Return whether the resource with the specified path has been exported.
	 * This method uses the ResouceFilter class to determine if a specified
	 * resource path has been exported. The method initialized the resource
	 * filters lazy on demand.
	 * 
	 * @param relativePath
	 *            the resource path to be checked
	 * @return if the resource has been exported
	 */
	private boolean matchesExports(String relativePath) {
		// initialize resource filters lazy
		if (this.resourceFilters == null) {
			this.resourceFilters = new ArrayList<ResourceFilter>();
			for (Library library : this.descriptor.getLibraries()) {
				// only export non-code libraries
				if (library.isCodeLibrary()) continue;
				this.resourceFilters.add(new ResourceFilter(library));
			}
		}

		// use resource filter to determine if the resource is exported
		for (ResourceFilter filter : this.resourceFilters) {
			if (filter.isResourceVisible(relativePath)) {
				return true;
			}
		}

		// if no filter has matched, it is not exported
		return false;
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

	public PluginDescriptor getDescriptor() {
		return descriptor;
	}
	
	
}
