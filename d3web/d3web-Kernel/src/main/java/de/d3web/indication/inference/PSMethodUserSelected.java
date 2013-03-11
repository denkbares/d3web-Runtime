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

package de.d3web.indication.inference;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import de.d3web.core.inference.PSMethodAdapter;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;

/**
 * This PSMethod is for user selections (e.g. in a Dialog) Creation date:
 * (03.01.2002 16:17:28)
 * 
 * @author Christian Betz
 */
public class PSMethodUserSelected extends PSMethodAdapter {

	private static PSMethodUserSelected instance = null;

	protected PSMethodUserSelected() {
		super();
	}

	/**
	 * @return the one and only instance of this PSMethod (Singleton)
	 */
	public static PSMethodUserSelected getInstance() {
		if (instance == null) {
			instance = new PSMethodUserSelected();
		}
		return instance;
	}

	/**
	 * @see de.d3web.core.inference.PSMethod
	 */
	@Override
	public void init(de.d3web.core.session.Session session) {
	}

	@Override
	public String toString() {
		return "User selections";
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		return Facts.mergeUniqueFact(facts);
	}

	@Override
	public boolean hasType(Type type) {
		return type == Type.source;
	}

	@Override
	public double getPriority() {
		// highest priority
		return 0;
	}

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {
		// do nothing
	}

	@Override
	public Set<TerminologyObject> getPotentialDerivationSources(TerminologyObject derivedObject) {
		return Collections.emptySet();
	}

	@Override
	public Set<TerminologyObject> getActiveDerivationSources(TerminologyObject derivedObject, Session session) {
		return Collections.emptySet();
	}
}