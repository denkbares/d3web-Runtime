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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.inference.condition.CondTextEqual;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QuestionText;

/**
 * FragmentHandler for CondTextEquals
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class TextEqualConditionHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkCondition(element, "textEqual");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof CondTextEqual);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		String questionID = element.getAttribute("name");
		if (questionID != null) {
			NamedObject idObject = kb.getManager().search(questionID);
			if (idObject instanceof QuestionText) {
				QuestionText q = (QuestionText) idObject;
				NodeList childNodes = element.getChildNodes();
				Node item = childNodes.item(0);
				if (item != null && childNodes.getLength() == 1) {
					return new CondTextEqual(q, item.getTextContent());
				}
			}
		}
		return null;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		CondTextEqual cond = (CondTextEqual) object;
		return XMLUtil.writeConditionWithValueNode(doc, cond.getQuestion(), "textEqual",
				cond.getValue());
	}

}
