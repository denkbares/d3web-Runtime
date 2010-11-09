/*
 * Copyright (C) 2010 denkbares GmbH
 * 
 * This is free software for non commercial use
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 */
package de.d3web.core.knowledge.terminology.info;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.Resource;

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
}
