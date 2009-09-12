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

/**
 * Insert the type's description here. Creation date: (16.10.2001 20:30:05)
 * 
 * @author: Norman Brümmer
 */
public class KnowledgeBaseDescriptor {
    private java.lang.String id = null;
    private java.lang.String name = null;
    private java.lang.String type = null;
    private java.lang.String location = null;
    private java.lang.String locationType = null;

    /**
     * KnowledgeBaseDescriptor constructor comment.
     */
    public KnowledgeBaseDescriptor() {
	super();
    }

    /**
     * @return boolean
     * @param o
     *            java.lang.Object
     */
    @Override
    public boolean equals(Object o) {

	if (o instanceof KnowledgeBaseDescriptor) {
	    KnowledgeBaseDescriptor other = (KnowledgeBaseDescriptor) o;
	    return other.getId().equals(id);
	}

	return false;
    }

    /**
     * @return java.lang.String
     */
    public java.lang.String getId() {
	return id;
    }

    /**
     * @return java.lang.String
     */
    public java.lang.String getLocation() {
	return location;
    }

    /**
     * @return java.lang.String
     */
    public java.lang.String getLocationType() {
	return locationType;
    }

    /**
     * @return java.lang.String
     */
    public java.lang.String getName() {
	return name;
    }

    /**
     * @return java.lang.String
     */
    public java.lang.String getType() {
	return type;
    }

    /**
     * @param newId
     *            java.lang.String
     */
    public void setId(java.lang.String newId) {
	id = newId;
    }

    /**
     * @param newLocation
     *            java.lang.String
     */
    public void setLocation(java.lang.String newLocation) {
	location = newLocation;
    }

    /**
     * @param newLocationType
     *            java.lang.String
     */
    public void setLocationType(java.lang.String newLocationType) {
	locationType = newLocationType;
    }

    /**
     * @param newName
     *            java.lang.String
     */
    public void setName(java.lang.String newName) {
	name = newName;
    }

    /**
     * @param newType
     *            java.lang.String
     */
    public void setType(java.lang.String newType) {
	type = newType;
    }

    /**
     * @return java.lang.String
     */
    @Override
    public String toString() {
	return id + ": " + location;
    }
}