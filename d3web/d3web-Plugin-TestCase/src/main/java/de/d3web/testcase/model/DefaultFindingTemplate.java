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
import de.d3web.core.session.Value;
import de.d3web.core.session.ValueUtils;

/**
 * Template defining a {@link Finding}.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 27.10.15
 */
public class DefaultFindingTemplate implements FindingTemplate {

	private final String objectName;
	private final String value;

	public DefaultFindingTemplate(String objectName, String value) {
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
	public Finding toFinding(KnowledgeBase knowledgeBase) throws TransformationException {
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
		return new DefaultFinding(object, value);
	}
}
