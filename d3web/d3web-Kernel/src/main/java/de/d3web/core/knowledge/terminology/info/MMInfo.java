/*
 * Copyright (C) 2011 denkbares GmbH
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.core.knowledge.terminology.info;

import java.util.Locale;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.Resource;
import de.d3web.core.knowledge.terminology.NamedObject;

/**
 * 
 * @author hoernlein, Markus Friedrich (denkbares GmbH)
 * @created 08.10.2010
 */
public class MMInfo {

	/**
	 * Element Name: Description Label: Description Definition: An account of
	 * the content of the resource. Comment: Examples of Description include,
	 * but is not limited to: an abstract, table of contents, reference to a
	 * graphical representation of content or a free-text account of the
	 * content.
	 */
	public static final Property<String> DESCRIPTION =
			Property.getProperty("description", String.class);

	/**
	 * doc: Nur bei Question: der Fragetext
	 */
	public final static Property<String> PROMPT = Property.getProperty("prompt", String.class);

	/**
	 * A String representing an external description. A link may contain an URL
	 * or a relative path to a resource of the {@link KnowledgeBase}.
	 * 
	 * @see KnowledgeBase#getResource(String)
	 */
	public final static Property<String> LINK = Property.getProperty("link", String.class);

	/**
	 * TODO: Remove when UnknownChoice is implemented
	 * 
	 * used for: Question doc: return of getValue of Unknown
	 * 
	 * @return String
	 */
	public static final Property<String> UNKNOWN_VERBALISATION = Property.getProperty(
			"unknown_verbalisation", String.class);

	/**
	 * used for: Question the unit of numerical questions
	 * 
	 * @return String
	 */
	public static final Property<String> UNIT = Property.getProperty("unit", String.class);

	/**
	 * Checks if the link points to an {@link Resource} of the
	 * {@link KnowledgeBase}
	 * 
	 * @created 05.11.2010
	 * @param link
	 * @return true, if the link points to a {@link Resource}, false otherwise
	 */
	public static boolean isResourceLink(String link) {
		if (link == null) return false;
		return !link.contains(":");
	}

	/**
	 * Returns the prompt of the object for the specified name. If no prompt is
	 * found for the locale or a parent (more common) locale, the object name is
	 * returned. Thus this method will always return the name to be displayed
	 * for the specified object.
	 * 
	 * @created 03.07.2012
	 * @param object the object to get the prompt for
	 * @param locale the language to get the prompt for
	 * @return the prompt or name if no prompt exists
	 */
	public static String getPrompt(NamedObject object, Locale locale) {
		String prompt = object.getInfoStore().getValue(MMInfo.PROMPT, locale);
		if (prompt == null) {
			prompt = object.getName();
		}
		return prompt;
	}
}
