package de.d3web.kernel.psMethods.heuristic;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisScore;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.HDTType;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.psMethods.PSMethodAdapter;
import de.d3web.kernel.psMethods.PSSubMethod;
import de.d3web.kernel.supportknowledge.Property;

/**
 * Heuristic problem-solver which adds scores to diagnoses
 * on the basis of question values. 
 * If score of a diagnosis exceeds a threshold value, then 
 * this diagnosis will be suggested/established/excluded.
 * <p>
 * <B>Currently implemented strategies:</B>
 * <P>
 * <B>SFA</B>: single fault assumption:<BR>
 * If a diagnosis established, then quit the case instantly (feature "continue case" is available)
 * All other diagnoses that are also suggested, are returned to be "suggested".
 * <P>
 * <B>Best Solution Only</B>
 * Only return the best established solution as "established";
 * all other established diagnoses are returned to be "suggsted"
 * 
 * Creation date: (28.08.00 18:04:09)
 * @author joba
 */
public class PSMethodHeuristic extends PSMethodAdapter {
	
	private Collection<PSSubMethod> subPSMethods = new LinkedList();
	// remembers if the case was stopped before (e.g., by SFA)
	private boolean wasPreviouslyStopped = false;
	
	private static PSMethodHeuristic instance = null;

	private PSMethodHeuristic() {
		super();
		setContributingToResult(true);
		//[TODO]: Peter: somebody should somewhere add the subproblemsolver:
		addSubPSMethod(ERSMethod.getInstance());
		addSubPSMethod(EDSMethod.getInstance());
		addSubPSMethod(SFAMethod.getInstance());
	}

	/**
	 * Creation date: (04.12.2001 12:36:25)
	 * @return the one and only instance of this ps-method (Singleton)
	 */
	public static PSMethodHeuristic getInstance() {
		if (instance == null) {
			instance = new PSMethodHeuristic();
		}
		return instance;
	}

	/**
	 * Calculates the state by checking the score of the diagnosis
	 * against a threshold value.
	 * Creation date: (05.10.00 13:41:07)
	 * @return de.d3web.kernel.domainModel.DiagnosisState
	 */
	public DiagnosisState getState(XPSCase theCase, Diagnosis diagnosis) {
		DiagnosisScore diagnosisScore =
			diagnosis.getScore(theCase, this.getClass());
		if (diagnosisScore == null)
			return DiagnosisState.UNCLEAR;
		else {
		    if (isSFA(theCase) || isBestSolutionOnly(theCase)) { 
		        DiagnosisState orgState = DiagnosisState.getState(diagnosisScore);
		        if (orgState.equals(DiagnosisState.ESTABLISHED)) {
		            Diagnosis bestDiagnosis = computeBestDiagnosis(theCase);
		            if (greaterScore(theCase, bestDiagnosis, diagnosis)) {
		                return DiagnosisState.SUGGESTED;
		            }
		            else
		                return orgState;
		        }
		        else {
		            return orgState;
		        }
		    }
		    else // the usual case 
		        return DiagnosisState.getState(diagnosisScore);
		}
	}

	/**
	 * Tests the double value of the Scores: d1 > d2 ?  
     * @return (d1.score > d2.score)
     */
    private boolean greaterScore(XPSCase theCase, Diagnosis d1, Diagnosis d2) {
        return d1.getScore(theCase, getClass()).getScore() > d2.getScore(theCase, getClass()).getScore();
    }

    /**
	 * Compute diagnosis that has max score for the specified case.
	 * Consider only final diagnoses here
     * @param theCase
     * @return Diagnosis instance
     */
    private Diagnosis computeBestDiagnosis(XPSCase theCase) {
        Diagnosis best = null;
        DiagnosisScore bestScore = null;
        for (Iterator iter = theCase.getKnowledgeBase().getDiagnoses().iterator(); iter.hasNext();) {
            Diagnosis d = (Diagnosis) iter.next();
            if (isFinalDiagnosis(d)) {
                if (best == null) {
                    best = d;
                    bestScore = best.getScore(theCase, getClass());
                }
                else {
                    if (bestScore.compareTo(d.getScore(theCase, getClass())) > 0) {
                        best = d;
                        bestScore = d.getScore(theCase, getClass());
                    }
                }
            }
            
        }
        return best;
    }

