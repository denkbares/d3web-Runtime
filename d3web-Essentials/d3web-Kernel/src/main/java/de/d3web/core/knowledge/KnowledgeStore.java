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

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.RuleSet;

/**
 * The problem-solving knowledge of the knowledge base is managed in
 * {@link KnowledgeStore} instances. Every {@link TerminologyObject} instance
 * then stores all knowledge that corresponds to the {@link TerminologyObject}
 * instance. Possible knowledge elements can be {@link RuleSet} instances or
 * set-covering models.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 06.05.2011
 */
public interface KnowledgeStore {

	/**
	 * Inserts the specified {@link KnowledgeSlice} instance with the specified
	 * {@link KnowledgeKind} to the store. {@link KnowledgeKind} instances
	 * correspond to the particular problem-solving methods and the
	 * {@link KnowledgeSlice} instances are concrete knowledge elements that can
	 * be processed by the related problem-solving method.
	 * 
	 * @created 06.05.2011
	 * @param kind the specified {@link KnowledgeKind}
	 * @param slice the specified knowledge element
	 */
	void addKnowledge(KnowledgeKind<?> kind, KnowledgeSlice slice);

	/**
	 * Removes the specified {@link KnowledgeSlice} instance with the specified
	 * {@link KnowledgeKind} from the store. {@link KnowledgeKind} instances
	 * correspond to the particular problem-solving methods and the
	 * {@link KnowledgeSlice} instances are concrete knowledge elements that can
	 * be processed by the related problem-solving method.
	 * 
	 * @created 06.05.2011
	 * @param kind the specified {@link KnowledgeKind}
	 * @param slice the specified knowledge element
	 */
	void removeKnowledge(KnowledgeKind<?> kind, KnowledgeSlice slice);

	/**
	 * Returns all {@link KnowledgeSlice} instances included in this
	 * {@link KnowledgeStore} that are connected with the specified
	 * {@link KnowledgeKind}.
	 * 
	 * @created 06.05.2011
	 * @param kind the specified {@link KnowledgeKind}
	 * @return all knowledge elements related to the specified
	 *         {@link KnowledgeKind}
	 */
	<T extends KnowledgeSlice> T getKnowledge(KnowledgeKind<T> kind);

	/**
	 * Returns all knowledge elements for all registered {@link KnowledgeKind},
	 * that are included in this {@link KnowledgeStore} instance.
	 * 
	 * @created 06.05.2011
	 * @return all knowledge elements no matter what the particular
	 *         {@link KnowledgeKind} is
	 */
	KnowledgeSlice[] getKnowledge();

}
