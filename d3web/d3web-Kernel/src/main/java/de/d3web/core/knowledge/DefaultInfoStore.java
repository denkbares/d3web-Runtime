package de.d3web.core.knowledge;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import de.d3web.core.utilities.Pair;
import de.d3web.core.utilities.Triple;

public class DefaultInfoStore implements InfoStore {

	private final Map<Pair<String, Locale>, Object> entries = 
		new HashMap<Pair<String,Locale>, Object>();
	
	@Override
	public Collection<Triple<String, Locale, Object>> entries() {
		Collection<Triple<String, Locale, Object>> result = 
			new LinkedList<Triple<String,Locale,Object>>();
		for (Entry<Pair<String,Locale>, Object> entry : this.entries.entrySet()) {
			result.add(new Triple<String, Locale, Object>(
					entry.getKey().getA(), 
					entry.getKey().getB(),
					entry.getValue()));
		}
		return result;
	}

	@Override
	public Object getValue(String key) {
		return getEntry(key, DEFAULT_LANGUAGE);
	}

	@Override
	public Object getValue(String key, Locale language) {
		Object value = getEntry(key, language);
		if (value != null) return value;
		return getEntry(key, DEFAULT_LANGUAGE);
	}
	
	private Object getEntry(String key, Locale language) {
		return this.entries.get(new Pair<String, Locale>(key, language));
	}

	@Override
	public void remove(String key) {
		remove(key, DEFAULT_LANGUAGE);
	}

	@Override
	public void remove(String key, Locale language) {
		this.entries.remove(new Pair<String, Locale>(key, language));
	}

}
