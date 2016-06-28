package de.d3web.mminfo.io;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.InfoStore;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.strings.Strings;
import de.d3web.utils.Log;

public class MMInfoContentHandler extends DefaultHandler {

	private final Persistence<KnowledgeBase> persistence;

	// some parsing variables
	private String currentData = null;
	private InfoStore currentStore = null;
	private String currentProperty = null;
	private Locale currentLocale = null;
	private DOMBuilder entityBuilder = null;

	public MMInfoContentHandler(Persistence<KnowledgeBase> persistence) {
		this.persistence = persistence;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		char[] copy = Arrays.copyOf(ch, ch.length);
		// build DOM for entity if required
		if (entityBuilder != null) {
			entityBuilder.characters(copy, start, length);
			return;
		}

		// otherwise use the data for own purposes
		currentData += new String(copy, start, length);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// build DOM for entity if required
		// to use fragment handlers later on to decode the content
		if (this.currentProperty != null) {
			if (entityBuilder == null) {
				entityBuilder = new DOMBuilder(persistence.getDocument());
			}
			entityBuilder.startElement(uri, localName, qName, attributes);
			return;
		}

		// otherwise we proceed in parsing the entries
		this.currentData = "";
		if (qName.equalsIgnoreCase("KnowledgeBase")) {
			this.currentStore = persistence.getArtifact().getInfoStore();
		}
		else if (qName.equalsIgnoreCase("idObject")) {
			String name = attributes.getValue("name");
			String choice = attributes.getValue("choice");
			this.currentStore = findInfoStore(name, choice);
		}
		else if (qName.equalsIgnoreCase("entry")) {
			this.currentProperty = attributes.getValue("property");
			String language = attributes.getValue("lang");
			if (language != null && !language.isEmpty()) {
				String[] split = language.split("_", 3);
				if (split.length < 2) {
					this.currentLocale = new Locale(language);
				}
				else if (split.length == 2) {
					this.currentLocale = new Locale(split[0], split[1]);
				}
				else {
					this.currentLocale = new Locale(split[0], split[1], split[2]);
				}
			}
			else {
				this.currentLocale = InfoStore.NO_LANGUAGE;
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// build DOM for entity if required
		if (entityBuilder != null && !entityBuilder.isDone()) {
			entityBuilder.endElement(uri, localName, qName);
			return;
		}

		// otherwise add entry
		if (qName.equalsIgnoreCase("entry")) {
			try {
				Property<Object> property = Property.getUntypedProperty(currentProperty);
				Object value;
				if (entityBuilder != null) {
					List<Element> childNodes = XMLUtil.getElementList(entityBuilder.getElement().getChildNodes());
					value = persistence.readFragment(childNodes.get(0));
				}
				else {
					value = property.parseValue(this.currentData);
				}
				this.currentStore.addValue(property, currentLocale, value);
			}
			catch (NoSuchElementException e) {
				Log.warning("Property '" + currentProperty +
								"' is not supported. Propably the corresponding plugin " +
								"is missing. This property will be lost when saving " +
								"the knowledge base.");
			}
			catch (NoSuchMethodException e) {
				throw new SAXException(e);
			}
			catch (IOException e) {
				throw new SAXException(e);
			}
			finally {
				// indicate that this property is finished
				this.currentProperty = null;
				this.currentLocale = null;
				this.entityBuilder = null;
			}
		}
	}

	/**
	 * Searches the info store for a specified object and returns it. The method
	 * throws an exception if there is no such specified object.
	 * 
	 * @created 20.09.2013
	 * @param name the name of the object
	 * @param choice the name of the objects choice or null if no choice is
	 *        wanted
	 * @return the info store matching the specified object
	 * @throws IOException if no such object with an info store exists
	 */
	private InfoStore findInfoStore(String name, String choice) throws SAXException {
		NamedObject namedObject = persistence.getArtifact().getManager().search(name);
		if (namedObject == null) {
			throw new SAXException("NamedObject " + name
					+ " cannot be found in KnowledgeBase.");
		}
		if (Strings.isBlank(choice)) return namedObject.getInfoStore();
		if (namedObject instanceof QuestionChoice) {
			namedObject = KnowledgeBaseUtils.findChoice((QuestionChoice) namedObject, choice);
			if (namedObject == null) {
				throw new SAXException("Choice " + choice + " not found in " + name);
			}
		}
		else {
			throw new SAXException(
					"The choice attribute is only allowed for QuestionChoices.");
		}
		return namedObject.getInfoStore();
	}

}
