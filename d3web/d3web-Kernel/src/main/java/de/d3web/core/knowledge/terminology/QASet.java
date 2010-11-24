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

package de.d3web.core.knowledge.terminology;

import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;

/**
 * This is a container to store Questions or Sets of Questions. <BR>
 * Composite Design Pattern: <LI>Component : QASet <LI>QContainer : Composite
 * <LI>Question : (abstract) Leaf <BR>
 * 
 * @author joba, Christian Betz, norman
 * @see QContainer
 * @see Question
 */
public abstract class QASet extends NamedObject implements InterviewObject {

	public QASet(String id) {
		super(id);
	}

	@Override
	public Indication getDefaultInterviewValue() {
		return new Indication(State.NEUTRAL);
	}

	/**
	 * Checks, if this QASet is a question or a non-empty QContainer (non-empty
	 * means that the QContainer has to have at least one question as a direct
	 * or recursive child).
	 * 
	 * @created 24.11.2010
	 * @return true, if this QASet is a question or a non-empty QContainer.
	 *         false, otherwise.
	 */
	public boolean isQuestionOrHasQuestions() {
		if (this instanceof Question) {
			return true;
		}
		else if (this instanceof QContainer) {
			for (TerminologyObject child : this.getChildren()) {
				if (child instanceof QASet) {
					return ((QASet) child).isQuestionOrHasQuestions();
				}
			}
		}
		return false;
	}
}