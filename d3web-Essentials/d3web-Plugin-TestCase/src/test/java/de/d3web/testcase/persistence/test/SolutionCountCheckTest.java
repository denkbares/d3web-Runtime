package de.d3web.testcase.persistence.test;

import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Element;

import com.denkbares.plugin.test.InitPluginManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.scoring.HeuristicRating;
import de.d3web.scoring.Score;
import de.d3web.testcase.model.SolutionCountCheck;
import de.d3web.testcase.model.SolutionCountCheckTemplate;
import de.d3web.testcase.model.TransformationException;
import de.d3web.testcase.persistence.SolutionCountCheckHandler;
import de.d3web.testcase.persistence.TestCasePersistence;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 18.07.16
 */
public class SolutionCountCheckTest {

	private KnowledgeBase kb;
	private Solution solution1;
	private Solution solution2;

	@BeforeClass
	public static void init() throws IOException {
		InitPluginManager.init();
	}


	@Before
	public void setUp() {
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		solution1 = new Solution(kb, "Solution1");
		solution2 = new Solution(kb, "Solution2");
	}

	@Test
	public void basic() throws IOException {

		Session session = SessionFactory.createSession(kb);

		assertTrue(new SolutionCountCheck(Rating.State.ESTABLISHED, 0).check(session));
		assertFalse(new SolutionCountCheck(Rating.State.ESTABLISHED, 1).check(session));

		session.getBlackboard()
				.addValueFact(FactFactory.createUserEnteredFact(solution1, new HeuristicRating(Score.P7)));

		assertTrue(new SolutionCountCheck(Rating.State.ESTABLISHED, 1).check(session));
		assertFalse(new SolutionCountCheck(Rating.State.ESTABLISHED, 0).check(session));

		session.getBlackboard()
				.addValueFact(FactFactory.createUserEnteredFact(solution2, new HeuristicRating(Score.P7)));

		assertTrue(new SolutionCountCheck(Rating.State.ESTABLISHED, 2).check(session));
		assertFalse(new SolutionCountCheck(Rating.State.ESTABLISHED, 1).check(session));

		session.getBlackboard()
				.addValueFact(FactFactory.createUserEnteredFact(solution1, new HeuristicRating(Score.N7)));

		assertTrue(new SolutionCountCheck(Rating.State.ESTABLISHED, 1).check(session));
		assertFalse(new SolutionCountCheck(Rating.State.ESTABLISHED, 0).check(session));

	}

	@Test
	public void persistence() throws IOException, TransformationException {

		SolutionCountCheckTemplate template = new SolutionCountCheckTemplate(Rating.State.ESTABLISHED, 1);
		SolutionCountCheckHandler handler = new SolutionCountCheckHandler();
		assertTrue(handler.canWrite(template));
		TestCasePersistence persistence = new TestCasePersistence();
		Element element = handler.write(template, persistence);

		assertTrue(handler.canRead(element));
		SolutionCountCheckTemplate readTemplate = (SolutionCountCheckTemplate) handler.read(element, persistence);

		assertEquals(template.toCheck(kb).getCondition(), readTemplate.toCheck(kb).getCondition());
	}
}
