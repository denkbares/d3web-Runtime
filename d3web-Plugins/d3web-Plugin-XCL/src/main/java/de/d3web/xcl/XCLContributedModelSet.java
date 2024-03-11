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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.Question;

/**
 * Stores XCLModels in one KnowledgeSlice. Its intention is to store the backward referenced xcl models from the {@link
 * TerminologyObject}s of the Conditions of the contained relations to the xcl model.
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class XCLContributedModelSet implements KnowledgeSlice {

	/**
	 * KnowledgeKind for backward referenced xclModels from the NamedObjects of the Conditions of the contained
	 * relations
	 */
	public final static KnowledgeKind<XCLContributedModelSet> KNOWLEDGE_KIND = new KnowledgeKind<>(
			"XCLContributedModelSet", XCLContributedModelSet.class);
	private final LinkedHashSet<XCLModel> models = new LinkedHashSet<>();

	/**
	 * Returns the XCL models that covers the specified value object, usually a {@link Question}. This means it returns
	 * all {@link XCLModel}s that have at least one {@link XCLRelation} that considers the value of the specified
	 * object. If no such model exists, an empty list is returned.
	 *
	 * @param coveredObject the question (or solution) that is covered by some XCL model(s)
	 * @return the XCL models that covers the object
	 */
	public static List<XCLModel> getModels(ValueObject coveredObject) {
		XCLContributedModelSet modelSet = coveredObject.getKnowledgeStore().getKnowledge(KNOWLEDGE_KIND);
		return (modelSet == null) ? Collections.emptyList() : modelSet.getModels();
	}

	public void addModel(XCLModel model) {
		models.add(model);
	}

	public boolean removeModel(XCLModel model) {
		return models.remove(model);
	}

	public List<XCLModel> getModels() {
		return List.copyOf(models);
	}

	public boolean isEmpty() {
		return models.isEmpty();
	}

	@Override
	public String toString() {
		return models.toString();
	}
}
