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

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;

/**
 * This class stores {@link Question} instances or (recursively) other
 * {@link QContainer} instances. Typically, this class is used to represent a
 * questionnaire that is jointly presented in a problem-solving session.
 * 
 * @author joba, norman
 * @see QASet
 */
public class QContainer extends QASet {

	/**
	 * Creates a new QContainer and adds it to the knowledgebase, so no manual
	 * adding of the created object to the kb is needed
	 * 
	 * @param kb {@link KnowledgeBase} in which the QContainer should be
	 *        inserted
	 * @param name the name of the new QContainer
	 */
	public QContainer(KnowledgeBase kb, String name) {
		super(kb, name);
	}

	/**
	 * Creates a new QContainer, adds it to the knowledgebase and adds it to
	 * it's parent. No manual adding of the created object to the kb is needed
	 * 
	 * @param parent the parent {@link QASet}
	 * @param name the name of the new QContainer
	 */
	public QContainer(QASet parent, String name) {
		this(parent.getKnowledgeBase(), name);
		parent.addChild(this);
	}

	@Override
	public boolean isQuestionOrHasQuestions() {
		for (TerminologyObject child : this.getChildren()) {
			if (child instanceof QASet) {
				if (((QASet) child).isQuestionOrHasQuestions()) {
					return true;
				}
			}
		}
		return false;

	}
}
