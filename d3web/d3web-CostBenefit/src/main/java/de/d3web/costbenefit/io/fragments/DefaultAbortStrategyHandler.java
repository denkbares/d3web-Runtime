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
package de.d3web.costbenefit.io.fragments;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.costbenefit.inference.DefaultAbortStrategy;
/**
 * FragementHandler for DefaultAbortStrategy
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class DefaultAbortStrategyHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		if (element.getNodeName().equals("abortStrategy")
				&& element.getAttribute("name").equals("DefaultAbortStrategy")) {
			return true;
		}
		return false;	
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof DefaultAbortStrategy;
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		String maxsteps = element.getAttribute("maxsteps");
		if (maxsteps.length()!= 0) {
			return new DefaultAbortStrategy(Integer.parseInt(maxsteps));
		}
		return new DefaultAbortStrategy();
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		DefaultAbortStrategy strategie = (DefaultAbortStrategy) object;
		Element element = doc.createElement("abortStrategy");
		element.setAttribute("name", "DefaultAbortStrategy");
		element.setAttribute("maxsteps", "" + strategie.getMaxsteps());
		return element;
	}

}
