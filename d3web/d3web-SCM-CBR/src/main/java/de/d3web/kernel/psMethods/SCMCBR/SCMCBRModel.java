package de.d3web.kernel.psMethods.SCMCBR;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Diagnosis;
import de.d3web.core.knowledge.terminology.DiagnosisState;
import de.d3web.core.session.IEventSource;
import de.d3web.core.session.KBOEventListener;
import de.d3web.core.session.XPSCase;
import de.d3web.xcl.XCLRelationType;


/**
 * 
 * @author Reinhard Hatko
 * Created: 17.09.2009
 *
 */
public class SCMCBRModel implements KnowledgeSlice, IEventSource {
	
	private static final long serialVersionUID = -7948460623330603066L;

	public final static MethodKind SCMCBR = new MethodKind("SCMCBR");

	public static double defaultEstablishedThreshold = 0.8;
	public static double defaultSuggestedThreshold = 0.3;
	public static double defaultMinSupport = 0.01;


	private double establishedThreshold = defaultEstablishedThreshold;
	private double suggestedThreshold = defaultSuggestedThreshold;
	private double minSupport = defaultMinSupport;
	
	private double coveringSuggestedThreshold = .5;
	private double coveringEstablishedThreshold = .8;
	
	private double completenessSuggestedThreshold = .5;
	private double completenessEstablishedThreshold = .8;

	private RelationHelper rh = RelationHelper.getInstance();
	private String id = null;
	private Collection<SCMCBRRelation> relations;
	private Collection<SCMCBRRelation> necessaryRelations;
	private Collection<SCMCBRRelation> sufficientRelations;
	private Collection<SCMCBRRelation> contradictingRelations;
	public static String DEFAULT_SOLUTION = "default_solution";

	private Map<XPSCase, SCMCBRInferenceTrace> explanation = new HashMap<XPSCase, SCMCBRInferenceTrace>();
	
	
	private Diagnosis solution;
	
	public SCMCBRModel(Diagnosis solution) {
		this.solution = solution;
		
		relations = new ArrayList<SCMCBRRelation>();
		necessaryRelations = new ArrayList<SCMCBRRelation>();
		sufficientRelations = new ArrayList<SCMCBRRelation>();
		contradictingRelations = new ArrayList<SCMCBRRelation>();
	}

	public SCMCBRInferenceTrace getInferenceTrace(XPSCase c) {
		return explanation.get(c);
	}

	public static String insertSCMCBRRelation(KnowledgeBase kb,
			Condition theCondition, Diagnosis d) {
		return insertSCMCBRRelation(kb, theCondition, d, XCLRelationType.explains, null);
	}
	
	public static String insertSCMCBRRelation(KnowledgeBase kb,
			Condition theCondition, Diagnosis d, String kdomNodeID) {
		return insertSCMCBRRelation(kb, theCondition, d, XCLRelationType.explains,kdomNodeID);
	}
	
	public static String insertSCMCBRRelation(KnowledgeBase kb,
			Condition theCondition, Diagnosis d, XCLRelationType type) {
		return insertSCMCBRRelation(kb, theCondition, d, type, 1, null);
	}

	public static String insertSCMCBRRelation(KnowledgeBase kb,
			Condition theCondition, Diagnosis d, XCLRelationType type, String kdomNodeID) {
		return insertSCMCBRRelation(kb, theCondition, d, type, 1, kdomNodeID);
	}
	
	public static String insertSCMCBRRelation(KnowledgeBase kb,
			Condition theCondition, Diagnosis d, XCLRelationType type,
			double weight) {
		return insertSCMCBRRelation(kb, theCondition, d, type, weight, null);
	}

