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

package de.d3web.core.knowledge.terminology;
import java.util.List;

import de.d3web.abstraction.formula.FormulaNumberElement;
import de.d3web.core.knowledge.terminology.info.NumericalInterval;
import de.d3web.core.session.Value;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.CaseQuestion;
import de.d3web.core.session.blackboard.CaseQuestionNum;
import de.d3web.core.session.blackboard.XPSCaseObject;
import de.d3web.core.session.values.AnswerNum;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;

/**
 * Storage for Questions which have a numerical (Double value) answer.
 * <BR>
 * Part of the Composite design pattern (see QASet for further description)
 * 
 * @author joba, Christian Betz, norman
 * @see QASet
 */
public class QuestionNum extends Question {

	public QuestionNum(String id){
		super(id);
	}
	
	/**
	 * List of meaningful partitions for the numerical
	 * value range of this question.
	 */
	private List<NumericalInterval> valuePartitions = null;

	public XPSCaseObject createCaseObject(Session session) {
		return new CaseQuestionNum(this);
	}

	/**
	 * @param theCase current case
	 * @return 1-element list containing an AnswerNum
	 */
	@Override
	public Value getValue(Session theCase) {
		Object value =
			((CaseQuestionNum) theCase.getCaseObject(this)).getValue();

		if (value instanceof FormulaNumberElement) {
			return new NumValue(((FormulaNumberElement) value).eval(theCase));
		}
		else {
			return (Value) value;
		}
	}

	/**
	 * @return AnswerNum (with value = value)
	 */
	public AnswerNum getAnswer(Session theCase, Double value) {
		if (value == null)
			return null;
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
	
	@Override
	public void setValue(Session theCase, Value value) {
		if (value instanceof NumValue
				|| value instanceof UndefinedValue
				|| value instanceof Unknown) {
			((CaseQuestionNum) theCase.getCaseObject(this)).setValue(value);
		}
		else if (value instanceof FormulaNumberElement) {
			CaseQuestionNum question = ((CaseQuestionNum) theCase.getCaseObject(this));
			question.setValue(new NumValue(
					((FormulaNumberElement) value).eval(theCase)));
		}
		else if (value instanceof Unknown || value instanceof UndefinedValue) {
			((CaseQuestion) (theCase.getCaseObject(this))).setValue(value);
		}
		else {
			throw new IllegalArgumentException(value
					+ " is not an applicable instance.");
		}
	}

	/**
	 * Returns the list of meaningful partitions for the numerical
	 * value range of this question.
	 * @return partitions of the numerical value range.
	 */
	public List<NumericalInterval> getValuePartitions() {
		return valuePartitions;
	}

	/**
	 * Sets the list of meaningful partitions for the numerical
	 * value range of this question.
	 * @param valuePartions meaningful partitions of the value range.
	 */
	public void setValuePartitions(List<NumericalInterval> valuePartitions) {
		this.valuePartitions = valuePartitions;
	}

}