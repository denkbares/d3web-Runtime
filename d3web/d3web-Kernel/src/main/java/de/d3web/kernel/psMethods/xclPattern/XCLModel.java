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

package de.d3web.kernel.psMethods.xclPattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.IEventSource;
import de.d3web.kernel.domainModel.KBOEventListener;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.NoAnswerException;
import de.d3web.kernel.domainModel.ruleCondition.UnknownAnswerException;
import de.d3web.kernel.psMethods.MethodKind;

public class XCLModel implements KnowledgeSlice, IEventSource,Comparable<XCLModel> {
	private static final long serialVersionUID = 1068721270222432667L;

	public final static MethodKind XCLMODEL = new MethodKind("XCLMODEL");

	public static double defaultEstablishedThreshold = 0.8;
	public static double defaultSuggestedThreshold = 0.3;
	public static double defaultMinSupport = 0.01;

	private Diagnosis solution;

	private double establishedThreshold = defaultEstablishedThreshold;
	private double suggestedThreshold = defaultSuggestedThreshold;
	private double minSupport = defaultMinSupport;

	private RelationHelper rh = RelationHelper.getInstance();
	private String id = null;
	private Map<String, XCLRelation> relations;
	private Map<String, XCLRelation> necessaryRelations;
	private Map<String, XCLRelation> sufficientRelations;
	private Map<String, XCLRelation> contradictingRelations;
	public static String DEFAULT_SOLUTION = "default_solution";

	private Map<XPSCase, XCLInferenceTrace> explanation = new HashMap<XPSCase, XCLInferenceTrace>();

	public XCLModel(Diagnosis solution) {
		this.solution = solution;
		
		relations = new HashMap<String, XCLRelation>();
		necessaryRelations = new HashMap<String, XCLRelation>();
		sufficientRelations = new HashMap<String, XCLRelation>();
		contradictingRelations = new HashMap<String, XCLRelation>();
	}

	public XCLInferenceTrace getInferenceTrace(XPSCase c) {
		XCLInferenceTrace temp=explanation.get(c);
		if (temp==null){
			XCLInferenceTrace trace = new XCLInferenceTrace();
			explanation.put(c, trace);}
		return explanation.get(c);
	}

	public static String insertXCLRelation(KnowledgeBase kb,
			AbstractCondition theCondition, Diagnosis d) {
		return insertXCLRelation(kb, theCondition, d, XCLRelationType.explains, null);
	}
	
	public static String insertXCLRelation(KnowledgeBase kb,
			AbstractCondition theCondition, Diagnosis d, String kdomNodeID) {
		return insertXCLRelation(kb, theCondition, d, XCLRelationType.explains,kdomNodeID);
	}
	
	public static String insertXCLRelation(KnowledgeBase kb,
			AbstractCondition theCondition, Diagnosis d, XCLRelationType type) {
		return insertXCLRelation(kb, theCondition, d, type, 1, null);
	}

	public static String insertXCLRelation(KnowledgeBase kb,
			AbstractCondition theCondition, Diagnosis d, XCLRelationType type, String kdomNodeID) {
		return insertXCLRelation(kb, theCondition, d, type, 1, kdomNodeID);
	}
	
	public static String insertXCLRelation(KnowledgeBase kb,
			AbstractCondition theCondition, Diagnosis d, XCLRelationType type,
			double weight) {
		return insertXCLRelation(kb, theCondition, d, type, weight, null);
	}

	public static String insertXCLRelation(KnowledgeBase kb,
			AbstractCondition theCondition, Diagnosis d, XCLRelationType type,
			double weight, String kdomNodeID) {
		
		//Nullchecks
		if(theCondition == null || d == null) {
			return null;
		}
		
		// insert XCL
		
		String relationID = null;
		Collection<KnowledgeSlice> models = kb
				.getAllKnowledgeSlicesFor(PSMethodXCL.class);

		boolean foundModel = false;
		for (KnowledgeSlice knowledgeSlice : models) {
			if (knowledgeSlice instanceof XCLModel) {
				if (((XCLModel) knowledgeSlice).getSolution().equals(d)) {
					XCLRelation rel = XCLRelation.createXCLRelation(
							theCondition, weight);
					if(kdomNodeID != null) {
						rel.setKdmomID(kdomNodeID);
					}
					relationID = rel.getId();
					((XCLModel) knowledgeSlice).addRelation(rel, type);
					foundModel = true;
				}
			}
		}
		if (!foundModel) {
			XCLModel newModel = new XCLModel(d);
			XCLRelation rel = XCLRelation.createXCLRelation(theCondition,
					weight);
			if(kdomNodeID != null) {
				rel.setKdmomID(kdomNodeID);
			}

			relationID = rel.getId();
			newModel.addRelation(rel, type);
			kb.addKnowledge(PSMethodXCL.class, newModel, XCLModel.XCLMODEL);
		}

		return relationID;
	}

