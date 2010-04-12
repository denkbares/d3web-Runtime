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
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.DiagnosisState;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.session.CaseObjectSource;
import de.d3web.core.session.IEventSource;
import de.d3web.core.session.KBOEventListener;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.XPSCaseObject;
import de.d3web.xcl.inference.PSMethodXCL;

public class XCLModel implements KnowledgeSlice, IEventSource, Comparable<XCLModel>, CaseObjectSource {

	public final static MethodKind XCLMODEL = new MethodKind("XCLMODEL");
	

	public static double defaultEstablishedThreshold = 0.8;
	public static double defaultSuggestedThreshold = 0.3;
	public static double defaultMinSupport = 0.01;

	private Solution solution;

	private double establishedThreshold = defaultEstablishedThreshold;
	private double suggestedThreshold = defaultSuggestedThreshold;
	private double minSupport = defaultMinSupport;

	private String id = null;
	private final Collection<XCLRelation> relations;
	private final Collection<XCLRelation> necessaryRelations;
	private final Collection<XCLRelation> sufficientRelations;
	private final Collection<XCLRelation> contradictingRelations;
	public static String DEFAULT_SOLUTION = "default_solution";

	private boolean considerOnlyRelevantRelations = true;
	// TODO: store these information in the NamedObjects, also required for
	// efficient propagation
	private transient Map<NamedObject, Set<XCLRelation>> coverage = new HashMap<NamedObject, Set<XCLRelation>>();

	public XCLModel(Solution solution) {
		this.solution = solution;

		relations = new LinkedList<XCLRelation>();
		necessaryRelations = new LinkedList<XCLRelation>();
		sufficientRelations = new LinkedList<XCLRelation>();
		contradictingRelations = new LinkedList<XCLRelation>();
	}

	public Set<NamedObject> getCoveredSymptoms() {
		return coverage.keySet();
	}

	public Set<XCLRelation> getCoveringRelations(NamedObject no) {
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

		// Nullchecks
		if (theCondition == null || d == null) {
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
					if (kdomNodeID != null) {
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

			if (kdomNodeID != null) {
				rel.setKdmomID(kdomNodeID);
			}

			relationID = rel.getId();
			newModel.addRelation(rel, type);
			// TODO: must it be added to the knowledge base?
//			kb.addKnowledge(PSMethodXCL.class, newModel, XCLModel.XCLMODEL);
			d.addKnowledge(PSMethodXCL.class, newModel, XCLModel.XCLMODEL);

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

	public DiagnosisState getState(Session theCase) {
		return getInferenceTrace(theCase).getState();

	}

	public boolean isConsiderOnlyRelevantRelations() {
		return considerOnlyRelevantRelations;
	}

	public void setConsiderOnlyRelevantRelations(
			boolean considerOnlyRelevantRelations) {
		this.considerOnlyRelevantRelations = considerOnlyRelevantRelations;
	}

	private PSMethodXCL getPSMethodXCL(Session theCase) {
		return (PSMethodXCL) theCase.getPSMethodInstance(getProblemsolverContext());
	}

	public boolean addRelation(XCLRelation relation) {
		return addRelation(relation, XCLRelationType.explains);
	}

	public boolean addRelation(XCLRelation relation, XCLRelationType type) {
		for (NamedObject nob : relation.getConditionedFinding().getTerminalObjects()) {
			KnowledgeSlice knowledge = nob.getKnowledge(PSMethodXCL.class, XCLContributedModelSet.XCL_CONTRIBUTED_MODELS);
			XCLContributedModelSet set = null;
			if (knowledge==null) {
				set = new XCLContributedModelSet();
				nob.addKnowledge(PSMethodXCL.class, set, XCLContributedModelSet.XCL_CONTRIBUTED_MODELS);
			} else {
				set = (XCLContributedModelSet) knowledge;
			}
			set.addModel(this);
		}
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

	public void removeRelation(XCLRelation rel) {
		for (NamedObject nob : rel.getConditionedFinding().getTerminalObjects()) {
			KnowledgeSlice knowledge = nob.getKnowledge(PSMethodXCL.class, XCLContributedModelSet.XCL_CONTRIBUTED_MODELS);
			if (knowledge!=null && knowledge instanceof XCLContributedModelSet) {
				XCLContributedModelSet set = (XCLContributedModelSet) knowledge;
				set.removeModel(this);
				if (set.isEmpty()) {
					nob.removeKnowledge(PSMethodXCL.class, set, XCLContributedModelSet.XCL_CONTRIBUTED_MODELS);
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

		if (theRelations.contains(relation))
			return false;
		theRelations.add(relation);
		List<? extends NamedObject> terminalObjects = relation.getConditionedFinding().getTerminalObjects();
		for (NamedObject no : terminalObjects) {
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
			if (id.equals(relation.getId()))
				return relation;
		}
		return null;
	}

	public Solution getSolution() {
		return solution;
	}

	public void setSolution(Solution solution) {
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

	public boolean isUsed(Session theCase) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.kernel.domainModel.KnowledgeSlice#remove()
	 */
	public void remove() {
		solution.getKnowledgeBase().removeKnowledge(PSMethodXCL.class, this,
				XCLModel.XCLMODEL);
		solution.removeKnowledge(getProblemsolverContext(), this, XCLMODEL);
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

	public void notifyListeners(Session xpsCase, IEventSource source) {
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
		return this.solution.getName().compareTo(o.solution.getName());
	}

	private static class XCLCaseModel extends XPSCaseObject {
		private final InferenceTrace inferenceTrace;

		private XCLCaseModel(XCLModel model, Session xpsCase) {
			super(model);
			ScoreAlgorithm scoreAlgorithm = model.getPSMethodXCL(xpsCase).getScoreAlgorithm();
			this.inferenceTrace = scoreAlgorithm.createInferenceTrace(model);
		}
	}

	public XPSCaseObject createCaseObject(Session xpsCase) {
		return new XCLCaseModel(this, xpsCase);
	}

	public InferenceTrace getInferenceTrace(Session xpsCase) {
		return getXCLCaseModel(xpsCase).inferenceTrace;
	}

	private XCLCaseModel getXCLCaseModel(Session xpsCase) {
		return (XCLCaseModel) xpsCase.getCaseObject(this);
	}
}
