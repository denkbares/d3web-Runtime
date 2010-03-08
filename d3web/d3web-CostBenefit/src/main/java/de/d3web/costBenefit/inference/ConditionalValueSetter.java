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
package de.d3web.costBenefit.inference;
import de.d3web.core.inference.condition.AbstractCondition;
import de.d3web.core.terminology.Answer;

/**
 * This class contains a condition and an answer. If the condition is true and no condition
 * of a previous ConditionalValueSetter if the same ValueTransition was true, this answer
 * is set.
 * @author Markus Friedrich (denkbares GmbH)
 *
 */
public class ConditionalValueSetter {

	private Answer answer;
	private AbstractCondition condition;
	
	public ConditionalValueSetter(Answer answer, AbstractCondition condition) {
		super();
		this.answer = answer;
		this.condition = condition;
	}
	
	public Answer getAnswer() {
		return answer;
	}
	
	public void setAnswer(Answer answer) {
		this.answer = answer;
	}
	
	public AbstractCondition getCondition() {
		return condition;
	}
	
	public void setCondition(AbstractCondition condition) {
		this.condition = condition;
	}
	
	
	
}
