package de.d3web.kernel.psMethods.setCovering;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.psMethods.MethodKind;

/**
 * This class represents a relation with probability between two SCNodes.
 * 
 * @author bates
 */
public class SCRelation implements KnowledgeSlice {

	public static String DEFAULT_RELATION = "default_relation";

	private Map knowledgeMap = null;
	private String id = null;

	private SCNode sourceNode = null;
	private SCNode targetNode = null;

	public SCRelation() {
		knowledgeMap = new HashMap();
	}

	public static SCRelation createDefaultRelation() {
		SCRelation ret = new SCRelation();
		ret.setId(DEFAULT_RELATION);
		return ret;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		if (id == null) {

			if ((sourceNode == null) || (targetNode == null)) {
				return DEFAULT_RELATION;
			}
			id = "SCR_" + sourceNode.getId() + "_" + targetNode.getId();
		}
		return id;
	}

	public Class getProblemsolverContext() {
		return PSMethodSetCovering.class;
	}

	public boolean isUsed(XPSCase theCase) {
		return true;
	}

	/**
	 * puts some knowledge (e.g. probability) into the knowledge map
	 * 
	 * @param context
	 *            knowledge context (e.g. SCProbability.class)
	 * @param knowledge
	 *            the knowledge (e.g. an instance of SCProbability)
	 */
	public void addKnowledge(Class context, Object knowledge) {
		knowledgeMap.put(context, knowledge);
	}

	/**
	 * retrieves some knowledge (e.g. probability) from the knowledge map.
	 * 
	 * @param context
	 *            Knowledge context (e.g. SCProbability.class)
	 * @return the matching knowledge
	 */
	public Object getKnowledge(Class context) {
		return knowledgeMap.get(context);
	}

	/**
	 * Removes the knowledge for the given context
	 * 
	 * @param context
	 *            Context to remove knowledge for (e.g. SCProbability.class)
	 */
	public void removeKnowledge(Class context) {
		knowledgeMap.remove(context);
	}

	/**
	 * Returns knowledge of every kind stored in this SCRelation
	 * 
	 * @return a List of knowledge stored in this relation
	 */
	public Collection getKnowledge() {
		return knowledgeMap.values();
	}

	/**
	 * sets the source node and adds this relation as forward-knowledge
	 * 
	 * @param sourceNode
	 *            node to set as cause
	 */
	public void setSourceNode(SCNode sourceNode) {
		this.sourceNode = sourceNode;
	}

	/**
	 * sets the target node and adds this relation as backward-knowledge
	 * 
	 * @param targetNode
	 *            node to set as effect
	 */
	public void setTargetNode(SCNode targetNode) {
		this.targetNode = targetNode;
	}

	public SCNode getSourceNode() {
		return sourceNode;
	}

	public SCNode getTargetNode() {
		return targetNode;
	}

	public int hashCode() {
		return getId().hashCode();
	}

	public boolean equals(Object o) {
		try {
			return hashCode() == o.hashCode();
		} catch (Exception e) {
			return false;
		}
	}

	public double getProbability() {
		Object o = getKnowledge(SCProbability.class);
		SCProbability prob = (SCProbability) o;
		if (prob != null) {
			return ((Double) prob.getValue()).doubleValue();
		}
		return 0;
	}

	public String getProbabilitySymbol() {
		SCProbability prob = (SCProbability) getKnowledge(SCProbability.class);
		if (prob != null) {
			return prob.getSymbol();
		}
		return "ZERO";
	}

	public String toString() {
		return getId();
	}

	public String verbalize() {
		if ((getSourceNode() == null) || (getTargetNode() == null)) {
			return "";
		} else {
			return getSourceNode().verbalize() + " => " + getTargetNode().verbalize();
		}
	}

	public void remove() {
		getSourceNode().getNamedObject().removeKnowledge(PSMethodSetCovering.class, this, MethodKind.FORWARD);
		getTargetNode().getNamedObject().removeKnowledge(PSMethodSetCovering.class, this, MethodKind.BACKWARD);
	}

}
