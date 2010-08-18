/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.core.session.protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.d3web.core.session.blackboard.Fact;

public class DefaultProtocol implements Protocol {

	private final List<ProtocolEntry> entries;

	public DefaultProtocol() {
		this.entries = new ArrayList<ProtocolEntry>();
	}

	@Override
	public List<ProtocolEntry> getProtocolHistory() {
		return Collections.unmodifiableList(this.entries);
	}

	@Override
	public void addEntry(Fact fact) {
		this.entries.add(new DefaultProtocolEntry(fact));
	}

}
