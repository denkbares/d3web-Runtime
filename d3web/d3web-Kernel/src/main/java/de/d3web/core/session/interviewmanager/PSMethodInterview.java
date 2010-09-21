/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.core.session.interviewmanager;

import java.util.Collection;

import de.d3web.core.inference.PSMethodAdapter;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;

/**
 * This PSMethod is used to notify the {@link Interview} of new facts added to
 * the {@link Session}, i.e., new (contra-)indications and values added to the
 * {@link Blackboard}.
 * 
 * @author joba
 * 
 */
public class PSMethodInterview extends PSMethodAdapter {

	private static PSMethodInterview instance;

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {
		for (PropagationEntry change : changes) {
			session.getInterview().notifyFactChange(change);
		}
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		for (Fact fact : facts) {
			System.out.println(fact);
		}
		// TODO Auto-generated method stub
		return facts[0];
	}

	public static PSMethodInterview getInstance() {
		if (instance == null) {
			instance = new PSMethodInterview();
		}
		return instance;
	}

	@Override
	public boolean hasType(Type type) {
		return type == Type.consumer;
	}

}
