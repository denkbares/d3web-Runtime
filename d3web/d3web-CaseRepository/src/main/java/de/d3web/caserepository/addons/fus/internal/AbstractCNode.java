/*
 * Created on 21.10.2003
 */
package de.d3web.caserepository.addons.fus.internal;

import java.util.Set;

import de.d3web.kernel.domainModel.Diagnosis;

/**
 * 21.10.2003 15:47:43
 * @author hoernlein
 */
public abstract class AbstractCNode {
	
	protected AbstractCNode parent=null;
	
	/**
	 * @param setOfSolutions Set of CaseObject.Solution
	 * @return
	 */
	public abstract boolean matches(Set<Diagnosis> diagnoses);
	
	public AbstractCNode getParent() {
		return parent;
	}

	public void setParent(AbstractCNode node) {
		parent=node;
	}
	
	public abstract Object clone();

}
