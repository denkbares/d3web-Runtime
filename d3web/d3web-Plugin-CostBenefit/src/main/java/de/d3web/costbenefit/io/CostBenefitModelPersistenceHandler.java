/*
 * Copyright (C) 2009 denkbares GmbH
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.KnowledgeWriter;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Value;
import de.d3web.core.session.ValueFactory;
import de.d3web.costbenefit.inference.ConditionalValueSetter;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.costbenefit.inference.ValueTransition;

/**
 * This PersistenceHandler saves and stores the default KnowledgeSlices of the
 * CostBenefitPackage
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * 
 */
public class CostBenefitModelPersistenceHandler implements KnowledgeReader, KnowledgeWriter {

	public final static String ID = "costbenefit";

	@Override
	public void read(KnowledgeBase kb, InputStream stream, ProgressListener listener) throws IOException {
		Document doc = Util.streamToDocument(stream);
		String message = "Loading cost benefit knowledge";
		listener.updateProgress(0, message);
		NodeList stmodels = doc.getElementsByTagName("StateTransition");
		int max = stmodels.getLength();
		float count = 0;
		for (int i = 0; i < stmodels.getLength(); i++) {
			Node current = stmodels.item(i);
			addSTKnowledge(kb, current);
			listener.updateProgress(++count / max, message);
		}
	}

	@Override
	public int getEstimatedSize(de.d3web.core.knowledge.KnowledgeBase kb) {
		Collection<StateTransition> relations = kb
				.getAllKnowledgeSlicesFor(StateTransition.KNOWLEDGE_KIND);
		return relations.size();
	}

	@Override
	public void write(de.d3web.core.knowledge.KnowledgeBase kb, OutputStream stream, de.d3web.core.io.progress.ProgressListener listener) throws IOException {
		Document doc = Util.createEmptyDocument();
		Element root = doc.createElement("KnowledgeBase");
		root.setAttribute("type", ID);
		root.setAttribute("system", "d3web");
		doc.appendChild(root);
		Element ksNode = doc.createElement("KnowledgeSlices");
		root.appendChild(ksNode);
		SortedSet<StateTransition> knowledgeSlices = new TreeSet<StateTransition>(
				new StateTransitionComparator());
		for (StateTransition knowledgeSlice : kb.getAllKnowledgeSlicesFor(StateTransition.KNOWLEDGE_KIND)) {
			if (knowledgeSlice != null) {
				knowledgeSlices.add(knowledgeSlice);
			}
		}
		for (StateTransition model : knowledgeSlices) {
			ksNode.appendChild(getElement(model, doc));
		}

		Util.writeDocumentToOutputStream(doc, stream);
	}

	private Element getElement(StateTransition st, Document doc) throws IOException {
		Element element = doc.createElement("StateTransition");
		element.setAttribute("QID", st.getQcontainer().getName());
		Condition activationCondition = st.getActivationCondition();
		if (activationCondition != null) {
			Element aCElement = doc.createElement("activationCondition");
			aCElement.appendChild(PersistenceManager.getInstance().writeFragment(
					activationCondition, doc));
			element.appendChild(aCElement);
		}
		List<ValueTransition> postTransitions = st.getPostTransitions();
		for (ValueTransition vt : postTransitions) {
			element.appendChild(getElement(vt, doc));

		}
		return element;
	}

	private Element getElement(ValueTransition vt, Document doc) throws IOException {
		Element element = doc.createElement("ValueTransition");
		if (vt.getQuestion() == null) {
			throw new IOException("ValueTransition has no question");
		}
		element.setAttribute("QID", vt.getQuestion().getName());
		List<ConditionalValueSetter> setters = vt.getSetters();
		for (ConditionalValueSetter cvs : setters) {
			element.appendChild(getElement(cvs, doc));
		}
		return element;
	}

	private Element getElement(ConditionalValueSetter cvs, Document doc) throws IOException {
		Element element = doc.createElement("ConditionalValueSetter");
		String id_or_value = ValueFactory.getID_or_Value(cvs.getAnswer());
		element.setAttribute("AID", id_or_value);
		Condition condition = cvs.getCondition();
		if (condition != null) {
			element.appendChild(PersistenceManager.getInstance().writeFragment(condition, doc));
		}
		return element;
	}

	private void addSTKnowledge(KnowledgeBase kb, Node current) throws IOException {
		String qcontainerID = current.getAttributes().getNamedItem("QID").getTextContent();
		QContainer qcontainer = kb.getManager().searchQContainer(qcontainerID);
		NodeList children = current.getChildNodes();
		Condition activationCondition = null;
		List<ValueTransition> postTransitions = new ArrayList<ValueTransition>();
		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			if (n.getNodeName().equals("activationCondition")) {
				for (Element child : XMLUtil.getElementList(n.getChildNodes())) {
					activationCondition = (Condition) PersistenceManager.getInstance().readFragment(
							child, kb);
				}
			}
			else if (n.getNodeName().equals("ValueTransition")) {
				String question = n.getAttributes().getNamedItem("QID").getTextContent();
				Question q = kb.getManager().searchQuestion(question);
				List<ConditionalValueSetter> cvss = new ArrayList<ConditionalValueSetter>();
				NodeList childNodes = n.getChildNodes();
				for (int j = 0; j < childNodes.getLength(); j++) {
					Node child = childNodes.item(j);
					if (child.getNodeName().equals("ConditionalValueSetter")) {
						Value answer = KnowledgeBaseUtils.findValue(
								q,
								child.getAttributes().getNamedItem("AID").getTextContent());
						Condition condition = null;
						for (Element grandchild : XMLUtil.getElementList(child.getChildNodes())) {
							condition = (Condition) PersistenceManager.getInstance().readFragment(
									grandchild, kb);
						}
						ConditionalValueSetter cvs = new ConditionalValueSetter(
								answer, condition);
						cvss.add(cvs);
					}
				}
				ValueTransition vt = new ValueTransition(q, cvss);
				postTransitions.add(vt);
			}
		}
		new StateTransition(activationCondition, postTransitions, qcontainer);
	}

	private class StateTransitionComparator implements Comparator<StateTransition> {

		@Override
		public int compare(StateTransition r1, StateTransition r2) {
			return (r1.getQcontainer().getName().compareTo(r2.getQcontainer().getName()));
		}

	}
}
