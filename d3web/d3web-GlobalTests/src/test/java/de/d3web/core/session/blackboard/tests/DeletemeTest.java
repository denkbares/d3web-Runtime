package de.d3web.core.session.blackboard.tests;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.ProtocolEntry;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.plugin.test.InitPluginManager;

public class DeletemeTest {

	public void runDemoCase(String knowledgeFilename, String[][] caseValues) {
		try {

			// Initializing the plugins for the persistence
			InitPluginManager.init();
			PersistenceManager persistenceManager = PersistenceManager
					.getInstance();
			// Load knowledge base
			KnowledgeBase knowledgeBase = persistenceManager.load(new File(
					knowledgeFilename));

			PrintStream out = System.out;
			// Create a case (problem-solving session and set all specified
			// question/answers
			out.println("+++ Setting values +++");
			Session session = SessionFactory.createSession(knowledgeBase);
			for (String[] caseValue : caseValues) {
				Question question = toQuestion(caseValue[0], knowledgeBase);
				Value value = toValue(question, caseValue[1], knowledgeBase);
				session.getBlackboard().addValueFact(
						FactFactory.createFact(question, value,
								PSMethodUserSelected.getInstance(),
								PSMethodUserSelected.getInstance()));
			}

			// Print all solutions with a state != UNCLEAR
			out.println("+++ Solutions +++");
			for (Solution solution : knowledgeBase.getSolutions()) {
				Rating state = session.getBlackboard().getState(solution);
				if (!state.hasState(Rating.State.UNCLEAR))
					out.println("  " + solution + " (" + state + ")");
			}

			// Show all entered findings
			out.println("+++ Entered Questions +++");
			for (ProtocolEntry entry : session.getProtocol()
					.getProtocolHistory()) {
				out.println(entry.getQuestion() + " = " + entry.getValue());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Value toValue(Question question, String answer,
			KnowledgeBase knowledgeBase) {
		KnowledgeBaseManagement kbm = KnowledgeBaseManagement
				.createInstance(knowledgeBase);
		if (question instanceof QuestionChoice) {
			return new ChoiceValue(kbm.findChoice((QuestionChoice) question,
					answer));
		} else if (question instanceof QuestionNum) {
			return new NumValue(Double.valueOf(answer));
		} else if (question instanceof QuestionText) {
			return new TextValue(answer);
		} else
			throw new IllegalArgumentException("No applicable question given ("
					+ question + ").");
	}

	private Question toQuestion(String id, KnowledgeBase knowledgeBase) {
		Question q = knowledgeBase.searchQuestion(id);
		if (q != null)
			return q;
		else
			throw new IllegalArgumentException("Question with ID=" + id
					+ " not found.");
	}

}
