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
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import de.d3web.core.inference.PSConfig;
import de.d3web.core.io.Persistence;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.plugin.io.fragments.DefaultPSConfigHandler;
import de.d3web.xcl.ScoreAlgorithm;
import de.d3web.xcl.inference.PSMethodXCL;

/**
 * FragmentHandler for the configuration of the PSMethodXCL
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class XCLPSConfigHandler extends DefaultPSConfigHandler {

	@Override
	public boolean canRead(Element element) {
		return super.canRead(element) && element.getAttribute(EXTENSION_ID).equals("PSMethodXCL");
	}

	@Override
	public boolean canWrite(Object object) {
		if (object instanceof PSConfig) {
			PSConfig psConfig = (PSConfig) object;
			if (psConfig.getPsMethod() instanceof PSMethodXCL) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		PSConfig psconfig = (PSConfig) super.read(element, persistence);
		PSMethodXCL psm = (PSMethodXCL) psconfig.getPsMethod();
		List<Object> fragments = new ArrayList<Object>();
		for (Element e : XMLUtil.getElementList(element.getChildNodes())) {
			fragments.add(persistence.readFragment(e));
		}
		for (Object o : fragments) {
			if (o instanceof ScoreAlgorithm) {
				psm.setScoreAlgorithm((ScoreAlgorithm) o);
			}
		}
		return psconfig;
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		Element e = super.write(object, persistence);
		PSConfig config = (PSConfig) object;
		PSMethodXCL psm = (PSMethodXCL) config.getPsMethod();
		e.appendChild(persistence.writeFragment(psm.getScoreAlgorithm()));
		return e;
	}

}
