/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.xcl;

import java.util.Collection;

import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.session.Session;

public final class RelationHelper {

	private static RelationHelper instance;

	private RelationHelper() {
		super();
	}

	public static RelationHelper getInstance() {
		if (instance == null) {
			instance = new RelationHelper();
		}
		return instance;
	}

	/**
	 * 
	 * used for the necessary relations: if unknown --> false
	 * 
	 * @param relations
	 * @param session
	 * @return
	 */
	public boolean allRelationsTrue(Collection<XCLRelation> relations, Session session) {
		for (XCLRelation relation : relations) {
			try {
				if (!relation.eval(session)) return false;
			}
			catch (NoAnswerException e) {
				return false;
			}
			catch (UnknownAnswerException e) {
				return false;
			}
		}
		return true;
	}

	public boolean atLeastOneRelationTrue(Collection<XCLRelation> relations, Session session) {
		for (XCLRelation relation : relations) {
			try {
				if (relation.eval(session)) return true;
			}
			catch (NoAnswerException e) {
				// do nothing
			}
			catch (UnknownAnswerException e) {
				// do nothing
			}
		}
		return false;
	}

}