    /**
	 * Check if NamedObject has nextQASet rules and check them, if available
	 */
	public void propagate(
		XPSCase theCase,
		NamedObject nob,
		Object[] newValue) {

	    
	    // do nothing, if case has been finished
	    if (theCase.isFinished()) {
	        wasPreviouslyStopped = true;
	        return;
	    }
	    else if (wasPreviouslyStopped) {
	        wasPreviouslyStopped = false;
	        reCheckAllRules(theCase);
	    }
	    
		try {
			//[MISC]:Peter: decide where to check if the subPS should be used:
			for (PSSubMethod subMethod : subPSMethods) {
				if(subMethod.isActivated(theCase)) {
					subMethod.propagate(theCase, nob, newValue);
				}
			}
			
			checkRulesFor(theCase, nob);
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).throwing(
				this.getClass().getName(), "propagate", ex);
		}
		
	    // If SFA: if a diagnosis is established, then finish the case 
	    if ((isSFA(theCase)) && (finalSolutionIsEstablished(theCase))) {
                theCase.finish(getClass());
                return;
	    }

	}

	
	

    private boolean finalSolutionIsEstablished(XPSCase theCase) {
        for (Iterator iter = theCase.getKnowledgeBase().getDiagnoses().iterator(); iter.hasNext();) {
            Diagnosis d = (Diagnosis) iter.next();
            if ((isFinalDiagnosis(d)) && 
                (getState(theCase, d).equals(DiagnosisState.ESTABLISHED))){
                return true;
            }
            
        }
        return false;
    }

    /**
     * @param theCase
     * @param nob
     */
    private void checkRulesFor(XPSCase theCase, NamedObject nob) {
        List knowledgeSlices = (nob.getKnowledge(this.getClass()));
        if (knowledgeSlices != null) {
            for (Iterator iter = knowledgeSlices.iterator(); iter.hasNext();) {
                try {
                    RuleComplex rule = (RuleComplex) iter.next();
                    rule.check(theCase);
                } catch (Exception e) {
                    Logger.getLogger(this.getClass().getName()).throwing(
                            this.getClass().getName(), "propagate", e);
                }
            }
        }
    }

    /**
     * If a case was stopped and then continued, some question 
     * values may have not propagated to the heuristic rules.
     * Therefore, check all heuristic rules for all diagnoses. 
     * @param theCase
     */
    private void reCheckAllRules(XPSCase theCase) {
        List oldEstablishedDiagnoses = theCase.getDiagnoses(DiagnosisState.ESTABLISHED);
        List objects = new LinkedList(theCase.getKnowledgeBase().getQuestions());
        objects.addAll(theCase.getKnowledgeBase().getDiagnoses());
        for (Iterator iter = objects.iterator(); iter.hasNext();) {
            NamedObject o = (NamedObject) iter.next();
            checkRulesFor(theCase, o);
        }
        
        List newEstalishedDiagnoses = theCase.getDiagnoses(DiagnosisState.ESTABLISHED);
        if ((!oldEstablishedDiagnoses.isEmpty()) &&
            (oldEstablishedDiagnoses.containsAll(newEstalishedDiagnoses)) &&
            (newEstalishedDiagnoses.containsAll(oldEstablishedDiagnoses))) {
            // nothing has changed and a diagnosis is established => finishCase
            theCase.finish(getClass());
        }
            
    }

    /**
	 * Is a leaf diagnosis or marked as a final solution.
     * @param diagnosis
     * @return
     */
    private boolean isFinalDiagnosis(Diagnosis diagnosis) {
        List c = diagnosis.getChildren();
        boolean hasNoChildren = ((c == null) || (c.isEmpty()));
        HDTType type = diagnosis.getHdtType();
        if (type != null) {
            return ((type.equals(HDTType.SOLUTION)) ||
                    (type.equals(HDTType.NONE) && hasNoChildren));
        }
        else {
            return hasNoChildren;
        }
    }

    public String toString() {
		return "heuristic problem-solver";
	}
	
	public void addSubPSMethod(PSSubMethod subMethod) {
		subPSMethods.add(subMethod);
	}
	public void removeSubPSMethod(PSSubMethod subMethod) {
		subPSMethods.remove(subMethod);
	}
    /**
     * Single Fault Assumption:
     * Once a diagnosis is established the case is finished. 
     * Only the best diagnosis (if some were established in parallel) 
     * is returned as "establshed" solution. 
     * @return Returns the sFA.
     */
    public boolean isSFA(XPSCase theCase) {
        return getProperty(theCase, Property.SINGLE_FAULT_ASSUMPTION);
    }
    
    public boolean isBestSolutionOnly(XPSCase theCase) {
        return getProperty(theCase, Property.BEST_SOLUTION_ONLY);
    }

    private boolean getProperty(XPSCase theCase, Property property) {
        Boolean b = (Boolean)theCase.getKnowledgeBase().getProperties().getProperty(property);
        if (b == null)
            return false;
        else
            return b.booleanValue();
    }
}