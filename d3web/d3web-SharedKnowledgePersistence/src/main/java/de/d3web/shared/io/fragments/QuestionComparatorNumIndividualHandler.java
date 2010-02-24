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
package de.d3web.shared.io.fragments;

import de.d3web.kernel.psMethods.shared.comparators.QuestionComparator;
import de.d3web.kernel.psMethods.shared.comparators.num.QuestionComparatorNumIndividual;
/**
 * Handles QuestionComparatorNumIndividual
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class QuestionComparatorNumIndividualHandler extends
		QuestionComparatorHandler {

	@Override
	protected String getType() {
		return "QuestionComparatorNumIndividual";
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof QuestionComparatorNumIndividual;
	}

	@Override
	protected QuestionComparator getQuestionComparator() {
		return new QuestionComparatorNumIndividual();
	}

}
