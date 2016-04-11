/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg denkbares GmbH
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
package de.d3web.core.io.fragments;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityInterval;
import de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityNum;

/**
 * Handles the AbnormalityNum
 * 
 * @author Norman Brümmer, Markus Friedrich (denkbares GmbH)
 */
public class AbnormalityNumHandler implements FragmentHandler<KnowledgeBase> {

	private static final String NODENAME = "numAbnormalities";

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals(NODENAME);
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof AbnormalityNum);
	}

	@Override
	public Object read(Element n, Persistence<KnowledgeBase> persistence) throws IOException {
		AbnormalityNum abnorm = new AbnormalityNum();
		NodeList abChildren = n.getChildNodes();
		for (Element child : XMLUtil.getElementList(abChildren)) {
			Object readFragment = persistence.readFragment(child);
			if (readFragment instanceof AbnormalityInterval) {
				abnorm.addValue((AbnormalityInterval) readFragment);
			}
			else if (readFragment instanceof List<?>) {
				for (Object o : (List<?>) readFragment) {
					if (o instanceof AbnormalityInterval) {
						abnorm.addValue((AbnormalityInterval) o);
					}
					else {
						throw new IOException("Object " + o + " is no AbnormalityInterval");
					}
				}
			}
			else {
				throw new IOException(
						"Object "
								+ readFragment
								+ " is neighter an AbnormalityInterval nor a list of AbnormalityIntervals");
			}
		}
		return abnorm;
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		Element element = persistence.getDocument().createElement(NODENAME);
		AbnormalityNum abnormalityNum = (AbnormalityNum) object;
		element.appendChild(persistence.writeFragment(abnormalityNum.getIntervals()));
		return element;
	}

}