	public Map<XCLRelationType, Collection<XCLRelation>> getTypedRelations() {

		Map<XCLRelationType, Collection<XCLRelation>> relationMap = new HashMap<XCLRelationType, Collection<XCLRelation>>();

		Collection<XCLRelation> relations = this.getRelations();
		relationMap.put(XCLRelationType.explains, relations);

		Collection<XCLRelation> contraRelations = this
				.getContradictingRelations();
		relationMap.put(XCLRelationType.contradicted, contraRelations);

		Collection<XCLRelation> suffRelations = this.getSufficientRelations();
		relationMap.put(XCLRelationType.sufficiently, suffRelations);

		Collection<XCLRelation> requRelations = this.getNecessaryRelations();
		relationMap.put(XCLRelationType.requires, requRelations);

		return relationMap;
	}

	public DiagnosisState getState(XPSCase theCase) {
		XCLInferenceTrace trace = new XCLInferenceTrace();
		explanation.put(theCase, trace);
		evalRelations(trace, theCase);

		if (rh.atLeastOneRelationTrue(contradictingRelations.values(), theCase)) {
			trace.setState(DiagnosisState.EXCLUDED);
			return DiagnosisState.EXCLUDED;
		} else if (rh.atLeastOneRelationTrue(sufficientRelations.values(), theCase)) {
			trace.setState(DiagnosisState.ESTABLISHED);
			return DiagnosisState.ESTABLISHED;
		} else {
			double currentXCLScore = computeXCLScore(theCase);
			double currentSupport = computeSupport(theCase);
			trace.setScore(currentXCLScore);
			trace.setSupport(currentSupport);
			if (minSupport <= currentSupport) {
				if (currentXCLScore >= establishedThreshold
						&& rh.allRelationsTrue(necessaryRelations.values(), theCase)) {
					trace.setState(DiagnosisState.ESTABLISHED);
					return DiagnosisState.ESTABLISHED;
				} else if (currentXCLScore >= establishedThreshold
						&& !rh.allRelationsTrue(necessaryRelations.values(), theCase)) {
					trace.setState(DiagnosisState.SUGGESTED);
					return DiagnosisState.SUGGESTED;
				} else if (currentXCLScore >= suggestedThreshold
						&& rh.allRelationsTrue(necessaryRelations.values(), theCase)) {
					trace.setState(DiagnosisState.SUGGESTED);
					return DiagnosisState.SUGGESTED;
				}
			}
		}

		return DiagnosisState.UNCLEAR;
	}

	private void evalRelations(XCLInferenceTrace trace, XPSCase c) {
		for (XCLRelation rel : relations.values()) {
			try {
				boolean b = rel.eval(c);
				if (b) {
					trace.addPosRelation(rel);
				} else {
					trace.addNegRelation(rel);
				}
			} catch (NoAnswerException e) {
				// do nothing
			} catch (UnknownAnswerException e) {
				// do nothing
			}

		}

		for (XCLRelation rel : this.necessaryRelations.values()) {
			try {
				boolean b = rel.eval(c);
				if (b) {
					trace.addReqPosRelation(rel);
				} else {
					trace.addReqPNegRelation(rel);
				}
			} catch (NoAnswerException e) {
				// do nothing
			} catch (UnknownAnswerException e) {
				// do nothing
			}

		}

		for (XCLRelation rel : this.contradictingRelations.values()) {
			try {
				boolean b = rel.eval(c);
				if (b) {
					trace.addContrRelation(rel);
				}
			} catch (NoAnswerException e) {
				// do nothing
			} catch (UnknownAnswerException e) {
				// do nothing
			}

		}

		for (XCLRelation rel : this.sufficientRelations.values()) {
			try {
				boolean b = rel.eval(c);
				if (b) {
					trace.addSuffRelation(rel);
				}
			} catch (NoAnswerException e) {
				// do nothing
			} catch (UnknownAnswerException e) {
				// do nothing
			}

		}

	}

	public double computeSupport(XPSCase theCase) {
		double positiveRelationsWeightedSum = weightedSumOf(computeRelations(
				theCase, true));
		double negativeRelationsWeightedSum = weightedSumOf(computeRelations(
				theCase, false));

		double allRelationsWeightedSum = weightedSumOf(computeAllWeightedRelations());

		double result = (positiveRelationsWeightedSum + negativeRelationsWeightedSum)
				* 1.0 / (allRelationsWeightedSum);

		return result;
	}

	private Collection<XCLRelation> computeAllWeightedRelations() {
		Collection<XCLRelation> all = new HashSet<XCLRelation>();
		all.addAll(this.relations.values());
		all.addAll(this.necessaryRelations.values());
		return all;
	}

	public double computeXCLScore(XPSCase theCase) {
		double positiveRelationsWeightedSum = weightedSumOf(computeRelations(
				theCase, true));
		double negativeRelationsWeightedSum = weightedSumOf(computeRelations(
				theCase, false));
		double result = positiveRelationsWeightedSum * 1.0
				/ (negativeRelationsWeightedSum + positiveRelationsWeightedSum);
		return result;
	}

	private double weightedSumOf(Collection<XCLRelation> relations) {
		double sum = 0;
		for (XCLRelation relation : relations) {
			sum += relation.getWeight();
		}
		return sum;
	}

