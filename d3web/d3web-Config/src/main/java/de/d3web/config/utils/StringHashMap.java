/*
 * Created on 28.11.2003
 */
package de.d3web.config.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author bannert
 */
public class StringHashMap extends HashMap<String, String> {
    
	private List<String> keys = new LinkedList<String>();
	
	public String put(String key, Object value) {
		if (value instanceof String)
			return super.put(key, (String) value);
		else return null;
	}

	@SuppressWarnings("unchecked")
    public void putAll(Map t) {
		if (t instanceof StringHashMap)
			super.putAll(t);
	}
	
	public boolean isKey(String key){
		return this.keys.contains(key);
	}
		
	public List getKeys(){
		return this.keys;
	}
	
	public void setKeys(List<String> keys){
		this.keys = keys;
	}
}
