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
package de.d3web.core.extensions;


/**
 * This class stores IDs for the ExtensionPoints of the Kernel.
 * 
 * @author Reinhard Hatko
 * @created 16.05.2013
 */
public class KernelExtensionPoints {

	public static final String PLUGIN_ID = "d3web-Kernel-ExtensionPoints";
	public static final String EXTENSIONPOINT_PROPERTY = "Property";
	public static final String EXTENSIONPOINT_PSMETHOD = "PSMethod";
	public static final String EXTENSIONPOINT_AUTODETECT = "Autodetect";
	public static final String EXTENSIONPOINT_NAMED_OBJECT_FINDER = "NamedObjectFinder";
	public static final String EXTENSIONPOINT_PROTOCOL_EXECUTOR = "ProtocolExecutor";

	private KernelExtensionPoints() {
	}

}
