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

package de.d3web.xcl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.AbstractTerminologyObject;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.CaseObjectSource;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.xcl.inference.PSMethodXCL;

public final class XCLModel implements KnowledgeSlice, Comparable<XCLModel>, CaseObjectSource {

	public final static MethodKind XCLMODEL = new MethodKind("XCLMODEL");

	private Solution solution;

	private Double establishedThreshold = null;
	private Double suggestedThreshold = null;
	private Double minSupport = null;

	private String id = null;
	private final Collection<XCLRelation> relations;
	private final Collection<XCLRelation> necessaryRelations;
	private final Collection<XCLRelation> sufficientRelations;
	private final Collection<XCLRelation> contradictingRelations;
	public final static String DEFAULT_SOLUTION = "default_solution";

	private boolean considerOnlyRelevantRelations = true;
	// TODO: store these information in the NamedObjects, also required for
	// efficient propagation
	private transient final Map<AbstractTerminologyObject, Set<XCLRelation>> coverage = new HashMap<AbstractTerminologyObject, Set<XCLRelation>>();

	public XCLModel(Solution solution) {
		this.solution = solution;

		relations = new LinkedList<XCLRelation>();
		necessaryRelations = new LinkedList<XCLRelation>();
		sufficientRelations = new LinkedList<XCLRelation>();
		contradictingRelations = new LinkedList<XCLRelation>();
	}

	public Set<AbstractTerminologyObject> getCoveredSymptoms() {
		return coverage.keySet();
	}

	public Set<XCLRelation> getCoveringRelations(TerminologyObject no) {
		return coverage.get(no);
	}

	public static String insertXCLRelation(KnowledgeBase kb,
			Condition theCondition, Solution d) {
		return insertXCLRelation(kb, theCondition, d, XCLRelationType.explains, null);
	}

	public static String insertXCLRelation(KnowledgeBase kb,
			Condition theCondition, Solution d, String kdomNodeID) {
		return insertXCLRelation(kb, theCondition, d, XCLRelationType.explains,
				kdomNodeID);
	}

	public static String insertXCLRelation(KnowledgeBase kb,
			Condition theCondition, Solution d, XCLRelationType type) {
		return insertXCLRelation(kb, theCondition, d, type, 1, null);
	}

	public static String insertXCLRelation(KnowledgeBase kb,
			Condition theCondition, Solution d, XCLRelationType type, String kdomNodeID) {
		return insertXCLRelation(kb, theCondition, d, type, 1, kdomNodeID);
	}

	public static String insertXCLRelation(KnowledgeBase kb,
			Condition theCondition, Solution d, XCLRelationType type,
			double weight) {
		return insertXCLRelation(kb, theCondition, d, type, weight, null);
	}

	public static String insertXCLRelation(KnowledgeBase kb,
			Condition theCondition, Solution d, XCLRelationType type,
			double weight, String kdomNodeID) {
		return insertAndReturnXCLRelation(kb, theCondition, d, type,
				weight, kdomNodeID).getId();
	}

	public static XCLRelation insertAndReturnXCLRelation(KnowledgeBase kb,
			Condition theCondition, Solution d, XCLRelationType type,
			double weight, String kdomNodeID) {

		// Nullchecks
		if (theCondition == null || d == null) {
			return null;
		}

		// insert XCL
		XCLRelation relation = null;
		Collection<KnowledgeSlice> models = kb
				.getAllKnowledgeSlicesFor(PSMethodXCL.class);

		boolean foundModel = false;
		for (KnowledgeSlice knowledgeSlice : models) {
			if (knowledgeSlice instanceof XCLModel) {
				if (((XCLModel) knowledgeSlice).getSolution().equals(d)) {
					relation = XCLRelation.createXCLRelation(
							theCondition, weight);
					if (kdomNodeID != null) {
						relation.setKdmomID(kdomNodeID);
					}
					((XCLModel) knowledgeSlice).addRelation(relation, type);
					foundModel = true;

				}
			}
		}
		if (!foundModel) {
			XCLModel newModel = new XCLModel(d);
			relation = XCLRelation.createXCLRelation(theCondition,
					weight);

			if (kdomNodeID != null) {
				relation.setKdmomID(kdomNodeID);
			}

			newModel.addRelation(relation, type);
			// TODO: must it be added to the knowledge base?
			// kb.addKnowledge(PSMethodXCL.class, newModel, XCLModel.XCLMODEL);
			d.getKnowledgeStore().addKnowledge(PSMethodXCL.class, XCLModel.XCLMODEL, newModel);

		}

		return relation;
	}

