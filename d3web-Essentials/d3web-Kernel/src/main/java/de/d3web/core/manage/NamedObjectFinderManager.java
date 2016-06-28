/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.d3web.core.manage;

import java.util.Collection;
import java.util.Collections;

import de.d3web.core.extensions.KernelExtensionPoints;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;

/**
 * Manager class that handles Finders for NamedObjects
 * 
 * @author Reinhard Hatko
 * @created 16.05.2013
 */
public class NamedObjectFinderManager {

	final NamedObjectFinder[] finders;

	public static final NamedObjectFinderManager INSTANCE;

	static {
		INSTANCE = new NamedObjectFinderManager();
	}

	public static NamedObjectFinderManager getInstance() {
		return INSTANCE;
	}
	
	
	private NamedObjectFinderManager() {
		Extension[] extensions = PluginManager.getInstance().getExtensions(
				KernelExtensionPoints.PLUGIN_ID,
				KernelExtensionPoints.EXTENSIONPOINT_NAMED_OBJECT_FINDER);
		
		finders = new NamedObjectFinder[extensions.length];

		for (int i = 0; i < extensions.length; i++) {
			finders[i] = (NamedObjectFinder) extensions[i].getSingleton();
		}
	}

	public Collection<NamedObject> find(String name, KnowledgeBase kb) {
		for (NamedObjectFinder finder : finders) {
			Collection<NamedObject> objects = finder.find(name, kb);
			if (!objects.isEmpty()) return objects;
		}
		return Collections.emptyList();

	}
	
}
