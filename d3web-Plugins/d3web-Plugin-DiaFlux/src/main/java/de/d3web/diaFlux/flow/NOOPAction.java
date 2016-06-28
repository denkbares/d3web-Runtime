/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;

/**
 * 
 * @author Reinhard Hatko
 * @created 25.11.2010
 */
public final class NOOPAction extends PSAction {

	private final List<TerminologyObject> objects = new LinkedList<>();

	public NOOPAction() {
	}

	public NOOPAction(TerminologyObject termObj) {
		objects.add(termObj);
	}

	@Override
	public void doIt(Session session, Object source, PSMethod psmethod) {
	}

	@Override
	public List<? extends TerminologyObject> getForwardObjects() {
		return objects;
	}

	@Override
	public List<? extends TerminologyObject> getBackwardObjects() {
		return Collections.emptyList();
	}

	@Override
	public void undo(Session session, Object source, PSMethod psmethod) {
	}

	@Override
	public String toString() {
		return "NOOP" + objects;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		NOOPAction that = (NOOPAction) o;
		return objects.equals(that.objects);
	}

	@Override
	public int hashCode() {
		return objects.hashCode();
	}
}
