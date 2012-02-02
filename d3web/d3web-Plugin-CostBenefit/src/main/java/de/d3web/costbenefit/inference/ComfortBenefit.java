/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.costbenefit.inference;

import java.util.LinkedList;

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeStore;
import de.d3web.core.knowledge.terminology.QContainer;

/**
 * This {@link KnowledgeSlice} marks a qContainer that has comfort benefit. If a
 * qContainer has comfort benefit, the condition is true and it does prevent
 * other qcontainers on the path from being applicable, the pathextender adds
 * the QContainer to the Path
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 02.02.2012
 */
public class ComfortBenefit implements KnowledgeSlice {

	public static final KnowledgeKind<ComfortBenefit> KNOWLEDGE_KIND = new KnowledgeKind<ComfortBenefit>(
			"COMFORT_BENEFIT", ComfortBenefit.class);
	private final QContainer qContainer;
	private final Condition condition;

	/**
	 * Creates a ComfortBenefit whose condition is always true. The
	 * {@link ComfortBenefit} is automatically added to the
	 * {@link KnowledgeStore} of the QContainer
	 * 
	 * @param qContainer QContainer this {@link KnowledgeSlice} belongs to
	 */
	public ComfortBenefit(QContainer qContainer) {
		this(qContainer, new CondAnd(new LinkedList<Condition>()));
	}

	/**
	 * Creates a ComfortBenefit with the specified condition for the specified
	 * QContainer. It will be automatically added to the {@link KnowledgeStore}
	 * of the QContainer.
	 * 
	 * @param qContainer QContainer this {@link KnowledgeSlice} belongs to
	 * @param condition specified Condition
	 */
	public ComfortBenefit(QContainer qContainer, Condition condition) {
		if (condition == null) {
			throw new NullPointerException();
		}
		this.qContainer = qContainer;
		this.condition = condition;
		qContainer.getKnowledgeStore().addKnowledge(KNOWLEDGE_KIND, this);
	}

	public QContainer getQContainer() {
		return qContainer;
	}

	public Condition getCondition() {
		return condition;
	}

}
