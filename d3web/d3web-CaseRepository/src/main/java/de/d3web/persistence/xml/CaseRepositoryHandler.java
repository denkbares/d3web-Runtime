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

import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.Document;

import de.d3web.core.knowledge.KnowledgeBase;

/**
 * This interface describes a handler for case repositories in 
 * knowledge base jar-sources
 */
public interface CaseRepositoryHandler {
	/**
	 * @return the ID of this handler
	 */
	public String getId();
	/**
	 * Loads the case repository from the given url
	 * @return a List of CaseObjects representing the cae repository
	 */
	public List load(KnowledgeBase kb, URL url);
	/**
	 * saves the given cases to the specified URL
	 */
	public Document save(Collection caseRepository);
	/**
	 * @return the default storage location (usually a URL) as String 
	 */
	public String getStorageLocation();
	/**
	 * Returns every MultimediaItem in this CaseRepository	 * @return List List of all MultimediaItems	 */
	public List getMultimediaItems(Collection caseRepository);
}
