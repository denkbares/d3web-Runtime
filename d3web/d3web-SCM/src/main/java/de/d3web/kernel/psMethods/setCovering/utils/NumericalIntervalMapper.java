/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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

package de.d3web.kernel.psMethods.setCovering.utils;

import java.util.HashMap;
import java.util.Map;

import de.d3web.kernel.domainModel.NumericalInterval;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerFactory;

/**
 * This Mapper translates NumericalIntervals into AnswerChoices and back. It is
 * used for transformation.
 * 
 * @author bruemmer
 */
public class NumericalIntervalMapper {

	private Map intervalByAnswerChoice = null;
	private Map answerChoiceByInterval = null;

	private static NumericalIntervalMapper instance = null;

	private NumericalIntervalMapper() {
		intervalByAnswerChoice = new HashMap();
		answerChoiceByInterval = new HashMap();
	}

	public static NumericalIntervalMapper getInstance() {
		if (instance == null) {
			instance = new NumericalIntervalMapper();
		}
		return instance;
	}

	public void putInterval(NumericalInterval interval) {
		map(interval);
	}

	public AnswerChoice map(NumericalInterval interval) {
		AnswerChoice ans = (AnswerChoice) answerChoiceByInterval.get(interval);
		if (ans == null) {
			ans = AnswerFactory.createAnswerChoice(interval.toString(), interval.toString());
			answerChoiceByInterval.put(interval, ans);
			intervalByAnswerChoice.put(ans, interval);
		}
		return ans;
	}

	public NumericalInterval map(AnswerChoice answer) {
		return (NumericalInterval) intervalByAnswerChoice.get(answer);
	}

}
