/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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
package de.d3web.core.session;

import java.util.List;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.blackboard.Fact;

/**
 * The {@link Protocol} stores all findings entered during a {@link Session}
 * in a sequential order. 
 * 
 * Comment: Later also timestamps and further information are stored in
 * the protocol, too.
 * 
 * @author joba
 *
 */
public interface Protocol {
	/**
	 * Return the list of entered findings ({@link Value} instances assigned
	 * to {@link Question} instances) in a chronological order. 
	 * @return the entered findings in a chronological order
	 */
	public List<ProtocolEntry> getProtocolHistory();
	
	/**
	 * Append a new protocol entry to the {@link Protocol}.
	 * The entry is defined by the specified {@link Fact}.
	 * @param fact the specified {@link Fact} instance
	 */
	public void addEntry(Fact fact);
	
	
}