	public Map<XCLRelationType, Collection<XCLRelation>> getTypedRelations() {

		Map<XCLRelationType, Collection<XCLRelation>> relationMap = new HashMap<XCLRelationType, Collection<XCLRelation>>();

		relationMap.put(XCLRelationType.explains, this.getRelations());
		relationMap.put(XCLRelationType.contradicted, this.getContradictingRelations());
		relationMap.put(XCLRelationType.sufficiently, this.getSufficientRelations());
		relationMap.put(XCLRelationType.requires, this.getNecessaryRelations());

		return relationMap;
	}

	public Rating getState(Session session) {
		return getInferenceTrace(session).getState();

	}

	public boolean isConsiderOnlyRelevantRelations() {
		return considerOnlyRelevantRelations;
	}

	public void setConsiderOnlyRelevantRelations(
			boolean considerOnlyRelevantRelations) {
		this.considerOnlyRelevantRelations = considerOnlyRelevantRelations;
	}

	private PSMethodXCL getPSMethodXCL(Session session) {
		return (PSMethodXCL) session.getPSMethodInstance(getProblemsolverContext());
	}

	public boolean addRelation(XCLRelation relation) {
		return addRelation(relation, XCLRelationType.explains);
	}

	public boolean addRelation(XCLRelation relation, XCLRelationType type) {
		for (AbstractTerminologyObject nob : relation.getConditionedFinding().getTerminalObjects()) {
			KnowledgeSlice knowledge = nob.getKnowledgeStore().getKnowledge(PSMethodXCL.class,
					XCLContributedModelSet.XCL_CONTRIBUTED_MODELS);
			XCLContributedModelSet set = null;
			if (knowledge == null) {
				set = new XCLContributedModelSet();
				nob.getKnowledgeStore().addKnowledge(PSMethodXCL.class,
						XCLContributedModelSet.XCL_CONTRIBUTED_MODELS,
						set);
			}
			else {
				set = (XCLContributedModelSet) knowledge;
			}
			set.addModel(this);
		}
		if (type.equals(XCLRelationType.explains)) {
			return addRelationTo(relation, relations);
		}
		else if (type.equals(XCLRelationType.contradicted)) {
			return addRelationTo(relation, contradictingRelations);
		}
		else if (type.equals(XCLRelationType.requires)) {
			return addRelationTo(relation, necessaryRelations);
		}
		else if (type.equals(XCLRelationType.sufficiently)) {
			return addRelationTo(relation, sufficientRelations);
		}
		else return false;
	}

	public void removeRelation(XCLRelation rel) {
		for (AbstractTerminologyObject nob : rel.getConditionedFinding().getTerminalObjects()) {
			KnowledgeSlice knowledge = nob.getKnowledgeStore().getKnowledge(PSMethodXCL.class,
					XCLContributedModelSet.XCL_CONTRIBUTED_MODELS);
			if (knowledge instanceof XCLContributedModelSet) {
				XCLContributedModelSet set = (XCLContributedModelSet) knowledge;
				set.removeModel(this);
				if (set.isEmpty()) {
					nob.getKnowledgeStore().removeKnowledge(PSMethodXCL.class,
							XCLContributedModelSet.XCL_CONTRIBUTED_MODELS,
							set);
				}
			}
		}
		relations.remove(rel);
		contradictingRelations.remove(rel);
		necessaryRelations.remove(rel);
		sufficientRelations.remove(rel);
	}

	private boolean addRelationTo(XCLRelation relation,
			Collection<XCLRelation> theRelations) {

		if (theRelations.contains(relation)) return false;
		theRelations.add(relation);
		Collection<? extends AbstractTerminologyObject> terminalObjects = relation.getConditionedFinding().getTerminalObjects();
		for (AbstractTerminologyObject no : terminalObjects) {
			Set<XCLRelation> set = coverage.get(no);
			if (set == null) {
				set = new HashSet<XCLRelation>();
				coverage.put(no, set);
			}
			set.add(relation);
		}
		return true;
	}

	public XCLRelation findRelation(String id) {

		Collection<XCLRelation> r = new ArrayList<XCLRelation>();
		r.addAll(relations);
		r.addAll(necessaryRelations);
		r.addAll(sufficientRelations);

		r.addAll(contradictingRelations);
		for (XCLRelation relation : r) {
			if (id.equals(relation.getId())) return relation;
		}
		return null;
	}

	@Override
	public String toString() {
		return "XCLModel[" + getSolution().getName() + "; " + getSuggestedThreshold() + "; "
				+ getEstablishedThreshold() + "]@" + Integer.toHexString(hashCode());
	}

	public Solution getSolution() {
		return solution;
	}

	public void setSolution(Solution solution) {
		this.solution = solution;
	}

