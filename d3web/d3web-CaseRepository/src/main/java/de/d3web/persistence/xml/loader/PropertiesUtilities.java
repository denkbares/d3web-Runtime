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

/*
 * Created on 02.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.d3web.persistence.xml.loader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.kpers.utilities.XMLTools;
import de.d3web.kernel.supportknowledge.Properties;
import de.d3web.kernel.supportknowledge.Property;
import de.d3web.kernel.supportknowledge.propertyCloner.PropertyCloner;
import de.d3web.xml.domtools.DOMAccess;
/**
 * PropertiesUtilties can be used to enhance the basic en/decoding capabilities to en/decode additional properties
 * 
 * add your own PropertyCodecs with addCodec
 * 
 * @see PropertyCodec
 * 
 * @author hoernlein
 */
public class PropertiesUtilities {
	
	public final static class CDataString {
		private String string;
		private CDataString() {}
		public CDataString(String string) {
			this.string = string;
		}
	
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return string;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof CDataString))
				return false;
			if (this == obj)
				return true;
			return toString().equals(obj.toString());
		}

	}
	
	/**
	 * PropertyCloner for de.d3web.persistence.xml.loader.PropertiesUtilities$CDataString.
	 * @see de.d3web.kernel.supportknowledge.PropertyCloner
	 */
	public static class CDataStringPropertyCloner extends PropertyCloner {
		public Object cloneProperty(Object o) {
			if (o instanceof CDataString) {
				return new CDataString(new String(((CDataString) o).toString()));
			}
			return null;
		}
	}
	

	/**
	 * package visibility is necessary since this method is use by ListCodec
	 */
	PropertyCodec findCodecFor(Object o) {
		return (PropertyCodec) codecs.get(o.getClass());
	}

	/**
	 * package visibility is necessary since this method is use by ListCodec
	 */
	PropertyCodec findCodecFor(String s) {
		try {
			return (PropertyCodec) codecs.get(Class.forName(s));
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * PropertyCodec for en/decode Objects stored in Propertys
	 * <Property class='...' ...>
	 * 	enoded Object
	 * </Property>
	 * encode gets the object and generates the String to be put between opening and closing tag
	 * decode gets the Property-Node and should return the object
	 * 
	 * Mapping of Objects<->PropertyCodecs by PropertyCodec.getClass and Object.getClass
	 * 
	 * IMPORTANT:
	 * one codec is only valid for the one specified class - it won't be used to en/decode any superclass or subclass 
	 *  
	 * @author hoernlein
	 */
	public static abstract class PropertyCodec {
		private PropertyCodec() {}

		private Class clazz;
		public PropertyCodec(Class clazz) {
			this.clazz = clazz;
		}
		public Class getClazz() {
			return clazz;
		}

		public abstract String encode(Object o);
		public abstract Object decode(Node n);
	}

	private Map codecs = new HashMap();

	public void addCodec(PropertyCodec pc) {
		codecs.put(pc.getClazz(), pc);
	}

	public PropertiesUtilities() {
		
		addCodec(new PropertyCodec(CDataString.class) {
			public String encode(Object o) {
				return "<![CDATA[" + XMLTools.prepareForCDATA(((CDataString) o).toString()) + "]]>";
			}
			public Object decode(Node n) {
				NodeList ns = n.getChildNodes();
				for (int i = 0; i < ns.getLength(); i++) {
					if (ns.item(i).getNodeType() == Node.CDATA_SECTION_NODE) {
						return new CDataString(XMLTools.prepareFromCDATA(ns.item(i).getNodeValue()));
					}
				}
				Logger.getLogger(this.getClass().getName()).warning("no CDATA_SECTION_NODE found.");
				return null;
			}
		});

		addCodec(new PropertyCodec(String.class) {
			public String encode(Object o) {
				return "<![CDATA[" + XMLTools.prepareForCDATA(o.toString()) + "]]>";
			}
			public Object decode(Node n) {
				NodeList ns = n.getChildNodes();
				for (int i = 0; i < ns.getLength(); i++) {
					if (ns.item(i).getNodeType() == Node.CDATA_SECTION_NODE) {
						return XMLTools.prepareFromCDATA(ns.item(i).getNodeValue());
					}
				}
				Logger.getLogger(this.getClass().getName()).warning("no CDATA_SECTION_NODE found.");
				return null;
			}
		});

		addCodec(new PropertyCodec(Integer.class) {
			public String encode(Object o) {
				return o.toString();
			}
			public Object decode(Node n) {
				return Integer.valueOf(DOMAccess.getText(n));
			}
		});

		addCodec(new PropertyCodec(Double.class) {
			public String encode(Object o) {
				return o.toString();
			}
			public Object decode(Node n) {
				return Double.valueOf(DOMAccess.getText(n));
			}
		});

		addCodec(new PropertyCodec(Boolean.class) {
			public String encode(Object o) {
				return o.toString();
			}
			public Object decode(Node n) {
				return Boolean.valueOf(DOMAccess.getText(n));
			}
		});

		addCodec(new PropertyCodec(URL.class) {
			public String encode(Object o) {
				return "<![CDATA[" + XMLTools.prepareForCDATA(o.toString()) + "]]>";
			}
			public Object decode(Node n) {
				String urlS = null;
				NodeList ns = n.getChildNodes();
				for (int i = 0; i < ns.getLength(); i++)
					if (ns.item(i).getNodeType() == Node.CDATA_SECTION_NODE)
						urlS = XMLTools.prepareFromCDATA(ns.item(i).getNodeValue());

				if (urlS == null) {
					Logger.getLogger(this.getClass().getName()).warning("no CDATA_SECTION_NODE found.");
					return null;
				}

				URL url = null;
				try {
					url = new URL(urlS);
				} catch (MalformedURLException e) {
					Logger.getLogger(this.getClass().getName()).warning("can't handle '" + urlS + "'!");
				}
				return url;
			}
		});

		addCodec(NumericalIntervalsCodec.getInstance());
		
		// codec for java.util.LinkedList
		addCodec(new LinkedListCodec(this));
		
		// [MISC] not needed, since ListCodec exists
		addCodec(IntegerListCodec.getInstance());
		
	}

	/**
	 * 
	 * @param properties Properties
	 * @param propertysList list of Propertys
	 * @return String <Properties>{all Propertys in propertysList }</Properties>
	 */
	public String propertiesToString(Properties properties, Collection propertysList) {
		StringBuffer sb = new StringBuffer();
		if (!propertysList.isEmpty()) {

			Iterator iter = propertysList.iterator();
			while (iter.hasNext())
				sb.append(propertyToString(properties, (Property) iter.next()));

			if (sb.length() > 0) {
				sb.insert(0, "<Properties>\n");
				sb.append("</Properties>\n");
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param properties Properties
	 * @param property Property
	 * @return String <Property ...>...</Property>
	 */
	public String propertyToString(Properties properties, Property property) {
		StringBuffer sb = new StringBuffer();

		Object value = properties.getProperty(property);
		if (value != null) {
			PropertyCodec pc = findCodecFor(value);
			if (pc == null)
				Logger.getLogger(this.getClass().getName()).warning("no codec for values of type '" + value.getClass() + "'!");
			else
				sb.append(
					"<Property"
						+ " name=\""
						+ property.getName()
						+ "\""
						+ " class=\""
						+ pc.getClazz().getName()
						+ "\""
						+ ">"
						+ pc.encode(value)
						+ "</Property>\n");
		}

		return sb.toString();
	}

	public Properties getProperties(Node node) {

		Properties ret = new Properties();

		NodeList nl = node.getChildNodes();
		for (int k = 0; k < nl.getLength(); ++k) {

			Node cnode = nl.item(k);
			if (cnode.getNodeName().equals("Properties")) {

				NodeList proplist = cnode.getChildNodes();
				for (int i = 0; i < proplist.getLength(); ++i) {
					Node prop = proplist.item(i);
					if (prop.getNodeName().equals("Property")) {

						// [MISC]:aha:obsolete after supportknowledge refactoring is propagated
						String name = "";
						try {
							name = prop.getAttributes().getNamedItem("name").getNodeValue();
						} catch (Exception ex) {
							name = prop.getAttributes().getNamedItem("descriptor").getNodeValue();
						}
						Property property = Property.getProperty(name);

						if (property == null)
							// [MISC]:aha:obsolete after supportknowledge refactoring is propagated
							if (name.equals("isTherapy"))
								property = Property.IS_THERAPY;
						// else if ...

						String classname = prop.getAttributes().getNamedItem("class").getNodeValue();
						PropertyCodec pc = findCodecFor(classname);

						// [MISC]:aha:obsolete after supportknowledge refactoring is propagated
						if (pc == null)
							pc = fuzzyFindCodecFor(classname);

						Object o = null;
						if (pc == null)
							Logger.getLogger(this.getClass().getName()).warning("no codec for '" + classname + "'!");
						else
							o = pc.decode(prop);

						ret.setProperty(property, o);

					}

				}

			}

		}

		return ret;
	}

	/**
	 * package visibility is necessary since this method is use by ListCodec
	 */
	PropertyCodec fuzzyFindCodecFor(String s) {
		
		Class clazz = null;
		double rating = 0;
		Iterator iter = codecs.keySet().iterator();
		while (iter.hasNext()) {
			Class key = (Class) iter.next();
			StringTokenizer st = new StringTokenizer(key.getName(), ".", false);
			String search = "";
			while (st.hasMoreTokens())
				search = st.nextToken();
				
			if (search.indexOf(s) != -1) {
				double crating = (double) s.length() / search.length();
				if (crating > rating) {
					clazz = key;
					rating = crating;
				}
			}
		}
		

		if (clazz != null) {
			Logger.getLogger(this.getClass().getName()).info("'" + s + "' -> using codec for '" + clazz.getName() + "'");
			return (PropertyCodec) codecs.get(clazz);
		} else
			return null;
	}

}
