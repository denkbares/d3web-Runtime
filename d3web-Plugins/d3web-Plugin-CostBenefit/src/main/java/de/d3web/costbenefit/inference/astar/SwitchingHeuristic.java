/*
 * Copyright (C) 2011 denkbares GmbH
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
package de.d3web.costbenefit.inference.astar;

import com.denkbares.utils.Log;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.costbenefit.model.Path;
import de.d3web.costbenefit.model.SearchModel;

/**
 * Switches from using {@link DividedTransitionHeuristic} and {@link TPHeuristic} depending on the amount of targets
 *
 * @author Markus Friedrich (denkbares GmbH)
 * @created 18.11.2011
 */
public class SwitchingHeuristic implements Heuristic {

	private final int switchingLevel;
	private final TPHeuristic tpHeuristic = new TPHeuristic();
	private final DividedTransitionHeuristic dividedTransitionHeuristic = new DividedTransitionHeuristic();
	private Heuristic actual;

	public SwitchingHeuristic(int switchingLevel) {
		super();
		this.switchingLevel = switchingLevel;
	}

	@Override
	public double getDistance(SearchModel model, Path path, State state, QContainer target) {
		return actual.getDistance(model, path, state, target);
	}

	@Override
	public double getDistance(SearchModel model, Path path, State state, Condition target) {
		return actual.getDistance(model, path, state, target);
	}

	@Override
	public void init(SearchModel searchModel) {
		if (searchModel.getTargets().size() > switchingLevel) {
			actual = dividedTransitionHeuristic;
		}
		else {
			actual = tpHeuristic;
		}
		Log.info("#Targets: " + searchModel.getTargets().size() + ", " +
				"using heuristic: " + actual.getClass().getSimpleName());
		actual.init(searchModel);
	}

	public int getSwitchingLevel() {
		return switchingLevel;
	}
}
