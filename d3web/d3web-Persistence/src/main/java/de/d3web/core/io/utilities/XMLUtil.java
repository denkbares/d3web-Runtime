/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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
package de.d3web.core.io.utilities;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.KnowledgeBase;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.session.values.AnswerUnknown;
import de.d3web.core.terminology.Answer;
import de.d3web.core.terminology.NamedObject;
import de.d3web.core.terminology.QASet;
import de.d3web.core.terminology.Question;
import de.d3web.core.terminology.QuestionChoice;
import de.d3web.core.terminology.QuestionNum;
import de.d3web.core.terminology.QuestionText;
/**
 * Provides useful static functions for xml persistence handlers 
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class XMLUtil {
	
	/**
	 * Appends a question element to a parent element.
	 * Appends no element, if the question is null.
	 * @param parent Element where the question element should be appended
	 * @param q Question which should be represented by the newly appended element
	 * @throws IOException if the question has no ID
	 */
	public static void appendQuestionLinkElement(Element parent, QASet q) throws IOException {
		Document doc = parent.getOwnerDocument();
		if (q!=null) {
			Element e = doc.createElement("Question");
			if (q.getId()!=null) {
				e.setAttribute("ID", q.getId());
				parent.appendChild(e);
			} else {
				throw new IOException("Question "+q.getText()+" has no ID");
			}
		}
	}
	
	/**
	 * Checks if the element has the specified element name and an attribute type with the value typeToCheck
	 * @param element Element be checked
	 * @param elementname element name to check
	 * @param typeToCheck Value for the Attribute type
	 * @return true, if the given element fulfills the requirements, false otherwise
	 */
	public static boolean checkNameAndType(Element element, String elementname, String typeToCheck) {
		String nodeName = element.getNodeName();
		String type = element.getAttribute("type");
		if (nodeName!=null && nodeName.equals(elementname) && type!=null && type.equals(typeToCheck)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if the element is named "Condition" and if has an attribute type with the value typeToCheck
	 * @param element to be checked
	 * @param typeToCheck Value for the Attribute type
	 * @return true, if the given element fulfills the requirements, false otherwise
	 */
	public static boolean checkCondition(Element element, String typeToCheck) {
		return checkNameAndType(element, "Condition", typeToCheck);
	}
	
	/**
	 * Creates a condition element with the id of the named object and the given type
	 * @param doc Document, where the Element should be created
	 * @param nob NamedObject, whose ID should be used
	 * @param type type of the condition
	 * @return condition element
	 */
	public static Element writeCondition(Document doc, NamedObject nob, String type) {
		Element element = doc.createElement("Condition");
		element.setAttribute("type", type);
		if (nob != null) {
			element.setAttribute("ID", nob.getId());
		} else {
			element.setAttribute("ID", "");
		}
		return element;
	}
	
	/**
	 * Creates a condition element with the id of the named object, the given type and value
	 * @param doc Document, where the Element should be created
	 * @param nob NamedObject, whose ID should be used
	 * @param type type of the condition
	 * @param value value of the condition
	 * @return condition element
	 */
	public static Element writeCondition(Document doc, NamedObject nob, String type, String value) {
		Element element = writeCondition(doc, nob, type);
		if (value!=null) {
			element.setAttribute("value", value);
		}
		return element;
	}
	
	/**
	 * Creates a condition element with the id of the named object, the given type and value.
	 * The value is stored in a child element.
	 * @param doc Document, where the Element should be created
	 * @param nob NamedObject, whose ID should be used
	 * @param type type of the condition
	 * @param value value of the condition
	 * @return condition element
	 */
	public static Element writeConditionWithValueNode(Document doc, NamedObject nob, String type, String value) throws IOException {
		Element valueElement = doc.createElement("Value");
		valueElement.setTextContent(value);
		return writeConditionWithValueNode(doc, nob, type, valueElement);
	}
	
	/**
	 * Creates a condition element with the id of the named object, the given type and value.
	 * The value node is inserted as a child element in the newly created node.
	 * @param doc Document, where the Element should be created
	 * @param nob NamedObject, whose ID should be used
	 * @param type type of the condition
	 * @param value value of the condition
	 * @return condition element
	 */
	public static Element writeConditionWithValueNode(Document doc, NamedObject nob, String type, Element value) {
		Element element = writeCondition(doc, nob, type);
		element.appendChild(value);
		return element;
	}

	/**
	 * Creates a condition element with the id of the named object, the given type and values
	 * @param doc Document, where the Element should be created
	 * @param nob NamedObject, whose ID should be used
	 * @param type type of the condition
	 * @param values List of answers, which are used as values
	 * @return condition element
	 * @throws IOException if one of the elements in values is neighter a Choice nor a Unknown Answer
	 */
	public static Element writeCondition(Document doc, NamedObject nob, String type,
			List<?> values) throws IOException {
		Element element = writeCondition(doc, nob, type);
		if (values != null && values.size()>0) {
			String s = "";
			for(Object o: values) {
				s+= getId(o) + ",";
			}
			s = s.substring(0,s.length()-1);
			element.setAttribute("value", s);
		}
		return element;
	}
	
	private static String getId(Object answer) throws IOException {
		if (answer instanceof AnswerChoice)
			return ((AnswerChoice) answer).getId();
		else if (answer instanceof AnswerUnknown)
			return ((AnswerUnknown) answer).getId();
		else {
			throw new IOException("Answer is neighter a Choice nor a Unknown Answer");
		}
	}

	/**
	 * Creates a condition element with the specified type
	 * @param doc Document, where the Element should be created
	 * @param type type of the condition
	 * @return condition element
	 */
	public static Element writeCondition(Document doc, String type) {
		Element element = doc.createElement("Condition");
		element.setAttribute("type", type);
		return element;
	}
	
	/**
	 * Appends an Element TargetQASets containing elements for the given qaSets
	 * @param element Element the TargetQASets Element sholud be appended
	 * @param qaSets List of QASet being represented by the appended element
	 */
	public static void appendTargetQASets(Element element, List<QASet> qaSets) {
		Document doc = element.getOwnerDocument();
		if (!qaSets.isEmpty()) {
			Element targetQASets = doc.createElement("TargetQASets");
			for (QASet qaset: qaSets) {
				Element qasetElement = doc.createElement("QASet");
				qasetElement.setAttribute("ID", qaset.getId());
				targetQASets.appendChild(qasetElement);
			}
			element.appendChild(targetQASets);
		}
	}
	
	/**
	 * Extract the qasets stored in the given element
	 * @param element Element representing the QASets
	 * @param kb Knowledgebase containing the qasets
	 * @return List of represented QASets
	 */
	public static List<QASet> getTargetQASets(Element element, KnowledgeBase kb) {
		List<QASet> ret = new LinkedList<QASet>();
		if (element.getNodeName().equalsIgnoreCase("targetQASets")) {
			NodeList qasets = element.getChildNodes();
			for (int k = 0; k < qasets.getLength(); ++k) {
				Node q = qasets.item(k);
				if (q.getNodeName().equalsIgnoreCase("QASet")) {
					String id = q.getAttributes().getNamedItem("ID").getNodeValue();
					QASet qset = (QASet) kb.search(id);
					ret.add(qset);
				}
			}
		}
		return ret;
	}

	private static boolean isLinkedChild(NamedObject topQ, NamedObject theChild) {
		return topQ.getLinkedChildren().contains(theChild);
	}

	/**
	 * Adds the children of the NamedObject to the specified element
	 * @param namedObject NamedObject, whose children should be appended as Elements
	 * @param element Element representing the namedObject, where the children will be appended
	 */
	public static void appendChildren(NamedObject namedObject, Element element) {
		Document doc = element.getOwnerDocument();
		List<? extends NamedObject> children = namedObject.getChildren();
		if (children.size()!=0) {
			Element childrenElement = doc.createElement("Children");
			for (NamedObject child: children) {
				Element childElement = doc.createElement("Child");
				childElement.setAttribute("ID", child.getId());
				if (isLinkedChild(namedObject, child)) {
					childElement.setAttribute("link", "true");
				}
				childrenElement.appendChild(childElement);
			}
			element.appendChild(childrenElement);
		}
	}
	
	/**
	 * Adds the children given from the xml structure to the NamedObject
	 * @param kb KnowledgeBase containing the children
	 * @param namedObject where the children should be appended
	 * @param element representing the namedObject and containing the children as childnodes
	 */
	public static void appendChildren(KnowledgeBase kb, NamedObject namedObject, Element element) {
		List<Element> children = null;
		NodeList childNodes = element.getChildNodes();
		for (int i=0; i<childNodes.getLength(); i++) {
			Node child = childNodes.item(i);
			if (child.getNodeName().equals("Children")) {
				children = XMLUtil.getElementList(child.getChildNodes());
			}
		}
		if (children != null) {
			for (Element child: children) {
				String id = child.getAttribute("ID");
				String link = child.getAttribute("link");
				NamedObject no = (NamedObject) kb.search(id);
				if (link != null && link.equals("true")) {
					namedObject.addLinkedChild(no);
				} else {
					namedObject.addChild(no);
				}
			}
		}
	}

	/**
	 * Appends an Element containing the text
	 * @param text Text to be represented by the newly appended element
	 * @param element, which should be appended
	 */
	public static void appendTextNode(String text, Element element) {
		Element textElement = element.getOwnerDocument().createElement("Text");
		textElement.setTextContent(text);
		element.appendChild(textElement);
	}

	/**
	 * Extracts primitive Values form a string
	 * @param textContent Sting containing the primitive value
	 * @param clazz Name of the Class
	 * @return Extracted Value
	 * @throws IOException if the class is not supported
	 */
	public static Object getPrimitiveValue(String textContent, String clazz) throws IOException {
		if (clazz.equals(String.class.getName())) {
			return textContent;
		} else if (clazz.equals(Integer.class.getName())) {
			return Integer.parseInt(textContent);
		} else if (clazz.equals(Double.class.getName())) {
			return Double.parseDouble(textContent);
		} else if (clazz.equals(Boolean.class.getName())) {
			return Boolean.parseBoolean(textContent);
		} else if (clazz.equals(URL.class.getName())) {
			return new URL(textContent);
		} else {
			throw new IOException("Class "+clazz+" is not supported");
		}
	}
	
	/**
	 * Filters all elements of a NodeList and returns them in a collection.
	 * @param list Nodelist containing all types of nodes (textnodes etc.)
	 * @return a list containing all elements from nodelist, but not containing other nodes such as textnodes etc.
	 */
	public static List<Element> getElementList(NodeList list) {
		List<Element> col = new ArrayList<Element>();
		for (int i = 0; i<list.getLength(); i++) {
			if (list.item(i) instanceof Element) {
				col.add((Element) list.item(i));
			}
		}
		return col;
	}
	
	/**
	 * Gets the Answer with the specified id for a Question
	 * @param theCase the actual case
	 * @param q Question
	 * @param idOrValue ID of the Answer
	 * @return Answer
	 */
	public static Answer getAnswer(XPSCase theCase, Question q, String idOrValue) {

		if (idOrValue.equals("MaU")) {
			return new AnswerUnknown();
		}

		if (q instanceof QuestionChoice) {
			return ((QuestionChoice) q).getAnswer(theCase, idOrValue);
		}

		if (q instanceof QuestionText) {
			return ((QuestionText) q).getAnswer(theCase, idOrValue);
		}

		if (q instanceof QuestionNum) {
			return ((QuestionNum) q).getAnswer(theCase, new Double(idOrValue));
		}

		return null;
	}

	/**
	 * Returns the content of the text-section of the given DOM-Node 
	 * @param node Node to grab the text-section from
	 * @return the content of the text-section of the given DOM-Node
	 */
	public static String getText(Node node) {
	
		Iterator<Node> iter = new ChildrenIterator(node);
		while (iter.hasNext()) {
			Node child = iter.next();
			if (child.getNodeType() == Node.CDATA_SECTION_NODE)
				return child.getNodeValue();
		}
	
		StringBuffer sb = new StringBuffer();
		iter = new ChildrenIterator(node);
		while (iter.hasNext()) {
			Node child = (Node) iter.next();
			if (child.getNodeType() == Node.TEXT_NODE)
				sb.append(child.getNodeValue());
		}
		return sb.toString().trim();
	}
}
