package de.d3web.kernel.psMethods.setCovering;

import java.util.Iterator;
import java.util.List;

import de.d3web.kernel.psMethods.MethodKind;

/**
 * This a factory for SCRelations.
 * 
 * @author bates
 */
public class SCRelationFactory {

	private static final SCRelation defaultRelation = SCRelation.createDefaultRelation();

	public static SCRelation createDefaultSCRelation() {
		return defaultRelation;
	}

	public static SCRelation createSCRelation(SCNode source, SCNode target, List knowledgeList) {
		return createSCRelation(null, source, target, knowledgeList);
	}

	public static SCRelation createSCRelation(String id, SCNode source, SCNode target,
			List knowledgeList) {
		// typecheck first
		if ((source == null) || (target == null)) {
			throw new IllegalArgumentException("Neither source nor target should be null!");
		} else if (source.isLeaf()) {
			throw new IllegalArgumentException("Source must not be a PredictedFinding!");
		}

		// creating the relation
		SCRelation ret = new SCRelation();
		if (id != null) {
			ret.setId(id);
		}
		ret.setSourceNode(source);
		ret.setTargetNode(target);

		source.getNamedObject().addKnowledge(PSMethodSetCovering.class, ret, MethodKind.FORWARD);
		target.getNamedObject().addKnowledge(PSMethodSetCovering.class, ret, MethodKind.BACKWARD);

		// adding knowledge

		if (knowledgeList != null) {

			Iterator iter = knowledgeList.iterator();
			while (iter.hasNext()) {
				Object knowledge = iter.next();
				ret.addKnowledge(knowledge.getClass(), knowledge);
			}
		}

		return ret;
	}
}
