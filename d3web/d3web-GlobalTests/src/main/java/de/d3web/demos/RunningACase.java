package de.d3web.demos;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.ProtocolEntry;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.scoring.Score;

public class RunningACase {
	private QContainer demoQuestions;
	private QuestionOC pregnant;
	private QuestionNum weight;
	private Solution dangerousMood;
	private ChoiceValue yes;

	public static void main(String[] args) throws IOException {
		RunningACase myApp = new RunningACase();
		myApp.runDemoCase();
	}

	public void runDemoCase() throws IOException {
		KnowledgeBase knowledgeBase = buildKnowledgeBase();
		
		PrintStream out = System.out;
		// Create a case (problem-solving session and set all specified
		// question/answers
		out.println("+++ Setting values +++");
		Session session = SessionFactory.createSession(knowledgeBase);

		// set: pregnant = yes
		Fact fact1 = FactFactory.createFact(pregnant, yes, PSMethodUserSelected
					.getInstance(), PSMethodUserSelected.getInstance());
		session.getBlackboard().addValueFact(fact1);
		out.println(fact1);
		// set: weight = 80
		Fact fact2 = FactFactory.createFact(weight, new NumValue(80), 
				PSMethodUserSelected.getInstance(), 
				PSMethodUserSelected.getInstance());
		session.getBlackboard().addValueFact(fact2);
		out.println(fact2);
		
		// Print all solutions with a state != UNCLEAR
		out.println("+++ Solutions +++");
		for (Solution solution : knowledgeBase.getSolutions()) {
			Rating state = session.getBlackboard().getState(solution);
			if (!state.hasState(Rating.State.UNCLEAR))
				out.println("  " + solution + " (" + state + ")");
		}

		// Show all entered findings
		out.println("+++ Entered Questions +++");
		for (ProtocolEntry entry : session.getProtocol().getProtocolHistory()) {
			out.println("  " + entry);
		}
	}


	private KnowledgeBase buildKnowledgeBase() throws IOException {
		// root {container}
		// - demoQuestion {containers}
		// - pregnant [oc]
		// - weight [num]

		// Solution: dangerousMood
		InitPluginManager.init();
		KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance();
		QASet root = kbm.getKnowledgeBase().getRootQASet();

		demoQuestions = kbm.createQContainer("demoQuestions", root);
		pregnant = kbm.createQuestionOC("pregnant", demoQuestions, new String[] { "yes",
				"no" });
		yes = new ChoiceValue(kbm.findChoice(pregnant, "yes"));
		weight = kbm.createQuestionNum("weight", "weight", demoQuestions);

		dangerousMood = kbm.createSolution("dangerousMood");
		
		// Define the init questionnaire
		kbm.getKnowledgeBase().setInitQuestions(Arrays.asList(demoQuestions));
		
		// Define the magic rule: preganant=yes AND weight > 70 => dangerousMood (P7)
		List<Condition> terms = new ArrayList<Condition>();
		terms.add(new CondEqual(pregnant, yes));
		terms.add(new CondNumGreater(weight, Double.valueOf(70)));
		RuleFactory.createHeuristicPSRule("r1", dangerousMood, Score.P7, new CondAnd(terms));
		
		return kbm.getKnowledgeBase();
	}

}
