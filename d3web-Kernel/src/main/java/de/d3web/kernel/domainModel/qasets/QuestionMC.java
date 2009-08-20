package de.d3web.kernel.domainModel.qasets;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.dynamicObjects.CaseQuestionMC;
import de.d3web.kernel.dynamicObjects.XPSCaseObject;

/**
 * Storage for Questions with predefined multiple answers (alternatives).
 * <BR>
 * Part of the Composite design pattern (see QASet for further description)
 * 
 * @author joba, Christian Betz
 * @see QASet
 */
public class QuestionMC extends QuestionChoice {

	/**
	 * 
	 */
	public QuestionMC() {
		super();
	}
	
	public QuestionMC(String id) {
		super(id);
	}

	public XPSCaseObject createCaseObject() {
		return new CaseQuestionMC(this);
	}

	public List<AnswerChoice> getAlternatives() {
		if (alternatives == null) {
			return new Vector<AnswerChoice>();
		} else
			return alternatives;
	}

	public List getValue(XPSCase theCase) {
		List values = ((CaseQuestionMC) theCase.getCaseObject(this)).getValue();
		if (values != null) {
			return values;
		} else {
			// System.err.println("Fehlerhafte initialisierung des Fall-Wertes von MC-Fragen");
			return new LinkedList();
		}
	}

	/**
	 * Sets the current values of this MC-question belonging to the
	 * specified XPSCase.<BR>
	 * <B>Caution:</B> It is possible to set numerical values to a MC-
	 * question. In this case, a Num2ChoiceSchema must be defined a KnowledgeSlice.
	 * @param theCase the belonging XPSCase
	 * @param antwort an array of Answer instances
	 */
	public void setValue(XPSCase theCase, Object[] values) {
		List newValues = new ArrayList(values.length);
		for (int i = 0; i < values.length; i++) {
			if (values[i] instanceof AnswerNum) {
				values[i] = convertNumericalValue(theCase, (AnswerNum)values[i]);
			}
			newValues.add(values[i]);
		}
		((CaseQuestionMC) theCase.getCaseObject(this)).setValue(newValues);
		notifyListeners(theCase,this);
	}

	public String toString() {
		return super.toString();
	}
}
