/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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

import de.d3web.core.inference.condition.CondSolutionConfirmed;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.IDObject;
import de.d3web.core.knowledge.terminology.Solution;

/**
 * FragmentHandler for CondSolutionConfirmed
 * 
 * @author Reinhard Hatko
 * @created 22.11.2010
 */
public class ConditionSolutionConfirmedHandler implements FragmentHandler {

	public static final String TYPE = "solutionConfirmed";

	@Override
	public boolean canRead(Element node) {
		return XMLUtil.checkCondition(node, TYPE);
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof CondSolutionConfirmed);
	}

	@Override
	public Object read(KnowledgeBase kb, Element node) throws IOException {
		String solutionID = node.getAttribute("ID");
		if (solutionID != null) {
			IDObject idObject = kb.search(solutionID);
			if (idObject instanceof Solution) {
				Solution s = (Solution) idObject;
				return new CondSolutionConfirmed(s);
			}
		}
		return null;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		CondSolutionConfirmed cond = (CondSolutionConfirmed) object;
		return XMLUtil.writeCondition(doc, cond.getSolution(), TYPE);
	}

}
