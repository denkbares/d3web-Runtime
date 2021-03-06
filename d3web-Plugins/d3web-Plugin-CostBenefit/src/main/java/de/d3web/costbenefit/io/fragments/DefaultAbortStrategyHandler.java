/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.d3web.costbenefit.io.fragments;

import java.io.IOException;

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.costbenefit.inference.DefaultAbortStrategy;

/**
 * FragementHandler for DefaultAbortStrategy
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class DefaultAbortStrategyHandler implements FragmentHandler<KnowledgeBase> {

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals("abortStrategy")
				&& element.getAttribute("name").equals("DefaultAbortStrategy");
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof DefaultAbortStrategy;
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		String maxsteps = element.getAttribute("maxsteps");
		String increasingFactor = element.getAttribute("increasingFactor");
		if (!maxsteps.isEmpty() && increasingFactor.isEmpty()) {
			return new DefaultAbortStrategy(Integer.parseInt(maxsteps));
		}
		else if (!maxsteps.isEmpty() && !increasingFactor.isEmpty()) {
			return new DefaultAbortStrategy(
					Integer.parseInt(maxsteps),
					Float.parseFloat(increasingFactor));
		}
		return new DefaultAbortStrategy();
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		DefaultAbortStrategy strategie = (DefaultAbortStrategy) object;
		Element element = persistence.getDocument().createElement("abortStrategy");
		element.setAttribute("name", "DefaultAbortStrategy");
		element.setAttribute("maxsteps", "" + strategie.getMaxSteps());
		element.setAttribute("increasingFactor", "" + strategie.getIncreasingFactor());
		return element;
	}

}
