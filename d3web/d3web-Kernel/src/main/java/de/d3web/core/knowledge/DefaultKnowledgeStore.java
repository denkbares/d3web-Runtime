/*
 * Copyright (C) 2011 denkbares GmbH
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

import java.util.HashMap;
import java.util.Map;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.utilities.Pair;

/**
 * Default implementation of a KnowledgeStore
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 14.02.2011
 */
public class DefaultKnowledgeStore implements KnowledgeStore {

	private final Map<Pair<Class<? extends PSMethod>, MethodKind>, KnowledgeSlice> entries = new HashMap<Pair<Class<? extends PSMethod>, MethodKind>, KnowledgeSlice>();

	@Override
	public void addKnowledge(Class<? extends PSMethod> solver, MethodKind kind, KnowledgeSlice slice) {
		entries.put(createEntryKey(solver, kind), slice);
	}

	@Override
	public void removeKnowledge(Class<? extends PSMethod> solver, MethodKind kind, KnowledgeSlice slice) {
		if (getKnowledge(solver, kind) == slice) {
			entries.remove(createEntryKey(solver, kind));
		}
		else {
			throw new IllegalArgumentException("Slice " + slice + " not contained with " + solver
					+ " and " + kind);
		}
	}

	@Override
	public KnowledgeSlice getKnowledge(Class<? extends PSMethod> solver, MethodKind kind) {
		return entries.get(createEntryKey(solver, kind));
	}

	@Override
	public KnowledgeSlice[] getKnowledge() {
		return entries.values().toArray(new KnowledgeSlice[entries.size()]);
	}

	private Pair<Class<? extends PSMethod>, MethodKind> createEntryKey(Class<? extends PSMethod> clazz, MethodKind methodKind) {
		return new Pair<Class<? extends PSMethod>, MethodKind>(clazz, methodKind);
	}

}
