package de.d3web.explain.eNodes.reasons;

import de.d3web.explain.ExplanationFactory;
import de.d3web.explain.eNodes.EReason;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.psMethods.PSMethodInit;
import de.d3web.kernel.psMethods.userSelected.PSMethodUserSelected;

public class EPSMethodReason extends EReason {

	private Class context = null;


	/**
	 * Constructor for ERuleReason. +
	 * @param qaSetReason
	 */
	public EPSMethodReason (ExplanationFactory factory, QASet.Reason qaSetReason) {
		super(factory);
		setContext(qaSetReason.getProblemSolverContext());
	}
	
	
	
	/** Getter for property context.
	 * @return Value of property context.
	 */
	public Class getContext() {
		return context;
	}

	/** Setter for property context.
	 * @param context New value of property context.
	 */
	private void setContext(Class context) {
		this.context = context;
	}

	
	

	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (getContext() == PSMethodUserSelected.class) {
			sb.append("... benutzerselektiert");
		} else if (getContext() == PSMethodInit.class) {
			sb.append("... Startfrageklasse");
		} else {
			sb.append("[PROBLEM in EPSMethodReason.toString] "+getContext());
		}
		return sb.toString();
	}

}
