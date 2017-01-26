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
package de.d3web.interview;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.UndefinedValue;

/**
 * This utility class is basically introduced to define some helper methods,
 * that are commonly used by implementations of {@link FormStrategy}.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 26.01.2017
 */
public final class FormStrategyUtils {

	private final Session session;
	private boolean inheritIndication = true;

	public FormStrategyUtils(Session session) {
		this.session = session;
	}

	/**
	 * Returns the associated session this utils instance is created for.
	 *
	 * @return the underlying session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Returns true if the object id directly indicated in a positive manner.
	 *
	 * @param object the object to be tested
	 * @return true if the object has currently a positive indication state
	 */
	public boolean isIndicated(InterviewObject object) {
		return session.getBlackboard().getIndication(object).isRelevant();
	}

	/**
	 * Sets if a qContainer is relevant when it is in a relevant parent container, so that
	 * it inherits the indication from the parent container to the child container. This is the
	 * default behaviour in d3web, but may be changed by some other interview strategy.
	 * <p>
	 * The default value is true, reflecting the d3web default behaviour
	 *
	 * @param inheritIndication true if the container inherits its indication from a indicated
	 * parent container
	 */
	public void setInheritIndication(boolean inheritIndication) {
		this.inheritIndication = inheritIndication;
	}

	/**
	 * Helper method to check, if a {@link Value} is assigned to the specified
	 * {@link Question} instance in the specified {@link Session} other than
	 * {@link UndefinedValue}.
	 *
	 * @param question the specified {@link Question} instance
	 * @return true, when the specified question has a value other than {@link UndefinedValue}
	 */
	public boolean hasValueUndefined(Question question) {
		Value value = session.getBlackboard().getValue(question);
		return (value instanceof UndefinedValue);
	}

	/**
	 * Returns true if any of the questions remains unanswered (by any problem solver!) in the
	 * specified session. In this case the questions are not totally completed. Returns false if the
	 * questions are completely answered and therefore.
	 *
	 * @param questions the questions to be checked
	 * @return if any question is unanswered and the list is completed
	 */
	public boolean hasAnyValueUndefined(Collection<Question> questions) {
		return questions.stream().anyMatch(this::hasValueUndefined);
	}

	public boolean isActive(Question question) {
		return BasicProperties.isAlwaysVisible(question)
				? isInRelevantContainer(question, new HashSet<>())
				: isRelevant(question, new HashSet<>());
	}

	public boolean isForcedActive(Question question) {
		return BasicProperties.isAlwaysVisible(question) && !isRelevant(question, new HashSet<>());
	}

	private boolean isRelevant(InterviewObject object, Set<TerminologyObject> processed) {
		// a object is relevant, if itself is relevant
		if (session.getBlackboard().getIndication(object).isRelevant()) return true;

		// if the container indication does not inherit,
		// we can stop if the container is not always visible (because it is not indicated, tested above)
		if ((object instanceof QContainer) && !inheritIndication && !BasicProperties.isAlwaysVisible(object)) {
			return false;
		}

		// or if any qcontainer (in which this object is a root object) is relevant
		for (TerminologyObject parent : object.getParents()) {
			if (parent instanceof QContainer) {
				QContainer container = (QContainer) parent;
				if (processed.add(container) && isRelevant(container, processed)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isInRelevantContainer(Question question, Set<TerminologyObject> processed) {
		// go upwards to the closest container(s) and check if they are relevant
		for (TerminologyObject parent : question.getParents()) {
			if (!processed.add(parent)) continue;
			if (parent instanceof Question) {
				if (isInRelevantContainer((Question) parent, processed)) {
					return true;
				}
			}
			else if (parent instanceof QContainer) {
				if (isRelevant((InterviewObject) parent, processed)) {
					return true;
				}
			}
		}
		return false;
	}
}
