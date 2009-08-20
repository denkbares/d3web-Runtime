package de.d3web.kernel.supportknowledge;

import java.util.ArrayList;
import java.util.Collection;

public enum DCElementTitle {

	SHORT_DESCRIPTION("shortDescription"),
	LONG_DESCRIPTION("longDescription"),
	EXAMPLES("examples"),
	REALISATION("realisation"),
	EXPLANATION("explanation");
	
	private final String tag;
	
	DCElementTitle(String tag) {
		this.tag = tag;
	}
	
	public String getTitle() {
		return tag;
	}
	
	public static String[] getAllTitles() {
		Collection<String> result = new ArrayList<String>();
		for (DCElementTitle element : values()) {
			result.add(element.getTitle());
		}
		return result.toArray(new String[0]);
	}
	
	public static DCElementTitle getDCElementTitle(String title) {
		for (DCElementTitle element : values()) {
			if(element.getTitle().equals(title)) {
				return element;
			}
		}
		return null;
	}
	
}
