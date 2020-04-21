/*
 * Copyright (C) 2011 denkbares GmbH, Germany
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
package de.d3web.costbenefit.blackboard;

import java.util.Date;

import de.d3web.core.session.DefaultSession;
import de.d3web.core.session.Session;

public class CopiedSession extends DefaultSession implements DerivedSession {

	private final Session originalSession;

	public CopiedSession(Session originalSession) {
		super(null, originalSession.getKnowledgeBase(), new Date(), psMethod -> false);
		this.originalSession = originalSession;
	}

	public Session getOriginalSession() {
		return originalSession;
	}

	@Override
	public Session getRootSession() {
		return DerivedSession.getRootSession(originalSession);
	}
}
