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

package de.d3web.indication;

import java.util.ArrayList;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.PSAction;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.session.Session;

/**
 * Indicates a QASet like ActionIndication, but for QContainers: Then, the
 * QContainer is inserted directly after the current QConatiner (i.e., instant
 * indication).
 * 
 * @author baumeister
 * @see ActionIndication
 */
public class ActionInstantIndication extends ActionIndication {

	public ActionInstantIndication() {
		super();
	}

	public PSAction copy() {
		ActionInstantIndication a = new ActionInstantIndication();
		a.setQASets(new ArrayList<QASet>(getQASets()));
		return a;
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o instanceof ActionInstantIndication) {
			ActionInstantIndication a = (ActionInstantIndication) o;
			return isSame(a.getQASets(), getQASets());
		}
		else return false;
	}

	@Override
	public void doIt(Session session, Object source, PSMethod psmethod) {
		doItWithContext(session, source);
	}

}