	/**
	 * Returns the established threshold of a Model, if it is not the default.
	 * To get the threshold of the current session, use the instance of XCL:
	 * instance.getScoreAlgorithm().getEstablishedThreshold(model)
	 * 
	 * @created 29.06.2010
	 * @return the established threshold, if it is not the default, null
	 *         otherwise
	 */
	public Double getEstablishedThreshold() {
		return establishedThreshold;
	}

	/**
	 * Sets the EstablishedThreshold, if the value is smaller than 0, it is set
	 * to the default value
	 * 
	 * @created 25.06.2010
	 * @param establishedThreshold value of EstablishedThreshold
	 */
	public void setEstablishedThreshold(double establishedThreshold) {
		if (establishedThreshold >= 0) {
			this.establishedThreshold = establishedThreshold;
		}
		else {
			this.establishedThreshold = null;
		}
	}

	/**
	 * Returns the suggested threshold of a Model, if it is not the default. To
	 * get the threshold of the current session, use the instance of XCL:
	 * instance.getScoreAlgorithm().getSuggestedThreshold(model)
	 * 
	 * @created 29.06.2010
	 * @return the suggested threshold, if it is not the default, null otherwise
	 */
	public Double getSuggestedThreshold() {
		return suggestedThreshold;
	}

	/**
	 * Sets the SuggestedThreshold, if the value is smaller than 0, it is set to
	 * the default value
	 * 
	 * @created 25.06.2010
	 * @param suggestedThreshold value of SuggestedThreshold
	 */
	public void setSuggestedThreshold(double suggestedThreshold) {
		if (suggestedThreshold >= 0) {
			this.suggestedThreshold = suggestedThreshold;
		}
		else {
			this.suggestedThreshold = null;
		}
	}

	/**
	 * Returns the minimal support this Model must have, if it is not the
	 * default. To get the minimal support of the current session, use the
	 * instance of XCL: instance.getScoreAlgorithm().getMinSupport(model)
	 * 
	 * @created 29.06.2010
	 * @return the minimal support, if it is not the default, null otherwise
	 */
	public Double getMinSupport() {
		return minSupport;
	}

	/**
	 * Sets the MinSupport, if the value is smaller than 0, it is set to the
	 * default value
	 * 
	 * @created 25.06.2010
	 * @param minSupport value of MinSupport
	 */
	public void setMinSupport(double minSupport) {
		if (minSupport >= 0) {
			this.minSupport = minSupport;
		}
		else {
			this.minSupport = null;
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		if (id == null) {
			if (solution == null) {
				return DEFAULT_SOLUTION;
			}
			id = "XCLM_" + solution.getName();
		}
		return id;
	}

	@Override
	public Class<PSMethodXCL> getProblemsolverContext() {
		return PSMethodXCL.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.kernel.domainModel.KnowledgeSlice#remove()
	 */
	@Override
	public void remove() {
		solution.getKnowledgeStore().removeKnowledge(getProblemsolverContext(), XCLMODEL, this);
		for (XCLRelation rel : new LinkedList<XCLRelation>(relations)) {
			removeRelation(rel);
		}
	}

	public List<XCLRelation> getAllRelations() {
		List<XCLRelation> allRels = new ArrayList<XCLRelation>();
		allRels.addAll(relations);
		allRels.addAll(necessaryRelations);
		allRels.addAll(sufficientRelations);
		allRels.addAll(contradictingRelations);
		return allRels;
	}

	public Collection<XCLRelation> getRelations() {
		return relations;
	}

	public Collection<XCLRelation> getNecessaryRelations() {
		return necessaryRelations;
	}

	public Collection<XCLRelation> getSufficientRelations() {
		return sufficientRelations;
	}

	public Collection<XCLRelation> getContradictingRelations() {
		return contradictingRelations;
	}

	@Override
	public int compareTo(XCLModel o) {
		return this.solution.getName().compareTo(o.solution.getName());
	}

	private static class XCLCaseModel extends SessionObject {

		private final InferenceTrace inferenceTrace;

		private XCLCaseModel(XCLModel model, Session session) {
			super(model);
			ScoreAlgorithm scoreAlgorithm = model.getPSMethodXCL(session).getScoreAlgorithm();
			this.inferenceTrace = scoreAlgorithm.createInferenceTrace(model);
		}
	}

	@Override
	public SessionObject createCaseObject(Session session) {
		return new XCLCaseModel(this, session);
	}

	public InferenceTrace getInferenceTrace(Session session) {
		return getXCLCaseModel(session).inferenceTrace;
	}

	private XCLCaseModel getXCLCaseModel(Session session) {
		return (XCLCaseModel) session.getCaseObject(this);
	}
}
