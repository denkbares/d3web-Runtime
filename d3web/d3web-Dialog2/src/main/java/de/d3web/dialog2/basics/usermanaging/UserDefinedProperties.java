package de.d3web.dialog2.basics.usermanaging;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author bates
 * 
 *         To change this generated comment edit the template variable
 *         "typecomment": Window>Preferences>Java>Templates. To enable and
 *         disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public class UserDefinedProperties {

    private Hashtable<String, String> properties = null;

    public UserDefinedProperties() {
	properties = new Hashtable<String, String>();
    }

    public void addProperty(String name, String value) {
	properties.put(name, value);
    }

    public boolean getBooleanPropertyValue(String name) {
	if (name != null) {
	    String val = properties.get(name);
	    if ((val != null) && val.equalsIgnoreCase("TRUE")) {
		return true;
	    }
	    return false;
	}
	return false;
    }

    public String getPropertyValue(String name) {
	if (name != null) {
	    return properties.get(name);
	}
	return null;
    }

    public String getXMLString() {
	StringBuffer sb = new StringBuffer();
	sb.append("<UserDefinedProperties>");
	Enumeration<String> enu = properties.keys();
	while (enu.hasMoreElements()) {
	    String name = enu.nextElement();
	    String value = properties.get(name);
	    sb.append("<Property name='" + name + "' value='" + value + "' />");
	}

	sb.append("</UserDefinedProperties>");
	return sb.toString();
    }

    public void removeProperty(String name) {
	if (name != null) {
	    properties.remove(name);
	}
    }

    @Override
    public String toString() {
	return properties.toString();
    }

}
