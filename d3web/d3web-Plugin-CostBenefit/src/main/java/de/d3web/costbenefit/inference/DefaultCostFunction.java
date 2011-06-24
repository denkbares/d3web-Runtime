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

import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Session;

/**
 * The DefaultCostFunction returns the static costs of a QContainer. The actual
 * case is not used.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class DefaultCostFunction implements CostFunction {

	public DefaultCostFunction() {
	}

	@Override
	public double getCosts(QContainer qcon, Session session) {
		return qcon.getInfoStore().getValue(BasicProperties.COST);
	}

}
