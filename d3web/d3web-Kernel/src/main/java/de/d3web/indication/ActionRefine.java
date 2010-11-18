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

package de.d3web.indication;

import java.util.LinkedList;

import de.d3web.core.inference.PSAction;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.utilities.HashCodeUtils;

/**
 * ActionRefine represents the indication of a QASet in order to refine an
 * established solution.
 * 
 * Creation date: (21.02.2002 13:14:06)
 * 
 * @author Christian Betz
 */
public class ActionRefine extends ActionNextQASet {

	private static final Indication INDICATION = new Indication(State.INDICATED);
	private Solution target = null;


	/**
	 * @return the Diagnosis to refine
	 */
	public Solution getTarget() {
		return target;
	}

	/**
	 * sets the Diagnosis to refine
	 */
	public void setTarget(de.d3web.core.knowledge.terminology.Solution newTarget) {
		target = newTarget;
	}

	@Override
	public PSAction copy() {
		ActionRefine a = new ActionRefine();
		a.setQASets(new LinkedList<QASet>(getQASets()));
		a.setTarget(getTarget());
		return a;
	}


	@Override
	public int hashCode() {
		int result = super.hashCode();

		result = HashCodeUtils.hash(result, getTarget());

		return result;
	}

	@Override
	protected Indication getIndication() {
		return INDICATION;
	}

	@Override
	public boolean equals(Object o) {

		return super.equals(o) && isSame(((ActionClarify) o).getTarget(), getTarget());
	}


}