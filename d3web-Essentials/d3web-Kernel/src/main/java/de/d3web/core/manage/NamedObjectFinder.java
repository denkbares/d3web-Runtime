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

import java.util.Set;

import org.jetbrains.annotations.NotNull;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.NamedObject;

/**
 * Implementors of this class can search the KnowledgeBase for NamedObjects.
 *
 * @author Reinhard Hatko
 * @created 16.05.2013
 */
public interface NamedObjectFinder {
	/**
	 * Returns the sub-set of named objects of the specified name form the specified knowledge base, that matches the
	 * searched type of the individual finder. If no such objects exists, the method returns an empty set.
	 * <p>
	 * Note: The individual implementors each only search for special kinds of named objects, so they are not intended
	 * to return all {@link NamedObject}s of the whole knowledge base.
	 *
	 * @param kb   the knowledge base to search the named object in
	 * @param name the name of the named object to be find
	 * @return the named object(s) of the finder-specific type that are available in the knowledge base
	 */
	@NotNull
	Set<NamedObject> find(@NotNull KnowledgeBase kb, @NotNull String name);
}
