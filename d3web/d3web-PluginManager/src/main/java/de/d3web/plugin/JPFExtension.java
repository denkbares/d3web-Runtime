/*
 * Copyright (C) 2009 denkbares GmbH
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.java.plugin.PluginLifecycleException;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.Extension.Parameter;
import org.java.plugin.registry.PluginDescriptor;

import de.d3web.utils.Log;

/**
 * The Implementation of the Extension-Interface for the java plugin framework
 * (jpf)
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class JPFExtension implements de.d3web.plugin.Extension {

	private Object singleton;
	private final Extension extension;
	private final org.java.plugin.PluginManager manager;

	public JPFExtension(Extension extension, org.java.plugin.PluginManager manager) {
		this.manager = manager;
		this.extension = extension;
	}

	@Override
	public String getParameter(String param) {
		Parameter parameter = extension.getParameter(param);
		if (parameter != null) {
			return parameter.rawValue();
		}
		else {
			return null;
		}
	}

	public Object getNewInstance(String className) {
		PluginDescriptor declaringPluginDescriptor = extension
				.getDeclaringPluginDescriptor();
		try {
			// secures that the plugin containing the definition of the
			// extension
			// point is activated
			manager.activatePlugin(extension.getExtendedPluginId());
		}
		catch (PluginLifecycleException e) {
			throw new InstantiationError(e.getMessage());
		}
		ClassLoader classLoader = manager.getPluginClassLoader(declaringPluginDescriptor);
		try {
			Class<?> clazz = classLoader.loadClass(className);
			return clazz.newInstance();
		}
		catch (ClassNotFoundException e) {
			Log.severe("Error in plugin '" + getPluginID() + "', extension '" + getID() + "'. " +
					"The class specified in the plugin was not found.  " +
					"This is a strong evidence for an incorrect plugin.",
					e);
			throw new NoClassDefFoundError(e.getMessage());
		}
		catch (InstantiationException e) {
			Log.severe("Error in plugin '" + getPluginID() + "', extension '" + getID() + "'. " +
					"It was not possible to instantiate an object. " +
					"This is a strong evidence for an incorrect plugin.",
					e);
			throw new InstantiationError(e.getMessage());
		}
		catch (IllegalAccessException e) {
			Log.severe("Error in plugin '" + getPluginID() + "', extension '" + getID() + "'. " +
					"The constructor or the class could not be accessed. " +
					"This is a strong evidence for an incorrect plugin.",
					e);
			throw new IllegalAccessError(e.getMessage());
		}
		catch (LinkageError e) {
			Log.severe("Error in plugin '" + getPluginID() + "', extension '" + getID() + "'. " +
					"The plugin uses code that cannot be found or linked. " +
					"This is a strong evidence for out-dated plugin code.",
					e);
			throw new InstantiationError(e.getMessage());
		}
	}

	@Override
	public Object getNewInstance() {
		return getNewInstance(getParameter("class"));
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
	public double getPriority() {
		return extension.getParameter("priority").valueAsNumber().doubleValue();
	}

	@Override
	public String getID() {
		return extension.getId();
	}

	@Override
	public String toString() {
		return getID();
	}

	@Override
	public String getExtendetPointID() {
		return extension.getExtendedPointId();
	}

	@Override
	public String getExtendedPluginID() {
		return extension.getExtendedPluginId();
	}

	@Override
	public List<String> getParameters(String parameter) {
		Collection<Parameter> parameters = extension.getParameters(parameter);
		if (parameters != null) {
			List<String> ret = new ArrayList<String>();
			for (Parameter p : parameters) {
				ret.add(p.valueAsString());
			}
			return ret;
		}
		else {
			return null;
		}
	}

	@Override
	public String getPluginID() {
		return extension.getDeclaringPluginDescriptor().getId();
	}

	@Override
	public Plugin getPlugin() {
		return PluginManager.getInstance().getPlugin(getPluginID());
	}
}
