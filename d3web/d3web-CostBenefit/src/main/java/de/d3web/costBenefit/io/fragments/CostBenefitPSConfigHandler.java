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
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.KnowledgeBase;
import de.d3web.core.inference.PSConfig;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.costBenefit.inference.CostFunction;
import de.d3web.costBenefit.inference.PSMethodCostBenefit;
import de.d3web.costBenefit.inference.SearchAlgorithm;
import de.d3web.costBenefit.inference.TargetFunction;
import de.d3web.plugin.io.fragments.DefaultPSConfigHandler;

public class CostBenefitPSConfigHandler extends DefaultPSConfigHandler {

	@Override
	public boolean canRead(Element element) {
		return super.canRead(element)&&element.getAttribute(EXTENSION_ID).equals("PSMethodCostBenefit");
	}

	@Override
	public boolean canWrite(Object object) {
		if (object instanceof PSConfig) {
			PSConfig psConfig = (PSConfig) object;
			if (psConfig.getPsMethod() instanceof PSMethodCostBenefit) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		PSConfig psconfig = (PSConfig) super.read(kb, element);
		PSMethodCostBenefit psm = (PSMethodCostBenefit) psconfig.getPsMethod();
		List<Object> fragments = new ArrayList<Object>();
		for (Element e: XMLUtil.getElementList(element.getChildNodes())) {
			fragments.add(PersistenceManager.getInstance().readFragment(e, kb));
		}
		for (Object o: fragments) {
			if (o instanceof CostFunction) {
				psm.setCostFunction((CostFunction) o);
			} else if (o instanceof TargetFunction) {
				psm.setTargetFunction((TargetFunction) o);
			} else if (o instanceof SearchAlgorithm) {
				psm.setSearchAlgorithm((SearchAlgorithm) o);
			}
		}
		return psconfig;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		Element e = super.write(object, doc);
		PSConfig config = (PSConfig) object;
		PSMethodCostBenefit psm = (PSMethodCostBenefit) config.getPsMethod();
		e.appendChild(PersistenceManager.getInstance().writeFragment(psm.getTargetFunction(), doc));
		e.appendChild(PersistenceManager.getInstance().writeFragment(psm.getSearchAlgorithm(), doc));
		e.appendChild(PersistenceManager.getInstance().writeFragment(psm.getCostFunction(), doc));
		return e;
	}

}
