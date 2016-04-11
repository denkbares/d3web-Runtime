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

import de.d3web.core.inference.condition.CondUnknown;
import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.Question;

/**
 * FragmentHandler for CondUnknowns
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class UnknownConditionHandler implements FragmentHandler<KnowledgeBase> {

	@Override
	public boolean canRead(Element node) {
		return XMLUtil.checkCondition(node, "unknown");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof CondUnknown);
	}

	@Override
	public Object read(Element node, Persistence<KnowledgeBase> persistence) throws IOException {
		String questionID = node.getAttribute("name");
		if (questionID != null) {
			NamedObject idObject = persistence.getArtifact().getManager().search(questionID);
			if (idObject instanceof Question) {
				Question q = (Question) idObject;
				return new CondUnknown(q);
			}
		}
		return null;
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		CondUnknown cond = (CondUnknown) object;
		return XMLUtil.writeCondition(persistence, cond.getQuestion(), "unknown");
	}

}
