/*
 * Copyright (C) 2012 denkbares GmbH, Germany
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
package de.d3web.core.session;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactStorage;

/**
 * This interface describes instances that are capable to create a deep copy of
 * themselves to create a duplicate of a session. "Deep copy" means that they
 * create a duplicate of itself and all of its contained objects that are not
 * immutable by altering the session.
 * <p>
 * E.g. objects like {@link TerminologyObject}s, {@link Fact}s, ... are
 * immutable for a session, while {@link Blackboard}, {@link FactStorage}s, ...
 * are not.
 * 
 * @author volker_belli
 * @created 04.06.2012
 */
public interface Copyable<T> {

	/**
	 * Creates a copy of this object, with the goal to have a deep copy of the
	 * current session. A deep copy means that you can used the copied session
	 * (and this copied object within that session), without influencing the
	 * existing session. Therefore this method shall also created a deep copy of
	 * all contained objects that are not immutable during this session.
	 * 
	 * @created 04.06.2012
	 * @return the deep copy
	 */
	T copy();
}
