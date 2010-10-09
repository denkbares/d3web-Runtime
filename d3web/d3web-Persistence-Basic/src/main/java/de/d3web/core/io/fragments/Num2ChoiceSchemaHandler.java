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
import java.util.StringTokenizer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.abstraction.inference.PSMethodAbstraction;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.info.Num2ChoiceSchema;

/**
 * Handler for Num2ChoiceSchemas
 * 
 * @author Norman Brümmer, baumeister, Markus Friedrich (denkbares GmbH)
 */
public class Num2ChoiceSchemaHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, "KnowledgeSlice", "Schema");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof Num2ChoiceSchema);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		Question q = null;
		Double[] numArray = null;
		String id = element.getAttribute("ID");
		NodeList nl = element.getChildNodes();
		Node condNode = null;
		for (int i = 0; i < nl.getLength(); ++i) {
			condNode = nl.item(i);
			if (condNode.getNodeName().equalsIgnoreCase("Question")) {
				String qID = condNode.getAttributes().getNamedItem("ID")
						.getNodeValue();
				q = kb.searchQuestion(qID);
				if (q == null) throw new IOException(
						"Could not create Num2ChoiceSchema - unknown question " + qID);
			}
			else if (condNode.getNodeName().equalsIgnoreCase(
					"LeftClosedInterval")) {
				// [FIXME]:?:set intervall
				String nArray = condNode.getAttributes().getNamedItem("value")
						.getNodeValue();
				numArray = toDoubleArray(nArray);
				if (numArray == null) throw new IOException(
						"Could not create Num2ChoiceSchema - unparseable value " + nArray);
			}
		}
		if ((q == null) || (numArray == null)) return null;
		else {
			Num2ChoiceSchema schema = new Num2ChoiceSchema(id);
			schema.setQuestion(q);
			q.addKnowledge(PSMethodAbstraction.class, schema, PSMethodAbstraction.NUM2CHOICE_SCHEMA);
			schema.setSchemaArray(numArray);
			return schema;
		}
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		Num2ChoiceSchema schema = (Num2ChoiceSchema) object;
		Element element = doc.createElement("KnowledgeSlice");
		element.setAttribute("ID", schema.getId());
		element.setAttribute("type", "Schema");
		Element questionNode = doc.createElement("Question");
		questionNode.setAttribute("ID", schema.getQuestion().getId());
		element.appendChild(questionNode);
		Element schemaNode = doc.createElement("LeftClosedInterval");
		schemaNode.setAttribute("value", arrayToString(schema.getSchemaArray()));
		element.appendChild(schemaNode);
		return element;
	}

	private static String arrayToString(Double[] array) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			if (i == 0) sb.append(array[i]);
			else sb.append(" " + array[i]);
		}
		return sb.toString();
	}

	private Double[] toDoubleArray(String str) {
		StringTokenizer s = new StringTokenizer(str);
		if (s.hasMoreTokens()) {
			Double[] result = new Double[s.countTokens()];
			int i = 0;
			while (s.hasMoreTokens()) {
				result[i] = new Double(s.nextToken());
				i++;
			}
			return result;
		}
		else {
			return null;
		}
	}
}
