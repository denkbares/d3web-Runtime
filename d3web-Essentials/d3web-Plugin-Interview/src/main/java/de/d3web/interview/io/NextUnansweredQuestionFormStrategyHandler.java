/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.d3web.interview.io;

import java.io.IOException;

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.interview.NextUnansweredQuestionFormStrategy;

/**
 * Handels {@link NextUnansweredQuestionFormStrategy}
 *
 * @author Markus Friedrich (denkbares GmbH)
 * @created 25.03.2013
 */
public class NextUnansweredQuestionFormStrategyHandler implements FragmentHandler<KnowledgeBase> {

	private static final String ELEMENT_NAME = "NextUnansweredQuestionFormStrategy";
	private static final String GROUP_ATTRIBUTE = "groupTopLevelQuestion";

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		NextUnansweredQuestionFormStrategy object = new NextUnansweredQuestionFormStrategy();
		object.setGroupTopLevelQuestions(Boolean.valueOf(element.getAttribute(GROUP_ATTRIBUTE)));
		return object;
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		Element element = persistence.getDocument().createElement(ELEMENT_NAME);
		if (((NextUnansweredQuestionFormStrategy) object).isGroupTopLevelQuestions()) {
			element.setAttribute(GROUP_ATTRIBUTE, String.valueOf(true));
		}
		return element;
	}

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals(ELEMENT_NAME);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof NextUnansweredQuestionFormStrategy;
	}
}
