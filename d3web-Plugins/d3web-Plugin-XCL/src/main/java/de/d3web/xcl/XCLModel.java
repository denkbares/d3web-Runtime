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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.xcl.XCLModel.XCLCaseModel;
import de.d3web.xcl.inference.PSMethodXCL;

public final class XCLModel implements KnowledgeSlice, Comparable<XCLModel>, SessionObjectSource<XCLCaseModel> {

	public final static KnowledgeKind<XCLModel> KNOWLEDGE_KIND =
			new KnowledgeKind<>("XCLModel", XCLModel.class);

	private final Solution solution;

	private Double establishedThreshold = null;
	private Double suggestedThreshold = null;
	private Double minSupport = null;

	private final Collection<XCLRelation> relations;
	private final Collection<XCLRelation> necessaryRelations;
	private final Collection<XCLRelation> sufficientRelations;
	private final Collection<XCLRelation> contradictingRelations;
	public final static String DEFAULT_SOLUTION = "default_solution";

	private boolean considerOnlyRelevantRelations = true;
	// TODO: store these information in the NamedObjects, also required for
	// efficient propagation
	private transient final Map<TerminologyObject, Set<XCLRelation>> coveringRelations = new HashMap<>();
	private transient final Set<TerminologyObject> positiveCoveredSymptoms = new HashSet<>();

	public XCLModel(Solution solution) {
		this.solution = solution;

		relations = new LinkedHashSet<>();
		necessaryRelations = new LinkedHashSet<>();
		sufficientRelations = new LinkedHashSet<>();
		contradictingRelations = new LinkedHashSet<>();
	}

	/**
	 * Returns a set of all the {@link TerminologyObject}s that are covered by this {@link XCLModel} with any type of
	 * {@link XCLRelation}.
	 *
	 * @return the covered terminology objects
	 * @created 31.05.2012
	 */
	public Set<TerminologyObject> getCoveredSymptoms() {
		return coveringRelations.keySet();
	}

	/**
	 * Returns a set of all the {@link TerminologyObject}s that are covered by this {@link XCLModel} with any type that
	 * is not {@link XCLRelationType#contradicted}.
	 *
	 * @return the covered terminology objects
	 * @created 31.05.2012
	 */
	public Set<TerminologyObject> getPositiveCoveredSymptoms() {
		return positiveCoveredSymptoms;
	}

	/**
	 * Returns all the {@link XCLRelation}s of any {@link XCLRelationType} that are covering the specified {@link
	 * TerminologyObject} within this {@link XCLModel}. If no relations are covering the specified object, an empty set
	 * is returned.
	 *
	 * @param no the object to be covered
	 * @return the covering relations
	 * @created 31.05.2012
	 */
	public Set<XCLRelation> getCoveringRelations(TerminologyObject no) {
		Set<XCLRelation> result = coveringRelations.get(no);
		return (result == null) ? Collections.emptySet() : result;
	}

	public static void insertXCLRelation(KnowledgeBase kb,
										 Condition theCondition, Solution d) {
		insertXCLRelation(kb, theCondition, d, XCLRelationType.explains);
	}

	public static void insertXCLRelation(KnowledgeBase kb,
										 Condition theCondition, Solution d, XCLRelationType type) {
		insertXCLRelation(kb, theCondition, d, type, 1);
	}

	public static void insertXCLRelation(KnowledgeBase kb,
										 Condition theCondition, Solution d, XCLRelationType type,
										 double weight) {
		insertAndReturnXCLRelation(kb, theCondition, d, type, weight);
	}

	public static XCLRelation insertAndReturnXCLRelation(KnowledgeBase kb,
														 Condition theCondition, Solution d, XCLRelationType type,
														 double weight) {

		// null checks
		if (theCondition == null || d == null) {
			return null;
		}

		// insert XCL
		XCLModel xclModel = d.getKnowledgeStore().computeIfAbsent(KNOWLEDGE_KIND, () -> new XCLModel(d));
		XCLRelation relation = new XCLRelation(theCondition, weight, type);
		xclModel.addRelation(relation);
		return relation;
	}

