/*
 * Copyright (C) 2017 denkbares GmbH. All rights reserved.
 */
package de.d3web.core.knowledge.terminology.info;

import java.util.Collections;
import java.util.LinkedList;

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
		return Strings.concatParsable(", ", this.toArray(new String[0]));
	}

	public static ObjectNameList valueOf(String value) {
		ObjectNameList strings = new ObjectNameList();
		Collections.addAll(strings, Strings.parseConcat(", ", value));
		return strings;
	}
}
