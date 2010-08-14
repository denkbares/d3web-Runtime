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

package de.d3web.explain.enodes.reasons;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.Rule;
import de.d3web.explain.ExplanationFactory;
import de.d3web.explain.enodes.ECondition;
import de.d3web.explain.enodes.EReason;

public class ERuleReason extends EReason {

	private Object context = null; // Erklärungskontext
	private Rule rule = null; // the "original" rule object
	private ECondition activeCondition = null;
	private ECondition activeContext = null; // Fragekontext (muss immer
												// beantwortet sein, damit die
												// Regel feuert)
	private ECondition activeException = null;
	private boolean initialized = false;

	/**
	 * Constructor for ERuleReason.
	 * 
	 * @param qaSetReason
	 */
	public ERuleReason(ExplanationFactory factory, Object qaSetReason) {
		super(factory);
		setContext(qaSetReason);
		// setRule(qaSetReason);
	}

	/**
	 * Constructor for ERuleReason.
	 * 
	 * @param qaSetReason
	 */
	public ERuleReason(ExplanationFactory factory, KnowledgeSlice reason) {
		super(factory);
		Rule rule = (Rule) reason;
		setContext(rule.getProblemsolverContext());
		setRule(rule);
	}

	/**
	 * Getter for property activeCondition.
	 * 
	 * @return Value of property activeCondition.
	 */
	public ECondition getActiveCondition() {
		if (activeCondition == null) {
			init();
		}
		return activeCondition;
	}

	/**
	 * Setter for property activeCondition.
	 * 
	 * @param activeCondition New value of property activeCondition.
	 */
	private void setActiveCondition(ECondition activeCondition) {
		this.activeCondition = activeCondition;
	}

	/**
	 * Getter for property activeContext.
	 * 
	 * @return Value of property activeContext.
	 */
	public ECondition getActiveContext() {
		if (activeContext == null) {
			init();
		}
		return activeContext;
	}

	/**
	 * Setter for property activeContext.
	 * 
	 * @param activeContext New value of property activeContext.
	 */
	private void setActiveContext(ECondition activeContext) {
		this.activeContext = activeContext;
	}

	/**
	 * Getter for property activeException.
	 * 
	 * @return Value of property activeException.
	 */
	public ECondition getActiveException() {
		if (activeException == null) {
			init();
		}
		return activeException;
	}

	/**
	 * Setter for property activeException.
	 * 
	 * @param activeException New value of property activeException.
	 */
	private void setActiveException(ECondition activeException) {
		this.activeException = activeException;
	}

	/**
	 * Getter for property context.
	 * 
	 * @return Value of property context.
	 */
	public Object getContext() {
		return context;
	}

	/**
	 * Setter for property context.
	 * 
	 * @param context New value of property context.
	 */
	private void setContext(Object context) {
		this.context = context;
	}

	/**
	 * Getter for property rule.
	 * 
	 * @return Value of property rule.
	 */
	public Rule getRule() {
		return rule;
	}

	/**
	 * Setter for property rule.
	 * 
	 * @param rule New value of property rule.
	 */
	private void setRule(Rule rule) {
		this.rule = rule;
	}

	private void init() {
		if (initialized) {
			return;
		}
		initialized = true;
		ECondition condition;

		condition = ECondition.createECondition(getFactory(), getRule().getCondition());
		// returns null if getRule().getCondition() is not active
		if (condition != null) {
			setActiveCondition(condition);
		}

		condition = ECondition.createECondition(getFactory(), getRule().getException());
		// returns null if getRule().getException() is not active
		if (condition != null) {
			setActiveException(condition);
		}

		// FF: activeContext nur für Diagnosen gültig? (==>
		// getDiagnosisContext())
		condition = ECondition.createECondition(getFactory(), getRule().getContext());
		if (condition != null) {
			setActiveContext(condition);
		}

	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (getActiveCondition() != null) {
			sb.append("<condition>\n");
			sb.append(getActiveCondition().toString());
			sb.append("</condition>\n");
		}
		if (getActiveException() != null) {
			sb.append("<exception>\n");
			sb.append(getActiveException().toString());
			sb.append("</exception>\n");
		}
		if (getActiveContext() != null) {
			sb.append("<context>\n");
			sb.append(getActiveContext().toString());
			sb.append("</context>\n");
		}
		return sb.toString();
	}

}
