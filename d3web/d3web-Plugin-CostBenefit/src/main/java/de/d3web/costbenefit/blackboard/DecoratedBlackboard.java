/*
 * Copyright (C) 2011 denkbares GmbH, Germany
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
package de.d3web.costbenefit.blackboard;

import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.DefaultBlackboard;

/**
 * This class represents a Blackboard that is capable to provide basic
 * capabilities to evaluate conditions on it and add facts to it.
 * <p>
 * The DecoratedBlackboard itself decorates an existing {@link Blackboard}, like
 * a glass panel. You can read the values from the session decorated one and
 * overwrite them by setting new facts.
 * <p>
 * Please not that this class does not fully implement the {@link Blackboard}
 * interface. It only provides the method required for searching. All other
 * methods throws an {@link UnsupportedOperationException}.
 * 
 * @author volker_belli
 * @created 16.09.2011
 */
public class DecoratedBlackboard extends DefaultBlackboard {

	public DecoratedBlackboard(DecoratedSession session, DefaultBlackboard decoratedBlackboard) {
		super(session, decoratedBlackboard);
		setAutosaveSource(false);
		setValueStorage(new DecoratedFactStorage(getValueStorage()));
		setInterviewStorage(new DecoratedFactStorage(getInterviewStorage()));
	}
}
