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
package de.d3web.core.io.utilities;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.io.NoSuchFragmentHandlerException;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.InfoStore;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.knowledge.terminology.info.Property.Autosave;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.core.utilities.Triple;

/**
 * Provides useful static functions for xml persistence handlers
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public final class XMLUtil {

	public static final String INFO_STORE = "infoStore";

	/**
	 * Avoids creating this class.
	 */
	private XMLUtil() {
	}

	/**
	 * Appends a question element to a parent element. Appends no element, if
	 * the question is null.
	 * 
	 * @param parent Element where the question element should be appended
	 * @param q Question which should be represented by the newly appended
	 *        element
	 * @throws IOException if the question has no ID
	 */
	public static void appendQuestionLinkElement(Element parent, QASet q) throws IOException {
		Document doc = parent.getOwnerDocument();
		if (q != null) {
			Element e = doc.createElement("Question");
			if (q.getName() != null) {
				e.setAttribute("name", q.getName());
				parent.appendChild(e);
			}
		}
	}

	/**
	 * Checks if the element has the specified element name and an attribute
	 * type with the value typeToCheck
	 * 
	 * @param element Element be checked
	 * @param elementname element name to check
	 * @param typeToCheck Value for the Attribute type
	 * @return true, if the given element fulfills the requirements, false
	 *         otherwise
	 */
	public static boolean checkNameAndType(Element element, String elementname, String typeToCheck) {
		String nodeName = element.getNodeName();
		String type = element.getAttribute("type");
		if (nodeName != null && nodeName.equals(elementname) && type != null
				&& type.equals(typeToCheck)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the element is named "Condition" and if has an attribute type
	 * with the value typeToCheck
	 * 
	 * @param element to be checked
	 * @param typeToCheck Value for the Attribute type
	 * @return true, if the given element fulfills the requirements, false
	 *         otherwise
	 */
	public static boolean checkCondition(Element element, String typeToCheck) {
		return checkNameAndType(element, "Condition", typeToCheck);
	}

	/**
	 * Creates a condition element with the id of the named object and the given
	 * type
	 * 
	 * @param doc Document, where the Element should be created
	 * @param nob TerminologyObject, whose ID should be used
	 * @param type type of the condition
	 * @return condition element
	 */
	public static Element writeCondition(Document doc, TerminologyObject nob, String type) {
		Element element = doc.createElement("Condition");
		element.setAttribute("type", type);
		if (nob != null) {
			element.setAttribute("name", nob.getName());
		}
		else {
			element.setAttribute("name", "");
		}
		return element;
	}

	/**
	 * Creates a condition element with the id of the named object, the given
	 * type and value
	 * 
	 * @param doc Document, where the Element should be created
	 * @param nob TerminologyObject, whose ID should be used
	 * @param type type of the condition
	 * @param value value of the condition
	 * @return condition element
	 */
	public static Element writeCondition(Document doc, TerminologyObject nob, String type, String value) {
		Element element = writeCondition(doc, nob, type);
		if (value != null) {
			element.setAttribute("value", value);
		}
		return element;
	}

	/**
	 * Creates a condition element with the id of the named object, the given
	 * type and value. The value is stored in a child element.
	 * 
	 * @param doc Document, where the Element should be created
	 * @param nob TerminologyObject, whose ID should be used
	 * @param type type of the condition
	 * @param value value of the condition
	 * @return condition element
	 */
	public static Element writeConditionWithValueNode(Document doc, TerminologyObject nob, String type, String value) throws IOException {
		Element valueElement = doc.createElement("Value");
		valueElement.setTextContent(value);
		return writeConditionWithValueNode(doc, nob, type, valueElement);
	}

	/**
	 * Creates a condition element with the id of the named object, the given
	 * type and value. The value node is inserted as a child element in the
	 * newly created node.
	 * 
	 * @param doc Document, where the Element should be created
	 * @param nob TerminologyObject, whose ID should be used
	 * @param type type of the condition
	 * @param value value of the condition
	 * @return condition element
	 */
	public static Element writeConditionWithValueNode(Document doc, TerminologyObject nob, String type, Element value) {
		Element element = writeCondition(doc, nob, type);
		element.appendChild(value);
		return element;
	}

	/**
	 * Creates a condition element with the id of the named object, the given
	 * type and values
	 * 
	 * @param doc Document, where the Element should be created
	 * @param nob TerminologyObject, whose ID should be used
	 * @param type type of the condition
	 * @param values List of answers, which are used as values
	 * @return condition element
	 * @throws IOException if one of the elements in values is neither a Choice
	 *         nor a Unknown Answer
	 */
	public static Element writeCondition(Document doc, TerminologyObject nob, String type,
			Value value) throws IOException {
		Element element = writeCondition(doc, nob, type);
		if (value != null) {
			String s = getValue(nob, value);
			element.setAttribute("value", s);
		}
		return element;
	}

	private static String getValue(TerminologyObject nob, Object answer) throws IOException {
		if (answer instanceof ChoiceValue) {
			ChoiceValue v = (ChoiceValue) answer;
			Choice choice = v.getChoice((QuestionChoice) nob);
			return choice.getName();
		}
		else if (answer instanceof Unknown) {
			return Unknown.UNKNOWN_ID;
		}
		else if (answer instanceof MultipleChoiceValue) {
			return ChoiceID.encodeChoiceIDs(((MultipleChoiceValue) answer).getChoiceIDs());
		}
		// for num answers, not the ID, but their actual value is returned
		else if (answer instanceof NumValue) {
			return ((NumValue) answer).getValue().toString();
		}
		else {
			throw new IOException(
					"The given value is neighter a Choice nor a Unknown Value.");
		}
	}

	/**
	 * Creates a condition element with the specified type
	 * 
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
	 * 
	 * @param element Element the TargetQASets Element should be appended
	 * @param qaSets List of QASet being represented by the appended element
	 */
	public static void appendTargetQASets(Element element, List<QASet> qaSets) {
		Document doc = element.getOwnerDocument();
		if (!qaSets.isEmpty()) {
			Element targetQASets = doc.createElement("TargetQASets");
			for (QASet qaset : qaSets) {
				Element qasetElement = doc.createElement("QASet");
				qasetElement.setAttribute("name", qaset.getName());
				targetQASets.appendChild(qasetElement);
			}
			element.appendChild(targetQASets);
		}
	}

	/**
	 * Extract the qasets stored in the given element
	 * 
	 * @param element Element representing the QASets
	 * @param kb knowledge base containing the qasets
	 * @return List of represented QASets
	 */
	public static List<QASet> getTargetQASets(Element element, KnowledgeBase kb) {
		List<QASet> ret = new LinkedList<QASet>();
		if (element.getNodeName().equalsIgnoreCase("targetQASets")) {
			NodeList qasets = element.getChildNodes();
			for (int k = 0; k < qasets.getLength(); ++k) {
				Node q = qasets.item(k);
				if (q.getNodeName().equalsIgnoreCase("QASet")) {
					String id = q.getAttributes().getNamedItem("name").getNodeValue();
					QASet qset = (QASet) kb.getManager().search(id);
					ret.add(qset);
				}
			}
		}
		return ret;
	}

	/**
	 * Adds the children of the TerminologyObject to the specified element
	 * 
	 * @param namedObject TerminologyObject, whose children should be appended
	 *        as Elements
	 * @param element Element representing the namedObject, where the children
	 *        will be appended
	 */
	public static void appendChildren(TerminologyObject namedObject, Element element) {
		Document doc = element.getOwnerDocument();
		TerminologyObject[] children = namedObject.getChildren();
		if (children.length != 0) {
			Element childrenElement = doc.createElement("Children");
			for (TerminologyObject child : children) {
				Element childElement = doc.createElement("Child");
				childElement.setAttribute("name", child.getName());
				childrenElement.appendChild(childElement);
			}
			element.appendChild(childrenElement);
		}
	}

	/**
	 * Adds the children given from the xml structure to the TerminologyObject
	 * 
	 * @param kb KnowledgeBase containing the children
	 * @param namedObject where the children should be appended
	 * @param element representing the namedObject and containing the children
	 *        as childnodes
	 */
	public static void appendChildren(KnowledgeBase kb, TerminologyObject namedObject, Element element) throws IOException {
		List<Element> children = null;
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node child = childNodes.item(i);
			if (child.getNodeName().equals("Children")) {
				children = XMLUtil.getElementList(child.getChildNodes());
			}
		}
		if (children != null) {
			for (Element child : children) {
				String id = child.getAttribute("name");
				TerminologyObject no = kb.getManager().search(
						id);
				if (namedObject instanceof QASet && no instanceof QASet) {
					((QASet) namedObject).addChild(((QASet) no));
				}
				else if (namedObject instanceof Solution && no instanceof Solution) {
					((Solution) namedObject).addChild(((Solution) no));
				}
				else {
					throw new IOException("The types of " + namedObject + " and " + no
							+ " don't match.");
				}
			}
		}
	}

	/**
	 * Extracts primitive Values form a string
	 * 
	 * @param textContent Sting containing the primitive value
	 * @param clazz Name of the Class
	 * @return Extracted Value
	 * @throws IOException if the class is not supported
	 */
	public static Object getPrimitiveValue(String textContent, String clazz) throws IOException {
		if (clazz.equals(String.class.getName())) {
			return textContent;
		}
		else if (clazz.equals(Integer.class.getName())) {
			return Integer.parseInt(textContent);
		}
		else if (clazz.equals(Double.class.getName())) {
			return Double.parseDouble(textContent);
		}
		else if (clazz.equals(Boolean.class.getName())) {
			return Boolean.parseBoolean(textContent);
		}
		else if (clazz.equals(URL.class.getName())) {
			return new URL(textContent);
		}
		else if (clazz.equals(Float.class.getName())) {
			return Float.parseFloat(textContent);
		}
		else {
			throw new IOException("Class " + clazz + " is not supported");
		}
	}

	/**
	 * Filters all elements of a NodeList and returns them in a collection.
	 * 
	 * @param list Nodelist containing all types of nodes (text nodes etc.)
	 * @return a list containing all elements from nodelist, but not containing
	 *         other nodes such as text nodes etc.
	 */
	public static List<Element> getElementList(NodeList list) {
		List<Element> col = new ArrayList<Element>();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i) instanceof Element) {
				col.add((Element) list.item(i));
			}
		}
		return col;
	}

	/**
	 * Determines the value with the specified id or value for a Question
	 * 
	 * @param session the actual case
	 * @param q Question
	 * @param id or value of the Answer
	 * @return value instance
	 */
	public static Value getAnswer(Question q, String idOrValue) {

		if (idOrValue.equals("MaU")) {
			return Unknown.getInstance();
		}

		if (q instanceof QuestionChoice) {
			Choice choice = KnowledgeBaseUtils.findChoice(
					(QuestionChoice) q, idOrValue);
			return new ChoiceValue(choice);
		}
		else if (q instanceof QuestionText) {
			return new TextValue(idOrValue);
		}
		else if (q instanceof QuestionNum) {
			return new NumValue(new Double(idOrValue));
		}
		else if (q instanceof QuestionDate) {
			return new DateValue(new Date(Long.parseLong(idOrValue)));
		}
		else {
			return UndefinedValue.getInstance();
		}
	}

	/**
	 * Appends all entries with the given autosave of the {@link InfoStore} to
	 * the specified father. If autosave is null, all entries will be appended
	 * 
	 * @created 08.11.2010
	 * @param father {@link Element}
	 * @param infoStore {@link InfoStore}
	 * @param autosave {@link Autosave}
	 * @throws IOException
	 */
	public static void appendInfoStoreEntries(Element father, InfoStore infoStore, Autosave autosave) throws IOException {
		Document doc = father.getOwnerDocument();
		for (Triple<Property<?>, Locale, Object> entry : sortEntries(infoStore.entries())) {
			if (autosave == null || (autosave != null && entry.getA().hasState(autosave))) {
				Element entryElement = doc.createElement("entry");
				father.appendChild(entryElement);
				entryElement.setAttribute("property", entry.getA().getName());
				Locale language = entry.getB();
				if (language != InfoStore.NO_LANGUAGE) {
					entryElement.setAttribute("lang", language.toString());
				}
				try {
					entryElement.appendChild(PersistenceManager.getInstance().writeFragment(
							entry.getC(), doc));
				}
				catch (NoSuchFragmentHandlerException e) {
					if (entry.getA().canParseValue()) {
						entryElement.setTextContent(entry.getC().toString());
					}
					else {
						throw e;
					}
				}
			}
		}
	}

	/**
	 * 
	 * @created 11.11.2010
	 * @param entries
	 * @return
	 */
	private static List<Triple<Property<?>, Locale, Object>> sortEntries(Collection<Triple<Property<?>, Locale, Object>> entries) {
		LinkedList<Triple<Property<?>, Locale, Object>> ret = new LinkedList<Triple<Property<?>, Locale, Object>>(
				entries);
		Collections.sort(ret, new Comparator<Triple<Property<?>, Locale, Object>>() {

			@Override
			public int compare(Triple<Property<?>, Locale, Object> arg0, Triple<Property<?>, Locale, Object> arg1) {
				if (arg0 == arg1) return 0;
				// if the property is different, compare the names
				if (arg0.getA().getName() != arg1.getA().getName()) {
					return arg0.getA().getName().compareTo(arg1.getA().getName());
				}
				// the next criteria is the locale
				else if (arg0.getB() != arg1.getB()) {
					if (arg0.getB() == InfoStore.NO_LANGUAGE) {
						return 1;
					}
					else if (arg1.getB() == InfoStore.NO_LANGUAGE) {
						return -1;
					}
					else {
						return arg0.getB().toString().compareTo(arg1.getB().toString());
					}
				}
				// finally compare the content using its toString()
				else {
					if (arg0.getC() == arg0.getC()) {
						return 0;
					}
					if (arg0.getC() == null) {
						return 1;
					}
					else if (arg1.getC() == null) {
						return -1;
					}
					else {
						return arg0.getC().toString().compareTo(arg1.getC().toString());
					}
				}
			}
		});
		return ret;
	}

	public static void appendInfoStore(Element idObjectElement, NamedObject idObject, Autosave autosave) throws IOException {
		InfoStore infoStore = idObject.getInfoStore();
		if (infoStore != null && !infoStore.isEmpty()) {
			Element infoStoreElement = idObjectElement.getOwnerDocument().createElement(INFO_STORE);
			appendInfoStoreEntries(infoStoreElement, infoStore, autosave);
			if (infoStoreElement.getChildNodes().getLength() > 0) {
				idObjectElement.appendChild(infoStoreElement);
			}
		}
	}

	/**
	 * Reads all children of the {@link Element} father and adds the created
	 * entries to the infostore
	 * 
	 * @created 08.11.2010
	 * @param infoStore {@link InfoStore}
	 * @param father {@link Element}
	 * @param kb {@link KnowledgeBase}
	 * @throws IOException
	 */
	public static void fillInfoStore(InfoStore infoStore, Element father, KnowledgeBase kb) throws IOException {
		for (Element child : getElementList(father.getChildNodes())) {
			Property<Object> property;
			try {
				property = Property.getUntypedProperty(child.getAttribute("property"));
			}
			catch (NoSuchElementException e) {
				Logger.getLogger("Persistence").log(
						Level.WARNING,
						"Property "
								+ child.getAttribute("property")
								+ " is not supported. Propably the corresponding plugin is missing. This property will be lost when saving the Knowledgebase.");
				continue;
			}
			List<Element> childNodes = XMLUtil.getElementList(child.getChildNodes());
			Object value = "";
			if (childNodes.size() > 0) {
				value = PersistenceManager.getInstance().readFragment(
						childNodes.get(0), kb);
			}
			else {
				String s = child.getTextContent();
				try {
					value = property.parseValue(s);
				}
				catch (NoSuchMethodException e) {
					throw new IOException(e);
				}
			}

			String language = child.getAttribute("lang");
			if (language.length() > 0) {
				String[] split = language.split("_", 3);
				Locale locale;
				if (split.length < 2) {
					locale = new Locale(language);
				}
				else if (split.length == 2) {
					locale = new Locale(split[0], split[1]);
				}
				else {
					locale = new Locale(split[0], split[1], split[2]);
				}
				infoStore.addValue(property, locale, value);
			}
			else {
				infoStore.addValue(property, value);
			}
		}
	}
}
