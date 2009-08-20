package de.d3web.kernel.supportknowledge;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties is for adding additional information to an object
 * @see de.d3web.kernel.supportknowledge.PropertiesContainer
 * @author hoernlein
 */
public class Properties implements java.io.Serializable {
	
	public String toString() {
	return properties.toString();}
	
	private Map properties = new HashMap();
	
	public Object getProperty(Property pd) {
		return properties.get(pd);
	}
	
	public void setProperty(Property pd, Object o) {
		properties.put(pd, o);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Properties)) return false;
		if (!properties.keySet().equals(((Properties) obj).properties.keySet())) return false;
		return properties.entrySet().equals(((Properties) obj).properties.entrySet());
	}
	
	public Set getKeys() {
		return properties.keySet();
	}

}
