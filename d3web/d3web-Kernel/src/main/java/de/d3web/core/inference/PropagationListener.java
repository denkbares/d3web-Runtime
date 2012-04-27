/*
 * Copyright (C) 2012 denkbares GmbH
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
 * Interface for classes, which want to get notified about the different
 * propagation states
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 27.03.2012
 */
public interface PropagationListener {

	void propagationStarted(Session session, Collection<PropagationEntry> entries);

	void postPropagationStarted(Session session, Collection<PropagationEntry> entries);

	void propagationFinished(Session session, Collection<PropagationEntry> entries);

}