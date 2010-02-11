/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.core.kpers.fragments.conditions;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.KnowledgeBase;
import de.d3web.core.inference.condition.CondNum;
import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.kpers.fragments.FragmentHandler;
import de.d3web.core.kpers.utilities.XMLUtil;
import de.d3web.core.terminology.IDObject;
import de.d3web.core.terminology.QuestionNum;
/**
 * FragmentHandler for CondNumGreaters
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class NumGreaterConditionHandler implements FragmentHandler{
	
	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkCondition(element, "numGreater");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof CondNumGreater);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		String questionID = element.getAttribute("ID");
		String value = element.getAttribute("value");
		if (questionID!=null && value != null) {
			IDObject idObject = kb.search(questionID);
			if (idObject instanceof QuestionNum) {
				QuestionNum q = (QuestionNum) idObject;
				return new CondNumGreater(q, Double.parseDouble(value));
			}
		}
		return null;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		CondNum cond = (CondNum) object;
		return XMLUtil.writeCondition(doc, cond.getQuestion(), "numGreater", cond.getAnswerValue().toString());
	}

}
