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

package de.d3web.kernel.domainModel.qasets;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.answers.AnswerUnknown;
import de.d3web.kernel.domainModel.formula.FormulaNumberElement;
import de.d3web.kernel.dynamicObjects.CaseQuestionNum;
import de.d3web.kernel.dynamicObjects.XPSCaseObject;

/**
 * Storage for Questions which have a numerical (Double value) answer.
 * <BR>
 * Part of the Composite design pattern (see QASet for further description)
 * 
 * @author joba, Christian Betz, norman
 * @see QASet
 */
public class QuestionNum extends Question {

 	public QuestionNum(){
 		super();
 	}
 	
 	public QuestionNum(String id){
		super(id);
	}
	
	/**
	 * List of meaningful partitions for the numerical
	 * value range of this question.
	 */ 
	private List valuePartitions = null;

	public XPSCaseObject createCaseObject(XPSCase session) {
		return new CaseQuestionNum(this);
	}

	/**
	 * @return boolean (false)
	 */
	public boolean expand(List onList, XPSCase theCase) {
		return false;
	}

	/**
	 * @param theCase current case
	 * @return 1-element-vector with containing an AnswerNum 
	 */
	public List getValue(XPSCase theCase) {
		Object value =
			((CaseQuestionNum) theCase.getCaseObject(this)).getValue();

		Object tempValue = null;
		if (value instanceof FormulaNumberElement)
			tempValue = ((FormulaNumberElement) value).eval(theCase);
		else
			tempValue = value;

		if (tempValue != null) {
			List v = new LinkedList();
			v.add(tempValue);
			return (v);
		} else {
			return new LinkedList();
		}
	}

	/**
	 * @return AnswerNum (with value = value)
	 */
	public AnswerNum getAnswer(XPSCase theCase, Double value) {
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

	public void setValue(XPSCase theCase, Object[] values) {
		if (values.length == 0) {
			((CaseQuestionNum) theCase.getCaseObject(this)).setValue(null);
		} else if (values.length == 1) {
			if (values[0] instanceof AnswerUnknown) {
				((CaseQuestionNum) theCase.getCaseObject(this)).setValue(
					(AnswerUnknown) values[0]);
			} else {
				// Object[] ist ein Array aus AnswerNums
				Object value = ((AnswerNum) values[0]).getValue(theCase);
				Double newValue;
				if (values.length == 1) {
					if (value instanceof FormulaNumberElement)
						newValue = ((FormulaNumberElement) value).eval(theCase);
					else
						newValue = (Double) value;
				} else {
					newValue = null;
				}
				AnswerNum answerNum = new AnswerNum();
				answerNum.setValue(newValue);
				answerNum.setQuestion(this);
				((CaseQuestionNum) theCase.getCaseObject(this)).setValue(
					answerNum);
			}
		} else {
			Logger.getLogger(this.getClass().getName()).warning("wrong number of answers");
		}
		notifyListeners(theCase,this);
	}

	/**
	 * Returns the list of meaningful partitions for the numerical
	 * value range of this question.
	 * @return partitions of the numerical value range.
	 */ 
	public List getValuePartitions() {
		return valuePartitions;
	}

	/**
	 * Sets the list of meaningful partitions for the numerical
	 * value range of this question.
	 * @param valuePartions meaningful partitions of the value range.
	 */
	public void setValuePartitions(List valuePartitions) {
		this.valuePartitions = valuePartitions;
	}

}