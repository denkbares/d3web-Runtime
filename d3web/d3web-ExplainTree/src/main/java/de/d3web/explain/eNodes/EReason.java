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

/*
 * EReason.java
 *
 * Created on 27. März 2002, 16:14
 */

package de.d3web.explain.eNodes;

import de.d3web.explain.ExplanationFactory;
import de.d3web.explain.eNodes.reasons.EPSMethodReason;
import de.d3web.explain.eNodes.reasons.ERuleReason;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.domainModel.QASet;



/**
 * Superclass for all explanation reasons
 * @author  betz
 */
public class EReason {

	private ExplanationFactory myFactory = null;

	/** Creates a new instance of EReason */
	protected EReason(ExplanationFactory factory) {
		super();
		myFactory = factory;
	}

	public static EReason createReason(
		ExplanationFactory factory,
		QASet.Reason reason) {
		if (reason.getRule() != null) {
			return new ERuleReason(factory, reason);
		} else {
			return new EPSMethodReason(factory, reason);
		}
	}
	
	public static EReason createReason(
		ExplanationFactory factory,
		KnowledgeSlice reason) {
		return new ERuleReason(factory, reason);
	}

	protected ExplanationFactory getFactory() {
		return myFactory;
	}

}