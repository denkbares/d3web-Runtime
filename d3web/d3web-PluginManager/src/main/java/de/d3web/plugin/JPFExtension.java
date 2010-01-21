/*
 * Copyright (C) 2009 denkbares GmbH
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.plugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.java.plugin.PluginLifecycleException;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.registry.Extension.Parameter;

/**
 * The Implementation of the Extension-Interface for the java plugin framework (jpf)
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class JPFExtension implements de.d3web.plugin.Extension {
	private Object singleton;
	private Extension extension;
	private org.java.plugin.PluginManager manager;

	public JPFExtension(Extension extension, org.java.plugin.PluginManager manager) {
		this.manager = manager;
		this.extension = extension;
	}

	@Override
	public String getParameter(String param) {
		Parameter parameter = extension.getParameter(param);
		if (parameter!=null) {
			return parameter.valueAsString();
		} else {
			return null;
		}
	}

	@Override
	public Object getNewInstance() {
		PluginDescriptor declaringPluginDescriptor = extension
				.getDeclaringPluginDescriptor();
		try {
			//secures that the plugin containing the defintion of the extension point is activated
			manager.activatePlugin(extension.getExtendedPluginId());
		}
		catch (PluginLifecycleException e) {
			throw new InstantiationError(e.getMessage());
		}
		ClassLoader classLoader = manager.getPluginClassLoader(declaringPluginDescriptor);
		try {
			Class<?> clazz = classLoader.loadClass(getParameter("class"));
			return clazz.newInstance();
		} 
		catch (ClassNotFoundException e) {
			Logger.getLogger("Plugin").log(Level.SEVERE, "The class specified in the plugin was not found.  This is a strong evidence for an incorrect plugin.", e);
			throw new NoClassDefFoundError(e.getMessage());
		} 
		catch (InstantiationException e) {
			Logger.getLogger("Plugin").log(Level.SEVERE, "It was not possible to instantiate an object. This is a strong evidence for an incorrect plugin.", e);
			throw new InstantiationError(e.getMessage());
		} 
		catch (IllegalAccessException e) {
			Logger.getLogger("Plugin").log(Level.SEVERE, "The constructor or the class could not be accessed. This is a strong evidence for an incorrect plugin.", e);
			throw new IllegalAccessError(e.getMessage());
		}
	}

	@Override
	public Object getSingleton() {
		if (singleton == null) {
			singleton = getNewInstance();
		}
		return singleton;
	}

	@Override
	public String getDescription() {
		return getParameter("description");
	}

	@Override
	public String getName() {
		return getParameter("name");
	}

	@Override
	public String getVersion() {
		return getParameter("version");
	}

	@Override
	public Double getPriority() {
		return extension.getParameter("priority").valueAsNumber().doubleValue();
	}

	@Override
	public String getID() {
		return extension.getId();
	}
	
	@Override
	public String getExtendetPointID() {
		return extension.getExtendedPointId();
	}
	
	@Override
	public String getExtendedPluginID() {
		return extension.getExtendedPluginId();
	}
}
