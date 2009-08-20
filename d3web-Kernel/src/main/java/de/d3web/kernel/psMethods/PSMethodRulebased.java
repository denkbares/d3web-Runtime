package de.d3web.kernel.psMethods;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.domainModel.RuleComplex;

/**
 * @author jochen
 * 
 * abstract PSMethod checking for rules relevant for the corresponding
 * problem-solver-subclass
 * 
 */
public abstract class PSMethodRulebased extends PSMethodAdapter {

	/**
	 * Check if NamedObject has rules connected with this problem-solver and
	 * check them, if available
	 */
	public void propagate(XPSCase theCase, NamedObject nob, Object[] newValue) {
		try {
			List<? extends KnowledgeSlice> slices = nob.getKnowledge(this
					.getClass());
			if (slices != null) {
				for (KnowledgeSlice slice : slices) {
					if (slice instanceof RuleComplex) {
						RuleComplex rule = (RuleComplex) slice;
						rule.check(theCase);
					}
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).throwing(
					this.getClass().getName(), "propagate", ex);
		}
	}

}
