/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.d3web.costBenefit2;

import java.util.Collection;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.costBenefit2.inference.PSMethodCostBenefit;
import de.d3web.plugin.Autodetect;
/**
 * Checks if the CostBenefit Plugin is necessary for the kb
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class AutodetectCostBenefit implements Autodetect {

	@Override
	public boolean check(KnowledgeBase kb) {
		Collection<KnowledgeSlice> ks = kb.getAllKnowledgeSlicesFor(PSMethodCostBenefit.class);
		return (ks.size()!=0);
	}

}
