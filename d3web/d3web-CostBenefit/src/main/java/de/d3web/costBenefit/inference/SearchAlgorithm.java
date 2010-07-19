/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.costBenefit.inference;

import de.d3web.core.session.Session;
import de.d3web.costBenefit.model.SearchModel;

/**
 * This Interface provides a method to start a search in a model.
 * The targets, the nodes etc. are stored in the model.
 * @author Markus Friedrich (denkbares GmbH)
 */
public interface SearchAlgorithm {

	/**
	 * Starts a search for the targets of the model. The result is stored in the model.
	 * @param session
	 * @param model
	 */
	void search(Session session, SearchModel model);
}
