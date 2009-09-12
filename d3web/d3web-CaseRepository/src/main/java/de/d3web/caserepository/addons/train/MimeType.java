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

package de.d3web.caserepository.addons.train;

/**
 * Representee of a mime-type. It still lacks a description and the list of suffixes..
 * @author: Christian Betz
 */
public class MimeType {
	
	private String type = null;
	private String subtype = null;

	public static MimeType TEXT = new MimeType("text", null);
	public static MimeType IMAGE = new MimeType("image", null);
	public static MimeType WMPVIDEO = new MimeType("video", "wmp");

	/**
	 * MimeType constructor comment.
	 */
	private MimeType(String type, String subtype) {
		super();
		this.type = type;
		this.subtype = subtype;
	}

	/**
	 * @return String
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return String
	 */
	public  String getSubtype() {
		return subtype;
	}

	/**
	 * @deprecated just don't use
	 */
	public boolean isSubtypeOf(MimeType supertype) {
		return getType().equalsIgnoreCase(supertype.getType());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object anotherType) {
		if (this == anotherType) return true;
		if (!(anotherType instanceof MimeType)) return false;
		if (getType() == null && ((MimeType) anotherType).getType() == null) return true;
		if (getType() == null) return false;
		if (getType().equals(((MimeType) anotherType).getType())) {
			if (getSubtype() == null && ((MimeType) anotherType).getSubtype() == null) return true;
			if (getSubtype() == null) return false;
			if (getSubtype().equals(((MimeType) anotherType).getSubtype())) return true;
		}
		return false;
	}

}