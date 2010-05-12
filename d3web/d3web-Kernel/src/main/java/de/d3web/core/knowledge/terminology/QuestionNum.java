/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.core.knowledge.terminology;

import java.util.List;

import de.d3web.core.knowledge.terminology.info.NumericalInterval;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.CaseQuestionNum;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.values.AnswerNum;

/**
 * Storage for Questions which have a numerical (Double value) answer. <BR>
 * Part of the Composite design pattern (see QASet for further description)
 * 
 * @author joba, Christian Betz, norman
 * @see QASet
 */
public class QuestionNum extends Question {

	public QuestionNum(String id) {
		super(id);
	}

	/**
	 * List of meaningful partitions for the numerical value range of this
	 * question.
	 */
	private List<NumericalInterval> valuePartitions = null;

	public SessionObject createCaseObject(Session session) {
		return new CaseQuestionNum(this);
	}

	/**
	 * @return AnswerNum (with value = value)
	 */
	public AnswerNum getAnswer(Session theCase, Double value) {
		if (value == null) return null;
		else {
			AnswerNum result = new AnswerNum();
			result.setValue(value);
			result.setQuestion(this);
			return result;
		}
	}

	/**
	 * Generates a XML identification (not the complete representation)
	 */
	public String getXMLString() {
		return "<QuestionNum ID='" + this.getId() + "'></QuestionNum>\n";
	}

	/**
	 * Returns the list of meaningful partitions for the numerical value range
	 * of this question.
	 * 
	 * @return partitions of the numerical value range.
	 */
	public List<NumericalInterval> getValuePartitions() {
		return valuePartitions;
	}

	/**
	 * Sets the list of meaningful partitions for the numerical value range of
	 * this question.
	 * 
	 * @param valuePartions meaningful partitions of the value range.
	 */
	public void setValuePartitions(List<NumericalInterval> valuePartitions) {
		this.valuePartitions = valuePartitions;
	}

}