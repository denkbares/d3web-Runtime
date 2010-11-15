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

import java.util.List;

import de.d3web.core.session.Session;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaFlux.inference.FluxSolver;

/**
 *
 *
 * @author Reinhard Hatko
 * @created 01.09.2010
 */
public class SnapshotNode extends Node {

	public SnapshotNode(String id, String name) {
		super(id, name);
	}

	@Override
	public void doAction(Session session) {

		DiaFluxUtils.getDiaFluxCaseObject(session).registerSnapshot(this, session);
	}

	@Override
	public void undoAction(Session session) {
		DiaFluxUtils.getDiaFluxCaseObject(session).unregisterSnapshot(this, session);
	}

	@Override
	public void takeSnapshot(Session session, SnapshotNode snapshotNode, List<INode> nodes) {
		super.takeSnapshot(session, snapshotNode, nodes);

		// this node is the new starting point of the flow
		// so give it support
		FluxSolver.addSupport(session, this, new ValidSupport());

	}

	/**
	 * Returns true, as SnapshotNodes can always be activated
	 */
	@Override
	public boolean couldActivate(Session session) {
		return true;
	}


}
