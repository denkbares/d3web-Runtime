/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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


package de.d3web.caserepository.addons;

import java.util.Collection;

import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.psMethods.PSMethodAdapter;
import de.d3web.kernel.psMethods.PropagationEntry;

/**
 * @author betz
 *
 * This PSMethod is used to determine, if a solution-rating comes from the trainer.
 * It is a marker class only.
 * Creation date: (29.04.2003 17:10:00)
 */
public class PSMethodClassicD3 extends PSMethodAdapter {

	private static PSMethodClassicD3 instance = null;

	private PSMethodClassicD3() { /* hide empty constructor */ }

	/**
	 * @return the one and only instance of this PSMethod (Singleton)
	 */
	public static PSMethodClassicD3 getInstance() {
		if (instance == null) {
			instance = new PSMethodClassicD3();
		}
		return instance;
	}

	@Override
	public void propagate(XPSCase theCase, Collection<PropagationEntry> changes) {
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		return Facts.mergeUniqueFact(facts);
	}

}
