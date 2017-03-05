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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;

import com.denkbares.progress.ProgressInputStream;
import com.denkbares.progress.ProgressListener;
import com.denkbares.strings.Locales;
import com.denkbares.strings.Strings;
import com.denkbares.utils.Log;
import com.denkbares.utils.Triple;
import com.denkbares.utils.XMLUtils;
import de.d3web.core.io.NoSuchFragmentHandlerException;
import de.d3web.core.io.Persistence;
import de.d3web.core.knowledge.InfoStore;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.knowledge.terminology.info.Property.Autosave;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.scoring.Score;

/**
 * Provides useful static functions for xml persistence handlers
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public final class XMLUtil {

	public static final String INFO_STORE = "infoStore";
	public static final String TYPE = "type";
	public static final String NAME = "name";
	public static final String TARGET_QA_SETS = "TargetQASets";
	public static final String QA_SET = "QASet";

	/**
	 * @deprecated Use {@link Strings#writeDate(Date)} instead
	 */
	@Deprecated
	public static String writeDate(Date date) {
		return Strings.writeDate(date);
	}

	/**
	 * @deprecated Use {@link Strings#readDate(String, SimpleDateFormat)} instead
	 */
	@Deprecated
	public static Date readDate(String dateString, SimpleDateFormat compatibilityFormat) throws ParseException {
		return Strings.readDate(dateString, compatibilityFormat);
	}

	/**
	 * @deprecated Use {@link Strings#readDate(String)} instead
	 * <p>
	 * TODO: Refactor {@link Strings#DATE_FORMAT_COMPATIBILITY} when removing this method.
	 */
	@Deprecated
	public static Date readDate(String dateString) throws ParseException {
		return Strings.readDate(dateString, Strings.DATE_FORMAT_COMPATIBILITY);
	}

	/**
	 * Appends a question element to a parent element. Appends no element, if
	 * the question is null.
	 *
	 * @param parent Element where the question element should be appended
	 * @param q Question which should be represented by the newly appended element
	 * @throws IOException if the question has no ID
	 */
	public static void appendQuestionLinkElement(Element parent, QASet q) throws IOException {
		Document doc = parent.getOwnerDocument();
		if (q != null) {
			Element e = doc.createElement("Question");
			if (q.getName() != null) {
				e.setAttribute(NAME, q.getName());
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
	 * @return true, if the given element fulfills the requirements, false otherwise
	 */
	public static boolean checkNameAndType(Element element, String elementname, String typeToCheck) {
		String nodeName = element.getNodeName();
		String type = element.getAttribute(TYPE);
		return nodeName != null && nodeName.equals(elementname) && type != null
				&& type.equals(typeToCheck);
	}

	/**
	 * Checks if the element is named "Condition" and if has an attribute type
	 * with the value typeToCheck
	 *
	 * @param element to be checked
	 * @param typeToCheck Value for the Attribute type
	 * @return true, if the given element fulfills the requirements, false otherwise
	 */
	public static boolean checkCondition(Element element, String typeToCheck) {
		return checkNameAndType(element, "Condition", typeToCheck);
	}

	/**
	 * Creates a condition element with the id of the named object and the given
	 * type
	 *
	 * @param persistence the persistence to access the Document, where the Element should be
	 * created
	 * @param nob TerminologyObject, whose ID should be used
	 * @param type type of the condition
	 * @return condition element
	 */
	public static Element writeCondition(Persistence<?> persistence, TerminologyObject nob, String type) {
		return writeCondition(persistence.getDocument(), nob, type);
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
			element.setAttribute(NAME, nob.getName());
		}
		else {
			element.setAttribute(NAME, "");
		}
		return element;
	}

	/**
	 * Creates a condition element with the id of the named object, the given
	 * type and value
	 *
	 * @param persistence Persistence to access Document, where the Element should be created
	 * @param nob TerminologyObject, whose ID should be used
	 * @param type type of the condition
	 * @param value value of the condition
	 * @return condition element
	 */
	public static Element writeCondition(Persistence<?> persistence, TerminologyObject nob, String type, String value) {
		return writeCondition(persistence.getDocument(), nob, type, value);
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
	 * @param persistence the persistence to access the Document, where the Element should be
	 * created
	 * @param nob TerminologyObject, whose ID should be used
	 * @param type type of the condition
	 * @param value value of the condition
	 * @return condition element
	 */
	public static Element writeConditionWithValueNode(Persistence<?> persistence, TerminologyObject nob, String type, String value) throws IOException {
		return writeConditionWithValueNode(persistence.getDocument(), nob, type, value);
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
	 * @param persistence the persistence to access the Document, where the Element should be
	 * created
	 * @param nob TerminologyObject, whose ID should be used
	 * @param type type of the condition
	 * @param value value of the condition
	 * @return condition element
	 */
	public static Element writeConditionWithValueNode(Persistence<?> persistence, TerminologyObject nob, String type, Element value) {
		return writeConditionWithValueNode(persistence.getDocument(), nob, type, value);
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
	 * @param value answer of the condition
	 * @return condition element
	 * @throws IOException if one of the elements in values is neither a Choice nor a Unknown
	 * Answer
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

	public static String getValue(TerminologyObject nob, Object answer) throws IOException {
		if (answer instanceof ChoiceValue) {
			ChoiceValue v = (ChoiceValue) answer;
			ChoiceID choice = v.getChoiceID();
			return choice.getText();
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
		else if (answer instanceof TextValue) {
			return ((TextValue) answer).getValue().toString();
		}
		else if (answer instanceof DateValue) {
			return Strings.writeDate(((DateValue) answer).getDate());
		}
		else {
			throw new IOException(
					"The given value is neighter a Choice nor a Unknown nor a text nor a date value.");
		}
	}

	/**
	 * Creates a condition element with the specified type
	 *
	 * @param persistence the persistence to access the Document, where the Element should be
	 * created
	 * @param type type of the condition
	 * @return condition element
	 */
	public static Element writeCondition(Persistence<?> persistence, String type) {
		return writeCondition(persistence.getDocument(), type);
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
			Element targetQASets = doc.createElement(TARGET_QA_SETS);
			for (QASet qaset : qaSets) {
				Element qasetElement = doc.createElement(QA_SET);
				qasetElement.setAttribute(NAME, qaset.getName());
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
		List<QASet> ret = new LinkedList<>();
		if (element.getNodeName().equalsIgnoreCase(TARGET_QA_SETS)) {
			NodeList qaSets = element.getChildNodes();
			for (int k = 0; k < qaSets.getLength(); ++k) {
				Node q = qaSets.item(k);
				if (q.getNodeName().equalsIgnoreCase(QA_SET)) {
					String id = q.getAttributes().getNamedItem(NAME).getNodeValue();
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
	 * @param namedObject TerminologyObject, whose children should be appended as Elements
	 * @param element Element representing the namedObject, where the children will be appended
	 */
	public static void appendChildren(TerminologyObject namedObject, Element element) {
		Document doc = element.getOwnerDocument();
		TerminologyObject[] children = namedObject.getChildren();
		if (children.length != 0) {
			Element childrenElement = doc.createElement("Children");
			for (TerminologyObject child : children) {
				Element childElement = doc.createElement("Child");
				childElement.setAttribute(NAME, child.getName());
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
	 * @param element representing the namedObject and containing the children as childnodes
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
				String id = child.getAttribute(NAME);
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
		return XMLUtils.getPrimitiveValue(textContent, clazz);
	}

	/**
	 * Get the children of a document element as a normal {@link java.util.List}, filtered by the
	 * given <tt>tagName</tt>.
	 *
	 * @param element the element to get the children from
	 * @return the children of the given element with the given <tt>tagName</tt> as a normal {@link
	 * java.util.List}
	 */
	public static List<Element> getChildren(Element element, String... tagName) {
		return getElementList(element.getChildNodes(), tagName);
	}

	/**
	 * Get the children of a document element as a normal  {@link java.util.List}.
	 *
	 * @param element the element to get the children from
	 * @return the children of the given element as a normal {@link java.util.List}
	 */
	public static List<Element> getChildren(Element element) {
		return XMLUtils.getChildren(element);
	}

	/**
	 * Filters all elements of a NodeList and returns them in a collection.
	 *
	 * @param list Nodelist containing all types of nodes (text nodes etc.)
	 * @return a list containing all elements from nodeist, but not containing other nodes such as
	 * text nodes etc.
	 */
	public static List<Element> getElementList(NodeList list) {
		return XMLUtils.getElementList(list);
	}

	/**
	 * Filters all elements of a NodeList and returns them in a collection. The
	 * list will only contain that elements of the NodeList that match the
	 * specified node name. The name selection is case insensitive.
	 *
	 * @param list Nodelist containing all types of nodes (text nodes etc.)
	 * @param nodeNames the name of the elements to be selected (case insensitive)
	 * @return a list containing all elements from nodelist, but not containing other nodes such as
	 * text nodes etc.
	 */
	public static List<Element> getElementList(NodeList list, String... nodeNames) {
		return XMLUtils.getElementList(list, nodeNames);
	}

	/**
	 * Appends all entries with the given autosave of the {@link InfoStore} to
	 * the specified father. If autosave is null, all entries will be appended
	 *
	 * @param father {@link Element}
	 * @param infoStore {@link InfoStore}
	 * @param autosave {@link Autosave}
	 * @throws IOException if a fragment cannot be read
	 * @created 08.11.2010
	 */
	public static void appendInfoStoreEntries(Persistence<?> persistance, Element father, InfoStore infoStore, Autosave autosave) throws IOException {
		Document doc = father.getOwnerDocument();
		for (Triple<Property<?>, Locale, Object> entry : sortEntries(infoStore.entries())) {
			if (autosave == null || entry.getA().hasState(autosave)) {
				Element entryElement = doc.createElement("entry");
				father.appendChild(entryElement);
				entryElement.setAttribute("property", entry.getA().getName());
				Locale language = entry.getB();
				if (!Locales.isEmpty(language)) {
					entryElement.setAttribute("lang", language.toString());
				}
				try {
					entryElement.appendChild(persistance.writeFragment(entry.getC()));
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

	public static List<Triple<Property<?>, Locale, Object>> sortEntries(Collection<Triple<Property<?>, Locale, Object>> entries) {
		LinkedList<Triple<Property<?>, Locale, Object>> ret = new LinkedList<>(
				entries);
		ret.sort((arg0, arg1) -> {
			if (arg0 == arg1) return 0;
			// if the property is different, compare the names
			//noinspection StringEquality
			if (arg0.getA().getName() != arg1.getA().getName()) {
				return arg0.getA().getName().compareTo(arg1.getA().getName());
			}
			// the next criteria is the locale
			else if (arg0.getB() != arg1.getB()) {
				if (Locales.isEmpty(arg0.getB())) {
					return 1;
				}
				else if (Locales.isEmpty(arg1.getB())) {
					return -1;
				}
				else {
					return arg0.getB().toString().compareTo(arg1.getB().toString());
				}
			}
			// finally compare the content using its toString()
			else {
				if (arg0.getC() == arg1.getC()) {
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
		});
		return ret;
	}

	public static void appendInfoStore(Persistence<?> persistance, Element idObjectElement, NamedObject idObject, Autosave autosave) throws IOException {
		InfoStore infoStore = idObject.getInfoStore();
		if (infoStore != null && !infoStore.isEmpty()) {
			Element infoStoreElement = idObjectElement.getOwnerDocument().createElement(INFO_STORE);
			appendInfoStoreEntries(persistance, infoStoreElement, infoStore, autosave);
			if (infoStoreElement.getChildNodes().getLength() > 0) {
				idObjectElement.appendChild(infoStoreElement);
			}
		}
	}

	/**
	 * Reads all children of the {@link Element} father and adds the created
	 * entries to the infostore
	 *
	 * @param infoStore {@link InfoStore}
	 * @param father {@link Element}
	 * @throws IOException if any property is known but cannot be read/parsed
	 * @created 08.11.2010
	 */
	public static void fillInfoStore(Persistence<?> persistence, InfoStore infoStore, Element father) throws IOException {
		for (Element child : getElementList(father.getChildNodes())) {
			Property<Object> property;
			String propertyName = child.getAttribute("property");
			try {
				property = Property.getUntypedProperty(propertyName);
			}
			catch (NoSuchElementException e) {
				logMissingProperty(persistence, propertyName);
				continue;
			}
			List<Element> childNodes = XMLUtil.getElementList(child.getChildNodes());
			Object value;
			if (childNodes.isEmpty()) {
				String s = child.getTextContent();
				try {
					value = property.parseValue(s);
				}
				catch (NoSuchMethodException e) {
					throw new IOException(e);
				}
			}
			else {
				value = persistence.readFragment(childNodes.get(0));
			}

			String language = child.getAttribute("lang");
			if (language.isEmpty()) {
				infoStore.addValue(property, value);
			}
			else {
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
		}
	}

	private static final Map<Object, Set<String>> knownMissingProperties = new WeakHashMap<>();

	/**
	 * Logs a missing property to the console. It avoids two log entries for the same missing
	 * property in the same persistence artifact.
	 *
	 * @param persistence the persistence currently used
	 * @param propertyName the property that is missing
	 */
	public static void logMissingProperty(Persistence<?> persistence, String propertyName) {
		// log only once per artifact
		Set<String> missing = knownMissingProperties.computeIfAbsent(
				persistence.getArtifact(), a -> new HashSet<>());
		if (missing.add(propertyName)) {
			Log.mock(1, Level.WARNING, "Property '" + propertyName +
					"' is not supported. Probably the corresponding plugin " +
					"is missing. This property will be lost when saving " +
					"the knowledge base.");
		}
	}

	/**
	 * Creates an XML {@link Document} from the given {@link InputStream}.
	 *
	 * @param stream the XML input stream
	 * @return Document the document created from the stream
	 * @throws IOException if the stream cannot be read or does not contains valid XML content or
	 * the XML parser cannot be configured
	 */
	public static Document streamToDocument(InputStream stream) throws IOException {
		return streamToDocument(stream, null);
	}

	/**
	 * Creates an XML {@link Document} from the given {@link InputStream}.
	 *
	 * @param stream the XML input stream
	 * @param resolver is a {@link EntityResolver} to specify how entities given in the {@link
	 * Document} should be resolved
	 * @return Document the document created from the stream
	 * @throws IOException if the stream cannot be read or does not contains valid XML content or
	 * the XML parser cannot be configured
	 */
	public static Document streamToDocument(InputStream stream, EntityResolver resolver) throws IOException {
		return XMLUtils.streamToDocument(stream, resolver);
	}

	/**
	 * Creates an XML {@link Document} from the {@link File}.
	 *
	 * @param file the file to be read
	 * @return Document the document created from the stream
	 * @throws IOException when an error occurs
	 */
	public static Document fileToDocument(File file) throws IOException {
		return XMLUtils.fileToDocument(file);
	}

	/**
	 * Creates an XML {@link Document} from the {@link File}, indicating the
	 * parse progress.
	 *
	 * @param file the file to be read
	 * @param progress the progress listener used to notify the progress
	 * @param message the progress message to be used when notifying the progress
	 * @return Document the document created from the stream
	 * @throws IOException when an error occurs
	 */
	public static Document fileToDocument(File file, ProgressListener progress, String message) throws IOException {
		try (InputStream in = new ProgressInputStream(new FileInputStream(file), progress, message)) {
			return streamToDocument(in);
		}
	}

	/**
	 * Creates an empty Document
	 *
	 * @return newly created document
	 * @throws IOException when an error occurs
	 */
	public static Document createEmptyDocument() throws IOException {
		return XMLUtils.createEmptyDocument();
	}

	/**
	 * Writes the Document to the given OutputStream
	 *
	 * @param doc input document
	 * @param stream outout stream
	 * @throws IOException when an error occurs
	 */
	public static void writeDocumentToOutputStream(Document doc, OutputStream stream) throws IOException {
		XMLUtils.documentToStream(doc, stream);
	}

	/**
	 * @return the Score matching the given String (e.g. "n7" to Score.N7)
	 * @throws IOException when an error occurs
	 */
	public static Score getScore(String value) throws IOException {
		Score score = null;
		if (value.equalsIgnoreCase("n7")) {
			score = Score.N7;
		}
		else if (value.equalsIgnoreCase("n6")) {
			score = Score.N6;
		}
		else if (value.equalsIgnoreCase("n5")) {
			score = Score.N5;
		}
		else if (value.equalsIgnoreCase("n5x")) {
			score = Score.N5x;
		}
		else if (value.equalsIgnoreCase("n4")) {
			score = Score.N4;
		}
		else if (value.equalsIgnoreCase("n3")) {
			score = Score.N3;
		}
		else if (value.equalsIgnoreCase("n2")) {
			score = Score.N2;
		}
		else if (value.equalsIgnoreCase("n1")) {
			score = Score.N1;
		}
		else if (value.equalsIgnoreCase("p1")) {
			score = Score.P1;
		}
		else if (value.equalsIgnoreCase("p2")) {
			score = Score.P2;
		}
		else if (value.equalsIgnoreCase("p3")) {
			score = Score.P3;
		}
		else if (value.equalsIgnoreCase("p4")) {
			score = Score.P4;
		}
		else if (value.equalsIgnoreCase("p5")) {
			score = Score.P5;
		}
		else if (value.equalsIgnoreCase("p5x")) {
			score = Score.P5x;
		}
		else if (value.equalsIgnoreCase("p6")) {
			score = Score.P6;
		}
		else if (value.equalsIgnoreCase("p7")) {
			score = Score.P7;
		}
		else if (value.equalsIgnoreCase("pp")) {
			throw new IOException(
					"knowledgebase uses pp-rules! - this will cause NullPointerException in rule firing");
		}
		return score;
	}

	/**
	 * Writes a number of Strings to the specified element with a specified tag
	 * name. Each specified string will create its own element with the
	 * specified tag name.
	 *
	 * @param element the element to add the tag(s) to
	 * @param tagName the name of the tag(s) to be created
	 * @param values the string values to add
	 * @created 25.01.2014
	 */
	public static void writeStrings(Element element, String tagName, String... values) {
		XMLUtils.writeStrings(element, tagName, values);
	}

	/**
	 * Writes a number of enum values to the specified element with a specified
	 * tag name. Each specified string will create its own element with the
	 * specified tag name.
	 *
	 * @param element the element to add the tag(s) to
	 * @param tagName the name of the tag(s) to be created
	 * @param values the string values to add
	 * @created 25.01.2014
	 */
	public static void writeEnums(Element element, String tagName, Enum<?>... values) {
		XMLUtils.writeEnums(element, tagName, values);
	}

	/**
	 * Reads a all elements with the specified tag name that are children of the
	 * specified element and return their text contents as a string array.
	 *
	 * @param element the element to get the tag's text contents for
	 * @param tagName the tag name of the child elements to get the text contents for
	 * @return the text contents of the matched elements
	 * @created 25.01.2014
	 */
	public static String[] readStrings(Element element, String tagName) {
		return XMLUtils.readStrings(element, tagName);
	}

	/**
	 * Reads a all elements with the specified tag name that are children of the
	 * specified element and return their text contents as an array of enum
	 * values of the specified enum class.
	 *
	 * @param element the element to get the tag's text contents for
	 * @param tagName the tag name of the child elements to get the text contents for
	 * @return the text contents of the matched elements
	 * @created 25.01.2014
	 */
	public static <T extends Enum<T>> T[] readEnums(Element element, String tagName, Class<T> clazz) {
		return XMLUtils.readEnums(element, tagName, clazz);
	}

	public static String getElementAsString(Element element) {
		return XMLUtils.getStringRepresentation(element);
	}
}
