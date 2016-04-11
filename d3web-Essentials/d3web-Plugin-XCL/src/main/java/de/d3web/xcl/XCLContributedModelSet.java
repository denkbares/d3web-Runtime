/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.d3web.xcl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.knowledge.TerminologyObject;

/**
 * Stores XCLModels in one KnowledgeSlice. Its intension is to store the
 * backward referenced xcl models from the {@link TerminologyObject}s of the
 * Conditions of the contained relations to the xcl model.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class XCLContributedModelSet implements KnowledgeSlice {

	/**
	 * KnowledgeKind for backward referenced xclModels from the NamedObjects of
	 * the Conditions of the contained relations
	 */
	public final static KnowledgeKind<XCLContributedModelSet> KNOWLEDGE_KIND = new KnowledgeKind<XCLContributedModelSet>(
			"XCLContributedModelSet", XCLContributedModelSet.class);
	private final List<XCLModel> models = new ArrayList<XCLModel>();

	public void addModel(XCLModel model) {
		if (!models.contains(model)) {
			models.add(model);
		}
	}

	public boolean removeModel(XCLModel model) {
		return models.remove(model);
	}

	public List<XCLModel> getModels() {
		return Collections.unmodifiableList(models);
	}

	public boolean isEmpty() {
		return models.isEmpty();
	}

	@Override
	public String toString() {
		return models.toString();
	}
}
