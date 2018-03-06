/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.interview.inference.condition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.TerminalCondition;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.interviewmanager.InterviewAgenda;
import de.d3web.core.session.values.Unknown;
import de.d3web.interview.Interview;
import de.d3web.interview.inference.PSMethodInterview;

/**
 * This condition checks, if an NamedObject (e.g. Question) has a value or was
 * answered with {@link Unknown} AFTER it was indicated, ie a value has
 * been set after the latest indication of the question.
 *
 * @author Reinhard Hatko
 * @created 02.03.2011
 */
public class CondRepeatedAnswered extends TerminalCondition {

	/**
	 * Creates a new CondRepeatedAnswered object for the given {@link Question}.
	 *
	 * @param qaSet the given question
	 */
	public CondRepeatedAnswered(@NotNull QASet qaSet) {
		super(Objects.requireNonNull(qaSet));
	}

	public static Condition create(Collection<? extends TerminologyObject> objects) {
		List<Condition> conditions = new ArrayList<>();
		for (TerminologyObject object : objects) {
			if (object instanceof QASet) {
				conditions.add(new CondRepeatedAnswered((QASet) object));
			}
			else {
				throw new IllegalArgumentException("Unsupported object type " + object);
			}
		}
		return conditions.size() == 1 ? conditions.get(0) : new CondAnd(conditions);
	}

	@NotNull
	public QASet getQASet() {
		return getTerminalObjects() instanceof List
				? (QASet) ((List) getTerminalObjects()).get(0)
				: (QASet) getTerminalObjects().iterator().next();
	}

	@Override
	public boolean eval(Session session) {
		Interview interview = session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class));
		boolean repeatedIndicated = session.getBlackboard()
				.getIndication(getQASet())
				.hasState(Indication.State.REPEATED_INDICATED);
		boolean active = interview.getInterviewAgenda().hasState(getQASet(), InterviewAgenda.InterviewState.ACTIVE);
		return repeatedIndicated && !active;
	}

	@Override
	public String toString() {
		return getQASet().getName()
				+ " = repeatedly answered";
	}
}
