/*
 * Copyright (C) 2012 denkbares GmbH, Germany
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
package de.d3web.xcl.io;

import java.util.HashMap;
import java.util.Map;

import de.d3web.xcl.XCLRelation;

/**
 * This class manages a pool of relations associated with a temporarily id each.
 * 
 * @author volker_belli
 * @created 07.09.2012
 */
public class RelationPool {

	private int counter = 0;

	private final Map<String, XCLRelation> idToRelation = new HashMap<String, XCLRelation>();
	private final Map<XCLRelation, String> relationtoID = new HashMap<XCLRelation, String>();

	/**
	 * Adds a condition (if not present yet) and returns if it has been added.
	 * It will not been added if an equal one already exists.
	 * 
	 * @created 07.09.2012
	 * @param relation the relation to be added
	 * @return if the relation has been added
	 */
	public boolean add(XCLRelation relation) {
		boolean contained = relationtoID.containsKey(relation);
		if (contained) return false;
		String id = "rel_" + (counter++);
		add(id, relation);
		return true;
	}

	/**
	 * Adds a condition and associate it to a specified id.
	 * 
	 * @created 07.09.2012
	 * @param id the id to add the condition for
	 * @param relation the relation to be added
	 */
	public void add(String id, XCLRelation relation) {
		idToRelation.put(id, relation);
		relationtoID.put(relation, id);
	}

	/**
	 * Returns the id associated to a specific relation.
	 * 
	 * @created 07.09.2012
	 * @param relation the relation to be look up
	 * @return the id of the relation
	 */
	public String getID(XCLRelation relation) {
		return relationtoID.get(relation);
	}

	/**
	 * Returns the relation associated to the specified id.
	 * 
	 * @created 07.09.2012
	 * @param id the id to be searched
	 * @return the relation for the id
	 */
	public XCLRelation getRelation(String id) {
		return idToRelation.get(id);
	}
}
