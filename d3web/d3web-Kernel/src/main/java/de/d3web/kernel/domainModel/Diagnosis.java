package de.d3web.kernel.domainModel;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.dynamicObjects.CaseDiagnosis;
import de.d3web.kernel.dynamicObjects.XPSCaseObject;
import de.d3web.kernel.psMethods.heuristic.PSMethodHeuristic;

/**
 * Class to store the static, non case-dependent parts of a diagnosis. You're
 * able to retrieve the score and state of a diagnosis through specified
 * routines. The state and score of a diagnosis is dependent from the
 * problem-solver context. If no problem-solver context is given the heuristic
 * problem-solver is assumed to be the context.
 * 
 * @author joba, chris
 * @see IDObject
 * @see NamedObject
 * @see DiagnosisScore
 * @see DiagnosisState
 */
public class Diagnosis extends NamedObject implements ValuedObject {

	static class DiagComp implements Comparator {
		private XPSCase theCase;
		public DiagComp(XPSCase theCase) {
			this.theCase = theCase;
		}
		/*
		 * compares scores in the heuristic context only. should be rewritten
		 * when other problemsolvers are added
		 */
		public int compare(Object o1, Object o2) {
			Diagnosis d1, d2;
			try {
				d1 = (Diagnosis) o1;
				d2 = (Diagnosis) o2;
			} catch (Exception e) {
				return 0;
			}
			return d1.getScore(theCase, PSMethodHeuristic.class).compareTo(
					d2.getScore(theCase, PSMethodHeuristic.class));
		}
	}

	private Score aprioriProbability;

	private HDTType hdtType = null;

	/**
	 * Constructs a new Diagnosis object. For attributes, which have to be
	 * filled with values, see the super class NamedObject. Important properties
	 * are:
	 * <LI>knowledgeBase : belonging to the KBObject
	 * <LI>id : an unique identifier for the KBObject
	 * <LI>text : a name for the KBObject
	 * 
	 * @see NamedObject
	 */
	public Diagnosis() {
		super();
	}
	
	public Diagnosis(String id) {
	    super(id);
	}

	//	private boolean addToList(Diagnosis theDiagnosis, List theList) {
	//		if (!(theList.contains(theDiagnosis))) {
	//			theList.add(theDiagnosis);
	//			return true;
	//		}
	//		return false;
	//	}

	/**
	 * [FIXME]:?:CHECK FOR STATE UNCLEAR!!!
	 */
	private void checkForNewState(DiagnosisState oldState, DiagnosisState newStatus,
			XPSCase theCase, Class context) {
		try {
			if (oldState != newStatus) {
				if (newStatus == DiagnosisState.ESTABLISHED) {
					establish(theCase);
				}
				if (oldState == DiagnosisState.ESTABLISHED) {
					deestablish(theCase);
				}
			}
		} catch (Exception e) {
			theCase.trace("<<EXP>> " + e + " in Diagnosis.checkForNewState(" + newStatus + ", "
					+ theCase + ", " + context + ")");
		}
	}

	public XPSCaseObject createCaseObject() {
		return new CaseDiagnosis(this);
	}

	/**
	 * Setzt den Wert (Status) der Diagnosis auf "deEtabliert" und propagiert
	 * seinen Wert weiter, wenn eine neue DeEtablierung stattgefunden hat.
	 */
	private void deestablish(XPSCase theCase) {
		theCase.trace("ziehe etablierte Diagnosis " + getId() + " zurueck.");
		((D3WebCase) theCase).removeEstablishedDiagnoses(this);
	}

	/**
	 * Setzt den Wert (Status) der Diagnosis auf "etabliert" und propagiert
	 * seinen Wert weiter, wenn eine neue Etablierung stattgefunden hat.
	 */
	private void establish(XPSCase theCase) {
		theCase.trace("etabliere Diagnose: " + getId());
		((D3WebCase) theCase).addEstablishedDiagnoses(this);
	}

	public Score getAprioriProbability() {
		return aprioriProbability;
	}

	public static Comparator getComparator(XPSCase theCase) {
		return new DiagComp(theCase);
	}

	/**
	 * @return the type of the heuristic decision tree
	 */
	public HDTType getHdtType() {
		return hdtType;
	}

	/**
	 * @return the score of the Diagnosis regarding to the specified
	 *         problem-solver context and specified user case.
	 */
	public DiagnosisScore getScore(XPSCase theCase, Class context) {
		return (DiagnosisScore) ((CaseDiagnosis) theCase.getCaseObject(this)).getValue(context);
	}

