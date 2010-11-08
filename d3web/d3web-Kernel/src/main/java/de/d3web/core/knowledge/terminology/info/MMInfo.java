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

import java.util.Date;

/**
 * 
 * @author hoernlein, Markus Friedrich (denkbares GmbH)
 * @created 08.10.2010
 */
public class MMInfo {

	/**
	 * Element Name: Title Label: Title Definition: A name given to the
	 * resource. Comment: Typically, Title will be a name by which the resource
	 * is formally known.
	 */
	public static final Property<String> TITLE =
			Property.getProperty("DC.title", String.class);

	/**
	 * Element Name: Creator Label: Creator Definition: An entity primarily
	 * responsible for making the content of the resource. Comment: Examples of
	 * Creator include a person, an organization, or a service. Typically, the
	 * name of a Creator should be used to indicate the entity.
	 */
	public static final Property<String> CREATOR =
			Property.getProperty("DC.creator", String.class);

	/**
	 * Element Name: Subject Label: Subject and Keywords Definition: A topic of
	 * the content of the resource. Comment: Typically, Subject will be
	 * expressed as keywords, key phrases or classification codes that describe
	 * a topic of the resource. Recommended best practice is to select a value
	 * from a controlled vocabulary or formal classification scheme.
	 * 
	 * Attention: d3web special usage: in DCMarkups of MMInfoObjects SUBJECT is
	 * used to discern between the different types of MMInfoObjects
	 * 
	 * @see MMInfoSubject public final static constants
	 */
	public static final Property<String> SUBJECT =
			Property.getProperty("DC.subject", String.class);

	/**
	 * Element Name: Description Label: Description Definition: An account of
	 * the content of the resource. Comment: Examples of Description include,
	 * but is not limited to: an abstract, table of contents, reference to a
	 * graphical representation of content or a free-text account of the
	 * content.
	 */
	public static final Property<String> DESCRIPTION =
			Property.getProperty("DC.description", String.class);

	/**
	 * Element Name: Publisher Label: Publisher Definition: An entity
	 * responsible for making the resource available Comment: Examples of
	 * Publisher include a person, an organization, or a service. Typically, the
	 * name of a Publisher should be used to indicate the entity.
	 */
	public static final Property<String> PUBLISHER =
			Property.getProperty("DC.publisher", String.class);

	/**
	 * Element Name: Contributor Label: Contributor Definition: An entity
	 * responsible for making contributions to the content of the resource.
	 * Comment: Examples of Contributor include a person, an organization, or a
	 * service. Typically, the name of a Contributor should be used to indicate
	 * the entity.
	 */
	public static final Property<String> CONTRIBUTOR =
			Property.getProperty("DC.contributor", String.class);

	/**
	 * Element Name: Date Label: Date Definition: A date of an event in the
	 * lifecycle of the resource. Comment: Typically, Date will be associated
	 * with the creation or availability of the resource. Recommended best
	 * practice for encoding the date value is defined in a profile of ISO 8601
	 * [W3CDTF] and includes (among others) dates of the form YYYY-MM-DD.
	 * 
	 * @see DCElement.date2string(Date) & DCElement.string2date(String)
	 */
	public static final Property<Date> DATE =
			Property.getProperty("DC.date", Date.class);

	/**
	 * Element Name: Type Label: Resource Type Definition: The nature or genre
	 * of the content of the resource. Comment: Type includes terms describing
	 * general categories, functions, genres, or aggregation levels for content.
	 * Recommended best practice is to select a value from a controlled
	 * vocabulary (for example, the DCMI Type Vocabulary [DCT1]). To describe
	 * the physical or digital manifestation of the resource, use the FORMAT
	 * element.
	 */
	public static final Property<String> TYPE =
			Property.getProperty("DC.resource_type", String.class);

	/**
	 * Element Name: Format Label: Format Definition: The physical or digital
	 * manifestation of the resource. Comment: Typically, Format may include the
	 * media-type or dimensions of the resource. Format may be used to identify
	 * the software, hardware, or other equipment needed to display or operate
	 * the resource. Examples of dimensions include size and duration.
	 * Recommended best practice is to select a value from a controlled
	 * vocabulary (for example, the list of Internet Media Types [MIME] defining
	 * computer media formats).
	 */
	public static final Property<String> FORMAT =
			Property.getProperty("DC.format", String.class);

	/**
	 * Element Name: Identifier Label: Resource Identifier Definition: An
	 * unambiguous reference to the resource within a given context. Comment:
	 * Recommended best practice is to identify the resource by means of a
	 * string or number conforming to a formal identification system. Formal
	 * identification systems include but are not limited to the Uniform
	 * Resource Identifier (URI) (including the Uniform Resource Locator (URL)),
	 * the Digital Object Identifier (DOI) and the International Standard Book
	 * Number (ISBN).
	 */
	public static final Property<String> IDENTIFIER = Property.getProperty(
			"DC.resource_identifier", String.class);

	/**
	 * Element Name: Source Label: Source Definition: A Reference to a resource
	 * from which the present resource is derived. Comment: The present resource
	 * may be derived from the Source resource in whole or in part. Recommended
	 * best practice is to identify the referenced resource by means of a string
	 * or number conforming to a formal identification system.
	 */
	public static final Property<String> SOURCE =
			Property.getProperty("DC.source", String.class);

	/**
	 * Element Name: Language Label: Language Definition: A language of the
	 * intellectual content of the resource. Comment: Recommended best practice
	 * is to use RFC 3066 [RFC3066] which, in conjunction with ISO639 [ISO639]),
	 * defines two- and three primary language tags with optional subtags.
	 * Examples include "en" or "eng" for English, "akk" for Akkadian", and
	 * "en-GB" for English used in the United Kingdom.
	 */
	public static final Property<String> LANGUAGE =
			Property.getProperty("DC.language", String.class);

	/**
	 * Element Name: Relation Label: Relation Definition: A reference to a
	 * related resource. Comment: Recommended best practice is to identify the
	 * referenced resource by means of a string or number conforming to a formal
	 * identification system.
	 */
	public static final Property<String> RELATION =
			Property.getProperty("DC.relation", String.class);

	/**
	 * Element Name: Coverage Label: Coverage Definition: The extent or scope of
	 * the content of the resource. Comment: Typically, Coverage will include
	 * spatial location (a place name or geographic coordinates), temporal
	 * period (a period label, date, or date range) or jurisdiction (such as a
	 * named administrative entity). Recommended best practice is to select a
	 * value from a controlled vocabulary (for example, the Thesaurus of
	 * Geographic Names [TGN]) and to use, where appropriate, named places or
	 * time periods in preference to numeric identifiers such as sets of
	 * coordinates or date ranges.
	 */
	public static final Property<String> COVERAGE =
			Property.getProperty("DC.coverage", String.class);

	/**
	 * Element Name: Rights Label: Rights Management Definition: Information
	 * about rights held in and over the resource. Comment: Typically, Rights
	 * will contain a rights management statement for the resource, or reference
	 * a service providing such information. Rights information often
	 * encompasses Intellectual Property Rights (IPR), Copyright, and various
	 * Property Rights. If the Rights element is absent, no assumptions may be
	 * made about any rights held in or over the resource.
	 */
	public static final Property<String> RIGHTS =
			Property.getProperty("DC.rights_management", String.class);

}
