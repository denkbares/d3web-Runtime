package de.d3web.core.session.blackboard.tests;


import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.plugin.test.InitPluginManager;

public class DialogTester {

	KnowledgeBaseManagement kbm;
	QuestionOC sex, pregnant, ask_for_pregnancy;
	ChoiceValue female, dont_ask; 
	private Session session;
	
	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kbm = KnowledgeBaseManagement.createInstance();
		
		sex = kbm.createQuestionOC("sex", kbm.getKnowledgeBase().getRootQASet(), new String[] {"male", "female"});
		pregnant = kbm.createQuestionOC("pregnant", sex, new String[] {"yes", "no"});
		female = new ChoiceValue(kbm.findChoice(sex, "female"));
		
		ask_for_pregnancy = kbm.createQuestionOC("ask for pregnancy", kbm.getKnowledgeBase().getRootQASet(), new String[] {"yes", "no"});
		dont_ask = new ChoiceValue(kbm.findChoice(ask_for_pregnancy, "no"));
		
		// Rule: sex = female => INDICATE ( pregnant ) 
		RuleFactory.createIndicationRule("r1", pregnant, new CondEqual(sex, female));
		// Rule: ask for pregnancy = no   =>  CONTRA_INDICATE ( pregnant ) 
		RuleFactory.createContraIndicationRule("r2", pregnant, new CondEqual(ask_for_pregnancy, dont_ask));
		
		session = SessionFactory.createSession(kbm.getKnowledgeBase());
	}

	@Test
	public void testIndication() {
		// SET:    sex = female
		// EXPECT: question "pregnant" is INDICATED
		session.setValue(sex, female);
		assertEquals(
				new Indication(State.INDICATED), 
				session.getBlackboard().getIndication(pregnant));

		// SET:    sex = undefined
		// EXPCECT: question "pregnant" is NEURTRAL
		session.setValue(sex, UndefinedValue.getInstance());
		assertEquals(
				new Indication(State.NEUTRAL), 
				session.getBlackboard().getIndication(pregnant));		
	}
	
	@Test
	public void testContraIndication() {
		// SET:    sex = female
		// EXPECT: question "pregnant" is INDICATED
		session.setValue(sex, female);
		assertEquals(
				new Indication(State.INDICATED), 
				session.getBlackboard().getIndication(pregnant));

		// SET:     ask_for_pregnancy = no
		// EXPCECT: question "pregnant" is CONTRA_INDICATED
		session.setValue(ask_for_pregnancy, dont_ask);
		assertEquals(
				new Indication(State.CONTRA_INDICATED), 
				session.getBlackboard().getIndication(pregnant));
		
		
		// SET:     ask_for_pregnancy = UNDEFINED
		// EXPCECT: question "pregnant" is INDICATED again
		session.setValue(ask_for_pregnancy, UndefinedValue.getInstance());
		assertEquals(
				new Indication(State.INDICATED), 
				session.getBlackboard().getIndication(pregnant));
	}

}
