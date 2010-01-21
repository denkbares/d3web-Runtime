/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.kernel.supportknowledge;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * only DC objects are keys for DCMarkup
 * 
 * @see de.d3web.kernel.supportknowledge.DCMarkup
 * @author hoernlein
 */
public class DCElement implements java.io.Serializable {

	private static final long serialVersionUID = 3422299195047203671L;

	private static List<DCElement> dcElements = new ArrayList<DCElement>();

	private String label;

	protected DCElement(String label) {
		super();
		this.label = label;
		dcElements.add(this);
	}

	public String getLabel() {
		return label;
	}

	public String toString() {
		return label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return label.hashCode();
	}

	/**
	 * Searches DCElement in the list of all created DCElements
	 * If none is found, one will be created.
	 * @param label of the DCElement
	 * @return DCElement with the given label
	 */
	public static DCElement getDCElementFor(String label) {
		Iterator<DCElement> iter = getIterator();
		while (iter.hasNext()) {
			DCElement dce = iter.next();
			if (dce.getLabel().equalsIgnoreCase(label))
				return dce;
		}
		return new DCElement(label);
	}

	/**
	 * use this method to get the Date encoded to the String-value of
	 * DCElement.DATE usage
	 * DCElement.string2date(DCMarkup.getContent(DCElement.DATA))
	 * 
	 * a Date is encoded as "YYYY-M-D h:m"
	 * 
	 * @param dateString
	 *            String
	 * @return Date
	 */
	public static Date string2date(String dateString) {
		try {
			StringTokenizer st = new StringTokenizer(dateString, " -:", false);
			int y = Integer.parseInt(st.nextToken());
			int m = Integer.parseInt(st.nextToken());
			int d = Integer.parseInt(st.nextToken());
			int h = Integer.parseInt(st.nextToken());
			int mi = Integer.parseInt(st.nextToken());
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, y);
			c.set(Calendar.MONTH, m - 1);
			c.set(Calendar.DAY_OF_MONTH, d);
			c.set(Calendar.HOUR_OF_DAY, h);
			c.set(Calendar.MINUTE, mi);
			return c.getTime();
		} catch (NumberFormatException e) {
//			Logger.getLogger("de.d3web").log(Level.WARNING,
//					e.getLocalizedMessage(), e);
		    Logger.getLogger("de.d3web").log(Level.WARNING, "can't parse date '" + dateString + "'");
			return new Date();
		} catch (NoSuchElementException e2) {
//			Logger.getLogger("de.d3web").log(Level.WARNING,
//					e2.getLocalizedMessage(), e2);
            Logger.getLogger("de.d3web").log(Level.WARNING, "can't parse date '" + dateString + "'");
			return new Date();
		}
	}

	/**
	 * use this method to set the String-value of DCElement.DATA usage
	 * DCMarkup.setContent(DCElement.DATA, DCElement.date2string(yourDate))
	 * 
	 * a Date is encoded as "YYYY-M-D h:m"
	 * 
	 * @param date
	 *            Date
	 * @return String
	 */
	public static String date2string(Date date) {
        Calendar c = Calendar.getInstance();
		c.setTime(date);
        // YYYY-MM-DD hh:mm
		return "" + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1)
				+ "-" + c.get(Calendar.DAY_OF_MONTH) + " "
				+ c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
	}

	/**
	 * 
	 * @return Iterator over all possible DC objects
	 */
	public static Iterator<DCElement> getIterator() {
		return dcElements.iterator();
	}

	/**
	 * This method is called immediately after an object of this class is
	 * deserialized. To avoid that several instances of a unique object are
	 * created, this method returns the current unique instance that is equal to
	 * the object that was deserialized.
	 * 
	 * @author georg
	 */
	private Object readResolve() {
		return getDCElementFor(label);
	}

	/**
	 * Element Name: Title Label: Title Definition: A name given to the
	 * resource. Comment: Typically, Title will be a name by which the resource
	 * is formally known.
	 */
	public static final DCElement TITLE = new DCElement("DC.title");

	/**
	 * Element Name: Creator Label: Creator Definition: An entity primarily
	 * responsible for making the content of the resource. Comment: Examples of
	 * Creator include a person, an organization, or a service. Typically, the
	 * name of a Creator should be used to indicate the entity.
	 */
	public static final DCElement CREATOR = new DCElement("DC.creator");

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
	public static final DCElement SUBJECT = new DCElement("DC.subject");

	/**
	 * Element Name: Description Label: Description Definition: An account of
	 * the content of the resource. Comment: Examples of Description include,
	 * but is not limited to: an abstract, table of contents, reference to a
	 * graphical representation of content or a free-text account of the
	 * content.
	 */
	public static final DCElement DESCRIPTION = new DCElement("DC.description");

	/**
	 * Element Name: Publisher Label: Publisher Definition: An entity
	 * responsible for making the resource available Comment: Examples of
	 * Publisher include a person, an organization, or a service. Typically, the
	 * name of a Publisher should be used to indicate the entity.
	 */
	public static final DCElement PUBLISHER = new DCElement("DC.publisher");

	/**
	 * Element Name: Contributor Label: Contributor Definition: An entity
	 * responsible for making contributions to the content of the resource.
	 * Comment: Examples of Contributor include a person, an organization, or a
	 * service. Typically, the name of a Contributor should be used to indicate
	 * the entity.
	 */
	public static final DCElement CONTRIBUTOR = new DCElement("DC.contributor");

	/**
	 * Element Name: Date Label: Date Definition: A date of an event in the
	 * lifecycle of the resource. Comment: Typically, Date will be associated
	 * with the creation or availability of the resource. Recommended best
	 * practice for encoding the date value is defined in a profile of ISO 8601
	 * [W3CDTF] and includes (among others) dates of the form YYYY-MM-DD.
	 * 
	 * @see DCElement.date2string(Date) & DCElement.string2date(String)
	 */
	public static final DCElement DATE = new DCElement("DC.date");

	/**
	 * Element Name: Type Label: Resource Type Definition: The nature or genre
	 * of the content of the resource. Comment: Type includes terms describing
	 * general categories, functions, genres, or aggregation levels for content.
	 * Recommended best practice is to select a value from a controlled
	 * vocabulary (for example, the DCMI Type Vocabulary [DCT1]). To describe
	 * the physical or digital manifestation of the resource, use the FORMAT
	 * element.
	 */
	public static final DCElement TYPE = new DCElement("DC.resource_type");

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
	public static final DCElement FORMAT = new DCElement("DC.format");

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
	public static final DCElement IDENTIFIER = new DCElement(
			"DC.resource_identifier");

	/**
	 * Element Name: Source Label: Source Definition: A Reference to a resource
	 * from which the present resource is derived. Comment: The present resource
	 * may be derived from the Source resource in whole or in part. Recommended
	 * best practice is to identify the referenced resource by means of a string
	 * or number conforming to a formal identification system.
	 */
	public static final DCElement SOURCE = new DCElement("DC.source");

	/**
	 * Element Name: Language Label: Language Definition: A language of the
	 * intellectual content of the resource. Comment: Recommended best practice
	 * is to use RFC 3066 [RFC3066] which, in conjunction with ISO639 [ISO639]),
	 * defines two- and three primary language tags with optional subtags.
	 * Examples include "en" or "eng" for English, "akk" for Akkadian", and
	 * "en-GB" for English used in the United Kingdom.
	 */
	public static final DCElement LANGUAGE = new DCElement("DC.language");

	/**
	 * Element Name: Relation Label: Relation Definition: A reference to a
	 * related resource. Comment: Recommended best practice is to identify the
	 * referenced resource by means of a string or number conforming to a formal
	 * identification system.
	 */
	public static final DCElement RELATION = new DCElement("DC.relation");

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
	public static final DCElement COVERAGE = new DCElement("DC.coverage");

	/**
	 * Element Name: Rights Label: Rights Management Definition: Information
	 * about rights held in and over the resource. Comment: Typically, Rights
	 * will contain a rights management statement for the resource, or reference
	 * a service providing such information. Rights information often
	 * encompasses Intellectual Property Rights (IPR), Copyright, and various
	 * Property Rights. If the Rights element is absent, no assumptions may be
	 * made about any rights held in or over the resource.
	 */
	public static final DCElement RIGHTS = new DCElement("DC.rights_management");
}