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
package de.d3web.xcl.io.fragments;

import java.io.IOException;

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.xcl.DefaultScoreAlgorithm;

public class DefaultScoreAlgorithmHandler implements FragmentHandler<KnowledgeBase> {

	private static final String DEFAULT_SCORE_ALGORITHM = "DefaultScoreAlgorithm";
	public static final String SCORE_ALGORITHM = "scoreAlgorithm";

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals(SCORE_ALGORITHM)
				&& element.getAttribute("name").equals(DEFAULT_SCORE_ALGORITHM);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof DefaultScoreAlgorithm;
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		DefaultScoreAlgorithm algorithm = new DefaultScoreAlgorithm();
		for (Element e : XMLUtil.getElementList(element.getChildNodes())) {
			if (e.getNodeName().equals("defaultEstablishedThreshold")) {
				algorithm.setDefaultEstablishedThreshold(Double.parseDouble(e.getAttribute("value")));
			}
			else if (e.getNodeName().equals("defaultSuggestedThreshold")) {
				algorithm.setDefaultSuggestedThreshold(Double.parseDouble(e.getAttribute("value")));
			}
			else if (e.getNodeName().equals("minSupport")) {
				algorithm.setDefaultMinSupport(Double.parseDouble(e.getAttribute("value")));
			}
		}
		return algorithm;
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		Element e = persistence.getDocument().createElement(SCORE_ALGORITHM);
		e.setAttribute("name", DEFAULT_SCORE_ALGORITHM);
		DefaultScoreAlgorithm algorithm = (DefaultScoreAlgorithm) object;
		Element established = persistence.getDocument().createElement("defaultEstablishedThreshold");
		established.setAttribute("value", "" + algorithm.getDefaultEstablishedThreshold());
		Element suggested = persistence.getDocument().createElement("defaultSuggestedThreshold");
		suggested.setAttribute("value", "" + algorithm.getDefaultSuggestedThreshold());
		Element minSupport = persistence.getDocument().createElement("minSupport");
		minSupport.setAttribute("value", "" + algorithm.getDefaultMinSupport());
		e.appendChild(established);
		e.appendChild(suggested);
		e.appendChild(minSupport);
		return e;
	}

}
