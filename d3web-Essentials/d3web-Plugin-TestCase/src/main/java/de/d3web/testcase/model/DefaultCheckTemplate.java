/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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

package de.d3web.testcase.model;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.QuestionValue;
import de.d3web.core.session.Value;
import de.d3web.core.session.ValueUtils;
import de.d3web.testcase.stc.DerivedQuestionCheck;
import de.d3web.testcase.stc.DerivedSolutionCheck;

/**
 * Template for checks simply testing the value of one object.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 30.10.15
 */
public class DefaultCheckTemplate implements CheckTemplate {

	private final String objectName;
	private final String value;

	public DefaultCheckTemplate(String objectName, String value) {
		this.objectName = objectName;
		this.value = value;
	}

	public String getObjectName() {
		return objectName;
	}

	public String getValue() {
		return value;
	}

	@Override
	public Check toCheck(KnowledgeBase knowledgeBase) throws TransformationException {
		TerminologyObject object = knowledgeBase.getManager().search(objectName);
		if (object == null) {
			throw new TransformationException("Object '" + objectName + "' not found in given knowledge base");
		}
		Value value;
		try {
			value = ValueUtils.createValue(object, this.value);
		}
		catch (IllegalArgumentException e) {
			throw new TransformationException(e.getMessage());
		}
		if (object instanceof Question && value instanceof QuestionValue) {
			return new DerivedQuestionCheck((Question) object, (QuestionValue) value);
		}
		else if (object instanceof Solution && value instanceof Rating) {
			return new DerivedSolutionCheck((Solution) object, (Rating) value);
		}
		throw new TransformationException("Unsupported object and value type: "
				+ objectName + " (" + object.getClass().getName() + "), "
				+ this.value + " (" + value.getClass().getName() + ")");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DefaultCheckTemplate that = (DefaultCheckTemplate) o;

		if (objectName != null ? !objectName.equals(that.objectName) : that.objectName != null) return false;
		return value != null ? value.equals(that.value) : that.value == null;

	}

	@Override
	public int hashCode() {
		int result = objectName != null ? objectName.hashCode() : 0;
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}
}
