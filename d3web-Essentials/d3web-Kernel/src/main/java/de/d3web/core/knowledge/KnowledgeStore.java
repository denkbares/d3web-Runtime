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

import java.util.Objects;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.RuleSet;

/**
 * The problem-solving knowledge of the knowledge base is managed in {@link KnowledgeStore} instances. Every {@link
 * TerminologyObject} instance then stores all knowledge that corresponds to the {@link TerminologyObject} instance.
 * Possible knowledge elements can be {@link RuleSet} instances or set-covering models.
 *
 * @author Markus Friedrich (denkbares GmbH)
 * @created 06.05.2011
 */
public interface KnowledgeStore {

	/**
	 * Inserts the specified {@link KnowledgeSlice} instance with the specified {@link KnowledgeKind} to the store.
	 * {@link KnowledgeKind} instances correspond to the particular problem-solving methods and the {@link
	 * KnowledgeSlice} instances are concrete knowledge elements that can be processed by the related problem-solving
	 * method.
	 *
	 * @param kind  the specified {@link KnowledgeKind}
	 * @param slice the specified knowledge element
	 * @created 06.05.2011
	 */
	void addKnowledge(KnowledgeKind<?> kind, KnowledgeSlice slice);

	/**
	 * Removes the specified {@link KnowledgeSlice} instance with the specified {@link KnowledgeKind} from the store.
	 * {@link KnowledgeKind} instances correspond to the particular problem-solving methods and the {@link
	 * KnowledgeSlice} instances are concrete knowledge elements that can be processed by the related problem-solving
	 * method.
	 *
	 * @param kind  the specified {@link KnowledgeKind}
	 * @param slice the specified knowledge element
	 * @created 06.05.2011
	 */
	void removeKnowledge(KnowledgeKind<?> kind, KnowledgeSlice slice);

	/**
	 * Returns the {@link KnowledgeSlice} included in this {@link KnowledgeStore} that is connected with the specified
	 * {@link KnowledgeKind}. If there is no such KnowledgeSlice, null is returned.
	 *
	 * @param kind the specified {@link KnowledgeKind}
	 * @return the knowledge related to the specified {@link KnowledgeKind}
	 * @created 06.05.2011
	 */
	@Nullable <T extends KnowledgeSlice> T getKnowledge(KnowledgeKind<T> kind);

	/**
	 * Returns the {@link KnowledgeSlice} included in this {@link KnowledgeStore} that is connected with the specified
	 * {@link KnowledgeKind}. If there is no such KnowledgeSlice, a new one is created, using the specified supplier,
	 * and added to this store. The supplier must not return null, otherwise a {@link NullPointerException} is thrown.
	 *
	 * @param kind     the specified {@link KnowledgeKind}
	 * @param supplier a supplier to create a new instance if there is no such knowledge in this store
	 * @return the knowledge related to the specified {@link KnowledgeKind}
	 */
	@NotNull
	default <T extends KnowledgeSlice> T computeIfAbsent(KnowledgeKind<T> kind, Supplier<? extends T> supplier) {
		T knowledge = getKnowledge(kind);
		if (knowledge == null) {
			knowledge = Objects.requireNonNull(supplier.get());
			addKnowledge(kind, knowledge);
		}
		return knowledge;
	}

	/**
	 * Returns all knowledge elements for all registered {@link KnowledgeKind}, that are included in this {@link
	 * KnowledgeStore} instance.
	 *
	 * @return all knowledge elements no matter what the particular {@link KnowledgeKind} is
	 * @created 06.05.2011
	 */
	KnowledgeSlice[] getKnowledge();
}
