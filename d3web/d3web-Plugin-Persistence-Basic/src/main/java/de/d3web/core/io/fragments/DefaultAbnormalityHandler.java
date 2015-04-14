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
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityUtils;
import de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality;
import de.d3web.core.session.Value;
import de.d3web.core.session.ValueUtils;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;

/**
 * Handles the Abnormality
 * 
 * @author Norman Brümmer, Markus Friedrich (denkbares GmbH)
 */
public class DefaultAbnormalityHandler implements FragmentHandler<KnowledgeBase> {

	private static final String NODENAME = "anormalities";

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals(NODENAME)
				// old format
				|| XMLUtil.checkNameAndType(element, "KnowledgeSlice", "abnormality");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof DefaultAbnormality);
	}

	@Override
	public Object read(Element n, Persistence<KnowledgeBase> persistence) throws IOException {
		Question question = null;
		NodeList abChildren = n.getChildNodes();
		for (int k = 0; k < abChildren.getLength(); ++k) {
			Node abChild = abChildren.item(k);
			if (abChild.getNodeName().equalsIgnoreCase("question")) {
				question = (Question) persistence.getArtifact().getManager().search(
						abChild.getAttributes().getNamedItem("ID").getNodeValue());
				break;
			}
		}
		DefaultAbnormality abnorm = new DefaultAbnormality();
		abChildren = n.getChildNodes();
		for (int k = 0; k < abChildren.getLength(); ++k) {
			Node abChild = abChildren.item(k);
			if (abChild.getNodeName().equalsIgnoreCase("values")) {
				NodeList vals = abChild.getChildNodes();
				for (int l = 0; l < vals.getLength(); ++l) {
					Node valChild = vals.item(l);
					if (valChild.getNodeName().equalsIgnoreCase(
							"abnormality")) {
						String ansID = valChild.getAttributes()
								.getNamedItem("ID").getNodeValue();
						Value ans = new ChoiceValue(new ChoiceID(ansID));
						String value = valChild.getAttributes()
								.getNamedItem("value").getNodeValue();
						abnorm.addValue(ans, AbnormalityUtils
								.convertConstantStringToValue(value));
					}
				}
			}
		}
		if (question != null) {
			question.getInfoStore().addValue(BasicProperties.DEFAULT_ABNORMALITIY, abnorm);
		}
		return abnorm;
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		Element element = persistence.getDocument().createElement(NODENAME);
		DefaultAbnormality abnormality = (DefaultAbnormality) object;
		Element valuesNode = persistence.getDocument().createElement("values");
		Set<Value> answers = abnormality.getAnswerSet();
		// while (answers.hasMoreElements()) {
		for (Value answer : answers) {
			// Value answer = answers.nextElement();
			Element abnormalityElement = persistence.getDocument().createElement("abnormality");
			abnormalityElement.setAttribute("ID", ValueUtils.getID_or_Value(answer));
			abnormalityElement.setAttribute(
					"value",
					AbnormalityUtils.convertValueToConstantString(abnormality.getValue(answer)));
			valuesNode.appendChild(abnormalityElement);
		}
		element.appendChild(valuesNode);

		return element;
	}

}
