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

package de.d3web.core.inference;

import java.util.Collection;

import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;

/**
 * This interface defines the strategic support of an problem solving method (
 * {@link PSMethod}). Strategic support allows strategic solvers to access some
 * information about the information gain of possible questions. The strategic
 * solvers then can use these information to decide on the next questions to be
 * asked.
 * 
 * @author volker_belli
 * @created 07.03.2011
 * @see PSMethod
 */
public interface StrategicSupport {

	/**
	 * Returns the not yet discriminated solutions that shall be still taken
	 * into account when selecting the questions to be asked. Usually the set of
	 * solutions are selected in a way that makes sure that the correct solution
	 * for the specified session is in set.
	 * 
	 * @created 07.03.2011
	 * @param session the session to return the solutions for
	 * @return the set of possible solutions
	 */
	Collection<Solution> getUndiscriminatedSolutions(Session session);

	/**
	 * Returns a set of questions that are able to discriminate the specified
	 * solutions. To do this, the problem solver looks at it knowledge slices
	 * and examines which of the existing questions are capable to derive
	 * different ratings to some of the specified solutions.
	 * <p>
	 * <b>Note:</b> The solutions specified are usually a subset (or all) of the
	 * solutions returned by {@link #getUndiscriminatedSolutions(Session)}.
	 * 
	 * @created 07.03.2011
	 * @param solutions the solutions to be discriminated
	 * @param session the session to discriminate in
	 * @return the set of discriminating questions.
	 */
	Collection<Question> getDiscriminatingQuestions(Collection<Solution> solutions, Session session);

	/**
	 * Returns the expected information gain to be obtained when <b>all</b> of
	 * the specified {@link QASet}s have been answered. The informations gain is
	 * rated based on a set of solutions specified as well.
	 * <p>
	 * <b>Note:</b> The solutions specified are usually a subset (or all) of the
	 * solutions returned by {@link #getUndiscriminatedSolutions(Session)}.
	 * 
	 * @created 07.03.2011
	 * @param qasets the question / qcontainers going to be answered
	 * @param solutions the solutions to be discriminated
	 * @param session the session to discriminate in
	 * @return the (expected) information gain
	 */
	double getInformationGain(Collection<? extends QASet> qasets, Collection<Solution> solutions, Session session);
}
