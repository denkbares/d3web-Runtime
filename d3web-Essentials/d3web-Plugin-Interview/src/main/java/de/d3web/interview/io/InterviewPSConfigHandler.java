/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.d3web.interview.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import de.d3web.core.inference.PSConfig;
import de.d3web.core.io.Persistence;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.interview.FormStrategy;
import de.d3web.interview.inference.PSMethodInterview;
import de.d3web.plugin.io.fragments.DefaultPSConfigHandler;

/**
 * Reads and writes the configuration of the PSMethodInterview
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 25.03.2013
 */
public class InterviewPSConfigHandler extends DefaultPSConfigHandler {

	@Override
	public boolean canRead(Element element) {
		return super.canRead(element)
				&& element.getAttribute(EXTENSION_ID).equals("PSMethodInterview");
	}

	@Override
	public boolean canWrite(Object object) {
		if (object instanceof PSConfig) {
			PSConfig psConfig = (PSConfig) object;
			if (psConfig.getPsMethod() instanceof PSMethodInterview) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		PSConfig psconfig = (PSConfig) super.read(element, persistence);
		PSMethodInterview psm = (PSMethodInterview) psconfig.getPsMethod();
		List<Object> fragments = new ArrayList<Object>();
		for (Element e : XMLUtil.getElementList(element.getChildNodes())) {
			fragments.add(persistence.readFragment(e));
		}
		for (Object o : fragments) {
			if (o instanceof FormStrategy) {
				psm.setDefaultFormStrategy((FormStrategy) o);
			}
		}
		return psconfig;
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		Element e = super.write(object, persistence);
		PSConfig config = (PSConfig) object;
		PSMethodInterview psm = (PSMethodInterview) config.getPsMethod();
		FormStrategy defaultFormStrategy = psm.getDefaultFormStrategy();
		if (defaultFormStrategy != null) {
			e.appendChild(persistence.writeFragment(defaultFormStrategy));
		}
		return e;
	}

}
