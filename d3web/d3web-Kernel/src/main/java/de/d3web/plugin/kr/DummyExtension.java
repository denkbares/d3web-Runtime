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
package de.d3web.plugin.kr;

import de.d3web.plugin.Extension;

/**
 * Objects of this class represent plugins, which are not available.
 * This class offers the possibility, to keep the configuration of these plugins in the kb.
 * @author Markus Friedrich (denkbares GmbH)
 */
public class DummyExtension implements Extension {

	private String id;
	private String extendetPluginID;
	private String extendetPointID;
	
	public DummyExtension(String id, String extendetPluginID, String extendetPointID) {
		this.id=id;
		this.extendetPluginID=extendetPluginID;
		this.extendetPointID=extendetPointID;
	}
	
	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getExtendedPluginID() {
		return extendetPluginID;
	}

	@Override
	public String getExtendetPointID() {
		return extendetPointID;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Object getNewInstance() {
		return null;
	}

	@Override
	public String getParameter(String parameter) {
		return null;
	}

	@Override
	public Double getPriority() {
		return null;
	}

	@Override
	public Object getSingleton() {
		return null;
	}

	@Override
	public String getVersion() {
		return null;
	}

}
