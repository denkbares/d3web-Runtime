/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.d3web.core.inference;

import java.util.Collection;

import de.d3web.core.session.Session;

/**
 * PSMethods can implement this interface, if they want do be notified after
 * propagation (for example for cleanup purposes)
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 17.11.2010
 */
public interface PostHookablePSMethod extends PSMethod {

	/**
	 * This method will be called after propagation.
	 * 
	 * @param session the session the propagation is started on
	 * @param entries the changes propagated so far, since propagation started
	 *        or last post-propagation
	 * 
	 * @created 17.11.2010
	 */
	void postPropagate(Session session, Collection<PropagationEntry> entries);
}
