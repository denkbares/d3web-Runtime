package de.d3web.costBenefit;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Node;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.CaseFactory;
import de.d3web.core.session.XPSCase;
import de.d3web.core.terminology.Answer;
import de.d3web.core.terminology.QContainer;
import de.d3web.core.terminology.Question;
import de.d3web.core.terminology.QuestionOC;
import de.d3web.costBenefit.inference.PSMethodCostBenefit;

/**
 * Provides basic static functions for the CostBenefit package.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class Util {

	public static XPSCase copyCase(XPSCase theCase) {
		XPSCase testCase = CaseFactory
				.createXPSCase(theCase.getKnowledgeBase(), new LinkedList<PSMethod>());
		List<? extends Question> answeredQuestions = new LinkedList<Question>(
				theCase.getAnsweredQuestions());
		for (Question q : answeredQuestions) {
			List<Answer> a = ((QuestionOC) q).getValue(theCase);
			Answer[] aarray = new Answer[a.size()];
			a.toArray(aarray);
			testCase.setValue(q, aarray);
		}
		return testCase;
	}

	public static void undo(XPSCase theCase, Map<Question, List<?>> undo) {
		for (Entry<Question, List<?>> entry : undo.entrySet()) {
			entry.getKey().setValue(theCase, entry.getValue().toArray());
			if (entry.getValue().size() == 0) {
				theCase.getAnsweredQuestions().remove(entry.getKey());
			}
		}
	}

	public static String getAttribute(String name, Node node) {
		if ((node != null) && (node.getAttributes() != null)) {
			try {
				return node.getAttributes().getNamedItem(name).getNodeValue();
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
	
	public static void setQuestion(XPSCase theCase, String question, String answer) {
		KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance(theCase.getKnowledgeBase());
		theCase.getPropagationContoller().openPropagation();
		QuestionOC question1 = (QuestionOC) kbm.findQuestion(question);
		theCase.setValue(question1, new Answer[] { kbm.findAnswer(question1, answer) });
		theCase.getPropagationContoller().commitPropagation();
	}
	
	public static StateTransition extractStateTransition(QContainer qcon) {
		List<? extends KnowledgeSlice> knowledge = qcon.getKnowledge(PSMethodCostBenefit.class, StateTransition.STATE_TRANSITION);
		for (KnowledgeSlice ks: knowledge) {
			if (ks instanceof StateTransition) {
				return (StateTransition) ks;
			}
		}
		return null;
	}
}
