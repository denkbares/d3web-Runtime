/*
 * Created on 21.08.2003
 */
package de.d3web.config;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.config.persistence.*;

/**
 * @author bannert
 */
public class Config implements Serializable {
	
    private static final long serialVersionUID = 1L;

    public static Config baseConfig;
	
	public final static String BOOLEAN = "Boolean";
	public final static String INTEGER = "Int";
	public final static String STRING = "String";
	public final static String DOUBLE = "Double";
	public final static String BOOLEANHASHMAP = "BooleanHashMap";
	public final static String STRINGHASHMAP = "StringHashMap";
	public final static String DOUBLEHASHMAP = "DoubleHashMap";
	public final static String STRINGLIST = "StringList";
		
	public final static String TYPE_BASE = "base";
	public final static String TYPE_SYSTEM = "system";
	public final static String TYPE_CONTEXT = "context";
	public final static String TYPE_KNOWLEDGEBASE = "knowledgebase";
	public final static String TYPE_CASEGROUP = "casegroup";
	public final static String TYPE_CASE = "case";
	public final static String TYPE_USER = "user";

	public final static List TYPE_ORDER = Arrays.asList(new String[] {
		Config.TYPE_BASE,
		Config.TYPE_SYSTEM,
		Config.TYPE_CONTEXT,
		Config.TYPE_KNOWLEDGEBASE,
		Config.TYPE_CASEGROUP,
		Config.TYPE_CASE,
		Config.TYPE_USER
	});
	
	private String type;
	private String name = "unnamed";
	private Map<String, Object> values;
	private Map<String, String> converters;
	private Map<String, Map<String, String>> comments;
	private Map<String, String> classes; 
	
	static{
		baseConfig = ConfigReader.createConfig(ConfigReader.class.getClassLoader().getResource("config_base.xml"));
	}
	
	private static class ConfigComparator implements Comparator<Config> {

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(T, T)
         */
        public int compare(Config o1, Config o2) {
            return TYPE_ORDER.indexOf(o1.getType()) - 
            TYPE_ORDER.indexOf(o2.getType());
        }
	}
	
	public Config(String type) {
		this.type = type;
		this.values = new HashMap<String, Object>(1);
		this.converters = new HashMap<String, String>(1);
		this.comments = new HashMap<String, Map<String,String>>(1);
		this.classes = new HashMap<String, String>(1);
	}
	
	public Config(String type, String name) {
	    this(type);
	    setName(name);
	}
	
	public String getName() {
	    return this.name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public void setClass(String key, String cclass){
		this.classes.put(key, cclass);
	}
	
	public String getClass(String key){
		if (this.classes.containsKey(key))
			return this.classes.get(key);
		return null;
	}
	
	public Object getValue(String key){
		if(this.values.containsKey(key))
			return this.values.get(key); 
		return null;
	}

	public void setValue(String key, Object value){
		this.values.put(key, value);
	}
	
	public void setValue(String key, Object value, String converter){
//	    if (!key.startsWith("dialog_"))
//	        System.err.println(key + " - " + value + " - " + converter);
		this.values.put(key, value);
		this.converters.put(key, converter);
	}
	
	public void setConverter(String key, String converter) {
		this.converters.put(key, converter);
	}
	

	public String getConverter(String key) {
		if (this.converters.containsKey(key))
			return this.converters.get(key); 
		return null;	
	}
	
	public void setComment(String key, String comment, String lang){
		if (this.comments.containsKey(lang)) {
			this.comments.get(lang).put(key, comment);
		} else {
			Map<String, String> map = new HashMap<String, String>(1);
			map.put(key, comment);
			this.comments.put(lang, map);
		}
	}
	
	public String getComment(String lang, String key){
		if (this.comments.containsKey(lang)){
			return (String) ((HashMap) this.comments.get(lang)).get(key);
		} else return null;
	}
	
	public Set<String> getKeySet(){
		return this.values.keySet();
	}	
	
	public static Config buildResultConfig(List<Config> configs){
		Collections.sort(configs, new ConfigComparator());
		Config result = new Config(configs.get(configs.size()-1).getType());
		
		String name = "derived from (";
		Iterator iter = configs.iterator();
		while (iter.hasNext()) {
		    name += ((Config) iter.next()).getName();
		    if (iter.hasNext())
		        name += ", ";
		}
		name += ")";
		result.setName(name);
		
		Set<String> keySet = getKeySet(configs);
		for (String key : keySet) {
			if (valueIsConfig(configs, key)) {
				result.setValue(key, getResultConfig(configs, key));
			} else {
				setValue(key, configs, result);
			}	
		}
		return result;
	}

	private static Config getResultConfig(List<Config> configs, String key) {
		List<Config> values = new LinkedList<Config>();
        for (Config conf : configs) {
			if (conf.getValue(key) != null)
				values.add((Config) conf.getValue(key));
		}
		if (values.size() < 2)
            return values.get(0);
		else
            return buildResultConfig(values);
	}

	private static boolean valueIsConfig(List<Config> configs, String key) {
        for (Config conf : configs)
			if (conf.getValue(key) instanceof Config)
                return true;
		return false;
	}

	private static void setValue(String key, List<Config> configs, Config result) {
        for (Config config : configs) {
			if (config.getValue(key) != null){
				if (result.getConverter(key) == null || result.getConverter(key).equals("")) {
					if (config.getConverter(key) == null || config.getConverter(key).equals("") ) {
						result.setValue(key, config.getValue(key));
					} else if (TYPE_ORDER.indexOf(config.getType()) <= TYPE_ORDER.indexOf(config.getConverter(key))) {
						result.setValue(key, config.getValue(key));
						result.setConverter(key, config.getConverter(key));
					}
				} else if ( TYPE_ORDER.indexOf(config.getType()) <= TYPE_ORDER.indexOf(result.getConverter(key))) {
					result.setValue(key, config.getValue(key));
					if (!(config.getConverter(key) == null || config.getConverter(key).equals("")))
					    result.setConverter(key, config.getConverter(key));
				}
			}
		}
	}

	/**
	 * @param configs
	 * @return Set
	 */
	private static Set<String> getKeySet(List<Config> configs) {
		Set<String> result = new HashSet<String>();
		Iterator iter = configs.iterator();
		while (iter.hasNext()) {
			Config config = (Config) iter.next();
			result.addAll(config.getKeySet());
		}
		return result;
	}

	/**
	 * @return String
	 */
	public String getType() {
		return this.type;
	}
	
	public Set getLanguages(){
		return this.comments.keySet();
	}

	/**
	 * @param converter
	 * @param type
	 * @return int
	 */
	public static int compare(String type1, String type2) {
		return TYPE_ORDER.indexOf(type1) -
				TYPE_ORDER.indexOf(type2);
	}
	
	/**
	 * @return Config
	 */
	public static Config getBaseConfig() {
		return baseConfig;
	}

	/**
	 * @param key
	 */
	public void deleteValue(String key) {
		this.values.remove(key);
	}

}
