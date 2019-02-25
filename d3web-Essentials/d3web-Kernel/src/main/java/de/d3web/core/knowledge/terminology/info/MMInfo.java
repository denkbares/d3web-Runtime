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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.denkbares.strings.Strings;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.Resource;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.Question;

/**
 * @author hoernlein, Markus Friedrich (denkbares GmbH)
 * @created 08.10.2010
 */
public class MMInfo {

	/**
	 * Element Name: Description Label: Description Definition: An account of the content of the resource. Comment:
	 * Examples of Description include, but is not limited to: an abstract, table of contents, reference to a graphical
	 * representation of content or a free-text account of the content.
	 */
	public static final Property<String> DESCRIPTION =
			Property.getProperty("description", String.class);

	/**
	 * The human readable name (prompt) for knowledge base object. This attribute may be used by any object that will we
	 * represented to the user, especially questions, choices and solutions, but also for question containers.
	 */
	public final static Property<String> PROMPT = Property.getProperty("prompt", String.class);

	/**
	 * A String representing an external description. A link may contain an URL or a relative path to a resource of the
	 * {@link KnowledgeBase}. By convention, if there are multiple links required, they will be added separated by ';'.
	 *
	 * @see KnowledgeBase#getResource(String)
	 */
	public final static Property<String> LINK = Property.getProperty("link", String.class);

	/**
	 * <b>Applies to</b>:<br> Question, KnowledgeBase
	 *
	 * <b>Documentation</b>:<br> Specifies how the answer 'unknown' should be displayed to the user. If this property
	 * is specified for the knowledge base it represents the default value for all questions. See also 'unknownVisible'
	 * to specify if 'unknown' is available to the user at all.
	 */
	public static final Property<String> UNKNOWN_VERBALISATION = Property.getProperty(
			"unknownPrompt", String.class);

	/**
	 * used for: Question the unit of numerical questions
	 */
	public static final Property<String> UNIT = Property.getProperty("unit", String.class);

	/**
	 * Checks if the link points to an {@link Resource} of the {@link KnowledgeBase}
	 *
	 * @param link the link to be checked
	 * @return true, if the link points to a {@link Resource}, false otherwise
	 * @created 05.11.2010
	 */
	public static boolean isResourceLink(String link) {
		return link != null && !link.contains(":");
	}

	/**
	 * Returns the multimedia-links of the specified object. If no link property is found for the language or a parent
	 * (more common) locale, an empty array is returned. If the found link property contains multiple links (separated
	 * by ";"), they will be split correctly and returned as an array. Otherwise the returned array contains the single
	 * link.
	 *
	 * @param object   the object to get the links for
	 * @param language the language(s) to get the links for, where the first one is the most preferred language
	 * @return the links or an empty array
	 * @created 03.07.2012
	 */
	@NotNull
	public static String[] getLinks(NamedObject object, Locale... language) {
		return splitLinks(object.getInfoStore().getValue(MMInfo.LINK, language));
	}

	/**
	 * Returns all the multimedia-links of the specified object, summarized for all languages. If no link property is
	 * found at all, an empty array is returned. If the found link properties contains multiple links (separated by
	 * ";"), they will be split correctly and returned in the resulting set. The resulting set preserves the order of
	 * the links for a particular language, but has no order over the languages.
	 *
	 * @param object the object to get the links for
	 * @return the links or an empty set
	 * @created 03.07.2012
	 */
	@NotNull
	public static Set<String> getAllLinks(NamedObject object) {
		Set<String> result = new LinkedHashSet<>();
		for (String property : object.getInfoStore().entries(MMInfo.LINK).values()) {
			Collections.addAll(result, splitLinks(property));
		}
		return result;
	}

	private static String[] splitLinks(String property) {
		if (Strings.isBlank(property)) return new String[0];
		property = Strings.trim(property);
		if (!property.contains(";")) return new String[] { property };
		return property.split("\\s*;\\s*");
	}

	/**
	 * Returns the prompt of the object for the specified name. If no prompt is found for the language or a parent (more
	 * common) locale, the object name is returned. Thus this method will always return the name to be displayed for the
	 * specified object.
	 *
	 * @param object   the object to get the prompt for
	 * @param language the language(s) to get the prompt for, where the first one is the most preferred language
	 * @return the prompt or name if no prompt exists
	 * @created 03.07.2012
	 */
	public static String getPrompt(NamedObject object, Locale... language) {
		String prompt = object.getInfoStore().getValue(MMInfo.PROMPT, language);
		if (prompt == null) {
			prompt = object.getName();
		}
		return prompt;
	}

	/**
	 * Sets the prompt for the specified object and the specified language.
	 *
	 * @param object   the object to set the prompt for
	 * @param language the language(s) to set the prompt for
	 * @param prompt   the prompt to be set
	 */
	public static void setPrompt(NamedObject object, Locale language, String prompt) {
		object.getInfoStore().addValue(MMInfo.PROMPT, language, prompt);
	}

	/**
	 * Sets the default-language prompt for the specified object.
	 *
	 * @param object the object to set the prompt for
	 * @param prompt the prompt to be set
	 */
	public static void setPrompt(NamedObject object, String prompt) {
		setPrompt(object, Locale.ROOT, prompt);
	}

	/**
	 * Return the prompt for the "unknown" alternative of a specific question. The prompt is defined by the property
	 * "unknownPrompt" for the question. If there is no such property, the "unknownPrompt" of the questions knowledge
	 * base object will be used as the default value. If there is no such knowledge base specific default value,
	 * "unknown" or "unbekannt" or something similar is used, based on the specified locale.
	 *
	 * @param question the question to get the unknown prompt for
	 * @param language the language(s) to get the unknown prompt for, where the first one is the most preferred
	 *                 language
	 * @return the questions unknown prompt
	 * @created 20.08.2012
	 */
	@NotNull
	public static String getUnknownPrompt(Question question, Locale... language) {
		String prompt = question.getInfoStore().getValue(UNKNOWN_VERBALISATION, language);
		if (prompt == null) {
			prompt = question.getKnowledgeBase().getInfoStore()
					.getValue(UNKNOWN_VERBALISATION, language);
		}
		if (prompt == null) {
			prompt = "unknown";
		}
		return prompt;
	}

	/**
	 * Returns the description of the object for the specified name. If no description is found for the locale or a
	 * parent (more common) locale, null is returned.
	 *
	 * @param object   the object to get the description for
	 * @param language the language(s) to get the description for, where the first one is the most preferred language
	 * @return the description, or null if no description exists
	 * @created 03.07.2012
	 */
	public static String getDescription(NamedObject object, Locale... language) {
		return object.getInfoStore().getValue(MMInfo.DESCRIPTION, language);
	}
}
