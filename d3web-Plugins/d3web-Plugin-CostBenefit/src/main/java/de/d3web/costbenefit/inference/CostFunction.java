/*
 * Copyright (C) 2009 denkbares GmbH
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

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.session.Session;

/**
 * This interface provides a method to calculate the costs of a QContainer depending on a case.
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
@FunctionalInterface
public interface CostFunction {

	/**
	 * Initializes this cost function for the specified knowledge base. It is granted that this method is called at
	 * least one for each usage, but it may be called multiple times, potentially even during usage, so this method
	 * should be thread-save and terminate quickly if there is nothing to be initialized.
	 *
	 * @param base the knowledge base to initialize this cost function for
	 */
	default void init(KnowledgeBase base) {
	}

	/**
	 * Calculates the actual costs of a qcontainer in dependency on session. If the costs are not dynamically depending
	 * on the session, it should return the same value as {@link #getStaticCosts(QContainer)}. Otherwise it should
	 * return a value <code>cost</code> which fulfills the following condition:<br>
	 * <code>getStaticCosts(...) &le; cost &le; getStaticCosts(...) + getMaxSupplement()</code>
	 */
	default double getCosts(QContainer qcon, Session session) {
		return getStaticCosts(qcon);
	}

	;

	/**
	 * Calculates the static costs of a qcontainer.
	 */
	double getStaticCosts(QContainer qcon);

	/**
	 * Returns the maximum dynamic supplementary costs, that can be added by this cost function based on the session
	 * status. if should return 0.0 if the method has no dynamic supplement on the static costs.
	 */
	default double getMaxSupplement() {
		return 0.0;
	}
}