	public static String insertSCMCBRRelation(KnowledgeBase kb,
			Condition theCondition, Diagnosis d, XCLRelationType type,
			double weight, String kdomNodeID) {
		
		//Nullchecks
		if(theCondition == null || d == null) {
			return null;
		}
		
		// insert XCL
		
		String relationID = null;
		Collection<KnowledgeSlice> models = kb
				.getAllKnowledgeSlicesFor(PSMethodSCMCBR.class);

		boolean foundModel = false;
		for (KnowledgeSlice knowledgeSlice : models) {
			if (knowledgeSlice instanceof SCMCBRModel) {
				if (((SCMCBRModel) knowledgeSlice).getSolution().equals(d)) {
					SCMCBRRelation rel = SCMCBRRelation.createSCMCBRRelation(
							theCondition, weight);
					if(kdomNodeID != null) {
						rel.setKdmomID(kdomNodeID);
					}
					relationID = rel.getId();
					((SCMCBRModel) knowledgeSlice).addRelation(rel, type);
					foundModel = true;
				}
			}
		}
		if (!foundModel) {
			SCMCBRModel newModel = new SCMCBRModel(d);
			SCMCBRRelation rel = SCMCBRRelation.createSCMCBRRelation(theCondition,
					weight);
			if(kdomNodeID != null) {
				rel.setKdmomID(kdomNodeID);
			}

			relationID = rel.getId();
			newModel.addRelation(rel, type);
			kb.addKnowledge(PSMethodSCMCBR.class, newModel, SCMCBRModel.SCMCBR);
		}

		return relationID;
	}

	public Map<XCLRelationType, Collection<SCMCBRRelation>> getTypedRelations() {

		Map<XCLRelationType, Collection<SCMCBRRelation>> relationMap = new HashMap<XCLRelationType, Collection<SCMCBRRelation>>();

		Collection<SCMCBRRelation> relations = this.getRelations();
		relationMap.put(XCLRelationType.explains, relations);

		Collection<SCMCBRRelation> contraRelations = this
				.getContradictingRelations();
		relationMap.put(XCLRelationType.contradicted, contraRelations);

		Collection<SCMCBRRelation> suffRelations = this.getSufficientRelations();
		relationMap.put(XCLRelationType.sufficiently, suffRelations);

		Collection<SCMCBRRelation> requRelations = this.getNecessaryRelations();
		relationMap.put(XCLRelationType.requires, requRelations);

		return relationMap;
	}

	public DiagnosisState getState(XPSCase theCase) {
		SCMCBRInferenceTrace trace = new SCMCBRInferenceTrace();
		explanation.put(theCase, trace);
		evalRelations(trace, theCase);

		if (rh.atLeastOneRelationTrue(contradictingRelations, theCase)) {
			trace.setState(DiagnosisState.EXCLUDED);
			return DiagnosisState.EXCLUDED;
		} else if (rh.atLeastOneRelationTrue(sufficientRelations, theCase)) {
			trace.setState(DiagnosisState.ESTABLISHED);
			return DiagnosisState.ESTABLISHED;
		} else {
			double currentXCLScore = computeXCLScore(theCase);
			double currentSupport = computeSupport(theCase);
			trace.setScore(currentXCLScore);
			trace.setSupport(currentSupport);
			if (minSupport <= currentSupport) {
				if (currentXCLScore >= establishedThreshold
						&& rh.allRelationsTrue(necessaryRelations, theCase)) {
					trace.setState(DiagnosisState.ESTABLISHED);
					return DiagnosisState.ESTABLISHED;
				} else if (currentXCLScore >= establishedThreshold
						&& !rh.allRelationsTrue(necessaryRelations, theCase)) {
					trace.setState(DiagnosisState.SUGGESTED);
					return DiagnosisState.SUGGESTED;
				} else if (currentXCLScore >= suggestedThreshold
						&& rh.allRelationsTrue(necessaryRelations, theCase)) {
					trace.setState(DiagnosisState.SUGGESTED);
					return DiagnosisState.SUGGESTED;
				}
			}
		}

		return DiagnosisState.UNCLEAR;
	}

