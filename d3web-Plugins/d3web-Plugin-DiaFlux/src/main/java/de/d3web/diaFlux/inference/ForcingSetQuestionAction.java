/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.d3web.diaFlux.inference;

import java.util.List;

import de.d3web.abstraction.ActionSetQuestion;
import de.d3web.core.inference.ActionAddValueFact;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.utils.EqualsUtils;


/**
 * 
 * @author Reinhard Hatko
 * @created 13.05.2013
 */
public class ForcingSetQuestionAction extends ActionSetQuestion {

	private final ActionSetQuestion delegate;

	/**
	 * @param delegate
	 */
	public ForcingSetQuestionAction(ActionSetQuestion delegate) {
		this.delegate = delegate;
	}

	public ActionAddValueFact getDelegate() {
		return delegate;
	}

	@Override
	public void doIt(Session session, Object source, PSMethod psmethod) {
		delegate.doIt(session, source, psmethod);
		if (getQuestion().getInfoStore().getValue(DiaFluxUtils.FORCE_PROPAGATION)) {
			session.getPropagationManager().forcePropagate(getQuestion());
		}
	}

	@Override
	public List<? extends TerminologyObject> getBackwardObjects() {
		return delegate.getBackwardObjects();
	}

	@Override
	public List<? extends TerminologyObject> getForwardObjects() {
		return delegate.getForwardObjects();
	}

	@Override
	public void undo(Session session, Object source, PSMethod psmethod) {
		delegate.undo(session, source, psmethod);
	}

	@Override
	public Question getQuestion() {
		return delegate.getQuestion();
	}

	@Override
	public void setQuestion(Question question) {
		delegate.setQuestion(question);
	}

	@Override
	public Object getValue() {
		return delegate.getValue();
	}

	@Override
	public void setValue(Object theValue) {
		delegate.setValue(theValue);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof ForcingSetQuestionAction) {
			ActionSetQuestion a = (ActionSetQuestion) o;
			return (EqualsUtils.isSame(a.getQuestion(), getQuestion()) && a.getValue()
					.equals(getValue()));
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 0;
		if (getQuestion() != null) {
			hash += getQuestion().hashCode();
		}
		if (getValue() != null) {
			hash += getValue().hashCode();
		}
		return hash;
	}
	
	
	
}
