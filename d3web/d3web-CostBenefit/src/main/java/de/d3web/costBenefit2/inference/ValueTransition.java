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
package de.d3web.costBenefit2.inference;
import java.util.List;

import de.d3web.core.knowledge.terminology.Question;

/**
 * A ValueTransition contains a question and a List of ConditionalValueSetters,
 * which are sorted by priority.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 *
 */
public class ValueTransition {
	
	private Question question;
	private List<ConditionalValueSetter> setters;
	
	public ValueTransition(Question question,
			List<ConditionalValueSetter> setters) {
		super();
		this.question = question;
		this.setters = setters;
	}


	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}
	
	public List<ConditionalValueSetter> getSetters() {
		return setters;
	}

	public void setSetters(List<ConditionalValueSetter> setters) {
		this.setters = setters;
	}
	
	

}
