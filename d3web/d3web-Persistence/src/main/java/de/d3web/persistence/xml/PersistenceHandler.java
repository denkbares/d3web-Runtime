/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.persistence.xml;
import org.w3c.dom.Document;

import de.d3web.kernel.domainModel.KnowledgeBase;
/**
 * This interface describes a identifiable handler that is able to save knowledge bases
 * Creation date: (06.06.2001 15:18:25)
 * @author Michael Scharvogel
 */
public interface PersistenceHandler {
	/**
	 * @return the ID of this handler
	 */
	public String getId();
	/**l
	 * @return the default location for saving the knowledge base (usually a URL)
	 */
	public String getDefaultStorageLocation();

	/**
	 * saves the given knowledge base
	 * @return the DOM-Document representing the saved knowledge base
	 */
	public Document save(KnowledgeBase kb);
	
}
