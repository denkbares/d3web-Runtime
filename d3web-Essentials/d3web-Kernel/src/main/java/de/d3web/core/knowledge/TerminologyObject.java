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

package de.d3web.core.knowledge;

import org.jetbrains.annotations.NotNull;

import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;

/**
 * This interface summarizes all common methods provided by objects that statically define the entities of a knowledge
 * base. For instance, {@link Solution} and {@link Question} instances (transitively) inherit their interfaces from
 * {@link TerminologyObject}. {@link TerminologyObject} instances should be organized in a poly-hierarchy having a root
 * element for solutions and questions, each.
 *
 * @author Markus Friedrich, Joachim Baumeister (denkbares GmbH)
 * @created 06.05.2011
 */
public interface TerminologyObject extends NamedObject {

	/**
	 * Gives all parents of this objects based on the defined poly-hierarchy.
	 *
	 * @return all parents of this object
	 * @created 06.05.2011
	 */
	@NotNull
	TerminologyObject[] getParents();

	/**
	 * Returns all children objects of this instance. Please note that an object can be a child of more than one object,
	 * this defining a poly-hierarchy.
	 *
	 * @return all children of this object
	 * @created 06.05.2011
	 */
	@NotNull
	TerminologyObject[] getChildren();

	/**
	 * Every instance has to correspond to a {@link KnowledgeBase} instance.
	 *
	 * @return returns the {@link KnowledgeBase} instance, this object corresponds to.
	 * @created 06.05.2011
	 */
	KnowledgeBase getKnowledgeBase();

	/**
	 * Removes this instance from all parents
	 *
	 * @created 15.02.2011
	 */
	void destroy();

	/**
	 * The problem-solving knowledge corresponding to this instance is managed by a {@link KnowledgeStore} instance, for
	 * example, (scoring) rules and set-covering models.
	 *
	 * @return the {@link KnowledgeStore} data structure storing all problem-solving knowledge corresponding with this
	 * object
	 */
	KnowledgeStore getKnowledgeStore();
}
