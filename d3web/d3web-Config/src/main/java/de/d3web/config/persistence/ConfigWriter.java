/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.config.persistence;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.config.Config;
import de.d3web.core.io.utilities.XMLTools;

/**
 * @author bannert
 */
public class ConfigWriter {

	public static void write(Config conf, StringBuffer sb) {
		sb.append(
				"<ConfigItems type=\"" + conf.getType() + "\">\n" +
						"<Name><![CDATA[" + XMLTools.prepareForCDATA(conf.getName())
						+ "]]></Name>\n"
				);
		for (String key : conf.getKeySet()) {
			Object o = conf.getValue(key);
			if (o instanceof Boolean) {
				sb.append(writeBoolean(key, (Boolean) o, conf));
			}
			else if (o instanceof String) {
				sb.append(writeString(key, (String) o, conf));
			}
			else if (o instanceof Integer) {
				sb.append(writeInteger(key, (Integer) o, conf));
			}
			else if (o instanceof Double) {
				sb.append(writeDouble(key, (Double) o, conf));
			}
			else if (o instanceof Map) {
				sb.append(writeMap(key, (Map) o, conf));
			}
			else if (o instanceof List) {
				sb.append(writeStringList(key, (List) o, conf));
			}
		}
		sb.append("</ConfigItems>\n");
	}

	/**
	 * @param key
	 * @param list
	 * @param string
	 * @return String
	 */

	private static String writeStringList(String key, List list, Config conf) {
		StringBuffer sb = new StringBuffer();
		sb.append("\t<ConfigItem name=\"" + key + "\"");
		sb.append(" value=\"");
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			Object o = iter.next();
			if (o instanceof String) {
				sb.append((String) o);
				sb.append(", ");
			}
		}
		sb = sb.deleteCharAt(sb.length() - 1);
		sb = sb.deleteCharAt(sb.length() - 1);
		sb.append("\"");
		sb.append(" converter=\"" + conf.getConverter(key) + "\"");
		sb.append(">\n");
		sb.append(writeComment(key, conf));
		sb.append("\t</ConfigItem>\n");

		return sb.toString();
	}

	/**
	 * @param key
	 * @param map
	 * @return Object
	 */
	private static String writeMap(String key, Map map, Config conf) {
		StringBuffer sb = new StringBuffer();
		String converter = conf.getConverter(key);
		if (converter == null || converter.equals("")) {
			sb.append("\t<ConfigItem name=\"" + key + "\">\n");
		}
		else {
			sb.append("\t<ConfigItem name=\"" + key + "\" converter=\"" + converter + "\">\n");
		}
		sb.append(writeComment(key, conf));
		for (String aKey : (Set<String>) map.keySet()) {
			Object o = map.get(key);
			if (o instanceof String) {
				sb.append("\t\t<MapEntry key=\"" + aKey + "\" value=\"" + (String) o + "\"/>\n");
			}
			else if (o instanceof List) {
				sb.append("\t\t<MapEntry key=\"" + aKey + "\">\n");
				sb.append(writeStringList("Map", (List<String>) o));
				sb.append("\t\t</MapEntry>\n");
			}
			else if (o instanceof Boolean) {
				sb.append("\t\t<MapEntry key=\"" + aKey + "\" value=\"" + ((Boolean) o).toString()
						+ "\"/>\n");
			}
		}
		sb.append("\t</ConfigItem>\n");
		return sb.toString();
	}

	/**
	 * @param list
	 * @return Object
	 */
	private static String writeStringList(String preTag, List<String> list) {
		StringBuffer sb = new StringBuffer();
		for (String value : list)
			sb.append("\t\t\t<" + preTag + "Value value=\"" + value + "\"/>\n");
		return sb.toString();
	}

	/**
	 * @param key
	 * @param double1
	 * @return Object
	 */
	private static Object writeDouble(String key, Double value, Config conf) {
		StringBuffer sb = new StringBuffer();
		String converter = conf.getConverter(key);
		sb.append("\t<ConfigItem name=\"");
		sb.append(key);
		sb.append("\" value=\"");
		sb.append(value.toString());
		if (converter == null || converter.equals("")) {
			sb.append("\">\n");
		}
		else {
			sb.append("\" converter=\"" + converter + "\">\n");
		}
		sb.append(writeComment(key, conf));
		sb.append("\t</ConfigItem>\n");
		return sb.toString();
	}

	/**
	 * @param key
	 * @param integer
	 * @return Object
	 */
	private static String writeInteger(String key, Integer value, Config conf) {
		StringBuffer sb = new StringBuffer();
		String converter = conf.getConverter(key);
		sb.append("\t<ConfigItem name=\"");
		sb.append(key);
		sb.append("\" value=\"");
		sb.append(value.toString());
		if (converter == null || converter.equals("")) {
			sb.append("\">\n");
		}
		else {
			sb.append("\" converter=\"" + converter + "\">\n");
		}
		sb.append(writeComment(key, conf));
		sb.append("\t</ConfigItem>\n");
		return sb.toString();
	}

	/**
	 * @param key
	 * @param string
	 * @return Object
	 */
	private static String writeString(String key, String value, Config conf) {
		StringBuffer sb = new StringBuffer();
		String converter = conf.getConverter(key);
		sb.append("\t<ConfigItem name=\"");
		sb.append(key);
		if (converter == null || converter.equals("")) {
			sb.append("\"");
		}
		else {
			sb.append("\" converter=\"" + converter + "\"");
		}
		sb.append(">\n\t\t<Value><![CDATA[");
		sb.append(value);
		sb.append("]]></Value>\n");
		sb.append(writeComment(key, conf));
		sb.append("\t</ConfigItem>\n");
		return sb.toString();
	}

	/**
	 * @param key
	 * @param value
	 * @return Object
	 */
	private static String writeBoolean(String key, Boolean value, Config conf) {
		StringBuffer sb = new StringBuffer();
		String converter = conf.getConverter(key);
		sb.append("\t<ConfigItem name=\"");
		sb.append(key);
		sb.append("\" value=\"");
		sb.append(value.toString());
		if (converter == null || converter.equals("")) {
			sb.append("\">\n");
		}
		else {
			sb.append("\" converter=\"" + converter + "\">\n");
		}
		sb.append(writeComment(key, conf));
		sb.append("\t</ConfigItem>\n");
		return sb.toString();
	}

	/**
	 * @param key
	 * @param conf
	 * @return Object
	 */
	private static Object writeComment(String key, Config conf) {
		StringBuffer sb = new StringBuffer();
		Set keyset = conf.getLanguages();
		Iterator iter = keyset.iterator();
		while (iter.hasNext()) {
			String lang = (String) iter.next();
			String comment = conf.getComment(lang, key);
			if (!(comment == null || comment.equals(""))) {
				sb.append("\t\t<Comment lang=\"" + lang + "\"><![CDATA[" + comment
						+ "]]></Comment>\n");
			}
		}
		return sb.toString();
	}
}
