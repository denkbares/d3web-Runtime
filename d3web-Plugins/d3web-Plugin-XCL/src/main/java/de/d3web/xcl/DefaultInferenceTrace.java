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
import java.util.HashSet;

import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.session.Session;

public class DefaultInferenceTrace implements InferenceTrace {

	private final Collection<XCLRelation> posRelations = new HashSet<>();
	private final Collection<XCLRelation> negRelations = new HashSet<>();
	private final Collection<XCLRelation> contrRelations = new HashSet<>();
	private final Collection<XCLRelation> reqPosRelations = new HashSet<>();
	private final Collection<XCLRelation> reqNegRelations = new HashSet<>();
	private final Collection<XCLRelation> suffRelations = new HashSet<>();

	private Rating state = new Rating(Rating.State.UNCLEAR);

	private double score = 0;
	private double support = 0;

	@Override
	public Rating getState() {
		return state;
	}

	public void setState(Rating state) {
		this.state = state;
	}

	@Override
	public Collection<XCLRelation> getPosRelations() {
		return posRelations;
	}

	@Override
	public Collection<XCLRelation> getNegRelations() {
		return negRelations;
	}

	@Override
	public Collection<XCLRelation> getContrRelations() {
		return contrRelations;
	}

	@Override
	public Collection<XCLRelation> getReqPosRelations() {
		return reqPosRelations;
	}

	@Override
	public Collection<XCLRelation> getReqNegRelations() {
		return reqNegRelations;
	}

	@Override
	public Collection<XCLRelation> getSuffRelations() {
		return suffRelations;
	}

	@Override
	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	@Override
	public double getSupport() {
		return support;
	}

	public void setSupport(double support) {
		this.support = support;
	}

	/**
	 * Recalculates the inference trace relations for this model and the given
	 * case.
	 * 
	 * @param session the current case
	 */
	@Override
	public void refreshRelations(XCLModel xclModel, Session session) {
		evalRelations(session, xclModel.getRelations(), posRelations, negRelations);
		evalRelations(session, xclModel.getNecessaryRelations(), reqPosRelations,
				reqNegRelations);
		evalRelations(session, xclModel.getContradictingRelations(), contrRelations, null);
		evalRelations(session, xclModel.getSufficientRelations(), suffRelations, null);
	}

	private void evalRelations(Session session, Collection<XCLRelation> source, Collection<XCLRelation> trueSet, Collection<XCLRelation> falseSet) {
		// clear result sets
		trueSet.clear();
		if (falseSet != null) falseSet.clear();
		// eval all relations
		for (XCLRelation rel : source) {
			try {
				boolean b = rel.eval(session);
				if (b) {
					trueSet.add(rel);
				}
				else {
					if (falseSet != null) falseSet.add(rel);
				}
			}
			catch (NoAnswerException e) {
				// do nothing
			}
			catch (UnknownAnswerException e) {
				// do nothing
			}
		}
	}

}
