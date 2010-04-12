/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.d3web.xcl;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.session.Session;
import de.d3web.xcl.inference.PSMethodXCL;
/**
 * Stores XCLModels in one KnowledgeSlice
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class XCLContributedModelSet implements KnowledgeSlice {

	/**
	 * MethodKind for backward referenced xclModels from the NamedObjects of the
	 * Conditions of the contained relations
	 */
	public final static MethodKind XCL_CONTRIBUTED_MODELS = new MethodKind(
			"XCL_CONTRIBUTED_MODELS");	
	private static int count = 0;
	private String id = null;
	private List<XCLModel> models = new ArrayList<XCLModel>();
	
	@Override
	public String getId() {
		if (id == null) {
			id = "XCLContributedModelSet"+count++;
		}
		return id;
	}

	@Override
	public Class<? extends PSMethod> getProblemsolverContext() {
		return PSMethodXCL.class;
	}

	@Override
	public boolean isUsed(Session theCase) {
		return true;
	}

	@Override
	public void remove() {
		// nothing to do, XCL Models should be removed from their diagnosis

	}
	
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
}
