/*
 * Created on 24.11.2003
 */
package de.d3web.caserepository.addons.shared;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.xml.sax.Attributes;

import de.d3web.caserepository.sax.AbstractTagReader;
import de.d3web.config.Config;
import de.d3web.xml.utilities.XMLTools;

/**
 * 24.11.2003 11:40:22
 * @author hoernlein
 */
public class ConfigReader extends AbstractTagReader {

	protected ConfigReader(String id) { super(id); }
	private static ConfigReader instance;
	private ConfigReader() { this("ConfigReader"); }
	public static AbstractTagReader getInstance() {
		if (instance == null)
			instance = new ConfigReader();
		return instance;
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#getTagNames()
	 */
	public List getTagNames() {
		return Arrays.asList(new String[] {
			"ConfigItems",
			"Name",
			"ConfigItem",
			"Value",
			"MapEntry",
			"MapValue"
		});
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	protected void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equals("ConfigItems")) {
			startConfigItems(attributes);
		} else if (qName.equals("Name")) {
		    // 
		} else if (qName.equals("ConfigItem")) {
		    startConfigItem(attributes);
		} else if (qName.equals("Value")) {
			// 
		} else if (qName.equals("Comment")) {
			startComment(attributes);
		} else if (qName.equals("MapEntry")) {
			startMapEntry(attributes);
		} else if (qName.equals("MapValue")) {
			startMapValue(attributes);
		}
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	protected void endElement(String uri, String localName, String qName) {
		if (qName.equals("ConfigItems")) {
			endConfigItems();
		} else if (qName.equals("Name")) {
		    endName();
		} else if (qName.equals("ConfigItem")) {
		    endConfigItem();
		} else if (qName.equals("Value")) {
			endValue();
		} else if (qName.equals("Comment")) {
			endComment();
		} else if (qName.equals("MapEntry")) {
			endMapEntry();
		} else if (qName.equals("MapValue")) {
			//
		}
	}

	private Config currentConfig = null;

	private void startConfigItems(Attributes attributes) {
		currentConfig = new Config(Config.TYPE_CASE);
	}

	private void endConfigItems() {
		getCaseObject().setConfig(currentConfig);
		currentConfig = null;
	}
	
	private void endName() {
	    if (currentConfig != null) {
		    String name = getTextBetweenCurrentTag();
		    name = XMLTools.prepareFromCDATA(name);
		    currentConfig.setName(name);
	    }
	}
	
	private String currentKey = null;
	private String currentClass = null;
	private Object currentValue = null;
	private String currentConverter = null;
	
	private void startConfigItem(Attributes attributes) {

		currentKey = attributes.getValue("name");

		currentClass = attributes.getValue("class");
		if (currentClass == null)
		currentClass = Config.baseConfig.getClass(currentKey);

		currentValue = attributes.getValue("value");

		currentConverter = attributes.getValue("converter");
		
	}

	private void endConfigItem() {
		Object value = createValue(currentClass, currentValue);
		currentClass = null;
		currentValue = null;
		currentConfig.setValue(currentKey, value, currentConverter);
		currentKey = null;
		currentConverter = null;
	}

	/**
	 * @param currentClass
	 * @param currentValue
	 * @return
	 */
	public static Object createValue(String currentClass, Object currentValue) {
		Object value = null;
		if (currentClass.equals(Config.BOOLEAN)){
			value = Boolean.valueOf((String) currentValue);
		} else if (currentClass.equals(Config.STRING)){
			value = currentValue;
		} else if (currentClass.equals(Config.BOOLEANHASHMAP)) {
			value = currentValue; // this is handled with MapEntry and MapValue
		} else if (currentClass.equals(Config.STRINGHASHMAP)) {
			value = currentValue; // this is handled with MapEntry and MapValue
		} else if (currentClass.equals(Config.INTEGER)) {
			value = Integer.valueOf((String) currentValue);
		} else if (currentClass.equals(Config.DOUBLE)) {
			value = Double.valueOf((String) currentValue);
		} else if (currentClass.equals(Config.STRINGLIST)) {
			StringTokenizer st = new StringTokenizer((String) currentValue, ",", false);
			value = new LinkedList();
			while (st.hasMoreTokens())
				((List) value).add(st.nextToken().trim());
		}
		return value;
	}
	
	private void endValue() {
		currentValue = getTextBetweenCurrentTag();
	}

	private String currentCommentLang = null;

	private void startComment(Attributes attributes) {
		currentCommentLang = attributes.getValue("lang");
	}

	private void endComment() {
		currentConfig.setComment(currentKey, getTextBetweenCurrentTag(), currentCommentLang);
		currentCommentLang = null;
	}

	private String currentMapKey = null;
	private Object currentMapValue = null;
	private boolean currentMapValueBlock = false;

	private void startMapEntry(Attributes attributes) {
		if (currentValue == null)
			currentValue = new HashMap();
		currentMapKey = attributes.getValue("key");
		currentMapValue = attributes.getValue("value");
		if (currentMapValue != null)
			currentMapValueBlock = true;
	}

	private void endMapEntry() {
		((Map) currentValue).put(currentMapKey, currentMapValue);
		currentMapKey = null;
		currentMapValue = null;
		currentMapValueBlock = false;
	}
	
	private void startMapValue(Attributes attributes) {
		if (!currentMapValueBlock) {
			if (currentMapValue == null)
				currentMapValue = new LinkedList();
			((List) currentMapValue).add(attributes.getValue("value"));
		}
	}
	
}
