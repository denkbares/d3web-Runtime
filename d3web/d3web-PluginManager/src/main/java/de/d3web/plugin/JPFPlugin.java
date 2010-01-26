package de.d3web.plugin;

import java.util.Collection;
import java.util.LinkedList;

import org.java.plugin.PathResolver;
import org.java.plugin.PluginManager;
import org.java.plugin.registry.Library;
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
		PathResolver pathResolver = manager.getPathResolver();
		for (Library library : this.descriptor.getLibraries()) {
			result.add(new JPFResource(pathResolver, library));
		}
		return result.toArray(new Resource[result.size()]);
	}

}
