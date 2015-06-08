/*
 * Copyright (C) 2015 denkbares GmbH
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

import de.d3web.utils.InstantiationContext;

/**
 * Implementation of the @link{Instantiation} interface for JPF.
 * The origin of a instantion is defined by the IDs of the plugin and the extension.
 *
 * @author Sebastian Furth (denkbares GmbH)
 * @created 08.06.15
 */
public class JPFInsantiationContext implements InstantiationContext {

	private final String pluginId;
	private final String extension;

	public JPFInsantiationContext(String pluginId, String extension) {
		this.pluginId = pluginId;
		this.extension = extension;
	}

	@Override
	public String getOrigin() {
		return "Plugin: " + String.valueOf(pluginId) + "; Extension: " + extension;
	}

	@Override
	public String toString() {
		return "JPFInsantiationContext{" +
				"pluginId='" + pluginId + '\'' +
				", extension='" + extension + '\'' +
				'}';
	}
}
