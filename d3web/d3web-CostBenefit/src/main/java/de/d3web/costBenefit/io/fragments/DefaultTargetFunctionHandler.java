/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.d3web.costBenefit.io.fragments;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.costBenefit.inference.DefaultTargetFunction;

public class DefaultTargetFunctionHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		if (element.getNodeName().equals("targetFunction")
				&& element.getAttribute("name").equals("DefaultTargetFunction")) {
			return true;
		}
		return false;	
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof DefaultTargetFunction;
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		return new DefaultTargetFunction();
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		Element element = doc.createElement("targetFunction");
		element.setAttribute("name", "DefaultTargetFunction");
		return element;
	}

}
