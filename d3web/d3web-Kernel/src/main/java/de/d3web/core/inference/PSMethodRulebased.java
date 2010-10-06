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

package de.d3web.core.inference;

import java.util.Collection;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.session.Session;

/**
 * @author jochen
 * 
 *         abstract PSMethod checking for rules relevant for the corresponding
 *         problem-solver-subclass
 * 
 */
public abstract class PSMethodRulebased extends PSMethodAdapter {

	/**
	 * Check if NamedObject has rules connected with this problem-solver and
	 * check them, if available
	 */
	protected final void propagate(Session session, TerminologyObject nob) {
		if (nob != null) {
			KnowledgeSlice slices = ((NamedObject) nob).getKnowledge(this
					.getClass(), MethodKind.FORWARD);
			if (slices instanceof RuleSet) {
				RuleSet rs = (RuleSet) slices;
				for (Rule rule : rs.getRules()) {
					rule.check(session);
				}
			}
		}
	}

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {
		// for rules we check all rules sequentally
		for (PropagationEntry change : changes) {
			if (!change.isStrategic()) {
				this.propagate(session, change.getObject());
			}
		}
	}
}
