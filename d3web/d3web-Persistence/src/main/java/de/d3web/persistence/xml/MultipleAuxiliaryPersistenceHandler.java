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

import java.util.Collection;

import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.persistence.utilities.PersistentObjectDescriptor;

/**
 * Interface for AuxiliaryPersistenceHandler, that need to save several documents
 * 
 * @author pkluegl
 *
 */
public interface MultipleAuxiliaryPersistenceHandler extends AuxiliaryPersistenceHandler {
	
	/**
	 * [TODO]: Peter: not the best solution:
	 * Problem : if a handler needs to save several documents AND there is no possiblity
	 * to create an extra handler for each extra document.
	 * Therefore PersistanceManager will differ and choose the correct method
	 * 
	 * saves the given knowledge base
	 * @return the DOM-Document OR InputStream representing the saved knowledge base
	 */
	public Collection<PersistentObjectDescriptor> saveAll(KnowledgeBase kb);

}
