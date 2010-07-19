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
package de.d3web.costBenefit2.inference;

import de.d3web.costBenefit2.model.Path;
import de.d3web.costBenefit2.model.SearchModel;

/**
 * Strategy which decides, when a calculation should be aborted or not
 * @author Markus Friedrich (denkbares GmbH)
 *
 */
public interface AbortStrategy {

	/**
	 * Initializes the abortion strategy with a searchmodel
	 * @param model
	 */
	void init(SearchModel model);
	
	/**
	 * The next steps always should be committed to the AbortStrategy,
	 * depending on the infomations gained, the calculation can be aborted
	 * by throwing an {@link AbortException}
	 * @param path
	 * @throws AbortException
	 */
	void nextStep(Path path) throws AbortException;
}
