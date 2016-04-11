/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.core.io.fragments.conditions;

import java.io.IOException;

import org.w3c.dom.Element;

import de.d3web.core.inference.condition.CondRegex;
import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;

/**
 * FragmentHandler for CondEquals It can also read choiceYes and choiceNo
 * elements of former persistence versions
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class RegexConditionHandler implements FragmentHandler<KnowledgeBase> {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkCondition(element, "matches");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof CondRegex);
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		String questionID = element.getAttribute("name");
		String regex = element.getAttribute("regex");
		Question idObject = persistence.getArtifact().getManager().searchQuestion(questionID);
		if (questionID == null) throw new IOException("no such question " + questionID);
		if (regex == null) throw new IOException("missing regex for question" + questionID);
		return new CondRegex(idObject, regex);
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		CondRegex cond = (CondRegex) object;
		Element element = XMLUtil.writeCondition(
				persistence.getDocument(), cond.getQuestion(), "matches");
		element.setAttribute("regex", cond.getRegex());
		return element;
	}

}
