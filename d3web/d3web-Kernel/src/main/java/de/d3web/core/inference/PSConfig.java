/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.d3web.core.inference;

import java.util.ArrayList;
import java.util.List;

/**
 * Saves the configuration of one problemsolver instance
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class PSConfig {

	private boolean active;
	private String psMethodID;
	private List<Object> params;
	
	public PSConfig(boolean active, String psMethodID) {
		super();
		this.active = active;
		this.psMethodID = psMethodID;
		params = new ArrayList<Object>();
	}
	
	public boolean isActive() {
		return active;
	}

	public String getPsMethodID() {
		return psMethodID;
	}

	public List<Object> getParams() {
		return params;
	}
	
	public void addParam(Object param) {
		params.add(param);
	}
}
