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

package de.d3web.config.persistence;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.config.Config;
import de.d3web.config.utils.BooleanHashMap;
import de.d3web.config.utils.DoubleHashMap;
import de.d3web.config.utils.StringHashMap;
import de.d3web.core.kpers.utilities.InputFilter;
import de.d3web.core.kpers.utilities.XMLTools;
import de.d3web.core.kpers.utilities.XMLUtil;

/**
 * @author bannert
 */
public class ConfigReader {

	private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(false);
        return dbf.newDocumentBuilder();
    }

	public static Config createConfig(URL url) {
		try {
			Document doc = getDocumentBuilder().parse(InputFilter.getFilteredInputSource(url));
			return init(doc);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static Config createConfig(InputStream stream, String type) {
		try {
			Document doc = getDocumentBuilder().parse(stream);
			return init(doc, type);
		} catch (Exception ex) {
		    Logger.getLogger(ConfigReader.class.getName()).warning("can't read " + stream + ": " + ex);
		}
		return null;
	}
	
	private static Config init(Document doc){
		NodeList configItemsList = doc.getElementsByTagName("ConfigItems");
		String type = "";
        // [MISC]:aha:this code sucks ... there is only one such item in the list
		for (int i = 0; i < configItemsList.getLength(); i++){
			NamedNodeMap attr = configItemsList.item(0).getAttributes();
			type = getStringValueForName(attr, "type");
		}
		return init(doc, type);
	}	
	
	private static Config init(Document doc, String type){
		Config config = new Config(type);
		NodeList configItemsList = doc.getElementsByTagName("ConfigItems");
		for (int i = 0; i < configItemsList.getLength(); i++){
			NodeList configItems = configItemsList.item(i).getChildNodes();
			for (int j = 0; j < configItems.getLength(); j++){
				Node currentNode = configItems.item(j);
				if (currentNode.getNodeName().equals("ConfigItem")){
					NamedNodeMap attr = currentNode.getAttributes();
					String name = getStringValueForName(attr, "name");
					
					String cclass = getStringValueForName(attr, "class");
					if (cclass == null) {
						cclass = Config.baseConfig.getClass(name);
						if (cclass == null){
							Logger.getLogger(Config.class.getName()).warning("Key " + name + " is not allowed, is not included in BaseConfig.");
							continue;
						}
					} else
						config.setClass(name, cclass);

					setComment(config, name, currentNode);

					Object value = null;
					if (cclass.equals(Config.BOOLEAN)){
						value = getBoolean(currentNode);
					} else if (cclass.equals(Config.STRING)){
						value = getString(currentNode);
					} else if (cclass.equals(Config.BOOLEANHASHMAP)){
						value = getBooleanHashMap(currentNode);
					} else if (cclass.equals(Config.DOUBLEHASHMAP)){
						value = getDoubleHashMap(currentNode);
					} else if (cclass.equals(Config.STRINGHASHMAP)){
						value = getStringHashMap(currentNode);
					} else if (cclass.equals(Config.INTEGER)) {
						value = getInt(currentNode);
					} else if (cclass.equals(Config.DOUBLE)) {
						value = getDouble(currentNode);
					} else if (cclass.equals(Config.STRINGLIST)) {
						value = getStringList(currentNode, ",");
					}
					if (value != null) {
					    // [FIXME]:aha:config must set converter even if there are no values
						config.setValue(name, value, getConverter(currentNode, type));	
					}
					config.setConverter(name, getConverter(currentNode, type));
					
				} else if (currentNode.getNodeName().equals("Name")) {
				    String name = XMLUtil.getText(currentNode);
				    name = XMLTools.prepareFromCDATA(name);
				    config.setName(name);
				}
			}
		}
		return config;
	}

	/**
	 * @param name
	 * @param currentNode
	 */
	private static void setComment(Config conf, String name, Node node) {
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++){
			if (childNodes.item(i).getNodeName().equals("Comment")){
				NamedNodeMap attr = childNodes.item(i).getAttributes();
				String lang = getStringValueForName(attr, "lang");
				NodeList childs = childNodes.item(i).getChildNodes();
				for(int j = 0; j < childs.getLength(); j++){
					if(childs.item(j).getNodeType() == Node.CDATA_SECTION_NODE){
						String comment = childs.item(j).getNodeValue();
						conf.setComment(name, comment, lang);		
					}
				}
			}
		}
	}

	private static String getConverter(Node node, String type) {
		NamedNodeMap attributes = node.getAttributes();
		String converter = getStringValueForName(attributes, "converter");
		if (converter == null)
		    return converter;
		if (Config.compare(converter, type) < 0){
		    Logger.getLogger(ConfigReader.class.getName()).warning("ConfigReader read in Node(" + getStringValueForName(attributes, "name") + ") converter= " + converter + " and ConfigType=" + type + " ==> Converter not insert in Config!");
			return null;
		}
		return converter;
	}


	private static HashMap getBooleanHashMap(Node node){
		BooleanHashMap result = new BooleanHashMap();
		NodeList valueNodes = node.getChildNodes();
		for (int j = 0; j < valueNodes.getLength(); j++) {
			Node childNode = valueNodes.item(j);
			if (childNode.getNodeName().equals("MapEntry")){
				NamedNodeMap attr = childNode.getAttributes();
				String key = getStringValueForName(attr, "key");
				String value = getStringValueForName(attr, "value");
				if (value != null) {
					if (value.equals("true") || value.equals("false")) {
						result.put(key, Boolean.valueOf(value));
					} else {
						Logger.getLogger(ConfigReader.class.getName()).warning("can't read >" + value + "< as Boolean in BooleanHashMap");
					}
				}
			} else if (childNode.getNodeName().equals("Keys")){
				NodeList childNodes = childNode.getChildNodes();
				for(int i = 0; i < childNodes.getLength(); i++){
					if(childNodes.item(i).getNodeType() == Node.CDATA_SECTION_NODE){
						result.setKeys(getHashMapKeys(childNodes.item(i).getNodeValue()));		
					}
				}
			}
		}
		if (result.getKeys().isEmpty() && result.isEmpty()) return null;	
		return result;
	}

	private static HashMap getDoubleHashMap(Node node){
		DoubleHashMap result = new DoubleHashMap();
		NodeList valueNodes = node.getChildNodes();
		for (int j = 0; j < valueNodes.getLength(); j++) {
			Node childNode = valueNodes.item(j);
			if (childNode.getNodeName().equals("MapEntry")){
				NamedNodeMap attr = childNode.getAttributes();
				String key = getStringValueForName(attr, "key");
				String value = getStringValueForName(attr, "value");
				if (value != null) {
					try {
						result.put(key, Double.valueOf(value));
					} catch (Exception ex) {
						Logger.getLogger(ConfigReader.class.getName()).warning("can't read >" + value + "< as Double in DoubleHashMap");
					}
				}
			} else if (childNode.getNodeName().equals("Keys")){
				NodeList childNodes = childNode.getChildNodes();
				for(int i = 0; i < childNodes.getLength(); i++){
					if(childNodes.item(i).getNodeType() == Node.CDATA_SECTION_NODE){
						result.setKeys(getHashMapKeys(childNodes.item(i).getNodeValue()));		
					}
				}
			}
		}
		if (result.getKeys().isEmpty() && result.isEmpty()) return null;	
		return result;
	}
	
	/**
	 * @param string
	 * @param result
	 */
	private static List<String> getHashMapKeys(String keys) {
		List<String> result = new LinkedList<String>();
		if (keys == null || keys.equals("")){
				return null;
			} else {
				StringTokenizer st = new StringTokenizer(keys, ",", false);
				while (st.hasMoreTokens()) {
					result.add(st.nextToken());
				}
				return result;
			}
	}

	private static HashMap getStringHashMap(Node node){
		StringHashMap result = new StringHashMap();
		NodeList valueNodes = node.getChildNodes();
		for (int j = 0; j < valueNodes.getLength(); j++) {
			Node childNode = valueNodes.item(j);
			if (childNode.getNodeName().equals("MapEntry")){
				NamedNodeMap attr = childNode.getAttributes();
				String key = getStringValueForName(attr, "key");
				String value = getString(childNode);
				if (value != null) {
					result.put(key, value);
				}
			} else if (childNode.getNodeName().equals("Keys")){
				NodeList childNodes = childNode.getChildNodes();
				for(int i = 0; i < childNodes.getLength(); i++){
					if(childNodes.item(i).getNodeType() == Node.CDATA_SECTION_NODE){
						result.setKeys(getHashMapKeys(childNodes.item(i).getNodeValue()));		
					}
				}
			}
		}
		if (result.getKeys().isEmpty() && result.isEmpty())
			return null;	
		return result;
	}

	private static Boolean getBoolean(Node node){
		Boolean result = null;
		NamedNodeMap attributes = node.getAttributes();
		String value = getStringValueForName(attributes, "value");
		if (value != null) {
			if (value.equals("true"))
			    result = Boolean.valueOf(value);
			else if (value.equals("false"))
			    result = Boolean.valueOf(value);
		}
		return result;
	}

	private static String getStringValueForName(NamedNodeMap map, String name) {
		String result = null;
		Node node = map.getNamedItem(name);
		if (node != null)
			result = node.getNodeValue();
		return result;
	}
	
	private static Integer getIntValueForName(NamedNodeMap map, String name) {
		Integer result = null;
		Node node = map.getNamedItem(name);
		if (node != null) {
			try {
				result = Integer.decode(node.getNodeValue());
			} catch (NumberFormatException ex) {
			    // null is ok then 
			}
		}
		return result;
	}

	private static String getString(Node node){
		NamedNodeMap attributes = node.getAttributes();
		String result = getStringValueForName(attributes, "value");
		if (result == null) {
			NodeList childs = node.getChildNodes();
			for (int j = 0; j < childs.getLength(); j++)
				if (childs.item(j).getNodeName().equals("Value")){
					NodeList childNodes = childs.item(j).getChildNodes();
					for (int i = 0; i < childNodes.getLength(); i++)
						if (childNodes.item(i).getNodeType() == Node.CDATA_SECTION_NODE)
                            result = childNodes.item(i).getNodeValue();
				}	
		}	
		return result;
	}

	private static Integer getInt(Node node){
		NamedNodeMap attributes = node.getAttributes();
		return getIntValueForName(attributes, "value");
	}

	/**
	 * 
	 * @return List
	 * @param doc Document
	 * @param name String
	 * @param delims String
	 */

	private static List<String> getStringList(Node node, String delims){
		List<String> result = null;
		String s = getString(node);
		if (s == null || s.equals("")){
			return result;
		} else {
			result = new LinkedList<String>();
			StringTokenizer st = new StringTokenizer(s, delims, false);
			while (st.hasMoreTokens()) {
				result.add(st.nextToken().trim());
			}
			return result;
		}
	}

	/**
	 * Method getDouble.
	 * @param doc Document
	 * @param string
	 * @return double
	 */
	private static Double getDouble(Node node){
		NamedNodeMap attributes = node.getAttributes();
		String value = getStringValueForName(attributes, "value");
		if (value == null || value.equals("")) return null;
		return new Double(value);
	}
	
}
