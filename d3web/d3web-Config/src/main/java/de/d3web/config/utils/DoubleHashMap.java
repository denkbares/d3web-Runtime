/*
 * Created on 20.01.2004
 */
package de.d3web.config.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author bannert
 */
public class DoubleHashMap extends HashMap<String, Double> {
    
    private List<String> keys = new LinkedList<String>();
	
	public Double put(String key, Object value) {
		if (value instanceof Double)
			return super.put(key, (Double) value);
		else
			return null;
	}

	@SuppressWarnings("unchecked")
    public void putAll(Map t) {
		if (t instanceof DoubleHashMap)
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
