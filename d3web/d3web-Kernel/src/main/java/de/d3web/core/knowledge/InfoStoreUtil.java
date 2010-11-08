/*
 * Copyright (C) 2010 denkbares GmbH
 * 
 * This is free software for non commercial use
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 */
package de.d3web.core.knowledge;

import java.util.Locale;

import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.utilities.Triple;

/**
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 11.10.2010
 */
public class InfoStoreUtil {

	public static void copyEntries(InfoStore source, InfoStore target) {
		for (Triple<Property<?>, Locale, ?> entry : source.entries()) {
			target.addValue(entry.getA(), entry.getB(), entry.getC());
		}
	}
}
