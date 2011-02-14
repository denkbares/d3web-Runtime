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

import java.util.Collection;

import de.d3web.abstraction.inference.PSMethodAbstraction;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.indication.inference.PSMethodStrategic;

/**
 * This is an abstract class, that stores the static parts of a question
 * (symptom, input) independent from the dynamic session-specific values. This
 * class is part of the applied design pattern <i>Composite</i> (including
 * {@link QASet} and {@link QContainer}).
 * 
 * @author joba, norman
 * @see QASet
 * @see DerivationType
 */
public abstract class Question extends QASet implements ValueObject {

	private static final Class<? extends PSMethod> QUESTION_SETTER = PSMethodAbstraction.class;
	private static final Class<? extends PSMethod> FOLLOW_QUESTION = PSMethodStrategic.class;
	private static final MethodKind KIND = MethodKind.BACKWARD;

	/**
	 * Creates a new Question and adds it to the knowledgebase, so no manual
	 * adding of the created object to the kb is needed
	 * 
	 * @param kb {@link KnowledgeBase} in which the Question should be inserted
	 * @param name the name of the new Question
	 */
	public Question(KnowledgeBase kb, String name) {
		super(kb, name);
	}

	/**
	 * DerivationType ({BASIC, DERIVED, MIXED}) means: <LI>BASIC: question
	 * should be asked in a basic dialogue <LI>DERIVED: question should not be
	 * asked, but is set by a RuleSymptom <LI>MIXED: both, should be asked but
	 * can also be derived
	 * 
	 * @return de.d3web.kernel.domainModel.DerivationType a static instance of
	 *         DerivationType
	 */
	public DerivationType getDerivationType() {
		if (hasElements(getKnowledgeStore().getKnowledge(QUESTION_SETTER, KIND))
				&& hasElements(getKnowledgeStore().getKnowledge(FOLLOW_QUESTION, KIND))) {
			return DerivationType.MIXED;
		}
		else if (hasElements(getKnowledgeStore().getKnowledge(QUESTION_SETTER, KIND))) {
			return DerivationType.DERIVED;
		}
		else {
			return DerivationType.BASIC;
		}
	}

	/**
	 * Checks, if the given Object is a not-empty Collection.
	 * 
	 * @created 01.10.2010
	 */
	private boolean hasElements(Object list) {
		// Left side of short-circuit operator "||" returns true if list is NOT
		// a Collection. If list is a collection, the right side of "||" checks
		// if this list is NOT empty.
		return !(list instanceof Collection<?>) || !((Collection<?>) list).isEmpty();
	}

	@Override
	public Value getDefaultValue() {
		return UndefinedValue.getInstance();
	}
}