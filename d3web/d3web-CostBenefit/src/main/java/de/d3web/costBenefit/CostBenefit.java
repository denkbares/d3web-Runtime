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
package de.d3web.costBenefit;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.session.XPSCase;
import de.d3web.core.terminology.QContainer;
import de.d3web.costBenefit.inference.PSMethodCostBenefit;

/**
 * This KnowledgeSlice is used to store additional information of a QContainer used
 * by the CostBenefit problemsolver.
 * @author Markus Friedrich (denkbares GmbH)
 *
 */
public class CostBenefit implements KnowledgeSlice {

	private static final long serialVersionUID = -6270661353927880883L;

	public static final MethodKind COST_BENEFIT= new MethodKind("COST_BENEFIT");
	
	private int costs;
	private float maloperationProbability;
	private String taskType;
	private QContainer qcontainer;
	
	
	public CostBenefit(int costs, float maloperationProbability,
			QContainer qcontainer, String taskType) {
		super();
		this.costs = costs;
		this.maloperationProbability = maloperationProbability;
		this.qcontainer = qcontainer;
		this.taskType = taskType;
	}

	public int getCosts() {
		return costs;
	}

	public void setCosts(int costs) {
		this.costs = costs;
	}

	public float getMaloperationProbability() {
		return maloperationProbability;
	}

	public void setMaloperationProbability(float maloperationProbability) {
		this.maloperationProbability = maloperationProbability;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	@Override
	public String getId() {
		if (qcontainer!=null) {
			return "CostBenefit_"+qcontainer.getId();
		} else {
			return "CostBenefit_withoutQcontainer";
		}
	}

	@Override
	public boolean isUsed(XPSCase theCase) {
		return true;
	}

	@Override
	public void remove() {
		qcontainer.removeKnowledge(PSMethodCostBenefit.class, this, COST_BENEFIT);

	}

	public QContainer getQcontainer() {
		return qcontainer;
	}

	@Override
	public Class<? extends PSMethod> getProblemsolverContext() {
		return PSMethodCostBenefit.class;
	}

}
