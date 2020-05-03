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

import org.jetbrains.annotations.Nullable;

import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.DefaultBlackboard;
import de.d3web.core.session.blackboard.Fact;

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
		setSourceRecording(false);
		setValueStorage(new DecoratedFactStorage(getValueStorage()));
		setInterviewStorage(new DecoratedFactStorage(getInterviewStorage()));
	}

	/**
	 * Returns the decorated value of a specified {@link ValueObject}. This
	 * value is defined by a fact contained in this {@link DecoratedBlackboard}
	 * or any decorated blackboard that is decorated by this instance, but not
	 * the underlying original session. If there is no value covered by any
	 * decorated blackboard in the sequence, null is returned.
	 * 
	 * @created 05.06.2012
	 * @param object the object to get the decorated value for
	 * @return the decorated value or null if not decorated
	 */
	@Nullable
	public Value getDecoratedValue(ValueObject object) {
		Fact fact = ((DecoratedFactStorage) getValueStorage()).getDecoratedMergedFact(object);
		return (fact == null) ? null : fact.getValue();
	}

	/**
	 * Returns the decorated value of a specified {@link ValueObject} if (and
	 * only if) the value has been changed compared to the original underlying
	 * (non-decorating) session. If there is no value covered by any decorated
	 * blackboard in the sequence, null is returned. If the value is covered,
	 * but is equal to the original one, also null is returned. Only if the
	 * value is covered and differs from the original one, the value is
	 * returned.
	 * 
	 * @created 05.06.2012
	 * @param object the object to get the changed value for
	 * @return the new value or null if unchanged
	 */
	@Nullable
	public Value getChangedValue(ValueObject object) {
		Value value = getDecoratedValue(object);
		if (value != null) {
			Session root = ((DecoratedSession) getSession()).getRootSession();
			Value original = root.getBlackboard().getValue(object);
			if (original.equals(value)) return null;
		}
		return value;
	}

	/**
	 * Returns if an object hat the same (equal) value in two decorating
	 * blackboards.
	 * 
	 * @created 05.06.2012
	 * @param object the object to test the value for
	 * @param board1 the first blackboard to test the value in
	 * @param board2 the second blackboard to test the value in
	 */
	public static boolean hasEqualValue(ValueObject object, DecoratedBlackboard board1, DecoratedBlackboard board2) {
		Value value1 = board1.getDecoratedValue(object);
		Value value2 = board2.getDecoratedValue(object);
		if (value1 == value2) return true;
		// if only one value is null, we require that one from the root session
		if (value1 == null) {
			Session root = ((DecoratedSession) board2.getSession()).getRootSession();
			value1 = root.getBlackboard().getValue(object);
		}
		else if (value2 == null) {
			Session root = ((DecoratedSession) board1.getSession()).getRootSession();
			value2 = root.getBlackboard().getValue(object);
		}
		return value1.equals(value2);
	}
}
