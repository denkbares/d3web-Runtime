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

package de.d3web.kernel.supportknowledge;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import de.d3web.kernel.supportknowledge.propertyCloner.BooleanPropertyCloner;
import de.d3web.kernel.supportknowledge.propertyCloner.DCMarkupPropertyCloner;
import de.d3web.kernel.supportknowledge.propertyCloner.DoublePropertyCloner;
import de.d3web.kernel.supportknowledge.propertyCloner.IntegerPropertyCloner;
import de.d3web.kernel.supportknowledge.propertyCloner.LinkedListPropertyCloner;
import de.d3web.kernel.supportknowledge.propertyCloner.PropertyCloner;
import de.d3web.kernel.supportknowledge.propertyCloner.StringPropertyCloner;
import de.d3web.kernel.supportknowledge.propertyCloner.URLPropertyCloner;

/**
 * This class provides the functionality of making a deep copy of a
 * Properties-object.
 * 
 * @author gbuscher
 */
public class PropertiesCloner {

	private static PropertiesCloner instance = null;

	private Map propertyClonerMap = new HashMap();

	public static PropertiesCloner getInstance() {
		if (instance == null) {
			instance = new PropertiesCloner();
		}
		return instance;
	}

	protected PropertiesCloner() {
		propertyClonerMap.put(Boolean.class, new BooleanPropertyCloner());
		propertyClonerMap.put(DCMarkup.class, new DCMarkupPropertyCloner());
		propertyClonerMap.put(Double.class, new DoublePropertyCloner());
		propertyClonerMap.put(Integer.class, new IntegerPropertyCloner());
		propertyClonerMap.put(String.class, new StringPropertyCloner());
		propertyClonerMap.put(URL.class, new URLPropertyCloner());
		propertyClonerMap.put(LinkedList.class, new LinkedListPropertyCloner());
	}

	public void addProperyCloner(Class forClass, PropertyCloner p) {
		propertyClonerMap.put(forClass, p);
	}

	/**
	 * Returns a deep copy of the given Properties-object: all property-values
	 * are cloned by the appropriate ProperyCloners.
	 * 
	 * @param properties
	 *            Properties to clone
	 * @return a deep copy of the given properties
	 */
	public Properties cloneProperties(Properties properties) {
		return cloneProperties(properties, true);
	}
	
	/**
	 * Returns a deep copy of the given Properties-object: all property-values
	 * are cloned by the appropriate ProperyCloners.
	 * 
	 * @param properties
	 *            Properties to clone
	 * @param verbose
	 *            Only warn about non-cloneable properties, when enabled!
	 * @return a deep copy of the given properties
	 */
	public Properties cloneProperties(Properties properties, boolean verbose) {
		Properties clonedProperties = new Properties();
		Iterator iter = properties.getKeys().iterator();
		while (iter.hasNext()) {
			Property key = (Property) iter.next();
			Object value = properties.getProperty(key);

			if (value != null) {
				PropertyCloner cloner = (PropertyCloner) propertyClonerMap
						.get(value.getClass());
				if (cloner != null) {
					clonedProperties.setProperty(key, cloner
							.cloneProperty(value));
				} else {
					if (verbose) {
						Logger.getLogger(this.getClass().getName()).warning(
								"Property " + key.getName()
										+ " cannot be cloned!");
					}
				}
			}
		}
		return clonedProperties;
	}

}