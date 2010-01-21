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

import de.d3web.core.kpers.fragments.FragmentHandler;
import de.d3web.core.kpers.utilities.XMLUtil;
import de.d3web.kernel.domainModel.IDObject;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.ruleCondition.CondUnknown;
/**
 * FragmentHandler for CondUnknowns
 * @author Markus Friedrich (denkbares GmbH)
 */
public class UnknownConditionHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element node) {
		return XMLUtil.checkCondition(node, "unknown");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof CondUnknown);
	}

	@Override
	public Object read(KnowledgeBase kb, Element node) throws IOException {
		String questionID = node.getAttribute("ID");
		if (questionID!=null) {
			IDObject idObject = kb.search(questionID);
			if (idObject instanceof Question) {
				Question q = (Question) idObject;
				return new CondUnknown(q);
			}
		}
		return null;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		CondUnknown cond = (CondUnknown) object;
		return XMLUtil.writeCondition(doc, cond.getQuestion(), "unknown");
	}

}
