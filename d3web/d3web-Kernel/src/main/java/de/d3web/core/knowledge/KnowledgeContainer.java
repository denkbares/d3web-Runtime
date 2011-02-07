/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.core.knowledge;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSMethod;

/**
 * This interface describes a type that contains knowledge. Of course a
 * knowledge base does, but also a AbstractTerminologyObject is able to store knowledge slices
 * (e.g. corresponding rules)
 * 
 * @see de.d3web.core.knowledge.KnowledgeBase
 * @see de.d3web.core.knowledge.terminology.AbstractTerminologyObject
 * @author Christian Betz
 */
public interface KnowledgeContainer {

	/**
	 * @return all knowledge matching the given problemsolver context and method
	 *         kind. usually a List or Map.
	 */
	public Object getKnowledge(Class<? extends PSMethod> problemsolver, MethodKind kind);

	/**
	 * adds a KnowledgeSlice with given problem solver context and method kind.
	 */
	public void addKnowledge(
			Class<? extends PSMethod> problemsolver,
			KnowledgeSlice knowledgeSlice,
			MethodKind knowledgeContext);
}