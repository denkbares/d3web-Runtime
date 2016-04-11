/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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

package de.d3web.core.inference.condition;

import java.util.Collection;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.UndefinedValue;

/**
 * A terminal condition that checks whether at least one of the given connected objects is properly defined. For
 * questions, there has to be a value other than Undefined, for Solutions there has to be a State other than UNCLEAR.
 * <p/>
 * This class is not intended to be used directly by users. In contrast it is a
 * utility condition to handle specific problem solver issues.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 30.06.15
 */
public class CondDefined extends TerminalCondition {

	public CondDefined(TerminologyObject... connectedObjects) {
		super(connectedObjects);
	}

	public CondDefined(Collection<? extends TerminologyObject> connectedObjects) {
		super(connectedObjects);
	}

	@Override
	public boolean eval(Session session) throws NoAnswerException, UnknownAnswerException {
		Collection<? extends TerminologyObject> terminalObjects = getTerminalObjects();
		if (terminalObjects.isEmpty()) return true;
		for (TerminologyObject terminalObject : terminalObjects) {
			if (terminalObject instanceof Question) {
				Value value = session.getBlackboard().getValue((ValueObject) terminalObject);
				if (value != null && !(value instanceof UndefinedValue)) {
					return true;
				}
			}
			else if (terminalObject instanceof Solution) {
				Value value = session.getBlackboard().getValue((ValueObject) terminalObject);
				if (value instanceof Rating && ((Rating) value).getState() != Rating.State.UNCLEAR) {
					return true;
				}
			}
		}
		return false;
	}
}
