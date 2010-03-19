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

package de.d3web.dialog2.basics.knowledge;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * Insert the type's description here. Creation date: (30.10.2001 16:48:22)
 * 
 * @author: Norman Brümmer
 */
public class CaseRepositoryDescriptor {

	public final static String LOCATIONTYPE_XML_CASEREPOSITORY = "xml";

	public final static String LOCATIONTYPE_XML_CASEFILEREPOSITORY = "xml_files";

	private String kbId = null;

	private String locationType = null;

	private String location = null;

	private List<String> userEmails = null;

	public static Logger logger = Logger
			.getLogger(CaseRepositoryDescriptor.class);

	/**
	 * CRDescriptor constructor comment.
	 */
	public CaseRepositoryDescriptor() {
		super();
	}

	/**
	 * 
	 * @return boolean
	 * @param o
	 *            java.lang.Object
	 */
	@Override
	public boolean equals(Object o) {
		if ((o != null) && (o instanceof CaseRepositoryDescriptor)) {
			CaseRepositoryDescriptor other = (CaseRepositoryDescriptor) o;
			try {
				return kbId.equals(other.getKbId())
						&& (location.equals(other.getLocation()));
			} catch (Exception x) {
				logger.warn(x);
				return false;
			}

		}
		return false;
	}

	/**
	 * @return java.lang.String
	 */
	public String getKbId() {
		return kbId;
	}

	/**
	 * @return java.lang.String
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @return java.lang.String
	 */
	public String getLocationType() {
		return locationType;
	}

	/**
	 * @return java.util.List
	 */
	public List<String> getUserEmails() {
		return userEmails;
	}

	/**
	 * @return int
	 */
	@Override
	public int hashCode() {
		return kbId.hashCode() * 1000 + location.hashCode();
	}

	/**
	 * @param newKbId
	 *            java.lang.String
	 */
	public void setKbId(String newKbId) {
		kbId = newKbId;
	}

	/**
	 * @param newLocation
	 *            java.lang.String
	 */
	public void setLocation(String newLocation) {
		location = newLocation;
	}

	/**
	 * @param newLocationType
	 *            java.lang.String
	 */
	public void setLocationType(String newLocationType) {
		locationType = newLocationType;
	}

	/**
	 * @param newUserEmails
	 *            java.util.List
	 */
	public void setUserEmails(List<String> newUserEmails) {
		userEmails = newUserEmails;
	}
}