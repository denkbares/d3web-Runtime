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

package de.d3web.core.inference;

/**
 * Specifies that implementing classes are used as explict knowledge. Each
 * KnowledgeSlice needs to have a problem-solver context in which it is relevant
 * and if it has already been used (e.g. rule = hasFired).
 * 
 * @author joba
 */
public interface KnowledgeSlice {

	/**
	 * Provide a unique id for each part of knowledge.
	 * 
	 * @return java.lang.String
	 */
	// TODO: vb: discuss whether each slice should have an id? what is the sense
	// behind that?
	String getId();

	/**
	 * Creation date: (30.08.00 17:23:04)
	 * 
	 * @return the class of the PSMethod in which this KnowledgeSlice makes
	 *         sense.
	 */
	// TODO: vb: Either (1) delete this method (only used for rules) or (2)
	// change addKnowledge in a way that the context need not to be specified.
	public Class<? extends PSMethod> getProblemsolverContext();

	/**
	 * Prompts the knowledgeslice to remove itsself from all objects
	 * 
	 */
	// TODO: vb: discuss whether this method should be defined. It forces the
	// slices to know where they are added. This may be useful for rules but not
	// appropriate for general knowledge slices.
	public void remove();
}
