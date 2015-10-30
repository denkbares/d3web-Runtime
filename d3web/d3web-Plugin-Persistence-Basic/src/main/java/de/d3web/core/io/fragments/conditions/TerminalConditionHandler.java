/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Element;

import de.d3web.core.inference.condition.TerminalCondition;
import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.utilities.NamedObjectComparator;

/**
 * Abstract handler to read and write TerminalConditions.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 30.06.15
 */
public abstract class TerminalConditionHandler implements FragmentHandler<KnowledgeBase> {

	private final Class<? extends TerminalCondition> condClass;
	private final String saveKey;

	public TerminalConditionHandler(Class<? extends TerminalCondition> condClass, String saveKey) {
		this.condClass = condClass;
		this.saveKey = saveKey;
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		List<Element> kbchildren = XMLUtil.getElementList(element.getChildNodes(), "object");
		List<TerminologyObject> objects = new ArrayList<>();
		for (Element child : kbchildren) {
			String name = child.getTextContent();
			TerminologyObject object = persistence.getArtifact().getManager().search(name);
			if (object == null) throw new IOException("Object '" + name + "' not found");
			objects.add(object);
		}
		return createCondition(objects);
	}

	/**
	 * Creates the terminal condition with the given objects.
	 *
	 * @param objects the terminal objects of the condition to create
	 * @return a new instance of the terminal condition
	 */
	public abstract TerminalCondition createCondition(List<TerminologyObject> objects);

	@Override
	public Element write(Object condition, Persistence<KnowledgeBase> persistence) throws IOException {
		TerminalCondition cond = (TerminalCondition) condition;
		Element element = XMLUtil.writeCondition(persistence.getDocument(), saveKey);
		List<NamedObject> terminalObjects = new ArrayList<>(cond.getTerminalObjects());
		Collections.sort(terminalObjects, new NamedObjectComparator());
		for (NamedObject object : terminalObjects) {
			Element objectElement = persistence.getDocument().createElement("object");
			objectElement.setTextContent(object.getName());
			element.appendChild(objectElement);
		}
		return element;
	}

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkCondition(element, saveKey);
	}

	@Override
	public boolean canWrite(Object object) {
		return condClass.equals(object.getClass());
	}

}
