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

import de.d3web.core.session.Session;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaFlux.inference.FluxSolver;
import de.d3web.diaFlux.inference.IPath;

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

		IPath path = DiaFluxUtils.getDiaFluxCaseObject(session).getActivePath();

		FluxSolver.takeSnapshot(session, path, this);
	}

	@Override
	public void undoAction(Session session) {
		// nothing...
	}

	@Override
	public void takeSnapshot(Session session) {
	}


}