	/**
	 * @return the state of the Diagnosis regarding to the specified
	 *         problem-solver context and specified user case.
	 */
	public DiagnosisState getState(XPSCase theCase, Class context) {
		return theCase.getPSMethodInstance(context).getState(theCase, this);
	}

	/**
	 * Removes a child for this diagnosis from the parents property and connects
	 * this removement to the parent property of the specified diagnosis (double
	 * linked connection).
	 * 
	 * @return true, if removal was successfull
	 * @param theDiagnosis
	 *            Diagnosis to remove
	 */
	public synchronized boolean removeChildren(Diagnosis diagnosis) {
		if (removeFromList(diagnosis, getChildren()))
			diagnosis.removeParent(this);
		return false;
	}

	private boolean removeFromList(Diagnosis diagnosis, List list) {
		if (list.contains(diagnosis)) {
			list.remove(diagnosis);
			return true;
		}
		return false;
	}

	/**
	 * Removes a parent for this diagnosis from the parents property and
	 * connects this removement to the child property of the specified diagnosis
	 * (double linked connection).
	 * 
	 * @return true, if the removal was successfull
	 * @param theDiagnosis
	 *            Diagnosis to remove
	 */
	public synchronized boolean removeParent(Diagnosis diagnosis) {
		if (removeFromList(diagnosis, getParents()))
			diagnosis.removeChildren(this);
		return false;
	}

	/**
	 * Sets the new apriori propability. The value is fixed to P5, P4, P3, P2,
	 * N2, N3, N4, N5. Creation date: (25.09.00 15:13:34)
	 * 
	 * @param newAprioriPropability
	 *            de.d3web.kernel.domainModel.DiagnosisScore
	 */
	public void setAprioriProbability(Score newAprioriProbability) throws ValueNotAcceptedException {
		// check if legal propability entry
		if (!Score.APRIORI.contains(newAprioriProbability) && (newAprioriProbability != null)) {
			throw new ValueNotAcceptedException(newAprioriProbability
					+ " not in apriori probability list");
		} else
			aprioriProbability = newAprioriProbability;
	}

	/**
	 * Sets the heuristic decision tree type
	 * 
	 * @param newHdtType
	 *            de.d3web.kernel.domainModel.HDTType
	 */
	public void setHdtType(HDTType hdtType) {
		this.hdtType = hdtType;
	}

	/**
	 * Sets the knowledgebase, to which these objects belongs to and adds this
	 * object to the knowledge base (reverse link).
	 * 
	 * @param newKnowledgeBase
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 */
	public void setKnowledgeBase(KnowledgeBase kb) {
		try {
			super.setKnowledgeBase(kb);
			// maybe somebody should remove this object from the old
			// knowledge base if available
			getKnowledgeBase().add(this);
		} catch (KnowledgeBaseObjectModificationException e) {
			Logger.getLogger(this.getClass().getName()).throwing(this.getClass().getName(),
					"setKnowledgeBase", e);
		}
	}

	/**
	 * This is the official method to change the state of the Diagnosis. The
	 * first Object in the values array must be an DiagnosisScore Object.
	 */
	public void setValue(XPSCase theCase, Object[] values) {
		setValue(theCase, values, null);
	}

	/**
	 * This is the official method to change the state of the Diagnosis. The
	 * first Object in the values array must be an DiagnosisScore Object.
	 */
	public void setValue(XPSCase theCase, Object[] values, Class context) {
		try {
			DiagnosisScore diagnosisScore = null;
			DiagnosisState oldState = getState(theCase, context);

			if ((values != null) && (values.length > 0)) {
				Object value = values[0];
				if (value instanceof DiagnosisScore) {
					diagnosisScore = (DiagnosisScore) values[0];
					((CaseDiagnosis) theCase.getCaseObject(this)).setValue(diagnosisScore, context);
				} else if (value instanceof DiagnosisState) {
					((CaseDiagnosis) theCase.getCaseObject(this)).setValue(value, context);
				}
			}

			DiagnosisState newState = getState(theCase, context);

			// this does simply a check if state has changed
			checkForNewState(oldState, newState, theCase, context);

			// d3web.debug
			notifyListeners(theCase, this);

		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).throwing(this.getClass().getName(),
					"setValue(XPSCase, values(" + values[0] + ")", ex);
		}
	}

	public String toString() {
		return super.toString();
	}

}