/*
 * EReason.java
 *
 * Created on 27. März 2002, 16:14
 */

package de.d3web.explain.eNodes;

import de.d3web.explain.ExplanationFactory;
import de.d3web.explain.eNodes.reasons.EPSMethodReason;
import de.d3web.explain.eNodes.reasons.ERuleReason;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.domainModel.QASet;



/**
 * Superclass for all explanation reasons
 * @author  betz
 */
public class EReason {

	private ExplanationFactory myFactory = null;

	/** Creates a new instance of EReason */
	protected EReason(ExplanationFactory factory) {
		super();
		myFactory = factory;
	}

	public static EReason createReason(
		ExplanationFactory factory,
		QASet.Reason reason) {
		if (reason.getRule() != null) {
			return new ERuleReason(factory, reason);
		} else {
			return new EPSMethodReason(factory, reason);
		}
	}
	
	public static EReason createReason(
		ExplanationFactory factory,
		KnowledgeSlice reason) {
		return new ERuleReason(factory, reason);
	}

	protected ExplanationFactory getFactory() {
		return myFactory;
	}

}