	private void evalRelations(SCMCBRInferenceTrace trace, XPSCase c) {
		for (SCMCBRRelation rel : relations) {
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

		for (SCMCBRRelation rel : this.necessaryRelations) {
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

		for (SCMCBRRelation rel : this.contradictingRelations) {
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

		for (SCMCBRRelation rel : this.sufficientRelations) {
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

	private Collection<SCMCBRRelation> computeAllWeightedRelations() {
		Collection<SCMCBRRelation> all = new HashSet<SCMCBRRelation>();
		all.addAll(this.relations);
		all.addAll(this.necessaryRelations);
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

	private double weightedSumOf(Collection<SCMCBRRelation> relations) {
		double sum = 0;
		for (SCMCBRRelation relation : relations) {
			sum += relation.getWeight();
		}
		return sum;
	}

	private Collection<SCMCBRRelation> computeRelations(XPSCase theCase,
			boolean direction) {
		Collection<SCMCBRRelation> r = new ArrayList<SCMCBRRelation>();
		Collection<SCMCBRRelation> toTest = new ArrayList<SCMCBRRelation>();	
		toTest.addAll(relations);
		toTest.addAll(this.necessaryRelations);
		for (SCMCBRRelation relation : toTest) {
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

	public boolean addRelation(SCMCBRRelation relation) {
		return addRelationTo(relation, relations);
	}

	public boolean addRelation(SCMCBRRelation relation, XCLRelationType type) {
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

	public boolean addNecessaryRelation(SCMCBRRelation relation) {
		return addRelationTo(relation, necessaryRelations);
	}

	public boolean addSufficientRelation(SCMCBRRelation relation) {
		return addRelationTo(relation, sufficientRelations);
	}

	public boolean addContradictingRelation(SCMCBRRelation relation) {
		return addRelationTo(relation, contradictingRelations);
	}

	private boolean addRelationTo(SCMCBRRelation relation,
			Collection<SCMCBRRelation> theRelations) {
		if (theRelations.contains(relation))
			return false;
		theRelations.add(relation);
		return true;
	}

	public SCMCBRRelation findRelation(String id) {
		Collection<SCMCBRRelation> r = new ArrayList<SCMCBRRelation>();
		r.addAll(relations);
		r.addAll(necessaryRelations);
		r.addAll(sufficientRelations);
		r.addAll(contradictingRelations);
		for (SCMCBRRelation relation : r) {
			if (id.equals(relation.getId()))
				return relation;
		}
		return null;
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
			id = "SCMCBRM_" + solution.getId();
		}
		return id;
	}
	
	

	public double getCoveringSuggestedThreshold() {
		return coveringSuggestedThreshold;
	}

	public void setCoveringSuggestedThreshold(double coveringSuggestedThreshold) {
		this.coveringSuggestedThreshold = coveringSuggestedThreshold;
	}

	public double getCoveringEstablishedThreshold() {
		return coveringEstablishedThreshold;
	}

	public void setCoveringEstablishedThreshold(double coveringEstablishedThreshold) {
		this.coveringEstablishedThreshold = coveringEstablishedThreshold;
	}

	public double getCompletenessSuggestedThreshold() {
		return completenessSuggestedThreshold;
	}

	public void setCompletenessSuggestedThreshold(
			double completenessSuggestedThreshold) {
		this.completenessSuggestedThreshold = completenessSuggestedThreshold;
	}

	public double getCompletenessEstablishedThreshold() {
		return completenessEstablishedThreshold;
	}

	public void setCompletenessEstablishedThreshold(
			double completenessEstablishedThreshold) {
		this.completenessEstablishedThreshold = completenessEstablishedThreshold;
	}

	public Class<? extends PSMethod> getProblemsolverContext() {
		return PSMethodSCMCBR.class;
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
		solution.removeKnowledge(getProblemsolverContext(), this, SCMCBR);
	}

	public Collection<SCMCBRRelation> getRelations() {
		return relations;
	}

	public Collection<SCMCBRRelation> getNecessaryRelations() {
		return necessaryRelations;
	}

	public Collection<SCMCBRRelation> getSufficientRelations() {
		return sufficientRelations;
	}

	public Collection<SCMCBRRelation> getContradictingRelations() {
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

	
	

}
