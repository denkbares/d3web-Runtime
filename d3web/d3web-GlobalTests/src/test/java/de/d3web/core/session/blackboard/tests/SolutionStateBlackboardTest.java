package de.d3web.core.session.blackboard.tests;


import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.Rule;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.scoring.HeuristicRating;
import de.d3web.scoring.Score;
import de.d3web.scoring.inference.PSMethodHeuristic;
import de.d3web.xcl.inference.PSMethodXCL;

/**
 * This test class checks the correct management of states of {@link Solution} instances.
 * Facts - based on solution derivations - are submitted to the blackboard and the 
 * valid merge of these facts is tested.
 * The following derivation sources are tested:
 * <ol>
 * <li> {@link PSMethodHeuristic} derivations based on {@link HeuristicRating}
 * <li> TODO: {@link PSMethodXCL} derivations 
 * </ol> 
 *  
 * @author joba
 *
 */
public class SolutionStateBlackboardTest {
	private static Blackboard blackboard;
	private static KnowledgeBaseManagement kbm;
	private static final PSMethod heuristicSource = PSMethodHeuristic.getInstance();
	
	// used as sources for blackboard merge management
	private static Rule rule1, rule2, rule3;
	
	private Solution happy;
	
	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kbm = KnowledgeBaseManagement.createInstance();
		Session session = SessionFactory.createSession(kbm.getKnowledgeBase());
		blackboard = session.getBlackboard();
		
		happy = kbm.createSolution("happy");
		
		// rules are only needed for the source context of the blackboard management
		rule1 = RuleFactory.createHeuristicPSRule("r1", happy, Score.P4, null);
		rule2 = RuleFactory.createHeuristicPSRule("r2", happy, Score.P5, null);
		rule3 = RuleFactory.createHeuristicPSRule("r3", happy, Score.N7, null);
	}

	@Test
	public void testHeuristicScoring() {
		// initially the state of a solution has to be UNCLEAR
		assertTrue(blackboard.getState(happy).hasState(State.UNCLEAR));
	
		// put P4 => P4 = suggested
		Fact p4Fact = new DefaultFact(happy, new HeuristicRating(Score.P4), rule1, heuristicSource);
		blackboard.addValueFact(p4Fact);
		assertTrue(blackboard.getState(happy).hasState(State.SUGGESTED));
		
		// put another P5 => P4+P5 = established
		Fact p5Fact = new DefaultFact(happy, new HeuristicRating(Score.P5), rule2, heuristicSource);
		blackboard.addValueFact(p5Fact);
		assertTrue(blackboard.getState(happy).hasState(State.ESTABLISHED));

		// retract rule1 (P4) => P4 = suggested
		blackboard.removeValueFact(p4Fact);
		assertTrue(blackboard.getState(happy).hasState(State.SUGGESTED));
		
		// categorically exclude by rule3 => P4+N7 = excluded
		Fact n7Fact = new DefaultFact(happy, new HeuristicRating(Score.N7), rule3, heuristicSource);
		blackboard.addValueFact(n7Fact);
		assertTrue(blackboard.getState(happy).hasState(State.EXCLUDED));
		
	}

}
