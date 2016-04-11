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

package de.d3web.diaFlux.flow;

import de.d3web.core.knowledge.DefaultInfoStore;
import de.d3web.core.knowledge.DefaultKnowledgeStore;
import de.d3web.core.knowledge.InfoStore;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.KnowledgeStore;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.diaFlux.inference.FluxSolver;

/**
 * 
 * 
 * @author Reinhard Hatko
 * @created 01.09.2010
 */
public class SnapshotNode extends AbstractNode implements ValueObject {

	private final KnowledgeStore knowledgeStore = new DefaultKnowledgeStore();
	private final InfoStore infoStore = new DefaultInfoStore();

	public SnapshotNode(String id, String name) {
		super(id, name);
		this.getKnowledgeStore().addKnowledge(FluxSolver.DEPENDANT_EDGES, new EdgeMap());
	}

	@Override
	protected boolean addOutgoingEdge(Edge edge) {
		EdgeMap edges = getKnowledgeStore().getKnowledge(FluxSolver.DEPENDANT_EDGES);
		edges.addEdge(edge);
		return super.addOutgoingEdge(edge);
	}



	@Override
	public Value getDefaultValue() {
		return UndefinedValue.getInstance();
	}

	@Override
	public TerminologyObject[] getParents() {
		return new TerminologyObject[0];
	}

	@Override
	public TerminologyObject[] getChildren() {
		return new TerminologyObject[0];
	}

	@Override
	public KnowledgeBase getKnowledgeBase() {
		return getFlow().getKnowledgeBase();
	}

	@Override
	public void destroy() {

	}

	@Override
	public KnowledgeStore getKnowledgeStore() {
		return knowledgeStore;
	}

	@Override
	public String getId() {
		return getID();
	}

	@Override
	public InfoStore getInfoStore() {
		return infoStore;
	}
}