	public Map<XCLRelationType, Collection<XCLRelation>> getTypedRelations() {

		Map<XCLRelationType, Collection<XCLRelation>> relationMap = new HashMap<>();

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

	public boolean addRelation(XCLRelation relation) {
		// add backward references from the terminal objects to this model
		for (TerminologyObject nob : relation.getConditionedFinding().getTerminalObjects()) {
			nob.getKnowledgeStore()
					.computeIfAbsent(XCLContributedModelSet.KNOWLEDGE_KIND, XCLContributedModelSet::new)
					.addModel(this);
		}
		// and add the relation to the particular set of relations
		return switch (relation.getType()) {
			case explains -> addRelationTo(relation, relations);
			case contradicted -> addRelationTo(relation, contradictingRelations);
			case requires -> addRelationTo(relation, necessaryRelations);
			case sufficiently -> addRelationTo(relation, sufficientRelations);
		};
	}

	public void removeRelation(XCLRelation rel) {
		for (TerminologyObject nob : rel.getConditionedFinding().getTerminalObjects()) {
			XCLContributedModelSet set = nob.getKnowledgeStore().getKnowledge(XCLContributedModelSet.KNOWLEDGE_KIND);
			if (set != null) {
				set.removeModel(this);
				if (set.isEmpty()) {
					nob.getKnowledgeStore().removeKnowledge(XCLContributedModelSet.KNOWLEDGE_KIND, set);
				}
			}
			// cleanup map
			Set<XCLRelation> xclRelationsOfObject = coveringRelations.get(nob);
			if (xclRelationsOfObject != null) {
				xclRelationsOfObject.remove(rel);
				if (xclRelationsOfObject.isEmpty()) {
					coveringRelations.remove(nob);
				}

				// cleanup positive cache
				boolean anyPositive = false;
				for (XCLRelation remainingRelation : xclRelationsOfObject) {
					if (isPositive(remainingRelation)) {
						anyPositive = true;
						break;
					}
				}
				if (!anyPositive) {
					positiveCoveredSymptoms.remove(nob);
				}
			}
		}
		relations.remove(rel);
		contradictingRelations.remove(rel);
		necessaryRelations.remove(rel);
		sufficientRelations.remove(rel);
	}

	private boolean addRelationTo(XCLRelation relation, Collection<XCLRelation> theRelations) {
		theRelations.add(relation);
		boolean isPositive = isPositive(relation);
		for (TerminologyObject no : relation.getConditionedFinding().getTerminalObjects()) {
			coveringRelations.computeIfAbsent(no, k -> new HashSet<>()).add(relation);
			if (isPositive) positiveCoveredSymptoms.add(no);
		}
		return true;
	}

	private boolean isPositive(XCLRelation relation) {
		return !relation.hasType(XCLRelationType.contradicted);
	}

	@Override
	public String toString() {
		return "XCLModel [" + getSolution().getName() + ": " + getAllRelations()
				+ (getSuggestedThreshold() == null ? "" : "; " + getSuggestedThreshold())
				+ (getEstablishedThreshold() == null ? "" : "; " + getEstablishedThreshold())
				+ "]@" + Integer.toHexString(hashCode());
	}

	public Solution getSolution() {
		return solution;
	}

	/**
	 * Returns the established threshold of a Model, if it is not the default. To get the threshold of the current
	 * session, use the instance of XCL: instance.getScoreAlgorithm().getEstablishedThreshold(model)
	 *
	 * @return the established threshold, if it is not the default, null otherwise
	 * @created 29.06.2010
	 */
	public Double getEstablishedThreshold() {
		return establishedThreshold;
	}

	/**
	 * Sets the EstablishedThreshold, if the value is smaller than 0, it is set to the default value
	 *
	 * @param establishedThreshold value of EstablishedThreshold
	 * @created 25.06.2010
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
	 * Returns the suggested threshold of a Model, if it is not the default. To get the threshold of the current
	 * session, use the instance of XCL: instance.getScoreAlgorithm().getSuggestedThreshold(model)
	 *
	 * @return the suggested threshold, if it is not the default, null otherwise
	 * @created 29.06.2010
	 */
	public Double getSuggestedThreshold() {
		return suggestedThreshold;
	}

	/**
	 * Sets the SuggestedThreshold, if the value is smaller than 0, it is set to the default value
	 *
	 * @param suggestedThreshold value of SuggestedThreshold
	 * @created 25.06.2010
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
	 * Returns the minimal support this Model must have, if it is not the default. To get the minimal support of the
	 * current session, use the instance of XCL: instance.getScoreAlgorithm().getMinSupport(model)
	 *
	 * @return the minimal support, if it is not the default, null otherwise
	 * @created 29.06.2010
	 */
	public Double getMinSupport() {
		return minSupport;
	}

	/**
	 * Sets the MinSupport, if the value is smaller than 0, it is set to the default value
	 *
	 * @param minSupport value of MinSupport
	 * @created 25.06.2010
	 */
	public void setMinSupport(double minSupport) {
		if (minSupport >= 0) {
			this.minSupport = minSupport;
		}
		else {
			this.minSupport = null;
		}
	}

	public List<XCLRelation> getAllRelations() {
		List<XCLRelation> allRels = new ArrayList<>();
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

	public static class XCLCaseModel implements SessionObject {

		private final InferenceTrace inferenceTrace;

		private XCLCaseModel(XCLModel model, Session session) {
			ScoreAlgorithm scoreAlgorithm = session.getPSMethodInstance(PSMethodXCL.class).getScoreAlgorithm();
			this.inferenceTrace = scoreAlgorithm.createInferenceTrace(model);
		}
	}

	@Override
	public XCLCaseModel createSessionObject(Session session) {
		return new XCLCaseModel(this, session);
	}

	public InferenceTrace getInferenceTrace(Session session) {
		return getXCLCaseModel(session).inferenceTrace;
	}

	private XCLCaseModel getXCLCaseModel(Session session) {
		return session.getSessionObject(this);
	}
}