	private Collection<XCLRelation> computeRelations(XPSCase theCase,
			boolean direction) {
		Collection<XCLRelation> r = new ArrayList<XCLRelation>();
		Collection<XCLRelation> toTest = new ArrayList<XCLRelation>();	
		toTest.addAll(relations.values());
		toTest.addAll(this.necessaryRelations.values());
		for (XCLRelation relation : toTest) {
			try {
				if (relation.eval(theCase) == direction)
					r.add(relation);
			} catch (NoAnswerException e) {
				// Do not count relation
			} catch (UnknownAnswerException e) {
				// Do not count relation
			}
		}
		return r;
	}

	public boolean addRelation(XCLRelation relation) {
		return addRelationTo(relation, relations);
	}

	public boolean addRelation(XCLRelation relation, XCLRelationType type) {
		if (type.equals(XCLRelationType.explains))
			return addRelationTo(relation, relations);

		if (type.equals(XCLRelationType.contradicted))
			return addContradictingRelation(relation);

		if (type.equals(XCLRelationType.requires))
			return addNecessaryRelation(relation);

		if (type.equals(XCLRelationType.sufficiently))
			return addSufficientRelation(relation);

		return false;
	}

	public boolean addNecessaryRelation(XCLRelation relation) {
		return addRelationTo(relation, necessaryRelations);
	}

	public boolean addSufficientRelation(XCLRelation relation) {
		return addRelationTo(relation, sufficientRelations);
	}

	public boolean addContradictingRelation(XCLRelation relation) {
		return addRelationTo(relation, contradictingRelations);
	}

	private boolean addRelationTo(XCLRelation relation,
			Map<String, XCLRelation> theRelations) {
		return theRelations.put(relation.getId(), relation) == null ? true : false;
	}

	public XCLRelation findRelation(String id) {
		XCLRelation r = null;
		r = relations.get(id);
		if (r != null) return r;
		r = necessaryRelations.get(id);
		if (r != null) return r;
		r = sufficientRelations.get(id);
		if (r != null) return r;
		r = contradictingRelations.get(id);
		return r;
	}
	
	public XCLRelation removeRelation(String id) {
		XCLRelation r = null;
		r = relations.remove(id);
		if (r != null) return r;
		r = necessaryRelations.remove(id);
		if (r != null) return r;
		r = sufficientRelations.remove(id);
		if (r != null) return r;
		r = contradictingRelations.remove(id);
		return r;
	}

	public Diagnosis getSolution() {
		return solution;
	}

	public void setSolution(Diagnosis solution) {
		this.solution = solution;
	}

	public double getEstablishedThreshold() {
		return establishedThreshold;
	}

	public void setEstablishedThreshold(double establishedThreshold) {
		this.establishedThreshold = establishedThreshold;
	}

	public double getSuggestedThreshold() {
		return suggestedThreshold;
	}

	public void setSuggestedThreshold(double suggestedThreshold) {
		this.suggestedThreshold = suggestedThreshold;
	}

	public double getMinSupport() {
		return minSupport;
	}

	public void setMinSupport(double minSupport) {
		this.minSupport = minSupport;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		if (id == null) {
			if (solution == null) {
				return DEFAULT_SOLUTION;
			}
			id = "XCLM_" + solution.getId();
		}
		return id;
	}

	public Class<PSMethodXCL> getProblemsolverContext() {
		return PSMethodXCL.class;
	}

	public boolean isUsed(XPSCase theCase) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.kernel.domainModel.KnowledgeSlice#remove()
	 */
	public void remove() {
		solution.removeKnowledge(getProblemsolverContext(), this, XCLMODEL);
	}
	
	public Map<String, XCLRelation> getAllRelations() {
		Map<String, XCLRelation> allRels = new HashMap<String, XCLRelation>();
		allRels.putAll(relations);
		allRels.putAll(necessaryRelations);
		allRels.putAll(sufficientRelations);
		allRels.putAll(contradictingRelations);
		return allRels;
	}

	public Collection<XCLRelation> getRelations() {
		return relations.values();
	}

	public Collection<XCLRelation> getNecessaryRelations() {
		return necessaryRelations.values();
	}

	public Collection<XCLRelation> getSufficientRelations() {
		return sufficientRelations.values();
	}

	public Collection<XCLRelation> getContradictingRelations() {
		return contradictingRelations.values();
	}

	Collection<KBOEventListener> listeners;

	public void addListener(KBOEventListener listener) {
		if (listeners == null)
			listeners = new LinkedList<KBOEventListener>();
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeListener(KBOEventListener listener) {
		if (listeners != null) {

			if (listeners.contains(listener)) {
				listeners.remove(listener);
			}
			if (listeners.size() == 0) {
				listeners = null;
			}
		}
	}

	public void notifyListeners(XPSCase xpsCase, IEventSource source) {
		if (listeners != null && xpsCase != null && source != null) {
			for (KBOEventListener cl : new ArrayList<KBOEventListener>(listeners)) {
				cl.notify(source, xpsCase);
			}
		}
	}

	public Collection<KBOEventListener> getListeners() {
		return listeners;
	}

	@Override
	public int compareTo(XCLModel o) {
		return this.solution.getText().compareTo(o.solution.getText());		
	}

}
