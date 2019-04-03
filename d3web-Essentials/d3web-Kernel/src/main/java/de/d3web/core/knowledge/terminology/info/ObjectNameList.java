/*
 * Copyright (C) 2017 denkbares GmbH. All rights reserved.
 */
package de.d3web.core.knowledge.terminology.info;

import java.util.LinkedList;
import java.util.List;

import com.denkbares.strings.Identifier;
import com.denkbares.strings.StringFragment;
import com.denkbares.strings.Strings;

/**
 * A list of object names that can be used as the type of a {@link de.d3web.core.knowledge.terminology.info.Property}.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 13.03.2013
 */
public class ObjectNameList extends LinkedList<String> {

	private static final long serialVersionUID = -2250136452100547825L;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (String object : this) {
			if (builder.length() > 0) builder.append(", ");
			builder.append(new Identifier(object).toExternalForm());
		}
		return builder.toString();
	}

	public static ObjectNameList valueOf(String value) {
		ObjectNameList list = new ObjectNameList();
		List<StringFragment> splitUnquoted = Strings.splitUnquoted(value, ",");
		for (StringFragment string : splitUnquoted) {
			list.add(Strings.unquote(string.getContent().trim()));
		}
		return list;
	}

}
