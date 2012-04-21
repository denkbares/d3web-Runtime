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
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.inference.condition.ConditionTrue;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;

/**
 * 
 * @author Reinhard Hatko
 * @created 11.11.2010
 */
public class ConditionTrueFragmentHandler implements FragmentHandler {

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		List<Element> kbchildren = XMLUtil.getElementList(element.getChildNodes(), "object");
		if (kbchildren.isEmpty()) return ConditionTrue.INSTANCE;
		List<TerminologyObject> objects = new LinkedList<TerminologyObject>();
		for (Element child : kbchildren) {
			String name = child.getTextContent();
			TerminologyObject object = kb.getManager().search(name);
			if (object == null) throw new IOException("Object '" + name + "' not found");
			objects.add(object);
		}
		return new ConditionTrue(objects);
	}

	@Override
	public Element write(Object condition, Document doc) throws IOException {
		ConditionTrue cond = (ConditionTrue) condition;
		Element element = XMLUtil.writeCondition(doc, "True");
		for (TerminologyObject object : cond.getTerminalObjects()) {
			Element objectElement = doc.createElement("object");
			objectElement.setTextContent(object.getName());
			element.appendChild(objectElement);
		}
		return element;
	}

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkCondition(element, "True");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof ConditionTrue);
	}

}
