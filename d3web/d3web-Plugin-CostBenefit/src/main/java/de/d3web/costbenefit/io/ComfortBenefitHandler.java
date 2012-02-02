/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.costbenefit.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.KnowledgeWriter;
import de.d3web.core.io.NoSuchFragmentHandlerException;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.costbenefit.inference.ComfortBenefit;

/**
 * PersistenceHandler for {@link ComfortBenefit}
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 02.02.2012
 */
public class ComfortBenefitHandler implements KnowledgeReader, KnowledgeWriter {

	private static final String NODE_NAME = "ComfortBenefit";
	public final static String ID = "comfortBenefit";

	@Override
	public void write(KnowledgeBase knowledgeBase, OutputStream stream, ProgressListener listener) throws IOException {
		Document doc = Util.createEmptyDocument();
		Element root = doc.createElement("KnowledgeBase");
		root.setAttribute("type", ID);
		root.setAttribute("system", "d3web");
		doc.appendChild(root);
		Element ksNode = doc.createElement("KnowledgeSlices");
		root.appendChild(ksNode);
		Collection<ComfortBenefit> comfortBenefits = knowledgeBase.getAllKnowledgeSlicesFor(ComfortBenefit.KNOWLEDGE_KIND);
		float count = 0;
		String message = "Writing ComfortBenefits.";
		listener.updateProgress(0, message);
		for (ComfortBenefit cb : comfortBenefits) {
			if (cb != null) {
				ksNode.appendChild(getElement(cb, doc));
			}
			listener.updateProgress(++count / comfortBenefits.size(), message);
		}
		Util.writeDocumentToOutputStream(doc, stream);
	}

	private Element getElement(ComfortBenefit cb, Document doc) throws IOException {
		Element element = doc.createElement(NODE_NAME);
		element.setAttribute("QID", cb.getQContainer().getName());
		Condition activationCondition = cb.getCondition();
		Element conditionElement = doc.createElement("condition");
		conditionElement.appendChild(PersistenceManager.getInstance().writeFragment(
					activationCondition, doc));
		element.appendChild(conditionElement);
		return element;
	}

	@Override
	public int getEstimatedSize(KnowledgeBase knowledgeBase) {
		return knowledgeBase.getAllKnowledgeSlicesFor(ComfortBenefit.KNOWLEDGE_KIND).size();
	}

	@Override
	public void read(KnowledgeBase knowledgeBase, InputStream stream, ProgressListener listener) throws IOException {
		Document doc = Util.streamToDocument(stream);
		String message = "Loading comfort benefit knowledge";
		listener.updateProgress(0, message);
		NodeList comfortBenefitNodes = doc.getElementsByTagName(NODE_NAME);
		int max = comfortBenefitNodes.getLength();
		float count = 0;
		for (int i = 0; i < comfortBenefitNodes.getLength(); i++) {
			Node current = comfortBenefitNodes.item(i);
			addComfortBenefitKnowledge(knowledgeBase, current);
			listener.updateProgress(++count / max, message);
		}
	}

	private void addComfortBenefitKnowledge(KnowledgeBase knowledgeBase, Node current) throws NoSuchFragmentHandlerException, IOException {
		String qcontainerID = current.getAttributes().getNamedItem("QID").getTextContent();
		QContainer qcontainer = knowledgeBase.getManager().searchQContainer(qcontainerID);
		NodeList children = current.getChildNodes();
		Condition condition = null;
		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			if (n.getNodeName().equals("condition")) {
				for (Element child : XMLUtil.getElementList(n.getChildNodes())) {
					condition = (Condition) PersistenceManager.getInstance().readFragment(
							child, knowledgeBase);
				}
			}
		}
		new ComfortBenefit(qcontainer, condition);
	}

}
