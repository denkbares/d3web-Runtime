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
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.inference.condition.CondNumIn;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.NumericalInterval;

/**
 * FragementHandler for CondNumIns
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class NumInConditionHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkCondition(element, "numIn");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof CondNumIn);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		String questionID = element.getAttribute("name");
		if (questionID != null) {
			NamedObject idObject = kb.getManager().search(questionID);
			if (idObject instanceof QuestionNum) {
				QuestionNum q = (QuestionNum) idObject;
				List<Element> childNodes = XMLUtil.getElementList(element.getChildNodes());
				Element item = childNodes.get(0);
				if (item != null && childNodes.size() == 1) {
					NumericalInterval interval = (NumericalInterval) PersistenceManager.getInstance().readFragment(
							item, kb);
					return new CondNumIn(q, interval);
				}
			}
		}
		return null;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		CondNumIn cond = (CondNumIn) object;
		Element intervalElemenent = PersistenceManager.getInstance().writeFragment(
				cond.getInterval(), doc);
		Element element = XMLUtil.writeConditionWithValueNode(doc, cond.getQuestion(), "numIn",
				intervalElemenent);
		;
		return element;
	}

}
