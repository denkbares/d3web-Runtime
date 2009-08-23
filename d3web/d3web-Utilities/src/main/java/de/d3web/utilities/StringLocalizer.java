package de.d3web.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class StringLocalizer {
	
	public static String[] getLocalizedStrings(ResourceBundle bundle, String prefix, String[] strings) {
		List<String> result = new ArrayList<String>();
		for (String string : strings) {
			result.add(getLocalizedString(bundle, prefix, string));
		}
		return result.toArray(new String[0]);
	}
	
	public static String getLocalizedString(ResourceBundle bundle, String prefix, String string) {
		try {
			return bundle.getString(prefix + string);
		} catch(Exception e) {
			return string;
		}
	}
	
}
