/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.diaFlux.io;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.diaFlux.flow.NOOPAction;

/**
 * @author Reinhard Hatko
 * @created 11.01.2011
 */
public class NOOPActionHandler implements FragmentHandler<KnowledgeBase> {

	private static final String NOOP = "NOOP";
	private static final String NOOP_OBJECT = "Object";

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		List<Element> children = XMLUtil.getChildren(element);
		if (children.size() > 1) {
			throw new IOException("Cannot handle NOOPAction with more than one forward object");
		}
		if (children.isEmpty()) {
			return new NOOPAction();
		}
		else {
			Element objectElement = children.get(0);
			String name = objectElement.getAttribute("name");
			TerminologyObject object = persistence.getArtifact().getManager().search(name);
			if (object == null) {
				throw new IOException("Object with name '" + name + " not found (trying to create NOOPAction)");
			}
			return new NOOPAction(object);
		}
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		Element actionElem = persistence.getDocument().createElement(ActionNodeFragmentHandler.ACTION);
		actionElem.setAttribute("type", NOOP);
		List<? extends TerminologyObject> forwardObjects = ((NOOPAction) object).getForwardObjects();
		if (forwardObjects.size() > 1) {
			throw new IOException("Cannot handle NOOPAction with more than one forward object");
		}
		else if (forwardObjects.size() == 1) {
			Element element = persistence.getDocument().createElement(NOOP_OBJECT);
			element.setAttribute("name", forwardObjects.get(0).getName());
			actionElem.appendChild(element);
		}
		return actionElem;
	}

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, "Action", NOOP);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof NOOPAction;
	}

}
