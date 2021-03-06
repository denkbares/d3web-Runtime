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
package de.d3web.core.io.fragments.conditions;

import java.io.IOException;

import org.w3c.dom.Element;

import de.d3web.core.inference.condition.CondDState;
import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;

/**
 * FragementHandler for CondDStates
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class DStateConditionHandler implements FragmentHandler<KnowledgeBase> {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkCondition(element, "DState");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof CondDState);
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		String solutionID = element.getAttribute("name");
		String value = element.getAttribute("value");
		if (solutionID != null && value != null) {
			NamedObject idObject = persistence.getArtifact().getManager().search(solutionID);
			if (idObject instanceof Solution) {
				Solution diag = (Solution) idObject;
				Rating diagnosisState = getRating(value);
				return new CondDState(diag, diagnosisState);
			}
		}
		return null;
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		CondDState cond = (CondDState) object;
		String status = "";
		State ratingState = cond.getRatingState();
		if (ratingState != null) {
			status = ratingState.name();
		}
		return XMLUtil.writeCondition(
				persistence.getDocument(), cond.getSolution(), "DState", status);
	}

	private static Rating getRating(String status) {

		if (status.equalsIgnoreCase("established")) return new Rating(State.ESTABLISHED);
		if (status.equalsIgnoreCase("excluded")) return new Rating(State.EXCLUDED);
		if (status.equalsIgnoreCase("suggested")) return new Rating(State.SUGGESTED);
		if (status.equalsIgnoreCase("unclear")) return new Rating(State.UNCLEAR);

		return null;
	}

}
