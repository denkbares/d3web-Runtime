package de.d3web.kernel.domainModel.ruleCondition;
import java.util.Iterator;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.psMethods.MethodKind;

/**
 * This condition checks, if a specified diagnosis is established or
 * is in a specified state.
 * The composite pattern is used for this. This class is "leaf".
 * 
 * @author Christian Betz
 */
public class CondDState extends TerminalCondition {
	private Diagnosis diagnosis;
	private DiagnosisState status;
	private Class context;

	/**
	 * Creates a new CondDState Expression:
	 * @param diagnose diagnosis to check
	 * @param status state of the diagnosis to check
	 * @param context the context in which the diagnosis has the state
	 */
	public CondDState(
		Diagnosis diagnosis,
		DiagnosisState status,
		Class context) {
		super(diagnosis);
		this.diagnosis = diagnosis;
		this.status = status;
		this.context = context;
	}

	/**
	 * This method checks the condition
	 * 
	 * Problem: UNCLEAR is default state of diagnosis. 
	 * But we need to check for NoAnswerException,
	 * if no rule has ever changed the state of the diagnosis.
	 */
	public boolean eval(XPSCase theCase) throws NoAnswerException {
		if (diagnosis
			.getState(theCase, context)
			.equals(DiagnosisState.UNCLEAR)) {
			if (!hasHeuristicRulesFired(diagnosis, theCase, context))
				throw NoAnswerException.getInstance();
		}
		return status.equals(diagnosis.getState(theCase, context));
	}

	/**
	 * @return true, if a knowledge slice of a given diagnosis has fired for a given context.
	 */
	private static boolean hasHeuristicRulesFired(
		Diagnosis diagnosis,
		XPSCase theCase,
		Class context) {

		List knowledge =
			diagnosis.getKnowledge(context, MethodKind.BACKWARD);
		if (knowledge != null) {
			Iterator slices = knowledge.iterator();
			while (slices.hasNext()) {
				KnowledgeSlice slice = (KnowledgeSlice) slices.next();
				if (slice.isUsed(theCase))
					return true;
			}
		}
		return false;
	}

	public de.d3web.kernel.domainModel.Diagnosis getDiagnosis() {
		return diagnosis;
	}

	public de.d3web.kernel.domainModel.DiagnosisState getStatus() {
		return status;
	}

	public void setDiagnosis(
		de.d3web.kernel.domainModel.Diagnosis newDiagnosis) {
		diagnosis = newDiagnosis;
	}

	public void setStatus(
		de.d3web.kernel.domainModel.DiagnosisState newStatus) {
		status = newStatus;
	}

	/**
	 * Verbalizes the condition.
	 */
	public String toString() {
		return "<Condition type='DState' ID='"
			+ diagnosis.getId()
			+ "' value='"
			+ this.getStatus()
			+ "'>"
			+ "</Condition>\n";
	}
	
	
	
	public boolean equals(Object other) {
		
		if (!super.equals(other)) return false;
		
			CondDState otherCDS = (CondDState)other;
			
			boolean test = true;
			
			if(this.getDiagnosis() != null)
				test = this.getDiagnosis().equals(otherCDS.getDiagnosis()) && test;
			else //== null
				test = (otherCDS.getDiagnosis() == null) && test;
				
			if(this.getStatus() != null)
				test = this.getStatus().equals(otherCDS.getStatus()) && test;
			else
				test = (otherCDS.getStatus() == null) && test;
			
			if(this.getContext() != null)
				test = this.getContext().equals(otherCDS.getContext()) && test;
			else
				test = (otherCDS.getContext() == null) && test;
					
			return test;
		
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		
		String str = getClass().toString();
		
		if (getDiagnosis() != null)
			str+=getDiagnosis().toString();
			
		if (getStatus() != null)
			str+=getStatus().toString();
		
		if (getContext() != null)
			str+=getContext().toString();
			
		if (getTerminalObjects() != null)
			str+=getTerminalObjects().toString();
			
		return str.hashCode();
	}

	/**
	 * @return Class
	 */
	public Class getContext() {
		return context;
	}

	public AbstractCondition copy() {
		return new CondDState(getDiagnosis(), getStatus(), getContext());
	}

}