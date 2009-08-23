/*
 * Created on 21.10.2003
 */
package de.d3web.caserepository.addons.fus.internal;

import java.util.Set;

import de.d3web.kernel.domainModel.Diagnosis;


/**
 * 21.10.2003 15:44:51
 * @author hoernlein
 */
public class Configuration {
	
	private ProbabilityList caseObjectIDProbabilityList;
	private AbstractCNode node;
	
	public boolean matches(Set<Diagnosis> diagnoses) {
		return getNode().matches(diagnoses);
	}

	/**
	 * @return
	 */
	public ProbabilityList getCaseObjectIDProbabilityList() {
		return caseObjectIDProbabilityList;
	}

	/**
	 * @param object
	 */
	public void setCaseObjectIDProbabilityList(ProbabilityList caseObjectIDProbabilityList) {
		this.caseObjectIDProbabilityList = caseObjectIDProbabilityList;
	}

	/**
	 * @return
	 */
	public AbstractCNode getNode() {
		return node;
	}

	/**
	 * @param node
	 */
	public void setNode(AbstractCNode node) {
		this.node = node;
	}

	public Object clone() {
		Configuration temp = new Configuration();
		if (node!=null) {
			temp.setNode((AbstractCNode)node.clone());
		}
		temp.setCaseObjectIDProbabilityList(new ProbabilityList());
		return temp;
	}

}
