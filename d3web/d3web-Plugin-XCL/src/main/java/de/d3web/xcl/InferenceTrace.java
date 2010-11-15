/*
 * Copyright (C) 2010 denkbares GmbH, Würzburg, Germany
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

import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.session.Session;

public interface InferenceTrace {

	public abstract Rating getState();

	public abstract Collection<XCLRelation> getPosRelations();

	public abstract Collection<XCLRelation> getNegRelations();

	public abstract Collection<XCLRelation> getContrRelations();

	public abstract Collection<XCLRelation> getReqPosRelations();

	public abstract Collection<XCLRelation> getReqNegRelations();

	public abstract Collection<XCLRelation> getSuffRelations();

	public abstract double getScore();

	public abstract double getSupport();

	public void refreshRelations(XCLModel xclModel, Session session);

}