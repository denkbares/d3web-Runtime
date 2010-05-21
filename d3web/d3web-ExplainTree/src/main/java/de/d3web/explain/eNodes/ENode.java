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
 * ENode.java
 *
 * Created on 26. März 2002, 17:10
 */

package de.d3web.explain.eNodes;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.Rule;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.explain.ExplanationFactory;
import de.d3web.explain.eNodes.values.DiagnosticValue;
import de.d3web.explain.eNodes.values.TargetValue;
import de.d3web.indication.inference.PSMethodNextQASet;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.inference.PSMethodHeuristic;

/**
 *
 * @author  betz
 */
public class ENode {
    private NamedObject target = null;		//e.g. the question or diagnosis to explain
    private TargetValue value = null;			//FF wie kann eine Aw-Alternative erklärt werden (in LispD3 nicht möglich!)
    private Collection contexts = null;

    private Collection proReasons = null;		//Collection of EReasons
    	// bei Diagnosis nur "P-Regeln"; bei QContainer und Questions nur Indikationsregeln
    private Collection contraReasons = null;	//Collection of EReasons
    	// bei Diagnosis nur "N-Regeln"; bei QContainer und Questions nur KontraInd-regeln
    private Collection unrealized = null;		//Collection of EReasons
    private ExplanationFactory factory = null;

    /** Creates a new instance of ENode */
    public ENode(ExplanationFactory myFactory, NamedObject myTarget, TargetValue myValue, Collection myContexts) {
        super();
        factory = myFactory;
        target = myTarget;
        value = myValue;
        contexts = myContexts;
    }

    /** Getter for property target.
     * @return Value of property target.
     */
    public NamedObject getTarget() {
        return target;
    }

    /** Getter for property contexts.
     * @return Value of property contexts.
     */
    public Collection getContexts() {
        return contexts;
    }

    /** Getter for property value.
     * @return Value of property value.
     */
    public TargetValue getValue() {
        return value;
    }



	public Collection getProReasons() {
		if (proReasons == null) {
			return new LinkedList();
//			//FIXME
//			if (getTarget() instanceof QASet) {	
//				if (getValue() == QState.ACTIVE) {
//					proReasons = getQASetActivationReasons();
//				}
//			} else if (getTarget() instanceof Solution) {
//				if (getValue() == DiagnosticValue.getInstance()) {
//					initDiagnosticReasons();
//				}
//			}
//			// so do other target objects. (welche?)
		}
		return proReasons;
	}

	public Collection getContraReasons() {
		if (contraReasons == null) {
			// TODO: hotfix
			return new LinkedList();
//			//FIXME
//			if (getTarget() instanceof QASet) {
//				if (getValue() == QState.ACTIVE) {
//					contraReasons = getQASetContraReasons();
//				}
//			} else if (getTarget() instanceof Solution) {
//				if (getValue() == DiagnosticValue.getInstance()) {
//					initDiagnosticReasons();
//				}
//			}
//			// so do other target objects. (welche?)
		}
		return contraReasons;
	}

	public Collection getUnrealizedReasons() {
		if (unrealized == null) {
			if (getTarget() instanceof QASet) {
				unrealized = getQASetUnrealizedReasons();
			} else if (getTarget() instanceof Solution) {
				if (getValue() == DiagnosticValue.getInstance()) {
					initDiagnosticReasons();
				}
			}
			// FIXME other target objects are still missing (welche?)
		}
		return unrealized;
	}

//	private Collection getQASetActivationReasons() {
//		Collection retValues = new LinkedList();
//		Iterator pros =
//			((QASet) getTarget()).getProReasons(getFactory().getSession()).iterator();
//		while (pros.hasNext()) {
//			Object pro = pros.next();
//			if ((pro instanceof QASet.Reason) && (contexts.contains(((QASet.Reason) pro).getProblemSolverContext()))) {
//				retValues.add(EReason.createReason(getFactory(), (QASet.Reason) pro));
//			} else {
//				System.err.println("Ups, kein QASet.Reason. Dürfte eigentlich nicht sein!");
//			}
//		}
//		return retValues;
//	}
	
	private Collection getQASetContraReasons() {
		Collection retValues = new LinkedList();
//		Iterator cons =
//			((QASet) getTarget()).getContraReasons(getFactory().getSession()).iterator();
//		while (cons.hasNext()) {
//			Object con = cons.next();
//			if ((con instanceof QASet.Reason) && (contexts.contains(((QASet.Reason) con).getProblemSolverContext()))) {
//				retValues.add(EReason.createReason(getFactory(), (QASet.Reason) con));
//			} else {
//				System.err.println("Ups, kein QASet.Reason. Dürfte eigentlich nicht sein!");
//			}
//		}
		return retValues;
	}
	
	private Collection getQASetUnrealizedReasons() {
		Collection unrealized = new LinkedList();
		Class context = PSMethodNextQASet.class;		//FIXME: Kontext berücksichtigen
		
		KnowledgeSlice ks = ((QASet)getTarget()).getKnowledge(context, MethodKind.BACKWARD);
		if (ks != null) {
			KnowledgeSlice rule = (KnowledgeSlice) ks;
			if (!rule.isUsed(getFactory().getSession())) {	//FIXME: right method "isUsed"?
				unrealized.add(EReason.createReason(getFactory(), rule));
			}
		}
		return(unrealized);
	}

	private void initDiagnosticReasons() {
		proReasons = new LinkedList();
		contraReasons = new LinkedList();
		unrealized = new LinkedList();

		KnowledgeSlice backwardKnowledge = ((Solution) getTarget()).getKnowledge(PSMethodHeuristic.class, MethodKind.BACKWARD);
		if (backwardKnowledge == null) {
			return;
		}
		KnowledgeSlice rule = (KnowledgeSlice) backwardKnowledge;
		if (rule.isUsed(getFactory().getSession())) {	//FIXME: right method "isUsed"?
			try {
				if (((ActionHeuristicPS)((Rule)rule).getAction()).getScore().aPrioriIsPositive()) {
					proReasons.add(EReason.createReason(getFactory(), rule));
				} else {
					contraReasons.add(EReason.createReason(getFactory(), rule));
				}
			} catch(ClassCastException ex) {
			        System.err.println("Error - not a RuleComplex-Object");
				ex.printStackTrace();
			}
		} else {
		        unrealized.add(EReason.createReason(getFactory(), rule));
		}	
	}


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getTarget().getId());
        sb.append(" ist ");
        sb.append(getValue());
        sb.append(", weil ");
        Iterator pros = getProReasons().iterator();
        while (pros.hasNext()) {
            sb.append("\n\t");
            sb.append(pros.next().toString());
        }
        return sb.toString();
    }

	/**
	 * Gets the factory.
	 * @return Returns a ExplanationFactory
	 */
	public ExplanationFactory getFactory() {
		return factory;
	}

}
