/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.shared.io.fragments;

import org.w3c.dom.Element;

import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.shared.comparators.QuestionComparator;
import de.d3web.shared.comparators.mc.QuestionComparatorMCGroupedSymmetric;

/**
 * Handles QuestionComparatorMCGroupedSymmetric
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class QuestionComparatorMCGroupedSymmetricHandler extends
		QuestionComparatorGroupedHandler {

	@Override
	protected String getType() {
		return "QuestionComparatorMCGroupedSymmetric";
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof QuestionComparatorMCGroupedSymmetric;
	}

	@Override
	protected QuestionComparator getQuestionComparator() {
		return new QuestionComparatorMCGroupedSymmetric();
	}

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, "KnowledgeSlice", getType())
				// in previous versions of the persistence, the type of the
				// slices was QuestionComparatorMCGrouped
				|| XMLUtil.checkNameAndType(element, "KnowledgeSlice",
						"QuestionComparatorMCGrouped");
	}
}
