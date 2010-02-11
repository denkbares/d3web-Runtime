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

package de.d3web.core.inference;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.core.session.XPSCase;
import de.d3web.core.terminology.NamedObject;

/**
 * @author jochen
 * 
 * abstract PSMethod checking for rules relevant for the corresponding
 * problem-solver-subclass
 * 
 */
public abstract class PSMethodRulebased extends PSMethodAdapter {

	/**
	 * Check if NamedObject has rules connected with this problem-solver and
	 * check them, if available
	 */
	protected final void propagate(XPSCase theCase, NamedObject nob) {
		try {
			List<? extends KnowledgeSlice> slices = nob.getKnowledge(this
					.getClass());
			if (slices != null) {
				for (KnowledgeSlice slice : slices) {
					if (slice instanceof Rule) {
						Rule rule = (Rule) slice;
						rule.check(theCase);
					}
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).throwing(
					this.getClass().getName(), "propagate", ex);
		}
	}

	public void propagate(XPSCase theCase, Collection<PropagationEntry> changes) {
		// for rules we check all rules sequentally
		for (PropagationEntry change : changes) {
			this.propagate(theCase, change.getObject());
		}
	}
}
